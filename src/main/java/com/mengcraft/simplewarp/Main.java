package com.mengcraft.simplewarp;

import lombok.val;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class Main extends JavaPlugin {

    private Map<Integer, Location> table;
    private Map<Integer, String> slot;
    private Inventory inventory;

    public void onEnable() {
        saveDefaultConfig();

        val title = this.getConfig().getString("global.title", "传送点");
        inventory = this.getServer().createInventory(null, 54, title);

        table = new HashMap<>();
        slot = new HashMap<>();

        val root = this.getConfig().getConfigurationSection("section");
        if (!nil(root)) {
            for (String key : root.getKeys(false)) {
                val node = root.getConfigurationSection(key);
                val item = new ItemStack(node.getInt("type"));
                val meta = item.getItemMeta();

                meta.setDisplayName(node.getString("name"));
                meta.setLore(node.getStringList("lore"));
                item.setItemMeta(meta);
                int idx = node.getInt("slot");

                val location = new Location(this.getServer().getWorld(node.getString("world")), node.getDouble("x"), node.getDouble("y"), node.getDouble("z"));

                slot.put(idx, key);
                table.put(idx, location);
                inventory.setItem(idx, item);
            }
        }

        PluginHelper.addExecutor(this, new MainCommand(this));

        this.getServer().getPluginManager().registerEvents(new MainListener(this, table), this);
        this.getServer().getConsoleSender().sendMessage(new String[]{ChatColor.GREEN + "梦梦家高性能服务器出租", ChatColor.GREEN + "淘宝店 http://shop105595113.taobao.com"});
    }

    public static boolean nil(Object i) {
        return i == null;
    }

    public Map<Integer, Location> getTable() {
        return this.table;
    }

    public Map<Integer, String> getSlot() {
        return this.slot;
    }

    public Inventory getInventory() {
        return this.inventory;
    }

}
