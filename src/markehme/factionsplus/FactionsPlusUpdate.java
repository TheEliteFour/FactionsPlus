package markehme.factionsplus;

import java.net.URL;
import java.net.URLConnection;
import java.util.Scanner;

import markehme.factionsplus.config.*;

public class FactionsPlusUpdate implements Runnable {
	private static FactionsPlusUpdate once=null;
	private static Thread updateThread=null;
	
	static public void checkUpdates() {
		if (null == once) {
			once=new FactionsPlusUpdate();
			assert null == updateThread;
			updateThread= new Thread(once);
			updateThread.setDaemon( false );
		}else {
			updateThread.stop();
		}
		updateThread.start();
	}
	
	public static void ensureNotRunning() {
		if ((null != updateThread)&&(updateThread.isAlive())) {
			updateThread.interrupt();
			if (!updateThread.isInterrupted()) {
				FactionsPlus.warn( "Killing update thread!" );
				updateThread.stop();
			}
			assert !updateThread.isAlive();
		}
	}

	@Override
	public void run() {
		String content = null;
		URLConnection connection = null;
		String v = FactionsPlus.version;
		
		if(Config._extras.disableUpdateCheck._) {
			return;
		}
		
		FactionsPlusPlugin.info("Checking for updates ... ");
		
		Scanner scanner=null;
		try {
			connection =  new URL("http://www.markeh.me/factionsplus.php?v=" + v).openConnection();
			scanner = new Scanner(connection.getInputStream());
			scanner.useDelimiter("\\Z");
			content = scanner.next();
		} catch ( Exception ex ) {
		    ex.printStackTrace();
		    FactionsPlusPlugin.info("Failed to check for updates.");
		    return;
		}finally{
			if (null != scanner) {
				scanner.close();
			}
		}

		// advanced checking
		if(!content.trim().equalsIgnoreCase(v.trim())){
			int web, current;
			String tempWeb = content.trim().replace(".", "");
			String tempThis = v.trim().replace(".", "");

			web = Integer.parseInt(tempWeb);
			current = Integer.parseInt(tempThis);
			
			// Check if version lengths are the same
			if(tempWeb.length() == tempThis.length()){
				if(web > current){
					// Version lengths different, unable to advance compare
					FactionsPlus.log.warning("! -=====================================- !");
					FactionsPlus.log.warning("FactionsPlus has an update, you");
					FactionsPlus.log.warning("can upgrade to version " + content.trim() + " via");
					FactionsPlus.log.warning("http://dev.bukkit.org/server-mods/factionsplus/");
					FactionsPlus.log.warning("! -=====================================- !");
				} else {
					FactionsPlusPlugin.info("Up to date!");
				}
			} else {
				// Version lengths different, unable to advance compare
				FactionsPlus.log.warning("! -=====================================- !");
				FactionsPlus.log.warning("FactionsPlus has an update, you");
				FactionsPlus.log.warning("can upgrade to version " + content.trim() + " via");
				FactionsPlus.log.warning("http://dev.bukkit.org/server-mods/factionsplus/");
				FactionsPlus.log.warning("! -=====================================- !");
			}
		} else {
			FactionsPlusPlugin.info("Up to date!");
		}
	}
}
