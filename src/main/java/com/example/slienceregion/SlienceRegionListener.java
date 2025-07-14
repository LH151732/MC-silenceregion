package com.example.slienceregion;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class SlienceRegionListener implements Listener {
  private final RegionManager manager;

  public SlienceRegionListener(RegionManager m) {
    this.manager = m;
  }

  @EventHandler
  public void onCreatureSpawn(CreatureSpawnEvent ev) {
    // 按优先级从高到低检查区域
    for (Region r : manager.listRegionsByPriority()) {
      if (r.contains(ev.getLocation()) && r.getTypes().contains(ev.getEntityType())) {
        ev.getEntity().setSilent(true);
        return;
      }
    }
  }
}
