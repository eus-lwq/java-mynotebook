#!/bin/bash

# Set JavaFX path
JAVAFX_PATH="/Users/tylerlwq/Downloads/javafx-sdk-17.0.15/lib"

# Run the application using Spring Boot Maven plugin
echo "Running the application with Spring Boot and JavaFX..."
JAVAFX_MODULES="--module-path=$JAVAFX_PATH --add-modules=javafx.controls,javafx.fxml,javafx.graphics"

# Run with Spring Boot Maven plugin
mvn spring-boot:run \
  -Dspring-boot.run.jvmArguments="$JAVAFX_MODULES \
  --add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED \
  --add-opens javafx.graphics/com.sun.glass.ui=ALL-UNNAMED \
  --add-opens java.base/java.lang=ALL-UNNAMED"
