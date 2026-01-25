# ---------- BUILD STAGE ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy parent + module pom
COPY pom.xml .
COPY gateway-service/pom.xml gateway-service/pom.xml

# Download dependencies
RUN mvn -B -pl gateway-service -am dependency:go-offline

# Copy source
COPY gateway-service gateway-service

# Build only gateway-service
RUN mvn -B -pl gateway-service -am clean package -DskipTests

# ---------- RUNTIME STAGE ----------
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/gateway-service/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
