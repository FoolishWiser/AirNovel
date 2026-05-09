# AirNovel

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
