package com.example.slienceregion;

import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;

public class SlienceregionPlugin extends JavaPlugin {
  private RegionManager manager;
  private BukkitTask checkTask;

  @Override
  public void onEnable() {
    saveDefaultConfig();
    manager = new RegionManager(this);
    manager.loadAll();

    // 注册命令
    SlienceRegionCommand commandExecutor = new SlienceRegionCommand(this, manager);
    getCommand("sr").setExecutor(commandExecutor);
    getCommand("sr").setTabCompleter(commandExecutor);

    // 注册事件
    getServer().getPluginManager().registerEvents(new SlienceRegionListener(manager), this);

    // 启动时对已有实体静音
    manager.applyToExisting();

    // 启动定时任务
    startPeriodicCheck();

    getLogger().info("SilenceRegion 启动完成，共加载区域: " + manager.listRegions().size());
  }

  @Override
  public void onDisable() {
    // 停止定时任务
    if (checkTask != null) {
      checkTask.cancel();
    }
  }

  /** 启动定时检查任务 */
  private void startPeriodicCheck() {
    if (checkTask != null) {
      checkTask.cancel();
    }

    if (!getConfig().getBoolean("enable-periodic-check", true)) {
      return;
    }

    long interval = getConfig().getInt("check-interval", 30) * 20L; // 转换为 ticks

    checkTask =
        getServer()
            .getScheduler()
            .runTaskTimer(
                this,
                () -> {
                  manager.applyToExisting();
                },
                interval,
                interval);

    getLogger().info("定时检查任务已启动，间隔: " + (interval / 20) + " 秒");
  }

  /** 重新加载配置和定时任务 */
  public void reload() {
    reloadConfig();
    manager.loadAll();
    manager.applyToExisting();
    startPeriodicCheck(); // 重新启动定时任务
  }

  public RegionManager getManager() {
    return manager;
  }
}
