plugins {
    kotlin("jvm") version "2.3.20"
}

val version = "0.1.0"
val group = "dev.kamiql"

repositories {
    mavenCentral()
    maven("https://repo.papermc.io/repository/maven-public/")
    maven("https://jitpack.io")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

kotlin {
    jvmToolchain(25)
}

dependencies {
    implementation(kotlin("stdlib"))

    compileOnly("io.papermc.paper:paper-api:26.1.2.build.64-stable")

    compileOnly("io.github.revxrsal:lamp.bukkit:4.0.0-rc.16")
    compileOnly("io.github.revxrsal:lamp.common:4.0.0-rc.16")
    compileOnly("com.github.MilkBowl:VaultAPI:1.7")

    implementation("com.fasterxml.jackson.core:jackson-databind:2.21.3")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.21.3")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-yaml:2.21.3")
    implementation("com.fasterxml.jackson.dataformat:jackson-dataformat-toml:2.21.3")
    implementation("org.xerial:sqlite-jdbc:3.50.3.0")
}

tasks.processResources {
    val props = mapOf("version" to version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") { expand(props) }
}
