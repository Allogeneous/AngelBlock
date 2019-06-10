package me.Allogeneous.AngelBlock;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.scheduler.BukkitRunnable;

public class AngelBlockCleaner extends BukkitRunnable{
	
	@Override
	public void run() {
		List<String> locs =  new ArrayList<>(AngelBlockMain.instance.angelBlock.getSerializedLocations());
		for(String loc : locs){
			if(AngelBlockUtils.getUnserializedLocation(loc).getBlock().getType() != AngelBlockMain.instance.angelBlock.getAngelBlockMaterial()){
				AngelBlockMain.instance.angelBlock.getSerializedLocations().remove(loc);
			}
		}
		AngelBlockMain.instance.angelBlock.saveSerializedLocations();
	}

}
