plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "zuo.biao.library"
    compileSdk = 36

    defaultConfig {
        minSdk = 29

        consumerProguardFiles("consumer-rules.pro")
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

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.fastjson)
    implementation(libs.unitauto)
    implementation(libs.apijson)
    implementation(libs.okhttp)
    implementation(libs.glide)
    implementation(libs.refreshlayoutkernel)
    implementation(libs.refreshheaderclassics)
    implementation(libs.refreshfooterclassics)
    api(project(":UIGOX"))
    api(project(":QRCodeLibrary"))
}