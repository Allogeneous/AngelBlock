package me.Allogeneous.AngelBlock;

import java.io.File;

import org.bukkit.scheduler.BukkitRunnable;

public class AngelBlockSaver extends BukkitRunnable{
	
	@Override
	public void run() {
		AngelBlockUtils.saveFile(AngelBlockMain.instance.angelBlock.getSerializedLocations(), new File(AngelBlockMain.instance.getDataFolder(), "AngelBlockLocations.dat"));
	}
}
