pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
        maven("https://jitpack.io")
        // Option 1: Reference GitHub Gate Repos Directly
        maven { url = uri("https://raw.githubusercontent.com/gate/gatepay-sdk-android/main/repos") }
        // Option 2: Use Local Repos
        maven { url = uri("${rootProject.projectDir}/repos") }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven("https://jitpack.io")
        // Option 1: Reference GitHub Gate Repos Directly
        maven { url = uri("https://raw.githubusercontent.com/gate/gatepay-sdk-android/main/repos") }
        // Option 2: Use Local Repos
        maven { url = uri("${rootProject.projectDir}/repos") }
    }
}

rootProject.name = "Gate Pay Demo"
include(":app")
 