pluginManagement {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    @Suppress("UnstableApiUsage")
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)

    versionCatalogs {
        create("libs") {
            from(files(rootProject.projectDir.resolve("libs.versions.toml")))
        }
    }

    @Suppress("UnstableApiUsage")
    repositories {
        mavenCentral()
        gradlePluginPortal()
        google()
        maven(url = "https://jitpack.io")
    }
}

plugins {
    id("de.fayard.refreshVersions") version "+"
    id("org.gradle.toolchains.foojay-resolver-convention") version "+"
}

rootProject.apply {
    name = "npgx-tachiyomi-extensions"
    buildFileName = "env.build.gradle.kts"
}

includeBuild(rootProject.projectDir.resolve("build-src").resolve("conventions"))

include(":default")

fun includeAllSubprojectsIn(dir: File, prefix: String, expectedScriptName: String? = "build.gradle") {
    if (!dir.exists() || !dir.isDirectory) return

    (dir.listFiles() ?: emptyArray())
        .asSequence()
        .filter { it.isDirectory }
        .filter { d ->
            expectedScriptName == null ||
                d.listFiles()?.any { it.name == expectedScriptName || it.name == "${expectedScriptName}.kts" } == true
        }
        .forEach { inclusion ->
            include(":${prefix}-${inclusion.name}")
            project(":${prefix}-${inclusion.name}").apply {
                this.projectDir = inclusion
            }
        }
}

includeAllSubprojectsIn(rootProject.projectDir.resolve("lib"), "lib")
includeAllSubprojectsIn(rootProject.projectDir.resolve("multisrc"), "multisrc")
includeAllSubprojectsIn(rootProject.projectDir.resolve("extensions"), "extensions")