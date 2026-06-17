plugins {
    alias(libs.plugins.kotlin.jvm)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.fabric.loom)
}

group = "dev.d4vid.mods.genesis"
version = "0.1.0"
description = "Mod for the Genesis SMP."

loom {
    splitEnvironmentSourceSets()

    mods {
        register("genesis") {
            sourceSet(sourceSets.main.get())
        }
    }
}

dependencies {
    implementation(libs.kotlin.serialization.json)

    minecraft(libs.minecraft)
    mappings(loom.officialMojangMappings())

    modImplementation(libs.fabric.loader)
    modImplementation(libs.fabric.api)
    modImplementation(libs.fabric.kotlin)
}

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

tasks.processResources {
    filesMatching("*.json") {
        expand(
            mapOf(
                "name" to rootProject.name,
                "description" to project.description,
                "version" to project.version,
                "group" to project.group,
            )
        )
    }
}

tasks.jar {
    from("LICENSE") {
        rename { "${it}_${rootProject.name}" }
    }
}

tasks.remapJar {
    archiveFileName = "${rootProject.name}.jar"
}
