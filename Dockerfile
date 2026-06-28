# ---------- Build stage ----------
FROM maven:3.9.6-eclipse-temurin-17 AS build
WORKDIR /app

# Cache dependencies separately
COPY pom.xml .
RUN mvn dependency:go-offline -B

COPY src ./src
RUN mvn clean package -DskipTests -B

# ---------- Runtime stage ----------
FROM eclipse-temurin:17-jre-alpine
WORKDIR /app

COPY --from=build /app/target/habitflow-1.0.0.jar app.jar

EXPOSE 8080

# -XX:TieredStopAtLevel=1 speeds up initial startup significantly
ENTRYPOINT ["java", "-XX:TieredStopAtLevel=1", "-Xms128m", "-Xmx384m", "-jar", "app.jar"]
