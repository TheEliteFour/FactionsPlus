package markehme.factionsplus;

import org.bukkit.*;
import org.bukkit.plugin.java.*;


public abstract class FactionsPlusPlugin extends JavaPlugin {
	private boolean allowPluginToEnable=true;

	public boolean isAllowPluginToEnable() {
		return allowPluginToEnable;
	}

	public void setDisAllowPluginToEnable() {
		allowPluginToEnable = false;
	}
	
	@Override
	public void onEnable() {
		if (!isAllowPluginToEnable()) {
			throw bailOut( "Failed to load(scroll up 1 or more pages to see error)" +
					" thus we won't allow bukkit to enable at this time" );
		}
	}

	/**
	 * allowed to be used as: throw bailOut(..); so that eclipse won't 
	 * complain about NPEs because it doesn't see that it can "throw"<br>
	 * the "throw " in front of the call, is also used to make sure the reader(and eclipse) knows that the call will 
	 * unconditionally throw, as to
	 * not expect the code to continue<br>
	 * @param msg
	 * @return is a dummy return so that it can be used like: throw bailOut(...);
	 */
	public static RuntimeException bailOut(String msg) {
		severe(msg);
		throw disableSelf(FactionsPlus.instance, true);
	}

	public static void warn(String logInfoMsg) {
	//		log.warn( FP_TAG_IN_LOGS+logInfoMsg );
			tellConsole(ChatColor.GOLD+FactionsPlus.FP_TAG_IN_LOGS+ChatColor.DARK_RED+"[WARNING]:"+ChatColor.RESET+logInfoMsg );//they are logged with [INFO] level
		}

	public static void info(String logInfoMsg) {
	//		log.info( FP_TAG_IN_LOGS+logInfoMsg );//log.info won't handle colors btw
			tellConsole(ChatColor.GOLD+FactionsPlus.FP_TAG_IN_LOGS+ChatColor.RESET+logInfoMsg );//they are logged with [INFO] level
		}

	public static void severe(String logInfoMsg) {
		FactionsPlus.log.severe( FactionsPlus.FP_TAG_IN_LOGS+logInfoMsg );//allowed so that [SEVERE] appears
		tellConsole(ChatColor.RED+FactionsPlus.FP_TAG_IN_LOGS+ChatColor.DARK_PURPLE+logInfoMsg);
	}

	/**
	 * allows the use of ChatColor in messages but they will be prefixed by [INFO]
	 * @param msg
	 */
	public static void tellConsole(String msg) {
		//nvm; find another way to display colored msgs in console without having [INFO] prefix
		//there's no other way it's done via ColouredConsoleSender of craftbukkit
		//there are only two ways: colors+[INFO] prefix, or no colors + whichever prefix
		Bukkit.getConsoleSender().sendMessage( msg);//this will log with [INFO] level
	}

	public static RuntimeException disableSelf(FactionsPlus fpInstance, boolean forceStop) {
		disableSelf(fpInstance);
		if (forceStop) {
			throw new RuntimeException(FactionsPlus.FP_TAG_IN_LOGS+" execution stopped by disableSelf() which means read the above colored message");
		}
		return null;
	}

	/**
	 * calling this will not stop execution at the point where the call is made, but will mark the plugin as disabled<br>
	 * ie. shown in red when /plugins  is issued
	 * @param fpInstance
	 */
	public static void disableSelf(FactionsPlus fpInstance) {
		Bukkit.getPluginManager().disablePlugin( fpInstance );//it will call onDisable()
		//it won't deregister commands ie. /f fc  will still work
	}
	
}
