import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.5.2"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    kotlin("jvm") version "1.5.20"
    kotlin("plugin.spring") version "1.5.20"
    kotlin("plugin.jpa") version "1.5.20"
}

group = "com.sgpublic"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_16

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://jitpack.io") }
    maven { url = uri("https://repo.spring.io/release") }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:2.5.4")
    implementation("org.springframework.boot:spring-boot-starter-web:2.5.4")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:2.12.5")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    developmentOnly("org.springframework.boot:spring-boot-devtools:2.5.4")
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client:2.7.3")
    annotationProcessor("org.springframework.boot:spring-boot-configuration-processor:2.5.4")
    testImplementation("org.springframework.boot:spring-boot-starter-test:2.5.4")

    // okhttp 用于网络访问
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.2")
    // json 解析
    implementation("org.json:json:20210307")
    // html 解析
    implementation("org.jsoup:jsoup:1.14.2")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "16"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

//tasks.withType<BootBuildImage> {
//    builder = "paketobuildpacks/builder:tiny"
//    environment = mapOf("BP_NATIVE_IMAGE" to "true")
//}

tasks.bootJar {
    // 设置打包文件名称
    archiveFileName.set("AideSCIT_API_SpringBoot.jar")
}
