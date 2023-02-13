package pw.arx.tycoonplugin.utils;

import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public final class ItemUtils {
	// prevent this class being constructed
	private ItemUtils() { }
	
	public static String SelectorName = "&dTycoon Tool &r| ";
	public static String SEL_ENABLED = "&a&lEnabled";
	public static String SEL_DISABLED = "&c&lDisabled";
	
	public static ItemStack LandSelector(Boolean active) {
		ItemStack i = new ItemStack(Material.BLAZE_ROD, 1);
		ItemMeta i_meta = i.getItemMeta();
		if(active)
			i_meta.setDisplayName(StringUtils.c(SelectorName + SEL_ENABLED));
		else
			i_meta.setDisplayName(StringUtils.c(SelectorName + SEL_DISABLED));
		
		List<String> i_lore = new ArrayList<>();
		i_lore.add(StringUtils.c("&7This tool allows you to select"));
		i_lore.add(StringUtils.c("&7land for your new tycoon."));
		i_meta.setLore(i_lore);
		
		i_meta.addEnchant(Enchantment.DURABILITY, 1, false);
		i_meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
		
		i.setItemMeta(i_meta);
		return i;
	}
	
	public static void ToggleSelector(Player p, Boolean active, ItemStack on, ItemStack off) {

		if(ItemUtils.HasSelector(p, on, off)) {
			
			for(int i = 0 ; i < p.getInventory().getSize() ; i++) {
				ItemStack THISITEM = p.getInventory().getItem(i);
				
				if(THISITEM == null)
					continue;
				
				if(THISITEM.getType() != on.getType())
					continue;
			
				if(!THISITEM.getItemMeta().getDisplayName().startsWith(StringUtils.c(SelectorName)))
					continue;
				
				p.getInventory().setItem(i, ItemUtils.LandSelector(active));
			}
			
		} else {
			p.getInventory().addItem(ItemUtils.LandSelector(true));
		}
		
	}
	
	public static Boolean HasSelector(Player p, ItemStack on, ItemStack off) {		
		if(p.getInventory().contains(on) || p.getInventory().contains(off))
			return true;

		return false;
	}
	
}
