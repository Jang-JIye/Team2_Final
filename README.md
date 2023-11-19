# Team2 최종 프로젝트 "갈래"

프론트엔드:
* 홍미경(프론트엔드 리더)
* 전하영(프론트엔드 멤버)
* 허원(프론트엔드 멤버)
> 프론트엔드에 대한 설명은 다음 링크를 참조: https://github.com/hyj01230/gallae_fe

백엔드(공통 관여: `commons`):  
* 장지예(프로젝트 리더): 로그인/회원가입, 소셜로그인, 이메일 인증 기능 ( `users`, `profile`, `redis`, `email`, `refreshToken`)
* 김정환(프로젝트 멤버/총무): 게시글 CRUD, 검색, 좋아요 랭킹 정렬, Swagger UI ( `posts`, `postslike`, `tags`)
* 김용글(프로젝트 멤버): 댓글/대댓글 CRUD, CI/CD ( `comments`, `replies`)
* 문승태(프로젝트 멤버): 날짜별 계획 및 세부일정 CRUD, 사진 관련 기능 ( `schedules`, `pictures`, `postspictures`, `tripDate`)

## 프로젝트 간략 설명
<aside>
💡 서비스 '갈래'는 다음과 같은 분들을 위해 탄생되었습니다.

* 🤔**여행을 재밌고, 멋있고, 특별하고, 완벽하게 가고 싶은데 계획을 세우는데 어려움을**                       **가진 사람들** 

* 🤩**나만의 여행 계획을 다른 사람들에게 공유하거나, 자랑하고 싶은 사람들**
 
</aside>



### 적용 기술

