# Clean up any previous build artifacts
mvn clean

# Build the Spring Boot application JAR file, skipping tests for faster build
mvn package -DskipTests

# Build multi-platform Docker image and push to registry
# Supports both AMD64 and ARM64 architectures for broader compatibility
docker buildx build . --platform="linux/amd64,linux/arm64" -t isaru66/springboot-azure-servicebus-producer  --push 