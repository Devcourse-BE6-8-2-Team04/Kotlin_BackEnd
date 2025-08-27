plugins {
	id("java")
	id("org.springframework.boot") version "3.5.3"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	kotlin("plugin.jpa") version "1.9.25"
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

	implementation("com.querydsl:querydsl-jpa:5.0.0:jakarta")
	annotationProcessor("com.querydsl:querydsl-apt:5.0.0:jakarta")
	annotationProcessor("jakarta.annotation:jakarta.annotation-api")
	annotationProcessor("jakarta.persistence:jakarta.persistence-api")

	implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")
}

tasks.named<Test>("test") {
	useJUnitPlatform()
}

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

// gradle clean 시에 QClass 디렉토리 삭제
tasks.named<Delete>("clean") {
	delete(generated)
}
