package com.example.slienceregion;

import java.util.*;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

public class Region {
  private final String name;
  private Location corner1, corner2;
  private final Set<EntityType> types;
  private int priority; // 优先级，数值越大优先级越高

  public Region(String name, Location c1, Location c2, Collection<EntityType> types) {
    this.name = name;
    this.corner1 = c1;
    this.corner2 = c2;
    this.types = new HashSet<>(types);
    this.priority = 0; // 默认优先级为 0
  }

  public String getName() {
    return name;
  }

  public Location getCorner1() {
    return corner1;
  }

  public Location getCorner2() {
    return corner2;
  }

  public Set<EntityType> getTypes() {
    return types;
  }

  public int getPriority() {
    return priority;
  }

  public void setPriority(int priority) {
    this.priority = priority;
  }

  /** 添加实体类型 */
  public void addTypes(Collection<EntityType> typesToAdd) {
    types.addAll(typesToAdd);
  }

  /** 移除实体类型 */
  public void removeTypes(Collection<EntityType> typesToRemove) {
    types.removeAll(typesToRemove);
  }

  /** 获取区域体积 */
  public long getVolume() {
    int width = Math.abs(corner2.getBlockX() - corner1.getBlockX()) + 1;
    int height = Math.abs(corner2.getBlockY() - corner1.getBlockY()) + 1;
    int length = Math.abs(corner2.getBlockZ() - corner1.getBlockZ()) + 1;
    return (long) width * height * length;
  }

  /** 判断 loc 是否在区域内 */
  public boolean contains(Location loc) {
    if (!loc.getWorld().equals(corner1.getWorld())) return false;
    double minX = Math.min(corner1.getX(), corner2.getX());
    double maxX = Math.max(corner1.getX(), corner2.getX());
    double minY = Math.min(corner1.getY(), corner2.getY());
    double maxY = Math.max(corner1.getY(), corner2.getY());
    double minZ = Math.min(corner1.getZ(), corner2.getZ());
    double maxZ = Math.max(corner1.getZ(), corner2.getZ());
    return loc.getX() >= minX
        && loc.getX() <= maxX
        && loc.getY() >= minY
        && loc.getY() <= maxY
        && loc.getZ() >= minZ
        && loc.getZ() <= maxZ;
  }

  /** 将本 Region 写入 config 节点 */
  public void saveToConfig(ConfigurationSection parent) {
    ConfigurationSection sec = parent.createSection(name);
    sec.set("world", corner1.getWorld().getName());
    sec.set("priority", priority);
    sec.set(
        "corner1", Arrays.asList(corner1.getBlockX(), corner1.getBlockY(), corner1.getBlockZ()));
    sec.set(
        "corner2", Arrays.asList(corner2.getBlockX(), corner2.getBlockY(), corner2.getBlockZ()));
    List<String> list = new ArrayList<>();
    for (EntityType et : types) list.add(et.name());
    sec.set("types", list);
  }

  /** 从 config 节点加载 Region */
  public static Region loadFromConfig(String name, ConfigurationSection parent) {
    if (!parent.isConfigurationSection(name)) return null;
    ConfigurationSection sec = parent.getConfigurationSection(name);
    String worldName = sec.getString("world", "world");
    org.bukkit.World world = org.bukkit.Bukkit.getWorld(worldName);
    if (world == null) return null;

    List<Integer> c1 = sec.getIntegerList("corner1");
    List<Integer> c2 = sec.getIntegerList("corner2");
    Location loc1 = new Location(world, c1.get(0), c1.get(1), c1.get(2));
    Location loc2 = new Location(world, c2.get(0), c2.get(1), c2.get(2));

    List<String> ts = sec.getStringList("types");
    Set<EntityType> types = new HashSet<>();
    if (ts.isEmpty()) {
      types.addAll(Arrays.asList(EntityType.values()));
    } else {
      for (String s : ts) {
        try {
          types.add(EntityType.valueOf(s.toUpperCase()));
        } catch (IllegalArgumentException ignore) {
        }
      }
    }
    Region r = new Region(name, loc1, loc2, types);
    r.setPriority(sec.getInt("priority", 0));
    return r;
  }
}
