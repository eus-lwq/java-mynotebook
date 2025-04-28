package com.notebook.ui;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Stage;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.text.Text;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

/**
 * A simple launcher for the UI that doesn't require backend connectivity.
 * This is useful for testing and development.
 */
public class SimpleLauncher extends Application {
    
    public static void main(String[] args) {
        launch(args);
    }
    
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("Personal Notebook Management System");
        
        // Create the main interface
        BorderPane root = new BorderPane();
        
        // Create the sidebar
        VBox sidebar = createSidebar();
        root.setLeft(sidebar);
        
        // Create the editor area
        TabPane editorTabs = new TabPane();
        root.setCenter(editorTabs);
        
        // Create the tool panel
        VBox toolPanel = createToolPanel();
        root.setRight(toolPanel);
        
        // Add a welcome tab
        Tab welcomeTab = new Tab("Welcome");
        welcomeTab.setClosable(false);
        VBox welcomeContent = new VBox(20);
        welcomeContent.setPadding(new Insets(20));
        welcomeContent.setAlignment(Pos.CENTER);
        
        Text welcomeText = new Text("Welcome to the Personal Notebook Management System");
        welcomeText.setFont(Font.font("System", FontWeight.BOLD, 20));
        
        Text infoText = new Text("This is a demo version without backend connectivity.\n" +
                "Create notebooks and pages using the sidebar buttons.");
        infoText.setFont(Font.font("System", 14));
        
        welcomeContent.getChildren().addAll(welcomeText, infoText);
        welcomeTab.setContent(welcomeContent);
        editorTabs.getTabs().add(welcomeTab);
        
        // Set up the scene
        Scene scene = new Scene(root, 1200, 800);
        primaryStage.setScene(scene);
        primaryStage.show();
    }
    
    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(10));
        sidebar.setPrefWidth(200);
        
        Button newNotebookBtn = new Button("New Notebook");
        newNotebookBtn.setMaxWidth(Double.MAX_VALUE);
        newNotebookBtn.setOnAction(e -> showDemoAlert("Create Notebook", "In a full implementation, this would create a new notebook."));
        
        Button newPageBtn = new Button("New Page");
        newPageBtn.setMaxWidth(Double.MAX_VALUE);
        newPageBtn.setOnAction(e -> showDemoAlert("Create Page", "In a full implementation, this would create a new page in the selected notebook."));
        
        TreeView<String> notebookTree = new TreeView<>();
        TreeItem<String> root = new TreeItem<>("My Notebooks");
        root.setExpanded(true);
        
        // Add some demo notebooks and pages
        TreeItem<String> notebook1 = new TreeItem<>("Personal Notes");
        notebook1.getChildren().add(new TreeItem<>("Ideas"));
        notebook1.getChildren().add(new TreeItem<>("To-Do List"));
        
        TreeItem<String> notebook2 = new TreeItem<>("Work Notes");
        notebook2.getChildren().add(new TreeItem<>("Meeting Notes"));
        notebook2.getChildren().add(new TreeItem<>("Project Plan"));
        
        root.getChildren().addAll(notebook1, notebook2);
        notebookTree.setRoot(root);
        
        VBox.setVgrow(notebookTree, Priority.ALWAYS);
        
        sidebar.getChildren().addAll(newNotebookBtn, newPageBtn, notebookTree);
        return sidebar;
    }
    
    private VBox createToolPanel() {
        VBox tools = new VBox(10);
        tools.setPadding(new Insets(10));
        tools.setPrefWidth(150);
        
        Label toolsLabel = new Label("Tools");
        toolsLabel.setFont(Font.font("System", FontWeight.BOLD, 14));
        
        Button createTableBtn = new Button("Create Table");
        createTableBtn.setMaxWidth(Double.MAX_VALUE);
        createTableBtn.setOnAction(e -> showDemoAlert("Create Table", "In a full implementation, this would create a data table."));
        
        Button createGraphBtn = new Button("Create Graph");
        createGraphBtn.setMaxWidth(Double.MAX_VALUE);
        createGraphBtn.setOnAction(e -> showDemoAlert("Create Graph", "In a full implementation, this would create a graph visualization."));
        
        Button exportBtn = new Button("Export");
        exportBtn.setMaxWidth(Double.MAX_VALUE);
        exportBtn.setOnAction(e -> showDemoAlert("Export", "In a full implementation, this would export the notebook content."));
        
        tools.getChildren().addAll(toolsLabel, createTableBtn, createGraphBtn, exportBtn);
        return tools;
    }
    
    private void showDemoAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
