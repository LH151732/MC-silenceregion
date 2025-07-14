package com.example.slienceregion;

import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.bukkit.WorldEditPlugin;
import com.sk89q.worldedit.IncompleteRegionException;
import com.sk89q.worldedit.LocalSession;
import com.sk89q.worldedit.WorldEdit;
import com.sk89q.worldedit.regions.Region;
import com.sk89q.worldedit.math.BlockVector3;
import java.util.*;
import java.util.stream.Collectors;
import org.bukkit.command.*;
import org.bukkit.entity.Animals;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.Location;

public class SlienceRegionCommand implements CommandExecutor, TabCompleter {
  private final SlienceregionPlugin plugin;
  private final RegionManager manager;

  public SlienceRegionCommand(SlienceregionPlugin p, RegionManager m) {
    this.plugin = p;
    this.manager = m;
  }

  @Override
  public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
    if (!(sender instanceof Player)) {
      sender.sendMessage("仅玩家可用");
      return true;
    }
    Player p = (Player) sender;
    if (args.length == 0) return false;

    switch (args[0].toLowerCase()) {
      case "create":
        if (!p.hasPermission("silenceregion.create")) {
          p.sendMessage("§c你没有创建静音区域的权限");
          return true;
        }
        if (args.length < 2) {
          p.sendMessage("用法：/sr create <名称> wand [类型,类型]");
          return true;
        }
        String name = args[1];
        // 检查区域是否已存在
        if (manager.get(name) != null) {
          p.sendMessage("§c区域 " + name + " 已存在");
          return true;
        }
        // 读取选区
        WorldEditPlugin we =
            (WorldEditPlugin) plugin.getServer().getPluginManager().getPlugin("FastAsyncWorldEdit");
        if (we == null) {
          we = (WorldEditPlugin) plugin.getServer().getPluginManager().getPlugin("WorldEdit");
        }
        if (we == null) {
          p.sendMessage("请安装 WorldEdit 或 FastAsyncWorldEdit");
          return true;
        }
        
        // 获取玩家选区
        try {
          com.sk89q.worldedit.entity.Player wePlayer = BukkitAdapter.adapt(p);
          LocalSession session = WorldEdit.getInstance().getSessionManager().get(wePlayer);
          com.sk89q.worldedit.world.World world = BukkitAdapter.adapt(p.getWorld());
          Region selection = session.getSelection(world);
          
          if (selection == null) {
            p.sendMessage("§c先用 //wand 选择区域");
            return true;
          }
          
          // 获取选区的最小和最大点
          BlockVector3 min = selection.getMinimumPoint();
          BlockVector3 max = selection.getMaximumPoint();
          
          // 转换为 Bukkit Location
          Location minLoc = new Location(p.getWorld(), min.getX(), min.getY(), min.getZ());
          Location maxLoc = new Location(p.getWorld(), max.getX(), max.getY(), max.getZ());
          
          List<EntityType> types = new ArrayList<>();
          if (args.length >= 4 && args[2].equalsIgnoreCase("wand")) {
            if (args[3].equalsIgnoreCase("ANIMALS")) {
              // 添加所有动物类型
              for (EntityType et : EntityType.values()) {
                if (et.getEntityClass() != null
                    && Animals.class.isAssignableFrom(et.getEntityClass())) {
                  types.add(et);
                }
              }
            } else {
              for (String s : args[3].split(",")) {
                try {
                  types.add(EntityType.valueOf(s.toUpperCase()));
                } catch (Exception ex) {
                  p.sendMessage("§e警告：未知实体类型 " + s);
                }
              }
            }
          }
          
          com.example.slienceregion.Region r = new com.example.slienceregion.Region(name, minLoc, maxLoc, types);
          manager.saveRegion(r);
          manager.applyToExisting();
          p.sendMessage("§a创建区域 " + name + " 成功");
        } catch (IncompleteRegionException e) {
          p.sendMessage("§c选区不完整，请使用 //wand 选择两个点");
          return true;
        } catch (Exception e) {
          p.sendMessage("§c获取选区时出错: " + e.getMessage());
          plugin.getLogger().warning("获取WorldEdit选区失败: " + e.getMessage());
          return true;
        }
        return true;

      case "delete":
        if (!p.hasPermission("silenceregion.delete")) {
          p.sendMessage("§c你没有删除静音区域的权限");
          return true;
        }
        if (args.length < 2) {
          p.sendMessage("用法：/sr delete <区域名>");
          return true;
        }
        if (manager.deleteRegion(args[1])) {
          manager.applyToExisting(); // 删除后更新实体状态
          p.sendMessage("§a删除区域 " + args[1] + " 成功");
        } else {
          p.sendMessage("§c未找到区域 " + args[1]);
        }
        return true;

      case "list":
        if (!p.hasPermission("silenceregion.list")) {
          p.sendMessage("§c你没有查看区域列表的权限");
          return true;
        }
        Collection<com.example.slienceregion.Region> regions = manager.listRegions();
        if (regions.isEmpty()) {
          p.sendMessage("§7当前没有定义任何静音区域");
        } else {
          p.sendMessage("§6=== 静音区域列表 ===");
          for (com.example.slienceregion.Region region : regions) {
            p.sendMessage(
                "§7- §f"
                    + region.getName()
                    + " §7(世界: "
                    + region.getCorner1().getWorld().getName()
                    + ", 类型数: "
                    + region.getTypes().size()
                    + ")");
          }
        }
        return true;

      case "info":
        if (!p.hasPermission("silenceregion.info")) {
          p.sendMessage("§c你没有查看区域信息的权限");
          return true;
        }
        if (args.length < 2) {
          p.sendMessage("用法：/sr info <区域名>");
          return true;
        }
        com.example.slienceregion.Region info = manager.get(args[1]);
        if (info == null) {
          p.sendMessage("§c未找到区域 " + args[1]);
          return true;
        }
        p.sendMessage("§6=== 区域信息: " + info.getName() + " ===");
        p.sendMessage("§7世界: §f" + info.getCorner1().getWorld().getName());
        p.sendMessage("§7优先级: §f" + info.getPriority());
        p.sendMessage("§7坐标1: §f" + formatLocation(info.getCorner1()));
        p.sendMessage("§7坐标2: §f" + formatLocation(info.getCorner2()));
        p.sendMessage("§7体积: §f" + info.getVolume() + " 方块");
        if (info.getTypes().isEmpty()) {
          p.sendMessage("§7静音类型: §f所有实体");
        } else {
          p.sendMessage("§7静音类型 (" + info.getTypes().size() + "):");
          String typeList =
              info.getTypes().stream()
                  .map(EntityType::name)
                  .sorted()
                  .collect(Collectors.joining(", "));
          p.sendMessage("§f" + typeList);
        }
        return true;

      case "types":
        if (!p.hasPermission("silenceregion.types")) {
          p.sendMessage("§c你没有修改区域类型的权限");
          return true;
        }
        if (args.length < 4) {
          p.sendMessage("用法：/sr types <区域名> <add|remove> <类型,类型...>");
          p.sendMessage("特殊类型: ANIMALS - 所有动物");
          return true;
        }
        com.example.slienceregion.Region typeRegion = manager.get(args[1]);
        if (typeRegion == null) {
          p.sendMessage("§c未找到区域 " + args[1]);
          return true;
        }

        String action = args[2].toLowerCase();
        List<EntityType> typesToModify = new ArrayList<>();

        // 解析类型
        if (args[3].equalsIgnoreCase("ANIMALS")) {
          for (EntityType et : EntityType.values()) {
            if (et.getEntityClass() != null
                && Animals.class.isAssignableFrom(et.getEntityClass())) {
              typesToModify.add(et);
            }
          }
        } else {
          for (String s : args[3].split(",")) {
            try {
              typesToModify.add(EntityType.valueOf(s.toUpperCase()));
            } catch (Exception ex) {
              p.sendMessage("§e警告：未知实体类型 " + s);
            }
          }
        }

        if (typesToModify.isEmpty()) {
          p.sendMessage("§c没有有效的实体类型");
          return true;
        }

        switch (action) {
          case "add":
            typeRegion.addTypes(typesToModify);
            manager.updateRegion(typeRegion);
            manager.applyToExisting();
            p.sendMessage("§a成功添加 " + typesToModify.size() + " 个类型到区域 " + args[1]);
            break;
          case "remove":
            typeRegion.removeTypes(typesToModify);
            manager.updateRegion(typeRegion);
            manager.applyToExisting();
            p.sendMessage("§a成功从区域 " + args[1] + " 移除 " + typesToModify.size() + " 个类型");
            break;
          default:
            p.sendMessage("§c无效的操作: " + action + " (使用 add 或 remove)");
            return true;
        }
        return true;

      case "reload":
        if (!p.hasPermission("silenceregion.reload")) {
          p.sendMessage("§c你没有重载配置的权限");
          return true;
        }
        plugin.reload(); // 使用新的 reload 方法
        p.sendMessage("§a配置重载成功");
        return true;

      default:
        return false;
    }
  }

  private String formatLocation(Location loc) {
    return loc.getBlockX() + ", " + loc.getBlockY() + ", " + loc.getBlockZ();
  }

  @Override
  public List<String> onTabComplete(
      CommandSender sender, Command cmd, String alias, String[] args) {
    if (!(sender instanceof Player)) return null;
    Player p = (Player) sender;

    List<String> completions = new ArrayList<>();

    if (args.length == 1) {
      // 主命令补全
      String[] cmds = {"create", "delete", "list", "info", "types", "reload"};
      for (String command : cmds) {
        if (command.startsWith(args[0].toLowerCase())
            && p.hasPermission("silenceregion." + command)) {
          completions.add(command);
        }
      }
    } else if (args.length == 2) {
      // 区域名补全
      switch (args[0].toLowerCase()) {
        case "delete":
        case "info":
        case "types":
          for (com.example.slienceregion.Region r : manager.listRegions()) {
            if (r.getName().toLowerCase().startsWith(args[1].toLowerCase())) {
              completions.add(r.getName());
            }
          }
          break;
      }
    } else if (args.length == 3) {
      // types 子命令补全
      if (args[0].equalsIgnoreCase("types")) {
        if ("add".startsWith(args[2].toLowerCase())) completions.add("add");
        if ("remove".startsWith(args[2].toLowerCase())) completions.add("remove");
      } else if (args[0].equalsIgnoreCase("create")) {
        completions.add("wand");
      }
    } else if (args.length == 4) {
      // 实体类型补全
      if ((args[0].equalsIgnoreCase("types") && args.length == 4)
          || (args[0].equalsIgnoreCase("create") && args[2].equalsIgnoreCase("wand"))) {
        // 添加特殊类型
        if ("ANIMALS".startsWith(args[args.length - 1].toUpperCase())) {
          completions.add("ANIMALS");
        }
        // 添加具体实体类型
        String lastArg = args[args.length - 1];
        String[] parts = lastArg.split(",");
        String prefix = parts[parts.length - 1].toUpperCase();

        for (EntityType et : EntityType.values()) {
          if (et.name().startsWith(prefix)) {
            String completion =
                lastArg.substring(0, lastArg.length() - parts[parts.length - 1].length())
                    + et.name();
            completions.add(completion);
          }
        }
      }
    }

    return completions;
  }
}
