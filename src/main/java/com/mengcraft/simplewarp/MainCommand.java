package com.mengcraft.simplewarp;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.mengcraft.jopt.OptionParser;
import com.mengcraft.jopt.OptionSet;
import lombok.val;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.UUID;

public class MainCommand extends Command {

    private final Main main;

    MainCommand(Main main) {
        super("simplewarp");
        setAliases(ImmutableList.of("warp"));
        setPermission("simplewarp.use");
        setPermissionMessage(ChatColor.RED + "你没有权限使用该指令");
        this.main = main;
    }

    private static OptionSet parse(String[] input) {
        val parser = new OptionParser();
        parser.accepts("name").withRequiredArg();
        parser.accepts("type").withRequiredArg().ofType(Integer.TYPE);
        parser.accepts("lore").withOptionalArg();
        parser.accepts("slot").withRequiredArg().ofType(Integer.TYPE);
        parser.accepts("location");
        return parser.parse(input);
    }

    private static void valid(int idx) {
        if (idx < 0 || idx > 53) throw new IllegalStateException("格选定范围溢出");
    }

    private static void valid(boolean b, String message) {
        if (b) throw new IllegalStateException(message);
    }

    private static void set(Main main, Player p, String[] input) {
        int idx = Integer.parseInt(input[1]);
        valid(idx);
        valid(!main.getSlot().containsKey(idx), "选定格没有定义");

        val option = parse(input);
        val item = main.getInventory().getItem(idx);
        val key = main.getSlot().get(idx);

        if (option.has("slot")) {// 可能失败所以放在最前
            int slot = (Integer) option.valueOf("slot");
            valid(slot);
            valid(main.getSlot().containsKey(slot), "无法移到非空格");

            main.getSlot().remove(idx);
            main.getSlot().put(slot, key);
            main.getInventory().setItem(idx, null);
            main.getInventory().setItem(slot, item);
            main.getConfig().getConfigurationSection("section").getConfigurationSection(key).set("slot", slot);
        }

        if (option.has("location")) {
            val place = p.getLocation();
            val node = main.getConfig().getConfigurationSection("section." + key);
            node.set("x", place.getX());
            node.set("y", place.getY());
            node.set("z", place.getZ());
            node.set("world", place.getWorld().getName());
            main.getTable().put(node.getInt("slot"), place);
        }


        if (option.has("name")) {
            val name = option.valueOf("name").toString().replace('&', '§');
            val meta = item.getItemMeta();
            meta.setDisplayName(name);
            item.setItemMeta(meta);
            main.getConfig().getConfigurationSection("section").getConfigurationSection(key).set("name", name);
        }

        if (option.has("type")) {
            int type = (Integer) option.valueOf("type");
            item.setTypeId(type);
            main.getConfig().getConfigurationSection("section").getConfigurationSection(key).set("type", type);
        }

        if (option.has("lore")) {
            List<String> lore = ListHelper.collect(option.valuesOf("lore"), i -> i.toString().replace('&', '§'));
            val meta = item.getItemMeta();
            meta.setLore(lore);
            item.setItemMeta(meta);
            main.getConfig().getConfigurationSection("section").getConfigurationSection(key).set("lore", lore);
        }

        main.saveConfig();
        p.sendMessage(ChatColor.GREEN + "操作已完成");
    }

    private static void del(Main main, Player who, String[] input) {
        int idx = Integer.valueOf(input[1]);// led fast fail
        valid(idx);
        valid(Main.nil(main.getSlot().remove(idx)), "不存在此传送点");

        main.getTable().remove(idx);
        main.getInventory().setItem(idx, null);
        main.getConfig().set("section." + main.getSlot().remove(idx), null);
        main.saveConfig();

        who.sendMessage(ChatColor.GREEN + "操作已完成");
    }

    private static void add(Main main, Player who, String[] input) {
        int idx = Integer.valueOf(input[1]);
        valid(idx);
        valid(main.getSlot().containsKey(idx), "此格已设置传送");

        val option = parse(input);
        int type = option.has("type") ? (Integer) option.valueOf("type") : 339;
        val item = new ItemStack(type);
        val meta = item.getItemMeta();

        meta.setDisplayName(option.has("name") ? option.valueOf("name").toString().replace('&', '§') : ChatColor.BLUE + "未设置名称");
        if (option.has("lore")) {
            List<String> lore = ListHelper.collect(option.valuesOf("lore"), i -> i.toString().replace('&', '§'));
            meta.setLore(lore);
        }

        item.setItemMeta(meta);
        main.getInventory().setItem(idx, item);

        val place = who.getLocation();
        val key = UUID.randomUUID().toString();
        val def = ImmutableMap.builder().put("world", place.getWorld().getName()).put("x", place.getX()).put("y", place.getY()).put("z", place.getZ()).put("slot", idx).put("type", type).put("name", meta.getDisplayName()).put("lore", meta.getLore()).build();

        main.getSlot().put(idx, key);
        main.getTable().put(idx, place);
        main.getConfig().createSection("section." + key, def);
        main.saveConfig();

        who.sendMessage(ChatColor.GREEN + "传送点设置成功");
    }

    enum SubCommand {

        CREATE(MainCommand::add),
        SET(MainCommand::set),
        DELETE(MainCommand::del);

        interface $ {

            void exec(Main main, Player who, String[] input);
        }

        private final $ exec;

        SubCommand($ exec) {
            this.exec = exec;
        }
    }

    private boolean exec(Player who, String[] input) {
        val sub = input[0];
        try {
            SubCommand.valueOf(sub.toUpperCase()).exec.exec(main, who, input);
            return true;// End func with no exception mean ok
        } catch (RuntimeException t) {
            who.sendMessage(ChatColor.RED + t.getMessage());
        }
        return false;
    }

    @Override
    public boolean execute(CommandSender who, String label, String[] input) {
        if (!(who instanceof Player)) return false;
        if (input.length == 1) return false;
        val p = ((Player) who);
        if (input.length > 1 && who.hasPermission("simplewarp.admin")) {
            return exec(p, input);
        } else {
            p.openInventory(main.getInventory());
        }
        return true;
    }

}
