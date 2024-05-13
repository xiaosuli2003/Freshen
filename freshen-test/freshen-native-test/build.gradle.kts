plugins {
    kotlin("jvm")
}

group = "cn.xiaosuli"
version = "unspecified"

repositories {
    mavenCentral()
}

dependencies {
    implementation(project(":freshen-core"))
    implementation("com.alibaba:druid:1.2.22")
    runtimeOnly("com.mysql:mysql-connector-j:8.2.0")
    implementation("org.projectlombok:lombok:1.18.32")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
    implementation("ch.qos.logback:logback-classic:1.5.6")
    implementation("org.slf4j:slf4j-api:2.0.13")
    testImplementation(kotlin("test"))
    implementation(kotlin("reflect"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}