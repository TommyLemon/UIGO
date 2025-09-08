plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "uigo.x"
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

    packaging {
        resources {
            excludes.add("**/application.properties")
        }
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.androidasync)
    implementation(libs.fastjson)
    implementation(libs.androidasync)
    implementation(libs.unitauto)
    implementation(libs.apijson)
    implementation(libs.okhttp)
    implementation(libs.jsoup)
    implementation(libs.floatwindow)
    api(project(":UnitAuto-Apk"))
}