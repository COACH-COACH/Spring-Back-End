# Java Application을 Docker Image로 Build하기 위한 파일

# Stage 1: Build stage
# 베이스 이미지 생성
FROM maven:3.8.4-openjdk-17-slim AS build

# Maven 빌드 시 필요한 pom.xml 파일을 현재 디렉토리로 복사
COPY pom.xml ./
# .mvn 디렉토리를 현재 디렉토리로 복사(maven 실행 위함)
COPY .mvn .mvn

# 애플리케이션 소스 코드 복사
COPY src src

# Maven의 package 명령어를 실행하여 소스코드 컴파일, jar 파일 생성
RUN mvn package

# Stage 2: Run stage
FROM openjdk:17-jdk-slim

# 작업 디렉토리 설정
WORKDIR /app
# Stage 1에서 생성된 JAR 파일을 /app 디렉토리로 복사, app.jar로 이름을 변경
COPY --from=build /target/*.jar app.jar
# 컨테이너가 시작될 때 java -jar app.jar 명령을 실행하여 Java 애플리케이션을 시작
CMD ["java", "-jar", "app.jar"]