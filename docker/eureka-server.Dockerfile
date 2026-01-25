# ---------- BUILD STAGE ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy parent + module pom
COPY pom.xml .
COPY eureka-server/pom.xml eureka-server/pom.xml

# Download dependencies
RUN mvn -B -pl eureka-server -am dependency:go-offline

# Copy source
COPY eureka-server eureka-server

# Build only eureka-server
RUN mvn -B -pl eureka-server -am clean package -DskipTests

# ---------- RUNTIME STAGE ----------
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/eureka-server/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
