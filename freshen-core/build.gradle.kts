import com.vanniktech.maven.publish.SonatypeHost
import org.jetbrains.kotlin.gradle.plugin.mpp.pm20.util.archivesName

plugins {
    kotlin("jvm")
    // id("java-library")
    // id("maven-publish")
    // id("org.jreleaser") version "1.12.0"
    id("com.vanniktech.maven.publish") version "0.28.0"
}

group = "cn.xiaosuli"
archivesName = "freshen-core"
version = "0.1.0-alpha"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation(kotlin("reflect"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}

mavenPublishing {
    // or when publishing to https://central.sonatype.com/
    publishToMavenCentral(SonatypeHost.CENTRAL_PORTAL)

    signAllPublications()

    coordinates("cn.xiaosuli", "freshen-core", "0.1.0-alpha")

    pom {
        name.set("Freshen")
        description.set("一个基于JDBC的kotlinDSL风格的库，但是玩具。")
        inceptionYear.set("2024")
        url.set("https://github.com/xiaosuli2003/Freshen/")
        licenses {
            license {
                name.set("The Apache License, Version 2.0")
                url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                distribution.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
            }
        }
        developers {
            developer {
                id.set("xiaosuli")
                name.set("xiaosuli")
                url.set("https://github.com/xiaosuli2003/")
            }
        }
        scm {
            url.set("https://github.com/xiaosuli2003/Freshen/")
            connection.set("scm:git:git://github.com/xiaosuli2003/Freshen.git")
            developerConnection.set("scm:git:ssh://git@github.com/xiaosuli2003/Freshen.git")
        }
    }
}