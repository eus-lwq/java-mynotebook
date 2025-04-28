package com.notebook.ui;

import com.notebook.dto.AuthRequest;
import com.notebook.dto.AuthResponse;
import com.notebook.service.AuthService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class RegistrationScreen extends Stage {
    private final TextField usernameField;
    private final PasswordField passwordField;
    private final PasswordField confirmPasswordField;
    private final AuthService authService;

    public RegistrationScreen(Stage owner, AuthService authService) {
        this.authService = authService;
        initOwner(owner);
        initModality(Modality.WINDOW_MODAL);
        setTitle("Register New Account");

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        // Username field
        usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.getStyleClass().add("login-field");

        // Password field
        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("login-field");

        // Confirm password field
        confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");
        confirmPasswordField.getStyleClass().add("login-field");

        // Register button
        Button registerButton = new Button("Register");
        registerButton.getStyleClass().add("login-button");
        registerButton.setOnAction(e -> handleRegistration());

        // Cancel button
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> close());

        layout.getChildren().addAll(
            new Label("Username:"),
            usernameField,
            new Label("Password:"),
            passwordField,
            new Label("Confirm Password:"),
            confirmPasswordField,
            registerButton,
            cancelButton
        );

        Scene scene = new Scene(layout);
        scene.getStylesheets().add(getClass().getResource("/styles/main.css").toExternalForm());
        setScene(scene);
    }

    private void handleRegistration() {
        String username = usernameField.getText();
        String password = passwordField.getText();
        String confirmPassword = confirmPasswordField.getText();

        if (username.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
            showError("All fields are required");
            return;
        }
        
        // Username validation
        if (username.length() < 4) {
            showError("Username must be at least 4 characters long");
            return;
        }
        
        if (!username.matches("^[a-zA-Z0-9_]+$")) {
            showError("Username can only contain letters, numbers, and underscores");
            return;
        }

        // Password validation
        if (password.length() < 8) {
            showError("Password must be at least 8 characters long");
            return;
        }
        
        if (!password.matches(".*[A-Z].*")) {
            showError("Password must contain at least one uppercase letter");
            return;
        }
        
        if (!password.matches(".*[0-9].*")) {
            showError("Password must contain at least one number");
            return;
        }
        
        if (!password.matches(".*[!@#$%^&*()_+\\-=\\[\\]{};\':\"\\\\|,.<>/?].*")) {
            showError("Password must contain at least one special character");
            return;
        }

        if (!password.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }

        try {
            AuthRequest request = new AuthRequest(username, password);
            AuthResponse response = authService.register(request);
            
            if (response != null && response.getToken() != null) {
                showSuccess("Registration successful! You can now log in.");
                close();
            } else {
                showError("Registration failed. Please try again.");
            }
        } catch (ResponseStatusException e) {
            if (e.getStatusCode() == HttpStatus.CONFLICT) {
                showError("Username already exists. Please choose another one.");
            } else {
                showError("Registration error: " + e.getMessage());
            }
        } catch (Exception e) {
            showError("An error occurred: " + e.getMessage());
        }
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showSuccess(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
