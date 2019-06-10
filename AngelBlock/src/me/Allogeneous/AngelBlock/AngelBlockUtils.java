package me.Allogeneous.AngelBlock;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.BlockIterator;

public class AngelBlockUtils {
	
	public static String hideMetaStrings(String data) {
        StringBuilder hidden = new StringBuilder("");
        for (char c : data.toCharArray()) {
        	hidden.append(ChatColor.COLOR_CHAR + "" + c);
        }
        return hidden.toString();
    }
	
	public static String retriveMetaStrings(String data) {
        return data.replace(ChatColor.COLOR_CHAR + "", "");
    }
	
	public static String getSerializedLocation(Location l){
		return new String(l.getWorld().getName() +  ":" + l.getX() + ":" + l.getY() + ":" + l.getZ());
	}
	
	public static Location getUnserializedLocation(String location){
		String[] parts = location.split(":");
		return new Location(Bukkit.getWorld(parts[0]), Double.parseDouble(parts[1]), Double.parseDouble(parts[2]), Double.parseDouble(parts[3]));
	}
	
	public static void saveFile(Object object, File file){
	    try{
	      if(!file.exists()){
	        file.createNewFile();
	      }
	      ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
	      oos.writeObject(object);
	      oos.flush();
	      oos.close();
	    }
	    catch (Exception e){
	      e.printStackTrace();
	    }
	  }
	 
	  public static Object loadFile(File file){
	    try{
	      ObjectInputStream ois = new ObjectInputStream(new FileInputStream(file));
	      Object object = ois.readObject();
	      ois.close();
	      return object;
	    }
	    catch (Exception e) {}
	    return null;
	  }
	  
	  public static Block getTargetBlock(Player player, int range) {
	        BlockIterator iter = new BlockIterator(player, range);
	        Block lastBlock = iter.next();
	        while (iter.hasNext()) {
	            lastBlock = iter.next();
	            if (lastBlock.getType() == Material.AIR) {
	                continue;
	            }
	            break;
	        }
	        return lastBlock;
	    }

}
