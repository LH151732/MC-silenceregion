package com.example.silenceregion;

import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.bukkit.selections.Selection;
import org.bukkit.command.*;
import org.bukkit.entity.Player;
import org.bukkit.entity.EntityType;

import java.util.*;

public class SilenceRegionCommand implements CommandExecutor {
  private final SilenceRegionPlugin plugin;
  private final RegionManager manager;

  public SilenceRegionCommand(SilenceRegionPlugin p, RegionManager m) {
    this.plugin = p;
    this.manager = m;
  }

  @Override
  public boolean onCommand(CommandSender sender,
                           Command cmd, String label, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("仅玩家可用");
      return true;
    }
    Player p = (Player) sender;
    if (args.length == 0) return false;

    switch (args[0].toLowerCase()) {
      case "create":
        if (args.length < 2) {
          p.sendMessage("用法：/sr create <名称> wand [类型,类型]");
          return true;
        }
        String name = args[1];
        // 读取选区
        WorldEditPlugin we = (WorldEditPlugin)
            plugin.getServer().getPluginManager().getPlugin("WorldEdit");
        if (we == null) {
          p.sendMessage("请安装 WorldEdit");
          return true;
        }
        Selection sel = we.getSelection(p);
        if (sel == null) {
          p.sendMessage("先用 //wand 选区");
          return true;
        }
        List<EntityType> types = new ArrayList<>();
        if (args.length >= 4 && args[2].equalsIgnoreCase("wand")) {
          for (String s : args[3].split(",")) {
            try { types.add(EntityType.valueOf(s.toUpperCase())); }
            catch (Exception ex) { }
          }
        }
        Region r = new Region(name,
            sel.getMinimumPoint(), sel.getMaximumPoint(), types);
        manager.saveRegion(r);
        manager.applyToExisting();
        p.sendMessage("创建区域 " + name);
        return true;

      case "delete":
        if (args.length < 2) break;
        if (manager.deleteRegion(args[1])) {
          p.sendMessage("删除区域 " + args[1]);
        } else {
          p.sendMessage("未找到区域 " + args[1]);
        }
        return true;

      case "list":
        p.sendMessage("已定义区域: " + manager.listRegions()
            .stream().map(Region::getName).toList());
        return true;

      case "info":
        if (args.length < 2) break;
        Region info = manager.get(args[1]);
        if (info == null) {
          p.sendMessage("未找到区域 " + args[1]);
          return true;
        }
        p.sendMessage("区域 " + info.getName() +
            " C1=" + info.getCorner1() +
            " C2=" + info.getCorner2() +
            " 类型=" + info.getTypes());
        return true;

      case "reload":
        plugin.reloadConfig();
        manager.loadAll();
        manager.applyToExisting();
        p.sendMessage("已重载配置");
        return true;
    }
    return false;
  }
}
