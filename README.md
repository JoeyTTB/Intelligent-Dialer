# 智能拨号器 (IntelligentDialer)

[![Android](https://img.shields.io/badge/Platform-Android-green.svg)](https://www.android.com/)
[![API](https://img.shields.io/badge/API-24%2B-brightgreen.svg)](https://android-arsenal.com/api?level=24)
[![Version](https://img.shields.io/badge/Version-2.1-blue.svg)](https://gitee.com/joeycliff/intelligent-dialer)

一款功能丰富、智能化的 Android 拨号应用，支持传统拨号、语音拨号、扫码拨号等多种拨号方式。

## ✨ 功能特性

### 核心功能
- 📱 **标准拨号** - 传统数字键盘拨号界面，简洁易用
- 👥 **联系人管理** - 完整的联系人查看、搜索和管理功能
- ⭐ **收藏功能** - 快速收藏常用联系人，一键拨打
- 📞 **通话记录** - 查看最近的来电、去电和未接来电记录
- 🎨 **个性化设置** - 支持深色主题、字体大小、按钮大小等自定义

### 智能拨号
- 🎤 **语音拨号** - 通过语音识别技术，说出号码即可拨打
- 📷 **扫码拨号** - 扫描二维码或条形码中的电话号码快速拨打

### 数据管理
- 💾 **本地存储** - 使用 Room 数据库存储通话记录和收藏联系人
- 🔄 **数据同步** - 自动同步系统联系人和通话记录

## 🛠 技术栈

### 开发环境
- **语言**: Java
- **最低 SDK**: API 24 (Android 7.0)
- **目标 SDK**: API 34 (Android 14)
- **编译 SDK**: API 34

### 核心依赖
- **UI 框架**
  - Material Design Components (1.12.0)
  - ConstraintLayout (2.2.1)
  - RecyclerView (1.3.2)
  - CardView (1.0.0)

- **数据库**
  - Room (2.6.1) - 本地数据持久化

- **功能库**
  - ZXing Android Embedded (4.3.0) - 二维码扫描
  - Google Play Services - 语音识别和定位服务

- **架构组件**
  - LiveData & ViewModel (2.7.0) - MVVM 架构支持

## 📋 系统要求

- Android 7.0 (API 24) 或更高版本
- 推荐使用 Android 10 或更高版本以获得最佳体验

### 硬件要求（可选）
- 电话功能（拨打电话）
- 相机（扫码拨号功能）
- 麦克风（语音拨号功能）

## 🚀 安装与构建

### 克隆项目
```bash
git clone https://github.com/JoeyTTB/Intelligent-Dialer.git
cd IntelligentDialer-v2.1
```

### 使用 Android Studio
1. 打开 Android Studio
2. 选择 `File` -> `Open`
3. 选择项目目录
4. 等待 Gradle 同步完成
5. 连接 Android 设备或启动模拟器
6. 点击 `Run` 按钮或使用快捷键 `Shift + F10`

### 使用命令行构建
```bash
# Windows
gradlew.bat assembleDebug

# Linux/Mac
./gradlew assembleDebug
```

构建完成后，APK 文件位于 `app/build/outputs/apk/debug/` 目录下。

## 🔐 权限说明

应用需要以下权限才能正常运行：

| 权限 | 用途 | 必需性 |
|------|------|--------|
| `CALL_PHONE` | 拨打电话 | 必需 |
| `READ_PHONE_STATE` | 读取电话状态 | 必需 |
| `READ_CONTACTS` | 读取联系人信息 | 必需 |
| `WRITE_CONTACTS` | 修改联系人信息 | 可选 |
| `READ_CALL_LOG` | 读取通话记录 | 必需 |
| `WRITE_CALL_LOG` | 写入通话记录 | 可选 |
| `RECORD_AUDIO` | 语音拨号功能 | 可选 |
| `CAMERA` | 扫码拨号功能 | 可选 |

> 💡 应用遵循最小权限原则，可选权限不影响核心拨号功能的使用。

## 📁 项目结构

```
IntelligentDialer-v2.1/
├── app/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/com/example/intelligent_dailer/
│   │   │   │   ├── adapters/          # RecyclerView 适配器
│   │   │   │   ├── database/          # Room 数据库相关
│   │   │   │   ├── models/            # 数据模型
│   │   │   │   ├── ui/                # Activity 和 Fragment
│   │   │   │   └── MainActivity.java  # 主活动
│   │   │   ├── res/                   # 资源文件
│   │   │   │   ├── drawable/          # 图标和背景
│   │   │   │   ├── layout/            # 布局文件
│   │   │   │   ├── menu/              # 菜单配置
│   │   │   │   └── values/            # 字符串、颜色、主题
│   │   │   └── AndroidManifest.xml    # 应用清单
│   │   ├── androidTest/               # 仪器测试
│   │   └── test/                      # 单元测试
│   └── build.gradle.kts               # 应用级构建配置
├── gradle/                            # Gradle 包装器
├── build.gradle.kts                   # 项目级构建配置
├── settings.gradle.kts                # 项目设置
└── README.md                          # 项目说明文档
```

## 💡 使用说明

### 基础拨号
1. 打开应用，默认显示拨号键盘
2. 输入电话号码
3. 点击拨号按钮发起通话

### 语音拨号
1. 点击拨号界面的"语音拨号"按钮
2. 授予麦克风权限（首次使用）
3. 清晰地说出要拨打的电话号码
4. 确认识别结果后拨打

### 扫码拨号
1. 点击拨号界面的"扫码拨号"按钮
2. 授予相机权限（首次使用）
3. 将二维码或条形码对准扫描框
4. 自动识别并拨打号码

### 收藏管理
1. 在联系人列表中点击联系人
2. 点击"收藏"按钮将其添加至收藏
3. 在"收藏"标签页快速访问常用联系人

## 🔧 开发说明

### 架构模式
- 采用 MVVM（Model-View-ViewModel）架构
- 使用 LiveData 实现数据驱动的 UI 更新
- Repository 模式管理数据源

### 数据库设计
- **CallRecord** - 通话记录表
- **FavoriteContact** - 收藏联系人表
- **UserSettings** - 用户设置表

### 代码规范
- 遵循 Android 开发最佳实践
- 使用 Material Design 设计规范
- 注重代码可读性和可维护性

## 🤝 贡献指南

欢迎提交问题和贡献代码！

1. Fork 本仓库
2. 创建新的功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 📄 许可证

本项目仅供学习和研究使用。

## 📧 联系方式

- 项目地址: [https://github.com/JoeyTTB/Intelligent-Dialer](https://github.com/JoeyTTB/Intelligent-Dialer)
- 问题反馈: 请提交 Issue

---

⭐ 如果这个项目对您有帮助，欢迎给个 Star！
