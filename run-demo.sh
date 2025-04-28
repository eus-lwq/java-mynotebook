#!/bin/bash

# Set JavaFX path
JAVAFX_PATH="/Users/tylerlwq/Downloads/javafx-sdk-17.0.15/lib"

# Compile the SimpleLauncher class
echo "Compiling the SimpleLauncher..."
javac -cp target/personal-notebook-1.0-SNAPSHOT.jar \
  --module-path="$JAVAFX_PATH" \
  --add-modules=javafx.controls,javafx.fxml,javafx.graphics \
  src/main/java/com/notebook/ui/SimpleLauncher.java

# Check if compilation was successful
if [ $? -ne 0 ]; then
  echo "Compilation failed. Please check the errors above."
  exit 1
fi

# Run the SimpleLauncher
echo "Running the demo UI..."
java -cp src/main/java:target/personal-notebook-1.0-SNAPSHOT.jar \
  --module-path="$JAVAFX_PATH" \
  --add-modules=javafx.controls,javafx.fxml,javafx.graphics \
  com.notebook.ui.SimpleLauncher
