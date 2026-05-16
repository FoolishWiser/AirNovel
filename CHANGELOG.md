# AirNovel

## v1.1.5

### Features
- 设置页新增「版本更新」功能：通过 GitHub Releases API 检查最新版本
- 提供 3 个下载镜像选项：直连 / ghproxy.net / ghproxy.cn
- 点击下载按钮直接浏览器跳转到 APK 下载链接

## v1.1.4

### Performance
- 移除 BookshelfScreen 的 SwipeRefresh（Accompanist 已弃用，是首页卡顿根源），改用手动刷新按钮
- 设置页 Slider 改用局部 mutableFloatStateOf + onValueChangeFinished，拖拽时不触发全屏 StateFlow 重组
- 章节列表封面改用纯色替代渐变，减少 GPU 开销
- 阅读器进度条修复（新增 snapshotFlow + sample(100) 限频追踪 scrollState）
- 已读状态改用 Set 替代 Map，减少每次标记时全量拷贝

### Features
- 章节列表新增书籍简介展示（通过路由参数传递 book.description）
- NavGraph 导航参数扩展支持书籍描述传递

## v1.1.3

### Fixed
- 回退 ReaderScreen 到 v1.1.0 的 Column+verticalScroll+BasicText 方案，消除 LazyColumn 分段落渲染导致的全局卡顿
- 移除博 animateItem 修饰器，恢复 v1.1.0 的简洁渲染方式
- 保留 derivedStateOf 颜色缓存优化作为长久改进
- 保留 BookshelfScreen 外观流畅度改进（移除无效 pressed 状态变量）
- 保留 ChapterListScreen BookInfoHeader 书籍信息头部

## v1.1.2

### Performance
- 阅读器滚动进度改用 derivedStateOf 追踪，消除每次滚动更新 StateFlow 导致的**全屏重组**（核心卡顿根源）
- 书架网格使用 Modifier.animateItem() 替代 AnimatedVisibility，兼顾动画顺滑与滚动性能
- 章节列表使用 Modifier.animateItem() 替代 AnimatedVisibility，兼顾动画顺滑与滚动性能
- 阅读器改用 LazyColumn 分段落渲染，仅渲染可见段落
- 使用 derivedStateOf 缓存颜色/字体变量，减少无效重组

### UI Improvements
- 章节列表新增书籍信息头部（渐变封面缩略图 + 书名 + "章节列表"）
- TopAppBar 标题从硬编码"章节列表"改为显示实际书名
- 章节 item 使用 remember + key 稳定 readStatus 查询，减少重绘

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
