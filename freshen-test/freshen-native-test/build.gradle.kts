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
    implementation("log4j:log4j:1.2.17")
    testImplementation(kotlin("test"))
    implementation(kotlin("reflect"))
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(21)
}