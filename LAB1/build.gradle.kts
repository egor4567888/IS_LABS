plugins {
    id("java")
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("javax:javaee-api:8.0.1")

    // EclipseLink JPA
    implementation("org.eclipse.persistence:org.eclipse.persistence.jpa:2.7.9")

    // PostgreSQL הנאיגונ
    implementation("org.postgresql:postgresql:42.6.0")

    // PrimeFaces הכÿ JSF UI
    implementation("org.primefaces:primefaces:12.0.0")
}

tasks.test {
    useJUnitPlatform()
}