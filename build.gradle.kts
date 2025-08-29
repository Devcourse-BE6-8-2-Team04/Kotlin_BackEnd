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
	testImplementation("org.mockito.kotlin:mockito-kotlin:5.1.0")
	testImplementation("org.mockito:mockito-inline:5.5.0")
	implementation("org.springframework:spring-aop")
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
// QueryDSL Q클래스 생성 경로 설정
val generated = "src/main/generated"

// querydsl QClass 파일 생성 위치를 지정
tasks.withType<JavaCompile>().configureEach {
	options.generatedSourceOutputDirectory.set(file(generated))
}

// java source set 에 querydsl QClass 위치 추가
sourceSets {
	named("main") {
		java.srcDirs(generated)
	}
}

allOpen {
	annotation("jakarta.persistence.Entity")
	annotation("jakarta.persistence.MappedSuperclass")
	annotation("jakarta.persistence.Embeddable")
// gradle clean 시에 QClass 디렉토리 삭제
tasks.named<Delete>("clean") {
	delete(generated)
}