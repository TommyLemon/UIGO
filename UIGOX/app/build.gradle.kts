plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "uigox.demo"
    compileSdk = 36

    defaultConfig {
        applicationId = "uigox.demo"
        minSdk = 29
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
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
    buildFeatures {
        viewBinding = true
    }
    kotlinOptions {
        jvmTarget = "11"
    }

    packaging {
        resources {
            excludes.add("**/application.properties")
        }
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.core.ktx)
    implementation(libs.fastjson)
    implementation(libs.androidasync)
    implementation(libs.unitauto)
    implementation(libs.apijson)
    implementation(libs.okhttp)
    implementation(libs.glide)
    implementation(libs.zxinglite)
    implementation(libs.refreshlayoutkernel)
    implementation(libs.refreshheaderclassics)
    implementation(libs.refreshfooterclassics)
//    implementation(project(":UnitAuto-Apk"))
//    implementation(project(":UIGOX"))
    api(project(":ZBLibrary")) {
        exclude(group = "com.android.support")
    }

}