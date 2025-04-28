package com.notebook;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import com.notebook.ui.NotebookUI;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Standalone application launcher that initializes both JavaFX and Spring Boot
 * This class is designed to be run directly from Java without Maven
 */
@SpringBootApplication
public class StandaloneApplication extends Application {
    private static String[] savedArgs;
    private ConfigurableApplicationContext springContext;
    
    public static void main(String[] args) {
        savedArgs = args;
        launch(args);
    }
    
    @Override
    public void init() {
        springContext = SpringApplication.run(StandaloneApplication.class, savedArgs);
    }
    
    @Override
    public void start(Stage primaryStage) {
        try {
            // Get the NotebookUI bean from Spring context
            NotebookUI ui = springContext.getBean(NotebookUI.class);
            
            // Start the UI
            ui.start(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
            showError("Failed to start application: " + e.getMessage());
        }
    }
    
    @Override
    public void stop() {
        springContext.close();
    }
    
    private void showError(String message) {
        Stage errorStage = new Stage();
        errorStage.setTitle("Application Error");
        
        VBox root = new VBox(10);
        root.setPadding(new Insets(20));
        root.setAlignment(Pos.CENTER);
        
        Label errorLabel = new Label("An error occurred:");
        TextArea errorText = new TextArea(message);
        errorText.setEditable(false);
        errorText.setWrapText(true);
        errorText.setPrefHeight(200);
        
        Button closeButton = new Button("Close");
        closeButton.setOnAction(e -> {
            errorStage.close();
            System.exit(1);
        });
        
        root.getChildren().addAll(errorLabel, errorText, closeButton);
        
        Scene scene = new Scene(root, 500, 300);
        errorStage.setScene(scene);
        errorStage.show();
    }
    
    // No need to define beans here as they are already defined in SecurityConfig
}
