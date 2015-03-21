package no.atc.floyd.bukkit.magicblocks;

import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
//import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
//import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.PluginManager;

import java.util.logging.Logger;
import com.sk89q.worldguard.bukkit.WorldGuardPlugin;
import com.sk89q.worldguard.protection.flags.DefaultFlag;
import com.sk89q.worldguard.protection.flags.InvalidFlagFormat;
import com.sk89q.worldguard.protection.flags.StateFlag;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.ProtectedRegion;


//import com.nijikokun.bukkit.Permissions.Permissions;

/**
* Multicast plugin for Bukkit
*
* @author FloydATC
*/

public class MagicBlocks extends JavaPlugin implements Listener {

    WorldGuardPlugin worldguard = null;
    
	public static final Logger logger = Logger.getLogger("Minecraft.MagicBlocks");
    

    public void onDisable() {
    	PluginDescriptionFile pdfFile = this.getDescription();
		logger.info( pdfFile.getName() + " version " + pdfFile.getVersion() + " is disabled!" );
    }

    public void onEnable() {
        PluginDescriptionFile pdfFile = this.getDescription();

		PluginManager pm = getServer().getPluginManager();
        pm.registerEvents((Listener) this, this);

    	// WorldGuard integration
    	Plugin wg = getServer().getPluginManager().getPlugin("WorldGuard");
    	if (wg == null || !(wg instanceof WorldGuardPlugin)) {
    		getLogger().info("WorldGuard not loaded, will not update regions");
    	} else {
    		worldguard = (WorldGuardPlugin) wg; 
    		getLogger().info("Using WorldGuard to update regions");
    	}
    	

		logger.info( pdfFile.getName() + " version " + pdfFile.getVersion() + " is enabled!" );
    }


  
  public boolean onCommand(CommandSender sender, Command cmd, String commandLabel, String[] args ) {
  	String cmdname = cmd.getName().toLowerCase();
      Player player = null;
      String pname = "(Server)";
      if (sender instanceof Player) {
      	player = (Player)sender;
      	pname = player.getName();
      }
      
      if (sender.isOp() == false) {
    	  logger.info("[MagicBlocks] pname="+pname+" is not an operator");
    	  return false;
      }

      if (cmdname.equalsIgnoreCase("regionflag") && args.length == 3) {
    	  
  		  if (worldguard == null) { 
  			  logger.info("[MagicBlocks] WorldGuard not detected");
  			  return false; 
  		  }
		  World w = sender.getServer().getWorld("world");
    	  String regionName = args[0];
    	  String flagName = args[1];
    	  StateFlag flagObject = null;
    	  String flagValue = args[2];

    	  // Get region
  		  RegionManager regionManager = worldguard.getRegionManager(w);
  		  ProtectedRegion region = regionManager.getRegion(regionName);
  		  if (region == null) {
  			  logger.info("[MagicBlocks] WorldGuard unknown region '"+regionName+"'");
  		      return false;
  		  } else {

  			  if (flagName.equalsIgnoreCase("pvp")) {
  				  flagObject = DefaultFlag.PVP;
  			  }
  			  if (flagName.equalsIgnoreCase("build")) {
  				  flagObject = DefaultFlag.BUILD;
  			  }
  			  if (flagName.equalsIgnoreCase("use")) {
  				  flagObject = DefaultFlag.USE;
  			  }
  			  if (flagName.equalsIgnoreCase("entry")) {
  				  flagObject = DefaultFlag.ENTRY;
  			  }
  			  if (flagName.equalsIgnoreCase("exit")) {
  				  flagObject = DefaultFlag.EXIT;
  			  }
  			  
  			  if (flagObject == null) {
  				  logger.info("[MagicBlocks] Unsupported region flag: "+flagName);
  				  return false;
  			  }
  			  
  			  try {
		  		  region.setFlag(flagObject, flagObject.parseInput(worldguard, sender, flagValue));
			      logger.info("[MagicBlocks] Worldguard region updated: '"+regionName+"' "+regionName+" => "+flagValue);
			      return true;
			  } catch (InvalidFlagFormat e) {
				  e.printStackTrace();
			      logger.info("[MagicBlocks] Worldguard region NOT updated: '"+regionName+"' "+regionName+" => "+flagValue);
			      return true;
			  }
  		  }

      }
      
      if (cmdname.equalsIgnoreCase("setblocks") && args.length >= 7) {
		  World w = sender.getServer().getWorld("world");
    	  Location loc = new Location(w, 0, 0, 0);
		  Block b = null;
		  
    	  Integer x1 = Integer.parseInt(args[0]);
    	  Integer x2 = Integer.parseInt(args[1]);
    	  Integer y1 = Integer.parseInt(args[2]);
    	  Integer y2 = Integer.parseInt(args[3]);
    	  Integer z1 = Integer.parseInt(args[4]);
    	  Integer z2 = Integer.parseInt(args[5]);
    	  Integer m = Integer.parseInt(args[6]);
    	  Byte d = 0;
    	  if (args.length == 8) {
    		  d = Byte.parseByte(args[7]);
    	  }
    	  
    	  // Sanity check 1
    	  if (x1 > x2) {
    		  logger.info("[MagicBlocks] pname="+pname+" x1 must be <= x2");
    		  return false;
    	  }
    	  if (y1 > y2) {
    		  logger.info("[MagicBlocks] pname="+pname+" y1 must be <= y2");
    		  return false;
    	  }
    	  if (z1 > z2) {
    		  logger.info("[MagicBlocks] pname="+pname+" z1 must be <= z2");
    		  return false;
    	  }

    	  // Sanity check 2
    	  if (x2 - x1 > 16) {
    		  logger.info("[MagicBlocks] pname="+pname+" x2-x1 must be <= 16");
    		  return false;
    	  }
    	  if (y2 - y1 > 16) {
    		  logger.info("[MagicBlocks] pname="+pname+" y2-y1 must be <= 16");
    		  return false;
    	  }
    	  if (z2 - z1 > 16) {
    		  logger.info("[MagicBlocks] pname="+pname+" z2-z1 must be <= 16");
    		  return false;
    	  }
    	  
    	  
    	  
    	  logger.fine("[MagicBlocks] pname="+pname+" x1="+x1+", x2="+x2+", y1="+y1+", y2="+y2+", z1="+z1+", z2="+z2+", material="+m);
    	  for (int x = x1; x <= x2; x++) {
    		  loc.setX(x);
        	  for (int y = y1; y <= y2; y++) {
        		  loc.setY(y);
            	  for (int z = z1; z <= z2; z++) {
            		  loc.setZ(z);
            		  b = w.getBlockAt(loc);
            		  b.setTypeId(m);
            		  b.setData(d);
            	  }
        	  }
    	  }
    	  return true;
      }

      if (cmdname.equalsIgnoreCase("breakblocks") && args.length >= 6) {
		  World w = sender.getServer().getWorld("world");
		  if (args.length == 7) {
			  w = sender.getServer().getWorld(args[6]);
    		  if (w == null) {
    			  logger.info("[MagicBlocks] pname="+pname+" unknown world '"+args[6]+"'");
    			  return false;
    		  }
		  }
    	  Location loc = new Location(w, 0, 0, 0);
		  Block b = null;
		  
    	  Integer x1 = Integer.parseInt(args[0]);
    	  Integer x2 = Integer.parseInt(args[1]);
    	  Integer y1 = Integer.parseInt(args[2]);
    	  Integer y2 = Integer.parseInt(args[3]);
    	  Integer z1 = Integer.parseInt(args[4]);
    	  Integer z2 = Integer.parseInt(args[5]);
    	  
    	  // Sanity check 1
    	  if (x1 > x2) {
    		  logger.info("[MagicBlocks] pname="+pname+" x1 must be <= x2");
    		  return false;
    	  }
    	  if (y1 > y2) {
    		  logger.info("[MagicBlocks] pname="+pname+" y1 must be <= y2");
    		  return false;
    	  }
    	  if (z1 > z2) {
    		  logger.info("[MagicBlocks] pname="+pname+" z1 must be <= z2");
    		  return false;
    	  }

    	  // Sanity check 2
    	  if (x2 - x1 > 16) {
    		  logger.info("[MagicBlocks] pname="+pname+" x2-x1 must be <= 16");
    		  return false;
    	  }
    	  if (y2 - y1 > 16) {
    		  logger.info("[MagicBlocks] pname="+pname+" y2-y1 must be <= 16");
    		  return false;
    	  }
    	  if (z2 - z1 > 16) {
    		  logger.info("[MagicBlocks] pname="+pname+" z2-z1 must be <= 16");
    		  return false;
    	  }
    	  
    	  
    	  
    	  logger.fine("[MagicBlocks] pname="+pname+" x1="+x1+", x2="+x2+", y1="+y1+", y2="+y2+", z1="+z1+", z2="+z2);
    	  for (int x = x1; x <= x2; x++) {
    		  loc.setX(x);
        	  for (int y = y1; y <= y2; y++) {
        		  loc.setY(y);
            	  for (int z = z1; z <= z2; z++) {
            		  loc.setZ(z);
            		  b = w.getBlockAt(loc);
            		  b.breakNaturally();
            	  }
        	  }
    	  }
    	  return true;
      }
      return false;
  }
  
  
  
	
}
