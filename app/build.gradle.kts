plugins {
    alias(libs.plugins.convention.android.application)
    alias(libs.plugins.convention.android.compose)
    alias(libs.plugins.convention.hilt)
}

android {

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
        }
    }

    namespace = "com.dao.android.template"

    defaultConfig {
        applicationId = "com.dao.android.template"
        versionName = "0.1.0"
        versionCode = 1
    }

    buildTypes {
        release {
            optimization {
                enable = true
            }
        }
    }
}

dependencies {
    implementation(project(":module-core"))
    implementation(project(":module-feature"))
    implementation(libs.android.appcompat)
    implementation(libs.android.core)
    implementation(libs.android.material)
    implementation(libs.compose.activity)
    implementation(libs.compose.lifecycle.runtime)
    implementation(libs.compose.lifecycle.viewmodel)

    testImplementation(libs.test.junit)
    testImplementation(libs.test.kotlin)
    testImplementation(testFixtures(project(":module-jvm")))

    androidTestImplementation(libs.test.android.core)
    androidTestImplementation(libs.test.android.junit)
    androidTestImplementation(libs.test.android.rules)
    androidTestImplementation(libs.test.android.runner)
    androidTestImplementation(libs.test.espresso.core)
}

dependencies {
    testImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    testImplementation("org.junit.jupiter:junit-jupiter-params:5.10.2")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    testRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.10.2")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.2")

    androidTestImplementation("org.junit.jupiter:junit-jupiter-api:5.10.2")
    androidTestImplementation("org.junit.jupiter:junit-jupiter-params:5.10.2")
    androidTestRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:5.10.2")
    androidTestRuntimeOnly("org.junit.vintage:junit-vintage-engine:5.10.2")
    androidTestRuntimeOnly("org.junit.platform:junit-platform-launcher:1.10.2")
}
