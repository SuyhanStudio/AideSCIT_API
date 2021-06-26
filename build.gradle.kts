import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    id("org.springframework.boot") version "2.5.1"
    id("io.spring.dependency-management") version "1.0.11.RELEASE"
    war
    kotlin("jvm") version "1.5.10"
    kotlin("plugin.spring") version "1.5.10"
}

group = "com.sgpublic"
version = "0.0.1-SNAPSHOT"
java.sourceCompatibility = JavaVersion.VERSION_1_8

repositories {
    mavenCentral()
    maven("https://jitpack.io")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    providedRuntime("org.springframework.boot:spring-boot-starter-tomcat")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    // MariaDB 驱动
    runtimeOnly("org.mariadb.jdbc:mariadb-java-client")
    // JPA
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    // okhttp 用于网络访问
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.2")
    // json 解析
    implementation("org.json:json:20210307")
    // html 解析
    implementation("org.jsoup:jsoup:1.13.1")
    // Excel、World 读写
    implementation("cn.afterturn:easypoi-base:4.4.0")
    implementation("cn.afterturn:easypoi-web:4.4.0")
    implementation("cn.afterturn:easypoi-annotation:4.4.0")
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict")
        jvmTarget = "1.8"
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}

tasks.bootJar {
    // 设置打包文件名称
    archiveFileName.set("SCITEduTool_API_SpringBoot.jar")
}
