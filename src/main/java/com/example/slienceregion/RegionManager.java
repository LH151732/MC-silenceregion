package com.example.slienceregion;

import java.util.*;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;

public class RegionManager {
  private final SlienceregionPlugin plugin;
  private final Map<String, Region> regions = new HashMap<>();

  public RegionManager(SlienceregionPlugin plugin) {
    this.plugin = plugin;
  }

  /** 加载所有区域 */
  public void loadAll() {
    regions.clear();
    FileConfiguration cfg = plugin.getConfig();
    if (cfg.isConfigurationSection("regions")) {
      for (String name : cfg.getConfigurationSection("regions").getKeys(false)) {
        Region r = Region.loadFromConfig(name, cfg.getConfigurationSection("regions"));
        if (r != null) regions.put(name, r);
      }
    }
  }

  /** 存入 config 并保存 */
  public void saveRegion(Region r) {
    plugin.getConfig().set("regions." + r.getName(), null);
    r.saveToConfig(plugin.getConfig().getConfigurationSection("regions"));
    plugin.saveConfig();
    regions.put(r.getName(), r);
  }

  /** 删除区域 */
  public boolean deleteRegion(String name) {
    if (regions.remove(name) != null) {
      plugin.getConfig().set("regions." + name, null);
      plugin.saveConfig();
      return true;
    }
    return false;
  }

  public Collection<Region> listRegions() {
    return regions.values();
  }

  /** 按优先级从高到低获取区域列表 */
  public List<Region> listRegionsByPriority() {
    List<Region> sorted = new ArrayList<>(regions.values());
    sorted.sort((a, b) -> Integer.compare(b.getPriority(), a.getPriority()));
    return sorted;
  }

  public Region get(String name) {
    return regions.get(name);
  }

  /** 更新区域到配置文件 */
  public void updateRegion(Region r) {
    plugin.getConfig().set("regions." + r.getName(), null);
    r.saveToConfig(plugin.getConfig().getConfigurationSection("regions"));
    plugin.saveConfig();
  }

  /** 对所有已存活实体静音或恢复 */
  public void applyToExisting() {
    // 收集所有涉及的世界
    Set<org.bukkit.World> worlds = new HashSet<>();
    for (Region r : regions.values()) {
      if (r.getCorner1().getWorld() != null) {
        worlds.add(r.getCorner1().getWorld());
      }
    }

    // 遍历每个世界的所有实体
    for (org.bukkit.World world : worlds) {
      for (Entity e : world.getEntities()) {
        boolean shouldBeSilent = false;

        // 按优先级从高到低检查实体是否在任何区域内且类型匹配
        for (Region r : listRegionsByPriority()) {
          if (r.contains(e.getLocation()) && r.getTypes().contains(e.getType())) {
            shouldBeSilent = true;
            break;
          }
        }

        // 设置实体静音状态
        e.setSilent(shouldBeSilent);
      }
    }
  }
}
