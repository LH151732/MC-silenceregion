# SilenceRegion 插件 TODO List

## 🧪 检查清单 (Check List)

- [ ] 启动时正确加载 `config.yml`
- [ ] `plugin.yml` 中命令节点（/sr）配置无误
- [ ] `RegionManager.loadAll()` 能遍历并生成所有区域
- [ ] `applyToExisting()`：启动时／reload 后已存在实体静音／恢复正常
- [ ] `CreatureSpawnEvent` 监听器：新生成实体正确静音／放行
- [ ] `/sr create … wand …`：WorldEdit 选区读取、坐标转换无误
- [ ] `/sr delete`：删除后区域内实体恢复正常叫声
- [ ] `/sr list`、`/sr info`：输出格式清晰、参数校验完备
- [ ] `/sr reload`：配置重载后生效
- [ ] 多世界场景：不同世界的实体不会交叉静音
- [ ] 并发安全：命令执行与定时或事件处理不会冲突
- [ ] 代码风格／格式化（Google Java Format）

## 🚀 待开发功能 (To Be Developed)

- [ ] `/sr types <region> add <type,…>`
- [ ] `/sr types <region> remove <type,…>`
- [ ] 支持空 `types` 列表时「所有 Animals」静音
- [ ] 给每个命令添加权限节点 (`silenceregion.*` / `silenceregion.create` …)
- [ ] Tab 补全命令与区域名、实体类型
- [ ] 在 `/sr info` 中显示所属世界、区域体积、类型列表
- [ ] 支持按区域优先级（重叠区域静音策略）
- [ ] 配置文件中保存世界名 (`world-name: <字符串>`)
- [ ] 增加定时任务可调参数（如扫描间隔）

## 🔧 待完善功能 (Improvements)

- [ ] 消息本地化／自定义（messages.yml）
- [ ] GUI 面板管理区域（Chest GUI 或 BossBar）
- [ ] 支持数据库持久化（MySQL / SQLite）
- [ ] 性能优化
  - 区域扫描时过滤实体数量
  - 大型世界下批量操作分帧执行
- [ ] 日志分级（INFO / DEBUG / ERROR）
- [ ] 单元测试覆盖核心逻辑（Region.contains / Config 加载等）
- [ ] README & 使用文档补充示例、截图、常见问题
- [ ] 自动化发布脚本（CI/CD，自动打包、上传 GitHub Releases）

## 📦 发布前清单 (Release Checklist)

- [ ] 更新 `pom.xml` 版本号 (`<version>`)
- [ ] 确认 `api-version` 与目标 Paper 版本匹配
- [ ] 删除无用示例代码与测试文件
- [ ] 运行 `mvn clean package -DskipTests`，生成最终 Jar
- [ ] 在 Docker 环境／本地服务器测试一轮
- [ ] 在 GitHub/Gitee 打 tag，并发布 Release

> ⚠️ 建议把以上 TODO 拆分到项目看板（如 GitHub Issues / Projects），并安排优先级与里程碑。
