plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.intelligent_dailer"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.intelligent_dailer"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    
    // 禁用lint检查，加快构建速度
/*    lint {
        disable += "MissingTranslation"
    }*/
    
    // 配置aaptOptions以跳过某些资源压缩
    androidResources {
        noCompress += listOf("jpg", "png")
    }
}

dependencies {
    // 基础依赖
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.1")
    
    // RecyclerView用于显示联系人列表
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("androidx.cardview:cardview:1.0.0")
    
    // Room数据库，用于存储通话记录和常用联系人
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("androidx.room:room-runtime-android:2.7.1")
    annotationProcessor("androidx.room:room-compiler:2.6.1")
    
    // 语音识别
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("com.google.android.gms:play-services-location:21.1.0")
    
    // 二维码扫描
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    
    // LiveData和ViewModel
    implementation("androidx.lifecycle:lifecycle-viewmodel:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata:2.7.0")
    
    // 测试依赖
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}