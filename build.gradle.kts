// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.1.2" apply false
}

// 定义所有子项目/模块的仓库
allprojects {
    repositories {
        // 添加阿里云Maven镜像，解决下载问题
        maven { url = uri("https://maven.aliyun.com/repository/public/") }
        maven { url = uri("https://maven.aliyun.com/repository/google/") }
        maven { url = uri("https://maven.aliyun.com/repository/gradle-plugin/") }
        // 原始仓库
        google()
        mavenCentral()
    }
}