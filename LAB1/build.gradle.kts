plugins {
    id("org.springframework.boot") version "3.1.6"
    id("io.spring.dependency-management") version "1.1.0"
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
    implementation("org.hibernate.validator:hibernate-validator:8.0.0.Final")
    implementation("com.h2database:h2")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
}

tasks.withType<Test> {
    useJUnitPlatform()
}
