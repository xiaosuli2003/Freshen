plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "0.5.0"
    kotlin("jvm") version "1.9.23" apply false
}
rootProject.name = "Freshen"
include("freshen-core")
include("freshen-test")
include("freshen-test:freshen-native-test")
findProject(":freshen-test:freshen-native-test")?.name = "freshen-native-test"
include("freshen-test:freshen-spring-test")
findProject(":freshen-test:freshen-spring-test")?.name = "freshen-spring-test"
include("freshen-test:freshen-spring-boot-test")
findProject(":freshen-test:freshen-spring-boot-test")?.name = "freshen-spring-boot-test"