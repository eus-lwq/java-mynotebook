package com.notebook.ui;

import com.notebook.dto.AuthRequest;
import com.notebook.dto.AuthResponse;
import com.notebook.service.AuthService;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

public class LoginScreen extends VBox {
    private final TextField usernameField;
    private final PasswordField passwordField;
    private final NotebookUI application;
    private final AuthService authService;

    public LoginScreen(NotebookUI application, AuthService authService) {
        this.application = application;
        this.authService = authService;
        setPadding(new Insets(20));
        setSpacing(10);
        setAlignment(Pos.CENTER);

        // Title
        Text title = new Text("Personal Notebook");
        title.getStyleClass().add("login-title");

        // Username field
        usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.getStyleClass().add("login-field");

        // Password field
        passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.getStyleClass().add("login-field");

        // Login button
        Button loginButton = new Button("Login");
        loginButton.getStyleClass().add("login-button");
        loginButton.setOnAction(e -> handleLogin());

        // Register link
        Hyperlink registerLink = new Hyperlink("Create new account");
        registerLink.setOnAction(e -> handleRegister());

        getChildren().addAll(
            title,
            new Label("Username:"),
            usernameField,
            new Label("Password:"),
            passwordField,
            loginButton,
            registerLink
        );
    }

    private void handleLogin() {
        String username = usernameField.getText();
        String password = passwordField.getText();

        if (username.isEmpty() || password.isEmpty()) {
            showError("Username and password are required");
            return;
        }

        try {
            AuthRequest request = new AuthRequest(username, password);
            AuthResponse response = authService.login(request);
            
            if (response != null && response.getToken() != null) {
                application.setCurrentUser(username, response.getToken());
                application.showMainInterface();
            } else {
                showError("Authentication failed. Please check your credentials.");
            }
        } catch (Exception e) {
            showError("Authentication error: " + e.getMessage());
        }
    }

    private void handleRegister() {
        RegistrationScreen registrationScreen = new RegistrationScreen(new Stage(), authService);
        registrationScreen.showAndWait();
    }

    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
