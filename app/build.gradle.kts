plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.giskesehatan"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.example.giskesehatan"
        minSdk = 24
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.8.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //okhttp dan network api intgrated
    implementation("com.squareup.okhttp3:okhttp:3.6.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.2.1")
    implementation("com.amitshekhar.android:android-networking:1.0.2")

    //scrolling pager adapter
    implementation("ru.tinkoff.scrollingpagerindicator:scrollingpagerindicator:1.2.5")

    //library maps
    implementation("com.google.android.gms:play-services-maps:17.0.1")
    implementation("com.google.android.gms:play-services-location:21.0.1")

    //retrofit library
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    //circle image view library
    implementation("de.hdodenhof:circleimageview:3.1.0")

    //viewpager2
    implementation("androidx.viewpager2:viewpager2:1.0.0")

    //RoundedImageView
    implementation("com.makeramen:roundedimageview:2.3.0")

    //glide
    implementation("com.github.bumptech.glide:glide:4.16.0")

    //circle image view
    implementation("de.hdodenhof:circleimageview:3.1.0")
}