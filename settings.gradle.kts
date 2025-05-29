plugins {
    id("com.gradle.develocity") version("4.0.2")
}

rootProject.name = "meilisearch"

dependencyResolutionManagement {
    versionCatalogs {
        create("meilisearchLibs") {
            from(files("gradle/libs.versions.toml"))
        }
    }
}

//includeBuild("advent-of-code-utils")
include("advent-of-code-utils")
apply( "advent-of-code-utils/settings.gradle.kts")
project(":aoc-utils").projectDir = file( path = "$rootDir/advent-of-code-utils/aoc-utils")
project(":aoc-utils:aoc-utils-kotlin").projectDir = file( path = "$rootDir/advent-of-code-utils/aoc-utils/aoc-utils-kotlin")
project(":aoc-utils:aoc-utils-java").projectDir = file( path = "$rootDir/advent-of-code-utils/aoc-utils/aoc-utils-java")
