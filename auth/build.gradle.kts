plugins {
    id("java")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web:3.3.0")
    implementation("org.springframework.boot:spring-boot-starter-security:3.3.0")
    implementation("org.springframework.security:spring-security-cas")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa:3.3.0")
    implementation("org.springframework.boot:spring-boot-starter-oauth2-authorization-server:3.3.0")
    implementation("org.liquibase:liquibase-core:4.24.0")
    runtimeOnly("com.h2database:h2:2.2.224")
    compileOnly("org.projectlombok:lombok:1.18.30")
    annotationProcessor("org.projectlombok:lombok:1.18.30")
    implementation(project(mapOf("path" to ":common")))
    implementation("org.postgresql:postgresql:42.7.2")
}
