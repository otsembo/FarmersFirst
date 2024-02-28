// Top-level build file where you can add configuration options common to all sub-projects/modules.
@Suppress("DSL_SCOPE_VIOLATION") // TODO: Remove once KTIJ-19369 is fixed
plugins {
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.kotlinAndroid) apply false
    id("org.jetbrains.dokka") version "1.9.10"
}
buildscript {
    dependencies {
        classpath(libs.secrets.gradle.plugin)
    }
}
tasks.dokkaHtmlMultiModule {
    outputDirectory.set(layout.projectDirectory.dir("docs/html"))
}
true // Needed to make the Suppress annotation work for the plugins block