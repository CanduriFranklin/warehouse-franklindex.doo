# ============================================
# Stage 1: Build Stage
# ============================================
FROM eclipse-temurin:25-jdk-alpine AS builder

# Set working directory
WORKDIR /workspace/app

# Copy Gradle wrapper and build files
COPY gradlew .
COPY gradle gradle
COPY build.gradle.kts .
COPY settings.gradle.kts .

# Download dependencies (cached layer)
RUN ./gradlew dependencies --no-daemon || return 0

# Copy source code
COPY src src

# Build application (skip tests for faster builds, run tests in CI/CD)
RUN ./gradlew bootJar --no-daemon -x test

# Extract JAR layers for better caching
RUN mkdir -p build/dependency && (cd build/dependency; jar -xf ../libs/*.jar)

# ============================================
# Stage 2: Runtime Stage (Minimal Image)
# ============================================
FROM eclipse-temurin:25-jre-alpine

# Install dumb-init for proper signal handling
RUN apk add --no-cache dumb-init

# Create non-root user for security
RUN addgroup -S spring && adduser -S spring -G spring

# Set working directory
WORKDIR /app

# Copy dependencies from builder
COPY --from=builder --chown=spring:spring /workspace/app/build/dependency/BOOT-INF/lib /app/lib
COPY --from=builder --chown=spring:spring /workspace/app/build/dependency/META-INF /app/META-INF
COPY --from=builder --chown=spring:spring /workspace/app/build/dependency/BOOT-INF/classes /app

# Switch to non-root user
USER spring:spring

# Expose application port
EXPOSE 8080

# Health check
HEALTHCHECK --interval=30s --timeout=3s --start-period=60s --retries=3 \
  CMD wget --no-verbose --tries=1 --spider http://localhost:8080/actuator/health || exit 1

# Use dumb-init to handle signals properly
ENTRYPOINT ["dumb-init", "--"]

# Run application with optimized JVM settings
CMD ["java", \
     "-XX:+UseContainerSupport", \
     "-XX:MaxRAMPercentage=75.0", \
     "-XX:InitialRAMPercentage=50.0", \
     "-XX:+UseG1GC", \
     "-XX:+OptimizeStringConcat", \
     "-Djava.security.egd=file:/dev/./urandom", \
     "-Dspring.profiles.active=${SPRING_PROFILES_ACTIVE:-prod}", \
     "org.springframework.boot.loader.launch.JarLauncher"]
