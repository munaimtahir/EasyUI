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
    }
}

rootProject.name = "easyui-senior-launcher"

include(
    ":app",
    ":core:ui",
    ":core:domain",
    ":core:data",
    ":core:platform",
    ":core:testing",
    ":feature:home",
    ":feature:apps",
    ":feature:caregiver",
    ":feature:onboarding",
)
