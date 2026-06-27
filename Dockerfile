# ---------- Build stage ----------
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Cache dependencies separately so they aren't re-downloaded on every code change
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

# ---------- Runtime stage ----------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/target/habitflow-1.0.0.jar app.jar

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
