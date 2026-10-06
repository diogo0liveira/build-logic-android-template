plugins {
    alias(libs.plugins.convention.android.feature)
}

android {

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
            excludes += "META-INF/LICENSE.md"
            excludes += "META-INF/LICENSE-notice.md"
        }
    }

    namespace = "com.dao.android.module.feature"
    testOptions.unitTests.isIncludeAndroidResources = true
}

dependencies {
    implementation(project(":module-core"))
    testImplementation(libs.test.robolectric)
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
