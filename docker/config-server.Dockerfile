# ---------- BUILD STAGE ----------
FROM maven:3.9-eclipse-temurin-21 AS build
WORKDIR /app

# Copy parent + module pom files
COPY pom.xml .
COPY config-server/pom.xml config-server/pom.xml

# Download dependencies
RUN mvn -B -pl config-server -am dependency:go-offline

# Copy source
COPY config-server config-server

# Build config-server only
RUN mvn -B -pl config-server -am clean package -DskipTests

# ---------- RUNTIME STAGE ----------
FROM eclipse-temurin:21-jre
WORKDIR /app
COPY --from=build /app/config-server/target/*.jar app.jar
ENTRYPOINT ["java","-jar","app.jar"]
