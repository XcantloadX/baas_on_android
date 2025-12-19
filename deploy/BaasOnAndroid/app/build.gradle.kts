plugins {
    alias(libs.plugins.android.application)
    id("com.chaquo.python")
}

android {
    namespace = "baas.android"
    compileSdk = 36

    defaultConfig {
        applicationId = "baas.android"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        ndk {
            // 同时支持手机与模拟器
            abiFilters += listOf("arm64-v8a", "x86_64")
        }
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

chaquopy {
    defaultConfig {
        buildPython("/Users/zxk/.pyenv/versions/3.10.17/bin/python")
        version = "3.10"
        pip {
            install("opencv-python")
            install("git+https://github.com/openatx/adbutils.git@2.12.0")
            install("uiautomator2==2.16.23")
            install("git+https://github.com/XcantloadX/pyadbserver.git")
        }
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}

// 把仓库根的 Python 源码同步到 app/src/main/python
val syncPythonSources by tasks.registering(Sync::class) {
    // app module 目录：deploy/android/app
    val repoRoot = projectDir.resolve("../../..").normalize()

    val outDir = projectDir.resolve("src/main/python")

    // 输出目录（Sync 会让 outDir 内容与输入保持一致）
    into(outDir)

    // 需要的源码目录：按需修改
    from(repoRoot.resolve("core"))   { into("core") }
    from(repoRoot.resolve("gui"))   { into("gui") }
    from(repoRoot.resolve("module")) { into("module") }
    from(repoRoot.resolve("src/atx_app")) { into("atx_app") }
    from(repoRoot.resolve("main.py"))
    from(repoRoot.resolve("window.py"))

    // 可选：排除无关文件
    exclude("**/__pycache__/**", "**/*.pyc", "**/.DS_Store")
}

afterEvaluate {
    val targets = tasks.matching { it.name == "mergeDebugPythonSources" }
    targets.configureEach { dependsOn(syncPythonSources) }
}


