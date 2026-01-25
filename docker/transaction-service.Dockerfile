# ---------- BUILD STAGE ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy parent + module poms (for dependency cache)
COPY pom.xml .
COPY common-lib/pom.xml common-lib/pom.xml
COPY transaction-service/pom.xml transaction-service/pom.xml

# Pre-download dependencies
RUN mvn -B -pl transaction-service -am dependency:go-offline

# Copy source
COPY common-lib common-lib
COPY transaction-service transaction-service

# Build only this service
RUN mvn -B -pl transaction-service -am clean package -DskipTests

# ---------- RUNTIME STAGE ----------
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/transaction-service/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
