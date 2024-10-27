plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.jetbrains.kotlin.android)
    id ("org.jetbrains.kotlin.kapt")
}

android {
    namespace = "com.example.weatherforecast"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.weatherforecast"
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Retrofit
    implementation(libs.retrofit)
    implementation(libs.gsonConverter)

    // OkHttp
    implementation(libs.okhttp)
    implementation(libs.loggingInterceptor)

    // Lifecycle
    implementation(libs.lifecycleRuntime)
    implementation(libs.lifecycleViewModel)
    implementation(libs.lifecycleLiveData)

    // Gson
    implementation(libs.gson)

    // Glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    implementation("androidx.activity:activity-ktx:1.9.3")

    ///implementation ("com.github.MatteoBattilana:WeatherView:3.0.0")
}
kapt{
    correctErrorTypes = true
}
