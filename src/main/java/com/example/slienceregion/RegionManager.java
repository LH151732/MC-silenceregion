package com.example.silenceregion;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.Location;
import java.util.*;

public class RegionManager {
  private final SilenceRegionPlugin plugin;
  private final Map<String, Region> regions = new HashMap<>();

  public RegionManager(SilenceRegionPlugin plugin) {
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

  public Collection<Region> listRegions() { return regions.values(); }
  public Region get(String name) { return regions.get(name); }

  /** 对所有已存活实体静音或恢复 */
  public void applyToExisting() {
    for (Region r : regions.values()) {
      for (Entity e : r.getCorner1().getWorld().getEntities()) {
        if (r.contains(e.getLocation())
            && r.getTypes().contains(e.getType())) {
          e.setSilent(true);
        } else if (r.contains(e.getLocation())) {
          // 在区域但类型不匹配，或已被移除
          e.setSilent(false);
        }
      }
    }
  }
}
