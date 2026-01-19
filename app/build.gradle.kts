plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.menuaplication"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "com.example.menuaplication"
        minSdk = 26
        targetSdk = 36
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
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

// Tarea para generar Javadoc (Corregida para Kotlin DSL)
tasks.register<Javadoc>("generarJavadoc") {
    // CORRECCIÓN: Usamos paréntesis () en lugar de =
    source(android.sourceSets.getByName("main").java.srcDirs)

    // Configuración del Classpath para encontrar las clases de Android
    classpath = files(android.bootClasspath)

    // Agregar las librerías externas del proyecto para que no falten referencias
    android.applicationVariants.all {
        if (name == "release") {
            classpath += javaCompileProvider.get().classpath
        }
    }

    // Configuración visual y de idioma
    options {
        this as StandardJavadocDocletOptions
        encoding = "UTF-8"
        docEncoding = "UTF-8"
        charSet = "UTF-8"
        links("https://docs.oracle.com/javase/8/docs/api/")
        links("https://d.android.com/reference/")
        addStringOption("Xdoclint:none", "-quiet")
    }

    isFailOnError = false
}


