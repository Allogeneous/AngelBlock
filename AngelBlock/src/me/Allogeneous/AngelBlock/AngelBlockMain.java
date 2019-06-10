package me.Allogeneous.AngelBlock;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class AngelBlockMain extends JavaPlugin implements Listener{
	
	public static AngelBlockMain instance;
	public AngelBlock angelBlock;
	private String tag = ChatColor.translateAlternateColorCodes('&', "&9[&3AngelBlock&9] ");
	
	@SuppressWarnings("unchecked")
	@Override
	public void onEnable(){
		createConfig();
		verifyConfigVersion();
		instance = this;
		angelBlock = new AngelBlock();
		angelBlock.setSerializedLocations((List<String>) AngelBlockUtils.loadFile(new File(getDataFolder(), "AngelBlockLocations.dat")));
		File dataFolder = getDataFolder();
	    if(!dataFolder.exists() && (!dataFolder.mkdir())){
	    	getLogger().info("Could not create directory!");
	    }
		if(angelBlock.getSerializedLocations() == null) {
			angelBlock.setSerializedLocations(new ArrayList<String>());
	    }
		getServer().getPluginManager().registerEvents(this, this);
		if(getConfig().getBoolean("useRecipe", true)){
			getServer().addRecipe(angelBlock.getAngelBlockRecipe());
		}
		angelBlock.scheduleSerializedLocationsCleaner(20 * 60 * 15);
	}
	
	private void createConfig(){
		try{
			if(!getDataFolder().exists()){
				getDataFolder().mkdirs();
			}
			File file = new File(getDataFolder(), "config.yml");
				if(!file.exists()){
					saveDefaultConfig();
				}
		    }
		    catch (Exception ex) {
		    	Bukkit.getLogger().info("Error creating config file!");
		    }
		}
	
	private void verifyConfigVersion(){
		if(getConfig().getInt("configVersion") != 1){
			getLogger().info("Invalid config file found, creating a new one and copying the old one...");
			try{
			      File file = new File(getDataFolder(), "config.yml");
			      File lastConfig = new File(getDataFolder(), "last_config.yml");
			      if (file.exists()){
			    	if(lastConfig.exists()){
			    		lastConfig.delete();
			    	}
			    	file.renameTo(lastConfig);
			        file.delete();
			        saveDefaultConfig();
			        getLogger().info("Config files updated!");
			      }
			    }
			    catch (Exception ex) {
			    	getLogger().info("Something went wrong creating the new config file!");
			    }  
			}
		}
	
	@Override
	public void onDisable(){
		angelBlock.cleanSerializedLocations();
		AngelBlockUtils.saveFile(angelBlock.getSerializedLocations(), new File(getDataFolder(), "AngelBlockLocations.dat"));
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args){
		if(commandLabel.equalsIgnoreCase("giveangelblock")){
			if(sender instanceof Player){
				Player p = (Player) sender;
				if(p.hasPermission("angelblock.give")){
					angelBlock.give(p);
				}else{
					p.sendMessage(tag + "You do not have permission to do that!");
				}
			}
		}
		return true;
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void angelBlockPlaceGroundEvent(BlockPlaceEvent e){
		if(e instanceof AngelBlockPlaceEvent){
			return;
		}
		if(e.getPlayer().getItemInHand().isSimilar(angelBlock.getAngelBlockItem()) || angelBlock.hasSameLoreLine(e.getPlayer().getItemInHand())){
			String sLoc = AngelBlockUtils.getSerializedLocation(e.getBlock().getLocation());
			if(!angelBlock.getSerializedLocations().contains(sLoc)){
				angelBlock.getSerializedLocations().add(sLoc);
				angelBlock.saveSerializedLocations();
			}
		}
	}
	
	@SuppressWarnings("deprecation")
	@EventHandler
	public void angelBlockPlaceAirEvent(PlayerInteractEvent e){
		if(e.getAction() == Action.RIGHT_CLICK_AIR){
			if(e.getPlayer().getItemInHand().isSimilar(angelBlock.getAngelBlockItem()) || angelBlock.hasSameLoreLine(e.getPlayer().getItemInHand())){
				Block b = AngelBlockUtils.getTargetBlock(e.getPlayer(), 3);
				AngelBlockPlaceEvent bpe = new AngelBlockPlaceEvent(b, b.getState(), b, e.getPlayer().getItemInHand(), e.getPlayer(), true);
				Bukkit.getServer().getPluginManager().callEvent(bpe);
				if(bpe.isCancelled()){
					return;
				}
				if(b.getType() == Material.AIR){
					b.setType(angelBlock.getAngelBlockMaterial());
					String sLoc = AngelBlockUtils.getSerializedLocation(b.getLocation());
					if(!angelBlock.getSerializedLocations().contains(sLoc)){
						angelBlock.getSerializedLocations().add(sLoc);
						angelBlock.saveSerializedLocations();
						if(e.getPlayer().getGameMode() != GameMode.CREATIVE){
							e.getPlayer().getItemInHand().setAmount(e.getPlayer().getItemInHand().getAmount() - 1);
						}
					}
				}
			}
		}
	}
	
	@EventHandler
	public void angelBlockBreakEvent(BlockBreakEvent e){
		if(e instanceof AngelBlockBreakEvent){
			return;
		}
		if(e.getBlock().getType() == angelBlock.getAngelBlockMaterial()){
			String sLoc = AngelBlockUtils.getSerializedLocation(e.getBlock().getLocation());
			if(angelBlock.getSerializedLocations().contains(sLoc)){
				AngelBlockBreakEvent bbe = new AngelBlockBreakEvent(e.getBlock(), e.getPlayer());
				Bukkit.getServer().getPluginManager().callEvent(bbe);
				if(bbe.isCancelled()){
					return;
				}
				angelBlock.getSerializedLocations().remove(sLoc);
				angelBlock.saveSerializedLocations();
				e.setCancelled(true);
				e.getBlock().setType(Material.AIR);
				if(e.getPlayer().getGameMode() != GameMode.CREATIVE){
					e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), angelBlock.getAngelBlockItem());
				}
			}
		}
	}
}
