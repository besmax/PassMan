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
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "PassMan"
include(":app")
include(":database")
include(":database:api")
include(":database:impl")
include(":cipher")
include(":cipher:api")
include(":cipher:impl")
include(":main")
include(":features")
include(":features:main")
include(":mylibrary")
include(":features:category")
