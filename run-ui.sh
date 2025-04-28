#!/bin/bash

# Set JavaFX path
JAVAFX_PATH="/Users/tylerlwq/Downloads/javafx-sdk-17.0.15/lib"

# First build the JAR file
echo "Building the application..."
mvn clean package -DskipTests

# Check if the build was successful
if [ $? -ne 0 ]; then
  echo "Build failed. Please check the errors above."
  exit 1
fi

# Run the main JavaFX class directly
echo "Running the JavaFX UI..."
java --module-path=$JAVAFX_PATH \
  --add-modules=javafx.controls,javafx.fxml,javafx.graphics \
  --add-exports javafx.graphics/com.sun.javafx.application=ALL-UNNAMED \
  --add-opens javafx.graphics/com.sun.glass.ui=ALL-UNNAMED \
  --add-opens java.base/java.lang=ALL-UNNAMED \
  -cp target/personal-notebook-1.0-SNAPSHOT.jar com.notebook.NotebookApplication
