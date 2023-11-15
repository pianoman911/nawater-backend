import com.github.jengelman.gradle.plugins.shadow.transformers.Log4j2PluginsCacheFileTransformer
import java.io.ByteArrayOutputStream
import java.util.stream.Stream
import kotlin.streams.asStream

plugins {
    id("java-library")

    id("application")
    id("com.github.johnrengelman.shadow") version "7.1.2"
}

group = "de.pianoman911"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    // GSON
    api("com.google.code.gson:gson:2.10.1")

    // Logging
    val log4jVersion = "2.19.0"
    api("org.apache.logging.log4j:log4j-slf4j-impl:$log4jVersion")
    api("org.apache.logging.log4j:log4j-iostreams:$log4jVersion")
    api("org.apache.logging.log4j:log4j-core:$log4jVersion")
    api("org.apache.logging.log4j:log4j-jul:$log4jVersion")
    api("org.apache.logging.log4j:log4j-api:$log4jVersion")

    // HOCON configuration parsing
    val configurateVersion = "4.1.2"
    api("org.spongepowered:configurate-core:$configurateVersion")
    api("org.spongepowered:configurate-yaml:$configurateVersion")
    api("org.spongepowered:configurate-xml:$configurateVersion")
    api("org.spongepowered:configurate-hocon:$configurateVersion")
    api("org.spongepowered:configurate-gson:$configurateVersion")

    // SQL
    api("org.mariadb.jdbc:mariadb-java-client:3.1.4")

    // Console handling
    api("net.minecrell:terminalconsoleappender:1.3.0")
    runtimeOnly("org.jline:jline-terminal-jansi:3.21.0")
    runtimeOnly("com.lmax:disruptor:3.4.4")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
        vendor.set(JvmVendorSpec.ADOPTIUM)
    }
}

val main = "de.pianoman911.nawater.NaWaterMain"

application {
    mainClass.set(main)
}

tasks {
    jar {
        manifest.attributes(
                "Implementation-Vendor" to "pianoman911",
                "Implementation-Version" to project.version,
                "Implementation-Title" to project.name,
                "Main-Class" to main,

                "Git-Commit" to gitRevParse("short"),
                "Git-Branch" to gitRevParse("abbrev-ref"),
                "Timestamp" to System.currentTimeMillis().toString(),
        )
    }

    shadowJar {
        transform(Log4j2PluginsCacheFileTransformer::class.java)
        minimize()
    }

    build {
        dependsOn(shadowJar)
    }
}

fun gitRevParse(arg: String): String {
    return gitCommand(rootDir, "rev-parse", "--$arg", "HEAD")
}

fun gitCommand(workDir: File, vararg args: String): String {
    val out = ByteArrayOutputStream()
    rootProject.exec {
        commandLine(Stream.concat(Stream.of("git"), args.asSequence().asStream()).toList())
        workingDir = workDir

        standardOutput = out
        errorOutput = out
    }
    return out.toString().trim()
}
