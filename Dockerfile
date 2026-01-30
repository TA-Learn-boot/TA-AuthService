FROM maven:3.9.6-eclipse-temurin-21 as builder
# Copy the Maven project files from the authservice subfolder
WORKDIR /app
COPY pom.xml .
COPY src ./src
# Build the project and copy the resulting jar to a fixed filename so the runtime stage
# can reliably pick it up regardless of the exact artifact name produced by Maven.
RUN mvn -f pom.xml clean package -DskipTests \
	&& mkdir -p /app/dist \
	&& cp target/*.jar /app/dist/authservice.jar || (echo "No jar found in target/" && exit 1)

FROM eclipse-temurin:21-jre-alpine
WORKDIR /app
# Copy the built jar from the builder stage. If multiple jars exist, adjust to the exact name.
# Use `authservice.jar` as the artifact name so container and pipeline use consistent naming.
COPY --from=builder /app/dist/authservice.jar authservice.jar

# Override profile with ENV (highest precedence)
ENV SPRING_PROFILES_ACTIVE=staging

EXPOSE 8080
ENTRYPOINT ["java", "-jar", "authservice.jar"]

HEALTHCHECK --interval=30s --timeout=3s --start-period=40s --retries=3 \
  CMD curl -f http://localhost:8080/identity/api/actuator/health || exit 1