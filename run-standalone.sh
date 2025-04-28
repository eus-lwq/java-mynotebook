#!/bin/bash

# Set JavaFX path - this should point to your JavaFX SDK lib directory
JAVAFX_PATH="/Users/tylerlwq/Downloads/javafx-sdk-17.0.15/lib"

# Verify JavaFX path exists
if [ ! -d "$JAVAFX_PATH" ]; then
  echo "Error: JavaFX path not found at $JAVAFX_PATH"
  echo "Please make sure you have downloaded JavaFX SDK and update the path in this script."
  exit 1
fi

# Build the application
echo "Building the application..."
mvn clean package -DskipTests

# Check if the build was successful
if [ $? -ne 0 ]; then
  echo "Build failed. Please check the errors above."
  exit 1
fi

# Run the standalone application
echo "Running the standalone application with full backend connectivity..."
java \
  --module-path="$JAVAFX_PATH" \
  --add-modules=javafx.controls,javafx.fxml,javafx.graphics \
  --add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED \
  --add-opens javafx.graphics/com.sun.glass.ui=ALL-UNNAMED \
  --add-opens java.base/java.lang=ALL-UNNAMED \
  -Dspring.profiles.active=dev \
  -jar target/personal-notebook-1.0-SNAPSHOT.jar
