# Team2 최종 프로젝트 "갈래"

프론트엔드:
* 홍미경(프론트엔드 리더)
* 전하영(프론트엔드 멤버)
* 허원(프론트엔드 멤버)
> 프론트엔드에 대한 설명은 다음 링크를 참조: https://github.com/hyj01230/gallae_fe

백엔드: 
* 장지예(프로젝트 리더): 로그인/회원가입, 소셜로그인, 이메일 인증 기능(Redis)
* 김정환(프로젝트 총무): 게시글 CRUD, 검색, 좋아요 랭킹 정렬, Swagger UI
* 김용글(프로젝트 서기): 댓글/대댓글 CRUD, CI/CD
* 문승태(프로젝트 발표): 날짜별 계획 및 세부일정 CRUD, 사진 관련 기능

## 프로젝트 간략 설명
2-30대의 여행 계획자들을 위한 여행 계획 세우기 및 공유 서비스

### 적용 기술

- 아키텍쳐
![project_architecture](https://github.com/Jang-JIye/Team2_Final/assets/53979008/b4c80832-f4bb-45c9-b423-9940d5c83a73)

- 프론트엔드: React, Tailwind, Recoil, React Router, Vercel, Axios, React Query
- 백엔드: Java Spring Boot, JPA, QueryDSL, Gradle
- 인터페이스: Swagger UI, POSTMAN
- 서버: AWS EC2
- 캐시: Redis
- 데이터베이스: MySQL(AWS RDS), AWS S3
- 인증/인가: JWT, Spring Security, OAuth
- 테스트: JUnit5, Mockito
- 배포(CI/CD): Github Actions

### ERD
![final_erd](https://github.com/Jang-JIye/Team2_Final/assets/53979008/37895b8a-2fe0-4da7-945b-17d419e6071b)

### 사용법
1. `application.properties` 에서 하기 설정 필요
```
# MySQL + AWS RDS 정보
spring.datasource.url=
spring.datasource.username=
spring.datasource.password=
spring.datasource.driver-class-name=com.mysql.cj.jdbc.Driver
spring.jpa.hibernate.ddl-auto=update
spring.jpa.properties.hibernate.show_sql=true
spring.jpa.properties.hibernate.format_sql=true
spring.jpa.properties.hibernate.use_sql_comments=true

# JWT관련 정보
jwt.secret.key=
ADMIN_TOKEN=

#이메일 관련 정보
# Gmail SMTP
spring.mail.host=smtp.gmail.com
# Gmail SMTP
spring.mail.port=587
# Gmail hanghae99**
spring.mail.username=
# Gmail
spring.mail.password=
# SMTP
spring.mail.properties.mail.smtp.auth=true
# SMTP StartTLS
spring.mail.properties.mail.smtp.starttls.enable=true

# Springdoc properties(Swagger UI관련)
springdoc.api-docs.enabled=true
springdoc.swagger-ui.enabled=true
springdoc.swagger-ui.path=/swagger-ui.html

#S3(사용할 S3 Bucket정보)
cloud.aws.credentials.access-key=
cloud.aws.credentials.secret-key=
cloud.aws.stack.auto=false
cloud.aws.region.static=
cloud.aws.s3.bucket=

# Social Kakao(사용할 카카오 API정보)
kakaoClientId=
kakaoRedirectUri=

# Kakao map(카카오맵 API키)
kakaoRestApiKey=

# Redis
spring.data.redis.host=svc.sel5.cloudtype.app
spring.data.redis.port=
```

2. Dependencies(`build.gradle`)

```
plugins {
	id 'java'
	id 'org.springframework.boot' version '3.1.4'
	id 'io.spring.dependency-management' version '1.1.3'
}

jar {
	enabled = false
}

group = 'com.sparta'
version = '0.0.1-SNAPSHOT'

java {
	sourceCompatibility = '17'
}

configurations {
	compileOnly {
		extendsFrom annotationProcessor

	}
}

repositories {
	mavenCentral()
}

dependencies {
	// Adobe S3Mock
	testImplementation 'io.findify:s3mock_2.13:0.2.6'

	// JUnit5
	testImplementation 'org.junit.jupiter:junit-jupiter:5.6.0'

	// Marvin
	implementation 'com.github.downgoon:marvin:1.5.5'
	implementation 'com.github.downgoon:MarvinPlugins:1.5.5'
	testImplementation 'org.springframework:spring-test:6.0.13'
	implementation 'org.springframework:spring-web:6.0.13'

	//testcontainer
	implementation("com.amazonaws:aws-java-sdk-s3")
	testImplementation "org.testcontainers:testcontainers:1.15.3"
	testImplementation "org.testcontainers:junit-jupiter:1.15.3"
	testImplementation "org.testcontainers:localstack:1.15.3"
	testImplementation("cloud.localstack:localstack-utils:0.2.20")
	implementation 'org.slf4j:slf4j-api:2.0.9'
	implementation 'com.github.docker-java:docker-java:3.3.3'


	// 소셜로그인
	implementation 'org.springframework.boot:spring-boot-starter-oauth2-client'

	// email
	implementation 'org.springframework.boot:spring-boot-starter-mail'

	// MySQL
	implementation 'mysql:mysql-connector-java:8.0.28'

	// JPA 설정
	implementation 'org.springframework.boot:spring-boot-starter-data-jpa'

	//SpringBoot Validation
	implementation 'org.springframework.boot:spring-boot-starter-validation'

	implementation 'org.springframework.boot:spring-boot-starter-data-jpa'
	implementation 'org.springframework.boot:spring-boot-starter-web'
	testImplementation 'org.testng:testng:7.1.0'
//	testImplementation 'repository:junit:junit'

	testImplementation 'junit:junit:4.13.1'
	testImplementation 'org.projectlombok:lombok:1.18.28'
	compileOnly 'org.projectlombok:lombok'
	runtimeOnly 'com.mysql:mysql-connector-j'
	annotationProcessor 'org.projectlombok:lombok'
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
	// Redis
	implementation 'org.springframework.boot:spring-boot-starter-data-redis'
	implementation 'org.springframework.boot:spring-boot-starter-cache'

	// JWT
	compileOnly group: 'io.jsonwebtoken', name: 'jjwt-api', version: '0.11.5'
	runtimeOnly group: 'io.jsonwebtoken', name: 'jjwt-impl', version: '0.11.5'
	runtimeOnly group: 'io.jsonwebtoken', name: 'jjwt-jackson', version: '0.11.5'

	// Security
	implementation 'org.springframework.boot:spring-boot-starter-security'
  

	// S3
	implementation 'org.springframework.cloud:spring-cloud-starter-aws:2.2.6.RELEASE'
	// aws s3
	// 스웨거
	implementation 'org.springdoc:springdoc-openapi-starter-webmvc-ui:2.1.0'

	// Querydsl
	implementation 'com.querydsl:querydsl-jpa:5.0.0:jakarta'
	annotationProcessor "com.querydsl:querydsl-apt:5.0.0:jakarta"
	annotationProcessor "jakarta.annotation:jakarta.annotation-api"
	annotationProcessor "jakarta.persistence:jakarta.persistence-api"

	// redis
	implementation 'org.springframework.boot:spring-boot-starter-data-redis'

	// Scalr
	implementation 'se.digiplant:play-scalr_2.11:1.1.2'
	implementation 'com.github.gavlyukovskiy:p6spy-spring-boot-starter:1.5.6'
	implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
	implementation 'nz.net.ultraq.thymeleaf:thymeleaf-layout-dialect'
	implementation 'commons-fileupload:commons-fileupload:1.5'
	implementation 'commons-io:commons-io:2.15.0'


	//SpringBoot Test
	testImplementation 'org.springframework.boot:spring-boot-starter-test'
	//SpringBoot Security Test
	testImplementation 'org.springframework.security:spring-security-test'
	// redis
	implementation 'org.springframework.boot:spring-boot-starter-data-redis'

}

tasks.named('test') {
	useJUnitPlatform()
}

def querydslDir = "$buildDir/generated/querydsl"

sourceSets {
	main.java.srcDirs += [ querydslDir ]
}

tasks.withType(JavaCompile).configureEach {
	options.getGeneratedSourceOutputDirectory().set(file(querydslDir))
}

clean.doLast {
	file(querydslDir).deleteDir()
}
```


### 프로젝트 상세정보:
- 배포된 웹 사이트: [https://gallae-trip.com](https://gallae-fe.vercel.app/)
- 프로젝트 기획 문서(노션): https://wind-song-8bf.notion.site/2-2571118e6ef94d2082bc372fca7260d8

