#!/bin/bash

# Set JavaFX path - this should point to your JavaFX SDK lib directory
JAVAFX_PATH="/Users/tylerlwq/Downloads/javafx-sdk-17.0.15/lib"

# Verify JavaFX path exists
if [ ! -d "$JAVAFX_PATH" ]; then
  echo "Error: JavaFX path not found at $JAVAFX_PATH"
  echo "Please make sure you have downloaded JavaFX SDK and update the path in this script."
  exit 1
fi

# Run the application using Maven with proper JavaFX configuration
echo "Running the application with Spring Boot and JavaFX..."

# Create JavaFX modules argument
JAVAFX_MODULES="--module-path=$JAVAFX_PATH --add-modules=javafx.controls,javafx.fxml,javafx.graphics"

# Run with Spring Boot Maven plugin and dev profile
mvn spring-boot:run \
  -Dspring-boot.run.profiles=dev \
  -Dspring-boot.run.jvmArguments="$JAVAFX_MODULES \
  --add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED \
  --add-opens javafx.graphics/com.sun.glass.ui=ALL-UNNAMED \
  --add-opens java.base/java.lang=ALL-UNNAMED"
