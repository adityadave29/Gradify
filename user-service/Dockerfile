# Build JAR inside Docker so `docker build` works without a prior local `mvn package`.
FROM eclipse-temurin:21-jdk-jammy AS builder

WORKDIR /build

COPY mvnw mvnw.cmd ./
COPY .mvn .mvn
COPY pom.xml .
COPY src src

RUN chmod +x mvnw \
	&& ./mvnw -B -q package -DskipTests

FROM eclipse-temurin:21-jre-jammy

WORKDIR /app

COPY --from=builder /build/target/user-service-0.0.1-SNAPSHOT.jar app.jar

EXPOSE 8081

CMD ["java", "-jar", "app.jar"]
