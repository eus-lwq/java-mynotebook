package com.notebook.ui;

import com.notebook.config.AuthTokenInterceptor;
import com.notebook.repository.UserRepository;
import com.notebook.service.AuthService;
import com.notebook.service.NotebookService;
import com.notebook.service.PageService;
import com.notebook.service.SecurityService;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.stage.Stage;
import javafx.scene.layout.BorderPane;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.Optional;

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
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private AuthTokenInterceptor authTokenInterceptor;
    
    @Autowired
    private SecurityService securityService;

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

    public void showLoginScreen() {
        // Ask if the user wants to use the debug login screen
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Login Mode");
        alert.setHeaderText("Choose Login Mode");
        alert.setContentText("Would you like to use the debug login screen? This will help diagnose login issues.");
        
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            // Show debug login screen
            DebugLoginScreen debugLoginScreen = new DebugLoginScreen(
                (Stage) rootLayout.getScene().getWindow(), 
                authService, 
                userRepository, 
                passwordEncoder, 
                this
            );
            debugLoginScreen.show();
        } else {
            // Show normal login screen
            LoginScreen loginScreen = new LoginScreen(this, authService);
            rootLayout.setCenter(loginScreen);
        }
    }
    
    public void setCurrentUser(String username, String token) {
        this.currentUsername = username;
        this.authToken = token;
        
        // Set the token in the interceptor so it's used for all service calls
        if (authTokenInterceptor != null) {
            authTokenInterceptor.setAuthToken(token);
            System.out.println("Auth token set in interceptor: " + (token != null ? "[TOKEN PRESENT]" : "[NO TOKEN]"));
        } else {
            System.err.println("Warning: AuthTokenInterceptor is null, authentication may not work properly");
        }
        
        // Directly set authentication in security context
        if (securityService != null) {
            boolean success = securityService.setAuthenticationForUser(username);
            System.out.println("Authentication set in security context: " + success);
        } else {
            System.err.println("Warning: SecurityService is null, authentication may not work properly");
        }
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
