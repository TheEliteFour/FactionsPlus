package markehme.factionsplus;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.util.Scanner;

import markehme.factionsplus.Cmds.CmdSetJail;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.massivecraft.factions.FPlayer;
import com.massivecraft.factions.FPlayers;
import com.massivecraft.factions.Faction;
import com.massivecraft.factions.Factions;

public class FactionsPlusJail {
	public static Server server;
	
	public static boolean removeFromJail(String unJailingPlayer) {
		File jailingFile = new File("plugins" + File.separator + "FactionsPlus" + File.separator + "jails" + File.separator + "jaildata." + unJailingPlayer);
		
		jailingFile.delete();
		
		return false;
	}
	
	public static Location getJailLocation(Player player) {
		
		Faction CWFaction = Factions.i.get(player.getName());
		
		File currentJailFile = new File("plugins" + File.separator + "FactionsPlus" + File.separator + "jails" + File.separator + "loc." + CWFaction.getId());
		
		World world;
				
		if(currentJailFile.exists()) {
			try {
				String JailData = new Scanner(currentJailFile).useDelimiter("\\A").next();
					
				String[] jail_data =  JailData.split(":");
					
			    double x = Double.parseDouble(jail_data[0]);
			    double y = Double.parseDouble(jail_data[1]); // Y-Axis
			    double z = Double.parseDouble(jail_data[2]);
			    
			    float Y = Float.parseFloat(jail_data[3]); // Yaw
			    float p = Float.parseFloat(jail_data[4]);
			        	
			    world = (World)server.getWorld(jail_data[5]);
			    
			    return(new Location(world, x, y, z, Y, p));
			    
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		return null;
	}
	
	public static void OrganiseJail(Player player) {
		// creates jail file for a certain player TODO: Implant timed jails 
		// 0 	=	Not jailed, so remove the file
		// -1	=	Permentaly Jailed
		// 1	=	Any number larger than 1 stands for minutes 
		
		File jailingFile = new File("plugins" + File.separator + "FactionsPlus" + File.separator + "jails" + File.separator + "jaildata." + player.getName());
		
		if(!jailingFile.exists()) {
			try {
				jailingFile.createNewFile();
				
				FileWriter filewrite = new FileWriter(jailingFile, true);
				filewrite.write("0");
				
				filewrite.close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
	}
	
	public static boolean doJailPlayer(Player player, String name, int time) {
		if(!FactionsPlus.permission.has(player, "factionsplus.jail")) {
			player.sendMessage(ChatColor.RED + "No permission!");
			return false;
		}
		
		String args[] = null;
		
		Player jplayer = server.getPlayer(name);
		
		FPlayer fjplayer = FPlayers.i.get(jplayer.getName());
		String jcurrentID = fjplayer.getFaction().getId();
		
		FPlayer fplayer = FPlayers.i.get(player.getName());
		String PcurrentID = fplayer.getFaction().getId();
		
		if(jcurrentID != PcurrentID) {
			player.sendMessage("You can only jail players in your Faction!");
			return false;
		}
		
		OrganiseJail(jplayer);
		
		name = jplayer == null ? name.toLowerCase() : jplayer.getName().toLowerCase();
		
		File jailingFile = new File("plugins" + File.separator + "FactionsPlus" + File.separator + "jails" + File.separator + "jaildata." + player.getName());
		
		if(!jailingFile.exists()) {
			try {
				jailingFile.createNewFile();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		if (name != null) {
			jplayer.sendMessage(FactionsPlusTemplates.Go("jailed_message", args));
			return sendToJail(name, player, time);
		}
		
		return false;

	}
	
	public static boolean sendToJail(String jailingplayer, CommandSender sender, Integer t) {
		Player player = (Player)sender;
		
		FPlayer fplayer = FPlayers.i.get(sender.getName());
		Faction currentFaction = fplayer.getFaction();
		
		
		File currentJailFile = new File("plugins" + File.separator + "FactionsPlus" + File.separator + "jails" + File.separator + "loc." + currentFaction.getId());
		
		World world;
		Player jplayer = server.getPlayer(jailingplayer);
		FPlayer fjplayer = FPlayers.i.get(jplayer);
		
		if(!fjplayer.getFactionId().equals(fplayer.getFactionId())) {
			fplayer.msg("You can only Jail players that are in your Faction!");
			return false;
		}
		
		if(currentJailFile.exists()) {
			try {
				String JailData = new Scanner(currentJailFile).useDelimiter("\\A").next();
					
				String[] jail_data =  JailData.split(":");
					
			    double x = Double.parseDouble(jail_data[0]);
			    double y = Double.parseDouble(jail_data[1]); // y axis
			    double z = Double.parseDouble(jail_data[2]);
			    
			    float Y = Float.parseFloat(jail_data[3]); // yaw
			    float p = Float.parseFloat(jail_data[4]);
			    
			    world = (World)server.getWorld(jail_data[5]);
			       	
			    
			    
			    jplayer.teleport(new Location(world, x, y, z, Y, p));
			    
			    File jailingFile = new File("plugins" + File.separator + "FactionsPlus" + File.separator + "jails" + File.separator + "jaildata." + jplayer.getName());
				
				FileWriter filewrite = new FileWriter(jailingFile, true);
				filewrite.write("0");
				
			    player.sendMessage(ChatColor.GREEN + "Jailed!");
			       	
			} catch (Exception e) {
				e.printStackTrace();
				sender.sendMessage(ChatColor.RED + "Can not read the jail data, is a jail set?");
			}

		} else {
			sender.sendMessage(ChatColor.RED + "There is no jail currently set.");
		}
		
		return false;
	}
	
	public static boolean setJail(CommandSender sender) {
		if(!FactionsPlus.permission.has(sender, "factionsplus.setjail")) {
			sender.sendMessage(ChatColor.RED + "No permission!");
			return false;
		}
		
		FPlayer fplayer = FPlayers.i.get(sender.getName());
		Faction currentFaction = fplayer.getFaction();
		
		Boolean authallow = false;
		
		if(FactionsPlus.config.getBoolean("leadersCanSetJails")) {
			if(fplayer.getRole().toString().contains("admin") || fplayer.getRole().toString().contains("LEADER")) { // 1.6.x
				authallow = true;
			}
		}
		
		if(FactionsPlus.config.getBoolean("officersCanSetJails")) {
			if(fplayer.getRole().toString().contains("mod") || fplayer.getRole().toString().contains("OFFICER")) {
				authallow = true;
			}
		}

		
		if(FactionsPlus.config.getBoolean("membersCanSetJails")) {
			authallow = true;
		}
		
		if(!authallow) {
			sender.sendMessage(ChatColor.RED + "Sorry, your ranking is not high enough to do that!");
			return false;
		}
		
		if(!fplayer.isInOwnTerritory()) {
			sender.sendMessage(ChatColor.RED + "You must be in your own territory to set the jail location!");
			return false;
		}
		
		if(FactionsPlus.economy != null) {
			if(FactionsPlus.config.getInt("economy_costToSetJail") > 0) {
				if(!CmdSetJail.doFinanceCrap(FactionsPlus.config.getInt("economy_costToSetJail"), "", "", FPlayers.i.get(Bukkit.getPlayer(sender.getName())))) {
					return false;
				}
			}
		}
		
		File currentJailFile = new File("plugins" + File.separator + "FactionsPlus" + File.separator + "jails" + File.separator + "loc." + currentFaction.getId());
		
		Player player = (Player)sender;
		
		Location loc = player.getLocation();
		
		String jailData = loc.getX() + ":" + 
        loc.getY() + ":" + 
        loc.getZ() + ":" + 
        loc.getYaw() + ":" + 
        loc.getPitch() + ":" + player.getWorld().getName();
		
		DataOutputStream jailWrite;
		try {
			jailWrite = new DataOutputStream(new FileOutputStream(currentJailFile, false));
			jailWrite.write(jailData.getBytes());
			jailWrite.close();
			
			sender.sendMessage("Jail set!");
			
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			
			sender.sendMessage("Failed to set jail (Internal error -2)");
			return false;
		}
		
	}

	public static void unjailPlayer(String name) {
		new File("plugins" + File.separator + "FactionsPlus" + File.separator + "jails" + File.separator + "jaildata." + name).delete();
	}

	public static double getTempJailTime(Player p) {
		// TODO: getTempJailTime Function
		return 0;
	}
}