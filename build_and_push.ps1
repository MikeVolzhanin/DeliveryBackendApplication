# Variables
$DOCKER_USER = "fairbearof"  # Your Docker Hub username
$APP_NAME = "delivery-app"   # Image name
$JAR_FILE = Get-ChildItem target\DeliveryBackendApplication-0.0.1-SNAPSHOT.jar -ErrorAction SilentlyContinue | Select-Object -First 1

# Check if the JAR file exists
if (-not $JAR_FILE) {
    Write-Host "JAR file not found in the target/ folder. Build the project first: mvn package" -ForegroundColor Red
    exit 1
}

# Build the Docker image
Write-Host "Building the Docker image..."
docker build -t "$DOCKER_USER/$APP_NAME" .

# Push the image to Docker Hub
Write-Host "Pushing the image to Docker Hub..."
docker push "$DOCKER_USER/$APP_NAME"

# Print success message
Write-Host "Done!" -ForegroundColor Green
