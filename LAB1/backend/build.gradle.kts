import com.github.gradle.node.npm.task.NpmTask

plugins {
    id("org.springframework.boot") version "3.1.6"
    id("io.spring.dependency-management") version "1.1.0"
    id("com.github.node-gradle.node") version "7.0.0" // Плагин для Node.js задач
    java
}

group = "com.example"
version = "0.1.0"
java.sourceCompatibility = JavaVersion.VERSION_17

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-websocket")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.h2database:h2")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    implementation("org.postgresql:postgresql")
}

// Конфигурация Node.js плагина
node {
    version.set("18.17.0")
    npmVersion.set("9.6.7")
    download.set(true)
}

// Задача для установки зависимостей фронтенда
//tasks.register<NpmTask>("npmInstallFrontend") {
//    dependsOn(tasks.named("npmSetup"))
//    workingDir.set(file("${project.projectDir}/../frontend"))
//    args.set(listOf("install"))
//}

// Задача для сборки фронтенда
tasks.register<NpmTask>("npmBuildFrontend") {
    //dependsOn(tasks.named("npmInstallFrontend"))
    workingDir.set(file("${project.projectDir}/../frontend"))
    args.set(listOf("run", "build:prod"))
}

// Задача для копирования собранного фронтенда в ресурсы Spring Boot
tasks.register<Copy>("copyFrontendToResources") {
    dependsOn(tasks.named("npmBuildFrontend"))
    from("${project.projectDir}/../frontend/dist")
    into("${project.projectDir}/src/main/resources/static")
}

 //Сделать процесс ресурсов зависимым от копирования фронтенда
tasks.named("processResources") {
    dependsOn(tasks.named("copyFrontendToResources"))
}

tasks.withType<Test> {
    useJUnitPlatform()
}

// Конфигурация для создания исполняемого JAR
springBoot {
    buildInfo()
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    launchScript()
    archiveFileName.set("space-marines-app.jar")
}

// Задача для сборки всего проекта
tasks.register("buildFull") {
    dependsOn(tasks.named("clean"), tasks.named("bootJar"))
    group = "build"
    description = "Builds the complete application with frontend"
}