package me.Allogeneous.AngelBlock;

import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.block.BlockBreakEvent;

public class AngelBlockBreakEvent extends BlockBreakEvent{

	public AngelBlockBreakEvent(Block theBlock, Player player) {
		super(theBlock, player);
	}

}
