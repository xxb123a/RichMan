plugins {
    alias(libs.plugins.android.application)
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.xb.selfrichapp"
    compileSdk {
        version = release(36)
    }

    signingConfigs {
        register("key") {
            storeFile = file("../demo.jks")
            storePassword = "as123456"
            keyAlias = "key0"
            keyPassword = "as123456"
        }
    }

    defaultConfig {
        applicationId = "com.xb.selfrichapp"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("key")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.7.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    //db
    implementation(libs.room.runtime)
    ksp(libs.room.compiler)


    implementation("com.squareup.okhttp3:okhttp:3.14.9")
    implementation("io.reactivex.rxjava3:rxjava:3.1.4")
    implementation("io.reactivex.rxjava3:rxandroid:3.0.0")
}