plugins {
    alias(libs.plugins.convention.android.feature)
}

android {
    namespace = "com.dao.android.module.feature"
    testOptions.unitTests.isIncludeAndroidResources = true
}

dependencies {
    implementation(project(":module-core"))
    testImplementation(libs.test.robolectric)
}
