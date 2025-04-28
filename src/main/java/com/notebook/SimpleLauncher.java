package com.notebook;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

/**
 * Simple launcher that bypasses Spring Boot and database dependencies
 * to demonstrate the UI on Apple M4 chips.
 */
public class SimpleLauncher extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            // Create a simple UI demonstration
            BorderPane rootLayout = new BorderPane();
            rootLayout.setPadding(new Insets(10));
            
            // Create a simple login form
            VBox loginForm = createLoginForm(primaryStage);
            
            rootLayout.setCenter(loginForm);
            
            Scene scene = new Scene(rootLayout, 800, 600);
            primaryStage.setTitle("Personal Notebook - Demo Mode");
            primaryStage.setScene(scene);
            primaryStage.show();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    private VBox createLoginForm(Stage primaryStage) {
        VBox loginBox = new VBox(10);
        loginBox.setAlignment(Pos.CENTER);
        loginBox.setPadding(new Insets(20));
        
        Label titleLabel = new Label("Personal Notebook");
        titleLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold;");
        
        Label subtitleLabel = new Label("Demo Mode - UI Only");
        subtitleLabel.setStyle("-fx-font-size: 14px; -fx-font-style: italic;");
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(300);
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(300);
        
        Button loginButton = new Button("Login");
        loginButton.setDefaultButton(true);
        loginButton.setOnAction(e -> showMainInterface(primaryStage));
        
        Hyperlink registerLink = new Hyperlink("Create new account");
        registerLink.setOnAction(e -> showRegistrationDialog());
        
        loginBox.getChildren().addAll(
            titleLabel,
            subtitleLabel,
            new Label(""),
            new Label("Username:"),
            usernameField,
            new Label("Password:"),
            passwordField,
            new Label(""),
            loginButton,
            registerLink
        );
        
        return loginBox;
    }
    
    private void showMainInterface(Stage primaryStage) {
        BorderPane mainLayout = new BorderPane();
        mainLayout.setPadding(new Insets(10));
        
        // Create a tree view for notebooks
        TreeItem<String> root = new TreeItem<>("My Notebooks");
        root.setExpanded(true);
        
        TreeItem<String> notebook1 = new TreeItem<>("Sample Notebook");
        notebook1.getChildren().add(new TreeItem<>("Page 1"));
        notebook1.getChildren().add(new TreeItem<>("Page 2"));
        notebook1.setExpanded(true);
        
        root.getChildren().add(notebook1);
        
        TreeView<String> treeView = new TreeView<>(root);
        treeView.setPrefWidth(200);
        
        // Create tabs for pages
        TabPane tabPane = new TabPane();
        
        Tab tab1 = new Tab("Welcome");
        TextArea textArea = new TextArea("Welcome to Personal Notebook!\n\nThis is a demo mode that shows the UI without database connectivity.");
        textArea.setWrapText(true);
        tab1.setContent(textArea);
        tabPane.getTabs().add(tab1);
        
        // Create toolbar
        ToolBar toolBar = new ToolBar();
        Button newNotebookBtn = new Button("New Notebook");
        Button newPageBtn = new Button("New Page");
        Button tableBtn = new Button("Insert Table");
        Button graphBtn = new Button("Insert Graph");
        Button exportBtn = new Button("Export");
        
        toolBar.getItems().addAll(newNotebookBtn, newPageBtn, tableBtn, graphBtn, exportBtn);
        
        VBox leftPanel = new VBox(5);
        leftPanel.getChildren().addAll(new Label("Notebooks"), treeView);
        
        mainLayout.setTop(toolBar);
        mainLayout.setLeft(leftPanel);
        mainLayout.setCenter(tabPane);
        
        Scene scene = new Scene(mainLayout, 800, 600);
        primaryStage.setTitle("Personal Notebook - Demo Mode");
        primaryStage.setScene(scene);
    }
    
    private void showRegistrationDialog() {
        Stage dialog = new Stage();
        dialog.setTitle("Register New Account");
        
        VBox content = new VBox(10);
        content.setPadding(new Insets(20));
        content.setAlignment(Pos.CENTER);
        
        Label titleLabel = new Label("Create New Account");
        titleLabel.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        TextField usernameField = new TextField();
        usernameField.setPromptText("Username");
        usernameField.setMaxWidth(300);
        
        PasswordField passwordField = new PasswordField();
        passwordField.setPromptText("Password");
        passwordField.setMaxWidth(300);
        
        PasswordField confirmPasswordField = new PasswordField();
        confirmPasswordField.setPromptText("Confirm Password");
        confirmPasswordField.setMaxWidth(300);
        
        Button registerButton = new Button("Register");
        registerButton.setDefaultButton(true);
        registerButton.setOnAction(e -> {
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Registration");
            alert.setHeaderText(null);
            alert.setContentText("Registration successful in demo mode!");
            alert.showAndWait();
            dialog.close();
        });
        
        Button cancelButton = new Button("Cancel");
        cancelButton.setOnAction(e -> dialog.close());
        
        content.getChildren().addAll(
            titleLabel,
            new Label("Username:"),
            usernameField,
            new Label("Password:"),
            passwordField,
            new Label("Confirm Password:"),
            confirmPasswordField,
            new Label(""),
            registerButton,
            cancelButton
        );
        
        Scene scene = new Scene(content, 400, 400);
        dialog.setScene(scene);
        dialog.showAndWait();
    }
}
