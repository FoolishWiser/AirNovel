# AirNovel

## v1.1.1

### Bug Fixes
- 修复 `UpdateCheckWorker` 中章节 ID 字符串比较错误（"10" < "2" 问题）
- 修复 `ReadStatusManager` 中 getLatestReadChapterIds 字符串比较导致的章节排序错误
- 修复 `RetrofitClient.initialize` 重复调用时 apiService 未更新的逻辑缺陷
- 移除 `SettingsViewModel` 中多余的 Dispatchers.IO 嵌套包装
- 移除 `BookshelfScreen` 中无效的 pressed 状态变量
- 修复 `NotificationHelper` 中 requestCode 使用时间戳可能导致冲突的问题

### Performance
- 移除 BookshelfScreen 中 AnimatedVisibility 导致的列表滚动卡顿
- 移除 ChapterListScreen 中 AnimatedVisibility 导致的章节列表滚动卡顿
- 优化 ReaderScreen 滚动监听，使用 snapshotFlow + 阈值过滤减少重组

## v1.1.0

- 全面 UI 现代化升级：Material 3 新配色、卡片圆角阴影优化、骨架屏加载
- 封面改渐变背景，更有质感
- 章节列表动画入场 + 圆角编号样式
- 阅读器新增底部设置面板（ModalBottomSheet），沉浸式全屏体验
- 设置页卡片分区 + 图标引导，布局更清晰
- 优化空状态 / 错误状态视觉表达
- 深层适配真实 AstrBot API 数据结构

## v1.0.0

- 首次发布，支持 AstrBot 小说创作插件 REST API 连接
- 书架页：网格展示书目，自动生成封面占位图，下拉刷新
- 章节列表页：显示章节标题/编号/更新时间，已读标记
- 阅读器：仿纸张背景，夜间模式，字号/行距/衬线字体可调，滑动翻页
- 更新提醒：WorkManager 定时检查 `/api/latest`，静默通知直达阅读器
- 设置页：服务端地址配置与连接测试，主题切换，阅读偏好
- 技术栈：Kotlin + Jetpack Compose + Material 3 + MVVM + Retrofit + WorkManager
