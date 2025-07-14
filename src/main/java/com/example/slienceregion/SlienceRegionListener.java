package com.example.silenceregion;

import org.bukkit.event.Listener;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.CreatureSpawnEvent;

public class SilenceRegionListener implements Listener {
  private final RegionManager manager;

  public SilenceRegionListener(RegionManager m) {
    this.manager = m;
  }

  @EventHandler
  public void onCreatureSpawn(CreatureSpawnEvent ev) {
    for (Region r : manager.listRegions()) {
      if (r.contains(ev.getLocation())
          && r.getTypes().contains(ev.getEntityType())) {
        ev.getEntity().setSilent(true);
        return;
      }
    }
  }
}
