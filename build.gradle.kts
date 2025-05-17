plugins {
    id("java")
    id("org.springframework.boot") version "3.2.5" apply false
    id("io.spring.dependency-management") version "1.1.4"
}

allprojects {
    group = "com.example"
    version = "0.0.1-SNAPSHOT"

    apply(plugin = "java")

    repositories {
        mavenCentral()
    }

    plugins.withType<JavaPlugin> {
        java {
            toolchain {
                languageVersion.set(JavaLanguageVersion.of(17))
            }
        }
    }
}

tasks.register("bootRunAll") {
    group = "application"
    description = "Run all Spring Boot apps in parallel"

    doLast {
        val isWindows = System.getProperty("os.name").toLowerCase().contains("windows")
        val script = if (isWindows) "cmd /c runAll.bat" else "./runAll.sh"

        exec {
            commandLine = script.split(" ")
        }
    }
}

