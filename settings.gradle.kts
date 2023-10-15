pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        mavenCentral()
        jcenter()
//        maven { url 'https://jitpack.io' }
    }
}

rootProject.name = "Gis Kesehatan"
include(":app")
