import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
	id("org.springframework.boot") version PluginVersions.SPRING_BOOT_VERSION
	id("io.spring.dependency-management") version PluginVersions.DEPENDENCY_MANAGER_VERSION
	id("org.jlleitschuh.gradle.ktlint") version PluginVersions.KTLINT_VERSION
	kotlin("jvm") version PluginVersions.JVM_VERSION
	kotlin("plugin.spring") version PluginVersions.SPRING_PLUGIN_VERSION
	kotlin("plugin.jpa") version PluginVersions.JPA_PLUGIN_VERSION
}

group = "dev.fodo"
version = "0.0.1-SNAPSHOT"

java {
	sourceCompatibility = JavaVersion.VERSION_21
}

configurations {
	compileOnly {
		extendsFrom(configurations.annotationProcessor.get())
	}
}

repositories {
	mavenCentral()
}

dependencies {
	// Default
	implementation(Dependencies.SPRING_WEB)
	implementation(Dependencies.JACKSON_KOTLIN)
	implementation(Dependencies.KOTLIN_REFLECT)
	testImplementation(Dependencies.SPRING_TEST)

	// Socket
	implementation(Dependencies.SPRING_SOCKET)

	// Database
	implementation(Dependencies.SPRING_JPA)
	implementation(Dependencies.POSTGRESQL)

	// Validation
	implementation(Dependencies.SPRING_VALIDATION)

	// Security
	implementation(Dependencies.SPRING_SECURITY)
}

tasks.withType<KotlinCompile> {
	kotlinOptions {
		freeCompilerArgs += "-Xjsr305=strict"
		jvmTarget = "21"
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.named("jar") {
	enabled = false
}
