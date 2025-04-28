#!/bin/bash

# Set JavaFX path
JAVAFX_PATH="/Users/tylerlwq/Downloads/javafx-sdk-17.0.15/lib"

# Build the application
echo "Building the application..."
mvn clean package -DskipTests

# Check if the build was successful
if [ $? -ne 0 ]; then
  echo "Build failed. Please check the errors above."
  exit 1
fi

# Run the application using the JAR file
echo "Running the application..."
java \
  --module-path="$JAVAFX_PATH" \
  --add-modules=javafx.controls,javafx.fxml,javafx.graphics \
  -jar target/personal-notebook-1.0-SNAPSHOT.jar
