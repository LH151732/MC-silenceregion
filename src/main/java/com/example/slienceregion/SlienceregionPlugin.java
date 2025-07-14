package com.example.silenceregion;

import org.bukkit.plugin.java.JavaPlugin;

public class SilenceRegionPlugin extends JavaPlugin {
  private RegionManager manager;

  @Override
  public void onEnable() {
    saveDefaultConfig();
    manager = new RegionManager(this);
    manager.loadAll();
    // 注册命令
    getCommand("sr").setExecutor(new SilenceRegionCommand(this, manager));
    // 注册事件
    getServer().getPluginManager().registerEvents(
      new SilenceRegionListener(manager), this);
    // 启动时对已有实体静音
    manager.applyToExisting();
    getLogger().info("SilenceRegion 启动完成，共加载区域: "
                     + manager.listRegions().size());
  }

  @Override
  public void onDisable() { }
}
