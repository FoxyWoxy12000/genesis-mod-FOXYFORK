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
    dependsOn("resourcePack")

    from("LICENSE") {
        rename { "${it}_${rootProject.name}" }
    }
}

tasks.remapJar {
    archiveFileName = "${rootProject.name}.jar"
}

tasks.runServer {
    dependsOn("resourcePack")

    doFirst {
        val python = if (System.getProperty("os.name").lowercase().contains("win")) "py" else "python3"
        val server = ProcessBuilder(
            python, "-m", "http.server", "4000",
            "--directory", file("build/libs").absolutePath
        ).inheritIO().start()

        Runtime.getRuntime().addShutdownHook(Thread {
            server.destroy()
        })
    }
}

tasks.register<Zip>("resourcePack") {
    description = "Packages the resource pack."

    archiveFileName.set("GenesisPack.zip")
    destinationDirectory.set(layout.buildDirectory.dir("libs"))

    from("src/resourcePack")
    from("LICENSE")
}
