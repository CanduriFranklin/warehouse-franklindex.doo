plugins {
    java
    id("org.springframework.boot") version "3.4.0"
    id("io.spring.dependency-management") version "1.1.6"
    id("org.flywaydb.flyway") version "10.20.1"
    id("com.google.cloud.tools.jib") version "3.4.4"
    id("jacoco")
    id("org.sonarqube") version "5.1.0.4882"
    id("org.owasp.dependencycheck") version "10.0.4"
}

group = "br.com.dio"
version = "1.0.0-SNAPSHOT"
description = "Warehouse Microservice - E-commerce Inventory Management System"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

extra["springCloudVersion"] = "2024.0.0"
extra["testcontainersVersion"] = "1.20.4"
extra["mapstructVersion"] = "1.6.3"

dependencies {
    // Spring Boot Starters
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.boot:spring-boot-starter-cache")
    implementation("org.springframework.boot:spring-boot-starter-aop")
    
    // Spring Cloud
    implementation("org.springframework.cloud:spring-cloud-starter-circuitbreaker-resilience4j")
    
    // Database
    implementation("org.postgresql:postgresql")
    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-database-postgresql")
    
    // OpenAPI/Swagger Documentation
    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")
    
    // Observability & Monitoring
    implementation("io.micrometer:micrometer-registry-prometheus")
    implementation("io.micrometer:micrometer-tracing-bridge-brave")
    implementation("io.zipkin.reporter2:zipkin-reporter-brave")
    runtimeOnly("net.logstash.logback:logstash-logback-encoder:8.0")
    
    // Security - JWT
    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")
    
    // Lombok - Code Generation (Latest version with Java 25 support)
    compileOnly("org.projectlombok:lombok:1.18.42")
    annotationProcessor("org.projectlombok:lombok:1.18.42")
    
    // MapStruct - DTO Mapping
    implementation("org.mapstruct:mapstruct:${property("mapstructVersion")}")
    annotationProcessor("org.mapstruct:mapstruct-processor:${property("mapstructVersion")}")
    annotationProcessor("org.projectlombok:lombok-mapstruct-binding:0.2.0")
    
    // Problem Details (RFC 7807)
    implementation("org.zalando:problem-spring-web:0.29.1")
    
    // Development Tools
    developmentOnly("org.springframework.boot:spring-boot-devtools")
    developmentOnly("org.springframework.boot:spring-boot-docker-compose")
    
    // Testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.springframework.amqp:spring-rabbit-test")
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:rabbitmq")
    testImplementation("io.rest-assured:rest-assured:5.5.0")
    testImplementation("net.datafaker:datafaker:2.4.2")
    
    // ArchUnit - Architecture Testing
    testImplementation("com.tngtech.archunit:archunit-junit5:1.3.0")
}

dependencyManagement {
    imports {
        mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
        mavenBom("org.testcontainers:testcontainers-bom:${property("testcontainersVersion")}")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
    finalizedBy(tasks.jacocoTestReport)
}

tasks.jacocoTestReport {
    dependsOn(tasks.test)
    reports {
        xml.required.set(true)
        html.required.set(true)
        csv.required.set(false)
    }
    classDirectories.setFrom(
        files(classDirectories.files.map {
            fileTree(it) {
                exclude(
                    "**/config/**",
                    "**/dto/**",
                    "**/entity/**",
                    "**/*Application*",
                    "**/*Properties*"
                )
            }
        })
    )
}

jacoco {
    toolVersion = "0.8.12"
}

tasks.jacocoTestCoverageVerification {
    violationRules {
        rule {
            limit {
                minimum = "0.80".toBigDecimal()
            }
        }
    }
}

// Jib - Container Image Builder
jib {
    from {
        image = "eclipse-temurin:25-jre-alpine"
        platforms {
            platform {
                architecture = "amd64"
                os = "linux"
            }
            platform {
                architecture = "arm64"
                os = "linux"
            }
        }
    }
    to {
        image = "warehouse-microservice"
        tags = setOf("latest", version.toString())
    }
    container {
        jvmFlags = listOf(
            "-Xms512m",
            "-Xmx1024m",
            "-XX:+UseG1GC",
            "-XX:MaxGCPauseMillis=100",
            "-Dspring.profiles.active=prod"
        )
        ports = listOf("8080", "8081")
        labels.set(mapOf(
            "maintainer" to "Franklin Canduri",
            "version" to version.toString(),
            "description" to "Warehouse Microservice"
        ))
        creationTime.set("USE_CURRENT_TIMESTAMP")
    }
}

// SonarQube Configuration
sonarqube {
    properties {
        property("sonar.projectKey", "warehouse-microservice")
        property("sonar.projectName", "Warehouse Microservice")
        property("sonar.sourceEncoding", "UTF-8")
        property("sonar.java.coveragePlugin", "jacoco")
        property("sonar.coverage.jacoco.xmlReportPaths", "${project.buildDir}/reports/jacoco/test/jacocoTestReport.xml")
        property("sonar.exclusions", "**/config/**,**/dto/**,**/entity/**,**/*Application*")
    }
}

// OWASP Dependency Check
dependencyCheck {
    format = org.owasp.dependencycheck.reporting.ReportGenerator.Format.ALL.toString()
    suppressionFile = "owasp-suppressions.xml"
    failBuildOnCVSS = 7.0f
}

tasks.withType<JavaCompile> {
    options.encoding = "UTF-8"
    options.compilerArgs.addAll(listOf(
        "-Xlint:unchecked",
        "-Xlint:deprecation",
        "-parameters"
    ))
}

tasks.named<org.springframework.boot.gradle.tasks.bundling.BootJar>("bootJar") {
    archiveFileName.set("${archiveBaseName.get()}.${archiveExtension.get()}")
    layered {
        enabled = true
    }
}

tasks.named<Jar>("jar") {
    enabled = false
}
