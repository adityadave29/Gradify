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

COPY --from=builder /build/target/admin-service-0.0.1-SNAPSHOT.jar app.jar

ENV SUPABASE_SERVICE_ROLE_KEY="eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJpc3MiOiJzdXBhYmFzZSIsInJlZiI6InhleXV2bHdlcGVwdm1lbnp3YXJxIiwicm9sZSI6InNlcnZpY2Vfcm9sZSIsImlhdCI6MTc3MzkwNTEyOSwiZXhwIjoyMDg5NDgxMTI5fQ.RXax6RMpJPqRAN7cZsEoM-aTVhYkehDHDmfMPU-yMgs"
EXPOSE 8083

CMD ["java", "-jar", "app.jar"]
