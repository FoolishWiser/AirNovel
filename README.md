# AirNovel

> 一款优雅的 Android 原生小说阅读器，专为连接局域网内的小说创作服务端（AstrBot 插件 REST API）设计。

## 项目概述

**AirNovel** 是一款使用 Kotlin + Jetpack Compose 开发的 Android 原生应用，提供小说书架管理、章节阅读和更新提醒功能。应用通过 REST API 连接局域网内的 AstrBot 小说创作插件服务端。

## 功能特性

### 1. 连接与设置
- 首次启动进入设置页，输入服务端地址（如 `http://192.168.x.x:14514`）
- 内置测试连接功能，验证地址有效性
- 设置页面可随时修改服务端地址

### 2. 书架
- 网格卡片展示所有书目
- 自动生成纯色封面占位图（取书名首字）
- 显示书名、最近更新时间、最新章节标题
- 下拉刷新同步最新数据

### 3. 章节列表
- 按序号排序展示章节
- 显示章节标题、字数、更新时间
- 已读/未读状态本地标记

### 4. 阅读器
- 仿真实纸张阅读界面（柔和米色/夜间背景）
- 可调节字号、行距、字体（支持衬线/无衬线切换）
- 滚动阅读，无干扰全屏模式
- 显示当前章节标题和阅读进度百分比
- 左右滑动切换上一章/下一章

### 5. 更新提醒
- WorkManager 后台定时检查更新（默认每小时，可自定义 15分钟~4小时）
- 发现新章节时发送本地通知
- 通知渠道：小说更新（静默，无声音）
- 点击通知直接打开对应章节

### 6. 界面风格
- "书香中国"设计风格（宣纸白、墨灰、淡棕配色）
- Material 3 组件，卡片、圆角、阴影提升质感
- 日间/夜间模式手动切换或跟随系统

## 技术架构

- **语言**: Kotlin
- **UI**: Jetpack Compose + Material 3
- **架构**: MVVM（Repository - ViewModel - UI）
- **网络**: Retrofit + OkHttp + Gson
- **后台**: WorkManager
- **导航**: Navigation Compose
- **最低 SDK**: Android 8.0 (API 26)
- **目标 SDK**: Android 14 (API 34)

## API 接口

应用依赖以下 REST API 接口：

| 接口 | 方法 | 说明 |
|------|------|------|
| `/api/books` | GET | 获取所有书目列表 |
| `/api/books/{book_id}/chapters` | GET | 获取指定书的章节列表 |
| `/api/books/{book_id}/chapters/{chapter_id}` | GET | 获取章节内容 |
| `/api/latest` | GET | 获取最新更新列表 |

## 项目结构

```
app/src/main/java/com/airnovel/app/
├── AirNovelApp.kt              # Application 类
├── MainActivity.kt             # 主 Activity
├── data/
│   ├── api/
│   │   ├── ApiService.kt       # Retrofit 接口定义
│   │   └── RetrofitClient.kt   # Retrofit 客户端
│   ├── local/
│   │   ├── ChapterCache.kt     # 章节 ID 缓存
│   │   ├── PreferencesManager.kt  # SharedPreferences 管理
│   │   └── ReadStatusManager.kt   # 阅读状态管理
│   ├── model/
│   │   ├── Book.kt             # 书籍数据模型
│   │   ├── Chapter.kt          # 章节数据模型
│   │   └── LatestUpdate.kt     # 更新数据模型
│   └── repository/
│       └── NovelRepository.kt  # 数据仓库
├── notification/
│   └── NotificationHelper.kt   # 通知工具类
├── ui/
│   ├── bookshelf/
│   │   ├── BookshelfScreen.kt  # 书架页面
│   │   └── BookshelfViewModel.kt
│   ├── chapters/
│   │   ├── ChapterListScreen.kt    # 章节列表页面
│   │   └── ChapterListViewModel.kt
│   ├── navigation/
│   │   └── NavGraph.kt         # 导航图
│   ├── reader/
│   │   ├── ReaderScreen.kt     # 阅读器页面
│   │   └── ReaderViewModel.kt
│   ├── settings/
│   │   ├── SettingsScreen.kt   # 设置页面
│   │   └── SettingsViewModel.kt
│   └── theme/
│       ├── Color.kt            # 主题色
│       ├── Theme.kt            # 主题配置
│       └── Type.kt             # 排版
└── worker/
    └── UpdateCheckWorker.kt    # 后台更新检查
```

## 构建与运行

1. 使用 Android Studio（Hedgehog 2023.1.1 或更高版本）打开项目
2. 等待 Gradle 同步完成
3. 连接 Android 设备或启动模拟器
4. 点击 Run 运行

### 构建要求

- Android Studio Hedgehog 或更新版本
- JDK 17
- Gradle 8.5
- Android SDK 34

## 配置说明

- 应用支持明文 HTTP 访问（`android:usesCleartextTraffic="true"`），可直接连接局域网 HTTP 服务
- 通知需在 Android 13+ 上手动授予权限
- 后台检查间隔可在设置页面调整（15分钟~4小时）

## 版本历史

- **v1.1.3** — 回退 ReaderScreen 为 Column+verticalScroll+BasicText 方案修复卡顿
- **v1.1.2** — 深度性能重构：PullToRefreshBox 替换 SwipeRefresh、阅读器 LazyColumn 分段落渲染、章节列表书籍信息头部、derivedStateOf 减少重组
- **v1.1.1** — 性能优化（移除 AnimatedVisibility 卡顿、优化滚动监听）、修复章节排序错误、修复 RetrofitClient 初始化缺陷、修复通知 requestCode 冲突
- **v1.1.0** — UI 全面现代化升级、Material 3 新配色、底部设置面板、骨架屏加载、动画流畅度提升
- **v1.0.0** — 初始发布

本项目开发过程中使用了 DeepSeek V4 Flash 进行辅助编程。 Powered by DeepSeek V4 Flash.
