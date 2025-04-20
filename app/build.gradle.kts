plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.findlostitemsapp"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.findlostitemsapp"
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.database)
    implementation(libs.glide.v4151)
    implementation(platform(libs.firebase.bom))
    implementation(libs.glide)
    implementation(libs.firebase.auth)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation ("com.google.firebase:firebase-storage:20.1.0")
    implementation ("com.squareup.okhttp3:okhttp:4.10.0")

    implementation ("com.github.bumptech.glide:glide:4.12.0") // Glide version, bạn có thể thay đổi nếu cần
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")
}