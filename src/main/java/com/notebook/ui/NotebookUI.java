package com.notebook.ui;

import com.notebook.service.AuthService;
import com.notebook.service.NotebookService;
import com.notebook.service.PageService;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class NotebookUI {
    private BorderPane rootLayout;
    private String currentUsername;
    private String authToken;
    
    @Autowired
    private AuthService authService;
    
    @Autowired
    private NotebookService notebookService;
    
    @Autowired
    private PageService pageService;

    public void start(Stage primaryStage) {
        try {
            primaryStage.setTitle("Personal Notebook Management System");
            
            // Initialize the root layout
            rootLayout = new BorderPane();
            
            // Set up the main scene
            Scene scene = new Scene(rootLayout, 1200, 800);
            String cssPath = getClass().getResource("/styles/main.css").toExternalForm();
            scene.getStylesheets().add(cssPath);

            primaryStage.setScene(scene);
            primaryStage.show();
            
            // Show login screen
            showLoginScreen();
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    private void showLoginScreen() {
        LoginScreen loginScreen = new LoginScreen(this, authService);
        rootLayout.setCenter(loginScreen);
    }
    
    public void setCurrentUser(String username, String token) {
        this.currentUsername = username;
        this.authToken = token;
    }

    public void showMainInterface() {
        MainInterface mainInterface = new MainInterface(this, notebookService, pageService);
        rootLayout.setCenter(mainInterface);
    }
    
    public String getCurrentUsername() {
        return currentUsername;
    }
    
    public String getAuthToken() {
        return authToken;
    }
}
