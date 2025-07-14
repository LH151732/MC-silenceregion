# SilenceRegion 插件 TODO List

## 🧪 检查清单 (Check List)

- [x] 启动时正确加载 `config.yml` ✅ (修复了包名问题)
- [x] `plugin.yml` 中命令节点（/sr）配置无误 ✅ (修复了主类路径和添加了 WorldEdit 依赖)
- [x] `RegionManager.loadAll()` 能遍历并生成所有区域 ✅
- [x] `applyToExisting()`：启动时／reload 后已存在实体静音／恢复正常 ✅ (修复了多世界支持)
- [x] `CreatureSpawnEvent` 监听器：新生成实体正确静音／放行 ✅
- [x] `/sr create … wand …`：WorldEdit 选区读取、坐标转换无误 ✅
- [x] `/sr delete`：删除后区域内实体恢复正常叫声 ✅ (添加了 applyToExisting 调用)
- [x] `/sr list`、`/sr info`：输出格式清晰、参数校验完备 ✅
- [x] `/sr reload`：配置重载后生效 ✅
- [x] 多世界场景：不同世界的实体不会交叉静音 ✅ (修复了世界名保存和加载)
- [ ] 并发安全：命令执行与定时或事件处理不会冲突
- [ ] 代码风格／格式化（Google Java Format）

### 🔧 已修复的问题：
1. **包名不一致**：统一为 `com.example.slienceregion`
2. **plugin.yml 主类路径**：修正为 `com.example.slienceregion.SlienceregionPlugin`
3. **添加 WorldEdit 依赖声明**：在 plugin.yml 中添加 `softdepend: [WorldEdit]`
4. **Region 世界支持**：
   - 在 saveToConfig 中保存世界名
   - 修复 loadFromConfig 中的世界获取逻辑
5. **applyToExisting 多世界支持**：重写为遍历所有相关世界
6. **删除区域后恢复声音**：在 delete 命令中添加 applyToExisting 调用

## 🚀 待开发功能 (To Be Developed)

- [x] `/sr types <region> add <type,…>` ✅
- [x] `/sr types <region> remove <type,…>` ✅
- [x] 支持空 `types` 列表时「所有 Animals」静音 ✅ (支持 ANIMALS 特殊类型)
- [x] 给每个命令添加权限节点 (`silenceregion.*` / `silenceregion.create` …) ✅
- [x] Tab 补全命令与区域名、实体类型 ✅
- [x] 在 `/sr info` 中显示所属世界、区域体积、类型列表 ✅
- [x] 支持按区域优先级（重叠区域静音策略）✅
- [x] 配置文件中保存世界名 (`world-name: <字符串>`) ✅ (已在检查清单中完成)
- [x] 增加定时任务可调参数（如扫描间隔）✅

## 🔧 待完善功能 (Improvements)

- [ ] 消息本地化／自定义（messages.yml）
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
