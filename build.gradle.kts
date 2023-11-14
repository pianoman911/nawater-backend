import java.io.ByteArrayOutputStream
import java.util.stream.Stream
import kotlin.streams.asStream

plugins {
    id("java-library")

    id("application")
}

group = "de.pianoman911"
version = "1.0.0"

repositories {
    mavenCentral()
}

dependencies {
    api("com.google.code.gson:gson:2.10.1")
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
        vendor.set(JvmVendorSpec.ADOPTIUM)
    }
}

application {
    mainClass.set("de.pianoman911.nawater.NaWaterMain")
}

tasks {
    jar {
        manifest.attributes(
                "Implementation-Vendor" to "pianoman911",
                "Implementation-Version" to project.version,
                "Implementation-Title" to project.name,

                "Git-Commit" to gitRevParse("short"),
                "Git-Branch" to gitRevParse("abbrev-ref"),
                "Timestamp" to System.currentTimeMillis().toString(),
        )
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
