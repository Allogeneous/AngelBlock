package me.Allogeneous.AngelBlock;

import java.util.ArrayList;
import java.util.ConcurrentModificationException;
import java.util.List;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.ShapedRecipe;
import org.bukkit.inventory.meta.ItemMeta;

public class AngelBlock {
	
	private final String metaID;
	private final ItemStack angelBlockItem;
	private final Material angelBlockMaterial;
	private List<String> serializedLocations;
	private final AngelBlockSaver angelBlockSaver;
	private final AngelBlockCleaner angelBlockCleaner;
	private final ShapedRecipe angelBlockRecipe;
	
	public AngelBlock(){
		metaID = AngelBlockUtils.hideMetaStrings("AngelBlock.isAngelBlock");
		angelBlockMaterial = Material.GLASS;
		angelBlockItem = makeAngelBlockItem();
		angelBlockSaver = new AngelBlockSaver();
		angelBlockCleaner = new AngelBlockCleaner();
		angelBlockRecipe = makeAngelBlockRecipe();
	}
	
	public ItemStack makeAngelBlockItem(){
		ItemStack angelBlockItem = new ItemStack(angelBlockMaterial);
		ItemMeta im = angelBlockItem.getItemMeta(); 
		im.setDisplayName(ChatColor.RESET + "Angel Block");
		List<String> lore  = im .getLore();
		if(lore == null){
			lore = new ArrayList<>();
		}
		lore.add(metaID);
		im.setLore(lore);
		im.addEnchant(Enchantment.PROTECTION_FALL, 1, true);
		angelBlockItem.setItemMeta(im);
		return angelBlockItem;
	}
	
	public ShapedRecipe makeAngelBlockRecipe(){
		@SuppressWarnings("deprecation")
		ShapedRecipe recipe = new ShapedRecipe(angelBlockItem);
		recipe.shape("fdf", "gGg", "fdf");
		recipe.setIngredient('f', Material.FEATHER);
		recipe.setIngredient('d', Material.DIAMOND);
		recipe.setIngredient('g', Material.GOLD_INGOT);
		recipe.setIngredient('G', Material.GLASS);
		return recipe;
	}
	
	public void give(Player player){
		if(player.getInventory().firstEmpty() != -1){
			player.getInventory().addItem(getAngelBlockItem());
		}else{
			player.getWorld().dropItemNaturally(player.getLocation(), getAngelBlockItem());
		}
	}
	
	public boolean hasSameLoreLine(ItemStack angelBlock){
		if(angelBlock.hasItemMeta()){
			if(angelBlock.getItemMeta().hasLore() && angelBlock.getItemMeta().hasEnchant(Enchantment.PROTECTION_FALL)){
				if(angelBlock.getItemMeta().getLore().get(0).equals(metaID)){
					return true;
				}
			}
		}
		return false;
	}

	public void scheduleSerializedLocationsCleaner(long interval){
		angelBlockCleaner.runTaskTimer(AngelBlockMain.instance, interval, interval);
	}
	
	public synchronized void saveSerializedLocations(){
		try{
			Thread save = new Thread(angelBlockSaver);
			save.start();
		}catch(ConcurrentModificationException e){
			AngelBlockMain.instance.getLogger().info("Error saving Angel Block locations!");
		}
	}
	
	public synchronized void cleanSerializedLocations(){
		try{
			Thread save = new Thread(angelBlockCleaner);
			save.start();
		}catch(ConcurrentModificationException e){
			AngelBlockMain.instance.getLogger().info("Error cleaning Angel Block locations!");
		}
	}
	
	public ItemStack getAngelBlockItem() {
		return angelBlockItem;
	}
	
	public String getMetaID() {
		return metaID;
	}
	
	public List<String> getSerializedLocations() {
		return serializedLocations;
	}

	public void setSerializedLocations(List<String> serializedLocations) {
		this.serializedLocations = serializedLocations;
	}

	public Material getAngelBlockMaterial() {
		return angelBlockMaterial;
	}
	
	public AngelBlockSaver getAngelBlockSaver() {
		return angelBlockSaver;
	}

	public AngelBlockCleaner getAngelBlockCleaner() {
		return angelBlockCleaner;
	}

	public ShapedRecipe getAngelBlockRecipe() {
		return angelBlockRecipe;
	}
}
