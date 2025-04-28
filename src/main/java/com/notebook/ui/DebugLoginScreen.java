package com.notebook.ui;

import com.notebook.dto.AuthRequest;
import com.notebook.dto.AuthResponse;
import com.notebook.model.User;
import com.notebook.repository.UserRepository;
import com.notebook.service.AuthService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.springframework.security.crypto.password.PasswordEncoder;

/**
 * A debug version of the login screen that shows more detailed error messages
 * and allows testing of password encoding/matching
 */
public class DebugLoginScreen extends Stage {
    private final TextField usernameField;
    private final PasswordField passwordField;
    private final TextArea debugOutput;
    private final AuthService authService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final NotebookUI application;

    public DebugLoginScreen(Stage owner, AuthService authService, UserRepository userRepository, 
                           PasswordEncoder passwordEncoder, NotebookUI application) {
        this.authService = authService;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.application = application;
        
        initOwner(owner);
        initModality(Modality.WINDOW_MODAL);
        setTitle("Debug Login");

        VBox layout = new VBox(10);
        layout.setAlignment(Pos.CENTER);
        layout.setPadding(new Insets(20));

        // Username field
        usernameField = new TextField();
        usernameField.setPromptText("Username");

        // Password field
        passwordField = new PasswordField();
        passwordField.setPromptText("Password");

        // Debug output
        debugOutput = new TextArea();
        debugOutput.setEditable(false);
        debugOutput.setPrefHeight(200);
        debugOutput.setPrefWidth(400);

        // Login button
        Button loginButton = new Button("Login");
        loginButton.setOnAction(e -> handleLogin());

        // Debug button
        Button debugButton = new Button("Debug User");
        debugButton.setOnAction(e -> debugUser());

        // Normal login button
        Button normalLoginButton = new Button("Normal Login");
        normalLoginButton.setOnAction(e -> handleNormalLogin());

        layout.getChildren().addAll(
            new Label("Username:"),
            usernameField,
            new Label("Password:"),
            passwordField,
            loginButton,
            debugButton,
            normalLoginButton,
            new Label("Debug Output:"),
            debugOutput
        );

        Scene scene = new Scene(layout, 500, 500);
        setScene(scene);
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            appendDebug("Username and password are required");
            return;
        }

        try {
            // First check if user exists
            userRepository.findByUsername(username).ifPresentOrElse(
                user -> {
                    appendDebug("User found: " + user.getUsername());
                    appendDebug("Stored password hash: " + user.getPasswordHash());
                    
                    // Test password match
                    boolean matches = passwordEncoder.matches(password, user.getPasswordHash());
                    appendDebug("Password matches: " + matches);
                    
                    if (matches) {
                        try {
                            // Try normal login
                            AuthRequest request = new AuthRequest(username, password);
                            AuthResponse response = authService.login(request);
                            appendDebug("Login successful! Token: " + response.getToken());
                            
                            // Set current user and show main interface
                            application.setCurrentUser(username, response.getToken());
                            application.showMainInterface();
                            close();
                        } catch (Exception e) {
                            appendDebug("Login error: " + e.getMessage());
                        }
                    }
                },
                () -> appendDebug("User not found: " + username)
            );
        } catch (Exception e) {
            appendDebug("Error: " + e.getMessage());
            e.printStackTrace();
        }
    }
    
    private void debugUser() {
        String username = usernameField.getText();
        
        if (username.isEmpty()) {
            appendDebug("Username is required");
            return;
        }
        
        try {
            userRepository.findByUsername(username).ifPresentOrElse(
                user -> {
                    appendDebug("User details:");
                    appendDebug("ID: " + user.getId());
                    appendDebug("Username: " + user.getUsername());
                    appendDebug("Password hash: " + user.getPasswordHash());
                    appendDebug("Role: " + user.getRole());
                },
                () -> appendDebug("User not found: " + username)
            );
        } catch (Exception e) {
            appendDebug("Error: " + e.getMessage());
        }
    }
    
    private void handleNormalLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            appendDebug("Username and password are required");
            return;
        }

        try {
            AuthRequest request = new AuthRequest(username, password);
            AuthResponse response = authService.login(request);
            
            if (response != null && response.getToken() != null) {
                appendDebug("Login successful!");
                application.setCurrentUser(username, response.getToken());
                application.showMainInterface();
                close();
            } else {
                appendDebug("Authentication failed. Please check your credentials.");
            }
        } catch (Exception e) {
            appendDebug("Authentication error: " + e.getMessage());
        }
    }
    
    private void appendDebug(String message) {
        debugOutput.appendText(message + "\n");
    }
}
