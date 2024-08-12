package be.openmc.ggames.utils;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import de.tr7zw.changeme.nbtapi.NBTItem;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.List;

public class ItemFactory {
    private ItemStack item;
    private @Nullable String skullOwner;

    public ItemFactory(Material material) {
        this(material, 1);
    }

    public ItemFactory(ItemStack itemStack) {
        this.item = itemStack;
    }

    public ItemFactory(Material material, int amount) {
        this.item = new ItemStack(material, amount);
    }

    public ItemFactory clone() {
        return new ItemFactory(this.item);
    }

    public ItemFactory setDurability(int durability) {
            ItemMeta im = this.item.getItemMeta();
            ((org.bukkit.inventory.meta.Damageable) im).setDamage(durability);
            this.item.setItemMeta(im);
        return this;
    }
    
    public ItemFactory setCustomModelData(int num) {
        	ItemMeta im = this.item.getItemMeta();
        	im.setCustomModelData(num);
        	this.item.setItemMeta(im);
        return this;
    }
    
    public ItemFactory setAmount(int amount) {
        this.item.setAmount(amount);
        return this;
    }

    public ItemFactory setType(Material material) {
        this.item.setType(material);
        return this;
    }

    public ItemFactory setName(String name) {
        ItemMeta im = this.item.getItemMeta();
        im.setDisplayName(ChatColor.translateAlternateColorCodes('&', name));
        this.item.setItemMeta(im);
        return this;
    }

    public ItemFactory setUnbreakable(boolean unbreakable){
        ItemMeta im = this.item.getItemMeta();
        im.setUnbreakable(unbreakable);
        this.item.setItemMeta(im);
        return this;
    }

    public ItemFactory setLore(List<String> lore) {
        ItemMeta im = this.item.getItemMeta();
        List<String> formatted = new ArrayList<>();
        for (String str : lore)
            formatted.add(ChatColor.translateAlternateColorCodes('&', str));
        im.setLore(formatted);
        this.item.setItemMeta(im);
        return this;
    }

    public ItemFactory setLore(String... lore) {
        List<String> loreList = new ArrayList<>();
        byte b;
        int i;
        String[] arrayOfString;
        for (i = (arrayOfString = lore).length, b = 0; b < i; ) {
            String loreLine = arrayOfString[b];
            loreList.add(ChatColor.translateAlternateColorCodes('&', loreLine));
            b++;
        }
        ItemMeta im = this.item.getItemMeta();
        assert im != null;
        im.setLore(loreList);
        this.item.setItemMeta(im);
        return this;
    }

    public ItemFactory setNBT(String key, String value) {
        NBTItem nbt = new NBTItem(this.item);
        nbt.setString(key, value);
        nbt.mergeNBT(this.item);
        return this;
    }

    public ItemStack toItemStack() {
        return this.item;
    }
}
