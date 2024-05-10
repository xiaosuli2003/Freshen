plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
    kotlin("jvm") version "1.9.23" apply false
}
rootProject.name = "Freshen"
include("core")
include("test")
