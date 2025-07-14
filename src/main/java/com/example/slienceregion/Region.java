package com.example.silenceregion;

import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;
import java.util.*;

public class Region {
  private final String name;
  private Location corner1, corner2;
  private final Set<EntityType> types; 

  public Region(String name, Location c1, Location c2, Collection<EntityType> types) {
    this.name = name;
    this.corner1 = c1;
    this.corner2 = c2;
    this.types = new HashSet<>(types);
  }

  public String getName() { return name; }
  public Location getCorner1() { return corner1; }
  public Location getCorner2() { return corner2; }
  public Set<EntityType> getTypes() { return types; }

  /** 判断 loc 是否在区域内 */
  public boolean contains(Location loc) {
    if (!loc.getWorld().equals(corner1.getWorld())) return false;
    double minX = Math.min(corner1.getX(), corner2.getX());
    double maxX = Math.max(corner1.getX(), corner2.getX());
    double minY = Math.min(corner1.getY(), corner2.getY());
    double maxY = Math.max(corner1.getY(), corner2.getY());
    double minZ = Math.min(corner1.getZ(), corner2.getZ());
    double maxZ = Math.max(corner1.getZ(), corner2.getZ());
    return loc.getX() >= minX && loc.getX() <= maxX
        && loc.getY() >= minY && loc.getY() <= maxY
        && loc.getZ() >= minZ && loc.getZ() <= maxZ;
  }

  /** 将本 Region 写入 config 节点 */
  public void saveToConfig(ConfigurationSection parent) {
    ConfigurationSection sec = parent.createSection(name);
    sec.set("corner1", Arrays.asList(
      corner1.getBlockX(), corner1.getBlockY(), corner1.getBlockZ()));
    sec.set("corner2", Arrays.asList(
      corner2.getBlockX(), corner2.getBlockY(), corner2.getBlockZ()));
    List<String> list = new ArrayList<>();
    for (EntityType et : types) list.add(et.name());
    sec.set("types", list);
  }

  /** 从 config 节点加载 Region */
  public static Region loadFromConfig(String name, ConfigurationSection parent) {
    if (!parent.isConfigurationSection(name)) return null;
    ConfigurationSection sec = parent.getConfigurationSection(name);
    List<Integer> c1 = sec.getIntegerList("corner1");
    List<Integer> c2 = sec.getIntegerList("corner2");
    Location loc1 = new Location(
      parent.getRoot().getWorld(), c1.get(0), c1.get(1), c1.get(2));
    Location loc2 = new Location(
      parent.getRoot().getWorld(), c2.get(0), c2.get(1), c2.get(2));
    List<String> ts = sec.getStringList("types");
    Set<EntityType> types = new HashSet<>();
    if (ts.isEmpty()) {
      types.addAll(Arrays.asList(EntityType.values()));
    } else {
      for (String s : ts) {
        try { types.add(EntityType.valueOf(s.toUpperCase())); }
        catch (IllegalArgumentException ignore) { }
      }
    }
    return new Region(name, loc1, loc2, types);
  }
}
