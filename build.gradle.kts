plugins {
	id("java")
	id("org.springframework.boot") version "3.5.3"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	kotlin("plugin.jpa") version "1.9.25"
	kotlin("kapt") version "1.9.25"
}

group = "com.team04"
version = "0.0.1-SNAPSHOT"

java {
	toolchain {
		languageVersion.set(JavaLanguageVersion.of(21))
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

dependencies {
	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-webflux")
	implementation("org.springframework.boot:spring-boot-starter-data-redis")
	implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.8.9")

	compileOnly("org.projectlombok:lombok")
	developmentOnly("org.springframework.boot:spring-boot-devtools")
	runtimeOnly("com.h2database:h2")
	runtimeOnly("com.mysql:mysql-connector-j")
	annotationProcessor("org.projectlombok:lombok")

	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testImplementation("org.mockito.kotlin:mockito-kotlin:5.+")
	testImplementation("io.mockk:mockk:1.14.5")

	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.springframework:spring-aop")
	testImplementation("org.mockito:mockito-inline:5.2.0")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	implementation("io.github.openfeign.querydsl:querydsl-jpa:7.0")
	kapt("io.github.openfeign.querydsl:querydsl-apt:7.0:jpa")

}

tasks.named<Test>("test") {
	useJUnitPlatform()
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
}

tasks.register<Exec>("composeUp") {
	commandLine("docker-compose", "up", "-d")
}

tasks.register<Exec>("composeDown") {
	commandLine("docker-compose", "stop")
}

tasks.named("bootRun") {
	dependsOn("composeUp")
	finalizedBy("composeDown")
}
