package com.mengcraft.simplewarp;

import lombok.val;
import org.bukkit.Location;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.plugin.Plugin;

import java.util.Map;

public class MainListener implements Listener {

    private Map<Integer, Location> table;
    private String title;
    private Plugin main;

    public MainListener(Main main, Map<Integer, Location> table) {
        this.title = main.getConfig().getString("global.title");
        this.main = main;
        this.table = table;
    }

    @EventHandler
    public void handle(InventoryClickEvent event) {
        if (event.getInventory().getTitle().equals(this.title)) {
            event.setCancelled(true);
            int raw = event.getClick() == ClickType.NUMBER_KEY ? event.getHotbarButton() : event.getRawSlot();
            val location = table.get(raw);
            if (!Main.nil(location)) {
                val who = event.getWhoClicked();
                main.getServer().getScheduler().runTask(this.main, () -> {
                    who.closeInventory();
                    who.teleport(location);
                });
            }
        }
    }

}
