plugins {
    alias(libs.plugins.convention.android.library)
    alias(libs.plugins.convention.hilt)
}

android {
    namespace = "com.dao.android.module.core"
}

dependencies {
    implementation(project(":module-jvm"))
    testImplementation(testFixtures(project(":module-jvm")))
}