- 아키텍쳐
![Blank diagram (3)](https://github.com/Jang-JIye/Team2_Final/assets/53979008/29403cb8-611b-4646-bbbf-8ddd46cadb8f)


- 프론트엔드: React, Tailwind, Recoil, React Router, Vercel, Axios, React Query
- 백엔드: Java Spring Boot, JPA, QueryDSL, Gradle
- 인터페이스: Swagger UI, POSTMAN
- 서버: AWS EC2
- 캐시: Redis
- 데이터베이스: MySQL(AWS RDS), AWS S3
- 인증/인가: JWT, Spring Security, OAuth
- 테스트: JUnit5, Mockito, TestContainers, LocalStack
- 배포(CI/CD): Github Actions, AWS CodeDeploy, AWS S3

### 기술 선정 이유(백엔드)
1. 백엔드 언어 및 프레임워크: Java ver 17 & Spring Boot ver 3
- 선정이유:
> - 익숙함: 스파르타코딩클럽 항해99 백엔드 Spring과정에서 Java 17버전 기반의 Spring Boot ver 3를 학습하여 이미 익숙한 언어와 프레임워크였다. 
> - 범용성: JVM이 설치되어 있으면 어느 플랫폼에서나 어플리케이션을 실행가능하다.
> - 간편성: 어플리케이션 개발시 필요한 이벤트 처리, 검증, 타입 변환 및 테스트 기능 등을 모두 Spring Framework에서 지원한다. 따라서 별도의 개별적인 기능들을 처음부터 구현할 필요없이 간편하게 Spring 프레임워크가 지원하는 기능들을 활용할 수 있다. 

2. 빌드 툴: Gradle
- 선정이유:
> - 관리 용이성: `Build.gradle`에 스크립트를 작성하는 방식이며 대규모 프로젝트에서 복잡해지는 경향이 있는 XML기반 스크립트에 비해 의존성(dependencies) 관리가 용이했다. 프로젝트가 진행되면서 추가되는 의존성을 관리하기 편했다. 

3. ORM: JPA, QueryDSL
- 선정 이유:
> - 가독성(JPA): 쿼리를 직접 작성하는 것이 아닌, Java 코드로 쿼리를 대신할 수 있어 가독성이 좋다. 따라서 타인의 코드를 파악하기 쉬웠고, 팀원 각자의 코드도 직관적으로 작성하기 용이했다.
> - 유지보수 용이성(JPA): 간편하게 수정 가능하며 유지보수 및 리팩토링이 용이하다. 쉽게 수정이 가능해 프로그램 수정을 더 효율적으로 만들어 주었다.  
> - 단순화(QueryDSL): 여러 테이블에 접근해 데이터를 조회하거나 필터링하는 경우, 그 때 마다 쿼리 메서드로 각 엔티티에 해당하는 REPOSITORY에서 접근하기보다는, QueryDSL의 특징인 복잡한 쿼리문을 Java 코드로 작성가능했다. JPA로 복잡한 쿼리를 작성해야할 때 대체하여 활용하기 용이했다.
> - 투명성(QueryDSL): JPA에서 작성한 쿼리 메서드는 문법 에러를 컴파일 시에 확인 불가한데, QueryDSL은 컴파일 시에 생긴 문법 오류를 확인할 수 있다. 이 점이 JPQL에 비해서도 확실한 장점이어서 오류 확인 및 수정의 용이성을 위해 선택했다. 

4. 인터페이스: Swagger UI, POSTMAN
- 선정 이유:
> - 간편성(Swagger UI): 테스트 코드 없이 어노테이션을 통해 API 문서를 작성 가능했다. 별도로 문서를 백엔드 개발자들이 만들 필요가 없었다. 
> - 가독성(Swagger UI): API를 통해 매개변수, 응답, 예제 등 스펙 정보 전달 용이했다. 프론트엔드에 API 정보를 공유하는데 용이했다. 
> - 가용성(Swagger UI):: 실제로 사용되는 매개변수로 테스트 가능하여, 즉각적으로 어플리케이션 API의 테스트를 용이하게했다.
> - 신속성(POSTMAN): API의 스펙을 활용해 빠른 테스트가 가능하여, API가 기댓값을 잘 출력하는지 쉽게 테스트 할 수 있엇다. 
> - 공유 용이성(POSTMAN): API 테스트 내용을 다른 사람과 편리하게 공유 가능하여, 타 팀원도 쉽게 다른 사람이 구현한 API를 테스트 가능했다. 

5. 서버: AWS EC2
- 선정 이유:
> - 유연성: 가상 서버 론칭 및 종료가 간편하며 필요에 따라 컴퓨팅 파워 조정 가능, 배포시 쉽게 서버를 테스트할 수 있게 해주었다.

6. 캐시: Redis
- 선정 이유:
> - 신속성: 데이터를 DB에 저장하지 않고도 빠르게 조회할 수 있게 해주어 이메일 인증 및 Refresh Token등의 기능 구현에 용이했다.

7. 데이터 베이스: AWS RDS, MySQL, AWS S3
- 선정 이유:
> - 간편성(RDS): AWS에서 모든 것을 관리하기 때문에  데이터베이스 최적화, 성능 튜닝 및 스키마 최적화 같은 작업에 집중할 수 있고 재해 발생시 모든 데이터를 자동으로 백업하기에 선택했다.
> - 간편성(S3): 개발자가 별도의 파일 업로드 로직을 구현할 필요 없이, Java와 호환되는 AWS SDK를 활용해 파일을 업로드할 수 있었고, URL상으로 저장되기에 프론트에서 출력하기 용이했다. 
> - 적합성(MySQL): 대규모 프로젝트가 아니기에 용량이 적고 비용적 부담이 없어 선택하였다.

8. 인증/인가: JWT, Spring Security, OAuth
- 선정 이유:
> - 확장성(JWT): 어플리케이션이 커짐에따라 서버가 처리해야할 로드가 커지는데, 서버에 데이터를 저장하지 않기때문에 대량의 트래픽이 발생해도 대처가 용이하다. 게시글에 연결된 많은 데이터들(댓글, 대댓글, 세부일정 등)을 처리하는데 있어 서버에 상대적으로 부담이 덜한 기술이기에 적합하다고 판단했다.
> - 간편성(Spring Security): 개발자가 일일히 보안 로직을 만들지 않고도 쉽게 CSRF 공격 방어를 포함한 유용한 보안 기능들을 적용할 수 있게 해주어 개발 속도를 빠르게 가져가는데 있어 도움이 된다고 판단했다.
> - 간편성(OAuth2): 이용하려는 서비스마다 회원가입을 일일히 원하지 않는 유저가 기존의 사용하던 타사 정보를 이용해 로그인을 할 수 있어, 소셜로그인 기능 구현에 적합하다고 판단했다.

9. 테스트: JUnit5, Mockito, TestContainers, LocalStack
- 선정 이유:
> - 간편성(Mockito): 간단한 API로 테스트 작성 가능하여, 구현한 API에 대해서 테스트코드를 편리하게 작성하기 용이하다고 판단해 선택했다.
> - 가독성(Mockito): 테스트 코드의 가독성이 높고, 따라서 검증 오류를 명확하게 알 수 있다. 되도록 직관적인 테스트 코드의 작성과 공유가 중요하다 판단되어 선택했다.
> - 간편성(JUnit5): 단위 테스트를 작성하여 프로그램의 각 부분이 정확히 동작하는지 검증할 필요가 있었고, 이를 위해 간편하게 테스트 코드 구현이 가능한 JUnit5를 선택했다.
> - 간편성(TestContainers + LocalStack): Docker상에 존재하는 컨테이너(Container)라는 개념을 활용하여 테스트를 할 수 있게 해주는 TestContainers를 활용해 오직 테스트만을 위한 환경을 쉽게 구성 가능했고, AWS의 서비스(RDS, S3등)을 모사하여 에뮬레이션 해 주는 이미지의 개념인 LocalStack를 활용할 수 있었다. 때문에 실제 S3를 활용하여 구현된 코드를 건드리지 않고 완전히 독립된 환경에서 오직 사진 업로드 기능 테스트를 위한 S3 에뮬레이션 환경을 구성할 수 있다는 장점 때문에 선택했다.

10. CI/CD: GitHub Actions, AWS CodeDeploy, AWS S3
- 선정 이유:
> - 간편성(GitHub Actions): 이미 작업중인 GitHub Repository에서 바로 어플리케이션을 배포할 수 있다는 점이 빠르게 CI/CD를 적용해 볼 수 있다는 점에서 매력적이었다. 또한 간단하고 직관적인 인터페이스 때문에 사용이 편리했고, CI/CD를 적용하는데 매우 수월할 것이라고 판단했다.
> - 경제성(GitHub Actions): 대규모 프로젝트를 진행하는 것이 아니기에 Jenkins등 다른 솔루션은 리소스의 낭비가 크다고 판단했고, 따라서 GitHub Actions가 진행하는 프로젝트의 규모에 적절하다고 판단되었다.
> - 유지보수 용이성(CodeDeploy): 빌드 과정이 투명하여 오류를 쉽게 확인 가능하다.
> - 호환성(CodeDeploy): 프로젝트에서 AWS의 여러 서비스인 EC2, S3, RDS를 활용하기로 결정했기에 통합하여 같이 활용하는데 적합하다고 판단했다.
> - 간편성(S3): 파일을 쉽게 업로드할 수 있게 해주는 S3를 활용해 jar파일을 zip으로 업로드하고 이를 CodeDeploy에 배포할 수 있게 줄 수 있는 도구로 판단되었다. S3가 배포에 필요한 파일을 저장할 수 있는 도구로써 좋겠다고 판단되어 선택했다. 

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

