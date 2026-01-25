# ---------- BUILD STAGE ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy parent + required module POMs
COPY pom.xml .
COPY common-lib/pom.xml common-lib/pom.xml
COPY auth-service/pom.xml auth-service/pom.xml

# Pre-download dependencies
RUN mvn -B -pl auth-service -am dependency:go-offline

# Copy source code
COPY common-lib common-lib
COPY auth-service auth-service

# Build only auth-service
RUN mvn -B -pl auth-service -am clean package -DskipTests

# ---------- RUNTIME STAGE ----------
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/auth-service/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
