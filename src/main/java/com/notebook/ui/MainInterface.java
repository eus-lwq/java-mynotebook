package com.notebook.ui;

import com.notebook.dto.NotebookDto;
import com.notebook.dto.PageDto;
import com.notebook.dto.TableDto;
import com.notebook.dto.ImageDto;
import com.notebook.service.NotebookService;
import com.notebook.service.PageService;
import com.notebook.service.TableService;
import com.notebook.service.ImageService;

import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.geometry.Insets;
import javafx.util.Pair;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import java.io.ByteArrayInputStream;
import javafx.stage.FileChooser;
import javafx.stage.DirectoryChooser;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class MainInterface extends BorderPane {
    private final NotebookUI application;
    private final NotebookService notebookService;
    private final PageService pageService;
    private final TableService tableService;
    private final ImageService imageService;
    
    private final TreeView<Pair<String, Long>> notebookTree;
    private final TabPane editorTabs;
    private final VBox toolPanel;
    
    private final Map<Long, NotebookDto> notebooksMap = new HashMap<>();
    private final Map<Long, PageDto> pagesMap = new HashMap<>();
    private final Map<Tab, Long> tabToPageIdMap = new HashMap<>();

    public MainInterface(NotebookUI application, NotebookService notebookService, PageService pageService, TableService tableService, ImageService imageService) {
        this.application = application;
        this.notebookService = notebookService;
        this.pageService = pageService;
        this.tableService = tableService;
        this.imageService = imageService;
        
        // Initialize components
        notebookTree = createNotebookTree();
        editorTabs = createEditorTabs();
        toolPanel = createToolPanel();

        // Set up layout
        setLeft(createSidebar());
        setCenter(editorTabs);
        setRight(toolPanel);
        
        setPadding(new Insets(10));
        
        // Load user's notebooks
        loadNotebooks();
    }

    private TreeView<Pair<String, Long>> createNotebookTree() {
        TreeItem<Pair<String, Long>> root = new TreeItem<>(new Pair<>("My Notebooks", 0L));
        root.setExpanded(true);
        
        TreeView<Pair<String, Long>> tree = new TreeView<>(root);
        tree.setPrefWidth(200);
        tree.setCellFactory(tv -> new TreeCell<Pair<String, Long>>() {
            @Override
            protected void updateItem(Pair<String, Long> item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.getKey());
                }
            }
        });
        
        tree.getSelectionModel().selectedItemProperty().addListener(
            (observable, oldValue, newValue) -> handleNotebookSelection(newValue)
        );
        
        return tree;
    }

    private TabPane createEditorTabs() {
        TabPane tabPane = new TabPane();
        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);
        return tabPane;
    }

    private VBox createToolPanel() {
        VBox tools = new VBox(10);
        tools.setPadding(new Insets(10));
        tools.setPrefWidth(200);

        Button createTableButton = new Button("Create Table");
        createTableButton.setMaxWidth(Double.MAX_VALUE);
        createTableButton.setOnAction(e -> handleCreateTable());
        
        Button createGraphButton = new Button("Create Graph");
        createGraphButton.setMaxWidth(Double.MAX_VALUE);
        createGraphButton.setOnAction(e -> handleCreateGraph());
        
        Button insertImageButton = new Button("Insert Image");
        insertImageButton.setMaxWidth(Double.MAX_VALUE);
        insertImageButton.setOnAction(e -> handleInsertImage());
        
        Button exportButton = new Button("Export");
        exportButton.setMaxWidth(Double.MAX_VALUE);
        exportButton.setOnAction(e -> handleExport());
        
        tools.getChildren().addAll(
            new Label("Tools"),
            createTableButton,
            createGraphButton,
            insertImageButton,
            exportButton
        );

        return tools;
    }

    private VBox createSidebar() {
        VBox sidebar = new VBox(10);
        sidebar.setPadding(new Insets(10));

        Button newNotebookBtn = new Button("New Notebook");
        newNotebookBtn.setMaxWidth(Double.MAX_VALUE);
        newNotebookBtn.setOnAction(e -> handleNewNotebook());

        Button newPageBtn = new Button("New Page");
        newPageBtn.setMaxWidth(Double.MAX_VALUE);
        newPageBtn.setOnAction(e -> handleNewPage());

        sidebar.getChildren().addAll(
            newNotebookBtn,
            newPageBtn,
            notebookTree
        );

        return sidebar;
    }

    private void handleNotebookSelection(TreeItem<Pair<String, Long>> item) {
        if (item != null && item.getValue() != null) {
            Long id = item.getValue().getValue();
            if (id > 0) {
                // If it's a page (leaf node with valid ID)
                if (item.isLeaf() && pagesMap.containsKey(id)) {
                    openPage(pagesMap.get(id));
                }
            }
        }
    }

    private void openPage(PageDto page) {
        // Debug information
        System.out.println("Opening page: " + page.getId() + " - " + page.getTitle());
        System.out.println("Page content: " + page.getContent());
        
        // Get the latest page data
        final Long pageId = page.getId();
        PageDto finalPage = page; // Default to original page
        try {
            PageDto refreshedPage = pageService.getPage(pageId);
            finalPage = refreshedPage; // Only assign if successful
            System.out.println("Successfully refreshed page data");
        } catch (Exception e) {
            System.out.println("Error refreshing page data: " + e.getMessage());
            // Keep using the original page if refresh fails
        }
        
        // Debug table information
        if (finalPage.getTables() != null) {
            System.out.println("Tables count: " + finalPage.getTables().size());
            for (TableDto table : finalPage.getTables()) {
                System.out.println("Table ID: " + table.getId());
                String[][] data = table.getData();
                if (data != null) {
                    System.out.println("Table data dimensions: " + data.length + "x" + (data.length > 0 ? data[0].length : 0));
                } else {
                    System.out.println("Table data is null");
                }
            }
        } else {
            System.out.println("Tables collection is null");
        }
        
        // Check if page is already open
        for (Tab tab : editorTabs.getTabs()) {
            if (tabToPageIdMap.containsKey(tab) && tabToPageIdMap.get(tab).equals(pageId)) {
                editorTabs.getSelectionModel().select(tab);
                return;
            }
        }
        
        // Create new tab for page
        final Tab tab = new Tab(finalPage.getTitle());
        
        // Create a scrollable container for the page content
        final ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        
        // Main content container
        final VBox pageContent = new VBox(10);
        pageContent.setPadding(new Insets(10));
        
        // Add save button and text editor
        final Button saveButton = new Button("Save");
        final TextArea editor = new TextArea(finalPage.getContent());
        editor.setWrapText(true);
        saveButton.setOnAction(e -> savePage(pageId, editor.getText()));
        
        // Add the editor and save button to the page content
        pageContent.getChildren().addAll(saveButton, editor);
        
        // Process the content to find table and image markers
        final String content = finalPage.getContent();
        if (content != null && !content.isEmpty()) {
            String[] lines = content.split("\\n");
            
            for (String line : lines) {
                // Process table markers
                if (line.matches("\\[TABLE_\\d+\\]")) {
                    try {
                        // Extract table ID
                        final Long tableId = Long.parseLong(line.substring(7, line.length() - 1));
                        System.out.println("Found table marker for ID: " + tableId);
                        
                        // Get the table from the service
                        final TableDto tableDto = tableService.getTable(tableId);
                        
                        if (tableDto != null) {
                            System.out.println("Retrieved table with ID: " + tableDto.getId());
                            
                            // Create a grid pane for the table
                            final GridPane tableGrid = new GridPane();
                            tableGrid.setHgap(5);
                            tableGrid.setVgap(5);
                            tableGrid.setPadding(new Insets(10));
                            tableGrid.setStyle("-fx-border-color: #cccccc; -fx-background-color: #f5f5f5;");
                            
                            // Get table data
                            final String[][] data = tableDto.getData();
                            if (data != null && data.length > 0) {
                                for (int row = 0; row < data.length; row++) {
                                    for (int col = 0; col < data[row].length; col++) {
                                        final TextField cell = new TextField(data[row][col]);
                                        cell.setPrefWidth(100);
                                        tableGrid.add(cell, col, row);
                                    }
                                }
                            }
                            
                            // Add the table to the page content
                            final VBox tableContainer = new VBox(5);
                            final Label tableLabel = new Label("Table " + tableDto.getId());
                            tableContainer.getChildren().addAll(tableLabel, tableGrid);
                            pageContent.getChildren().add(tableContainer);
                        }
                    } catch (Exception e) {
                        System.out.println("Error rendering table: " + e.getMessage());
                        e.printStackTrace();
                        // Add a placeholder for the table
                        final Label errorLabel = new Label("Error loading table: " + line);
                        errorLabel.setStyle("-fx-text-fill: red;");
                        pageContent.getChildren().add(errorLabel);
                    }
                }
                
                // Process image markers
                else if (line.matches("\\[IMAGE_\\d+\\]")) {
                    try {
                        // Extract image ID
                        final Long imageId = Long.parseLong(line.substring(7, line.length() - 1));
                        System.out.println("Found image marker for ID: " + imageId);
                        
                        // Get the image from the service
                        final ImageDto imageDto = imageService.getImage(imageId);
                        
                        if (imageDto != null && imageDto.getImageData() != null) {
                            System.out.println("Retrieved image with ID: " + imageDto.getId() + ", size: " + imageDto.getImageData().length + " bytes");
                            
                            // Create an image view
                            final Image image = new Image(new ByteArrayInputStream(imageDto.getImageData()));
                            final ImageView imageView = new ImageView(image);
                            imageView.setFitWidth(400); // Set a reasonable default width
                            imageView.setPreserveRatio(true);
                            
                            // Add the image to the page content
                            final VBox imageContainer = new VBox(5);
                            final Label imageLabel = new Label("Image: " + imageDto.getFileName());
                            imageContainer.getChildren().addAll(imageLabel, imageView);
                            pageContent.getChildren().add(imageContainer);
                        }
                    } catch (Exception e) {
                        System.out.println("Error rendering image: " + e.getMessage());
                        e.printStackTrace();
                        // Add a placeholder for the image
                        final Label errorLabel = new Label("Error loading image: " + line);
                        errorLabel.setStyle("-fx-text-fill: red;");
                        pageContent.getChildren().add(errorLabel);
                    }
                }
            }
        }
        
        // Set the content of the scroll pane
        scrollPane.setContent(pageContent);
        tab.setContent(scrollPane);
        tab.setOnClosed(e -> tabToPageIdMap.remove(tab));
        
        // Add the tab to the tab pane
        tabToPageIdMap.put(tab, pageId);
        editorTabs.getTabs().add(tab);
        editorTabs.getSelectionModel().select(tab);
        
        // Store the page in the pages map
        pagesMap.put(pageId, finalPage);
    }

    private void handleNewNotebook() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Notebook");
        dialog.setHeaderText("Create a new notebook");
        dialog.setContentText("Enter notebook name:");
        
        Optional<String> result = dialog.showAndWait();
        result.ifPresent(notebookName -> {
            if (!notebookName.isEmpty()) {
                try {
                    NotebookDto newNotebook = notebookService.createNotebook(notebookName);
                    notebooksMap.put(newNotebook.getId(), newNotebook);
                    
                    TreeItem<Pair<String, Long>> notebookItem = new TreeItem<>(new Pair<>(newNotebook.getTitle(), newNotebook.getId()));
                    notebookTree.getRoot().getChildren().add(notebookItem);
                    
                    // Select the new notebook
                    notebookTree.getSelectionModel().select(notebookItem);
                    
                    // Show success message
                    showInfo("Notebook '" + notebookName + "' created successfully!");
                } catch (Exception e) {
                    e.printStackTrace();
                    
                    // Check if it's an authentication error
                    if (e.getMessage() != null && (e.getMessage().contains("Authentication") || 
                                                 e.getMessage().contains("authorized") || 
                                                 e.getMessage().contains("token"))) {
                        showError("Authentication error. The application will restart so you can log in again.");
                        
                        // Return to login screen
                        application.showLoginScreen();
                    } else {
                        showError("Error creating notebook: " + e.getMessage());
                    }
                }
            }
        });
    }

    private void handleNewPage() {
        // Get selected notebook
        TreeItem<Pair<String, Long>> selectedItem = notebookTree.getSelectionModel().getSelectedItem();
        if (selectedItem == null || selectedItem.getValue() == null) {
            showError("Please select a notebook first");
            return;
        }
        
        // Find the notebook item (might be a page that's selected)
        TreeItem<Pair<String, Long>> notebookItem = selectedItem;
        while (notebookItem != null && notebookItem.getParent() != notebookTree.getRoot()) {
            notebookItem = notebookItem.getParent();
        }
        
        if (notebookItem == null || notebookItem.getValue() == null || !notebooksMap.containsKey(notebookItem.getValue().getValue())) {
            showError("Please select a valid notebook");
            return;
        }
        
        Long notebookId = notebookItem.getValue().getValue();
        
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("New Page");
        dialog.setHeaderText("Create a new page");
        dialog.setContentText("Enter page title:");
        
        Optional<String> result = dialog.showAndWait();
        final TreeItem<Pair<String, Long>> finalNotebookItem = notebookItem;
        result.ifPresent(title -> {
            if (!title.trim().isEmpty()) {
                try {
                    PageDto newPage = pageService.createPage(notebookId, title, "");
                    if (newPage != null) {
                        loadPages(notebookId, finalNotebookItem); // Refresh the pages
                        // Open the new page
                        openPage(newPage);
                    } else {
                        showError("Failed to create page");
                    }
                } catch (Exception e) {
                    showError("Error creating page: " + e.getMessage());
                }
            }
        });
    }

    private void handleCreateTable() {
        Tab selectedTab = editorTabs.getSelectionModel().getSelectedItem();
        if (selectedTab == null || !tabToPageIdMap.containsKey(selectedTab)) {
            showError("Please open a page first");
            return;
        }
        
        Long pageId = tabToPageIdMap.get(selectedTab);
        
        // Simple dialog for table dimensions
        Dialog<Pair<Integer, Integer>> dialog = new Dialog<>();
        dialog.setTitle("Create Table");
        dialog.setHeaderText("Enter table dimensions");
        
        // Set the button types
        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);
        
        // Create the dimensions inputs
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        TextField rowsField = new TextField();
        rowsField.setPromptText("Rows");
        TextField colsField = new TextField();
        colsField.setPromptText("Columns");
        
        grid.add(new Label("Rows:"), 0, 0);
        grid.add(rowsField, 1, 0);
        grid.add(new Label("Columns:"), 0, 1);
        grid.add(colsField, 1, 1);
        
        dialog.getDialogPane().setContent(grid);
        
        // Convert the result when the create button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                try {
                    int rows = Integer.parseInt(rowsField.getText());
                    int cols = Integer.parseInt(colsField.getText());
                    return new Pair<>(rows, cols);
                } catch (NumberFormatException e) {
                    return null;
                }
            }
            return null;
        });
        
        Optional<Pair<Integer, Integer>> result = dialog.showAndWait();
        
        result.ifPresent(dimensions -> {
            int rows = dimensions.getKey();
            int cols = dimensions.getValue();
            
            if (rows > 0 && cols > 0) {
                // Create empty data
                String[][] data = new String[rows][cols];
                for (int i = 0; i < rows; i++) {
                    for (int j = 0; j < cols; j++) {
                        data[i][j] = "";
                    }
                }
                
                // Call service to create table
                try {
                    TableDto newTable = tableService.createTable(pageId, data);
                    System.out.println("Created table with ID: " + newTable.getId());
                    
                    // Get the current content
                    PageDto page = pagesMap.get(pageId);
                    String content = page.getContent();
                    
                    // Add table marker to the content
                    String tableMarker = "\n[TABLE_" + newTable.getId() + "]\n";
                    content += tableMarker;
                    
                    // Update the page content
                    pageService.updatePage(pageId, page.getTitle(), content);
                    
                    // Add the table directly to the UI
                    if (selectedTab.getContent() instanceof ScrollPane) {
                        ScrollPane scrollPane = (ScrollPane) selectedTab.getContent();
                        if (scrollPane.getContent() instanceof VBox) {
                            VBox pageContent = (VBox) scrollPane.getContent();
                            
                            // Create a grid pane for the table
                            GridPane tableGrid = new GridPane();
                            tableGrid.setHgap(5);
                            tableGrid.setVgap(5);
                            tableGrid.setPadding(new Insets(10));
                            tableGrid.setStyle("-fx-border-color: #cccccc; -fx-background-color: #f5f5f5;");
                            
                            // Populate the table grid
                            for (int row = 0; row < data.length; row++) {
                                for (int col = 0; col < data[row].length; col++) {
                                    TextField cell = new TextField(data[row][col]);
                                    cell.setPrefWidth(100);
                                    tableGrid.add(cell, col, row);
                                }
                            }
                            
                            // Add the table to the page content
                            VBox tableContainer = new VBox(5);
                            Label tableLabel = new Label("Table " + newTable.getId());
                            tableContainer.getChildren().addAll(tableLabel, tableGrid);
                            pageContent.getChildren().add(tableContainer);
                        } else {
                            // If the content structure is unexpected, refresh the page
                            openPage(pageService.getPage(pageId));
                        }
                    } else {
                        // If the content structure is unexpected, refresh the page
                        openPage(pageService.getPage(pageId));
                    }
                    
                    showInfo("Table created successfully!");
                } catch (Exception e) {
                    showError("Error creating table: " + e.getMessage());
                    e.printStackTrace();
                }
            }
        });
    }

    private void handleCreateGraph() {
        Tab selectedTab = editorTabs.getSelectionModel().getSelectedItem();
        if (selectedTab == null || !tabToPageIdMap.containsKey(selectedTab)) {
            showError("Please open a page first");
            return;
        }
        
        Long pageId = tabToPageIdMap.get(selectedTab);
        
        // Simple dialog for graph type
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("Create Graph");
        dialog.setHeaderText("Select graph type");
        
        // Set the button types
        ButtonType createButtonType = new ButtonType("Create", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(createButtonType, ButtonType.CANCEL);
        
        // Create the type chooser
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));
        
        ComboBox<String> typeComboBox = new ComboBox<>();
        typeComboBox.getItems().addAll("BAR", "LINE", "PIE");
        typeComboBox.setValue("BAR");
        
        grid.add(new Label("Graph Type:"), 0, 0);
        grid.add(typeComboBox, 1, 0);
        
        dialog.getDialogPane().setContent(grid);
        
        // Convert the result when the create button is clicked
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == createButtonType) {
                return typeComboBox.getValue();
            }
            return null;
        });
        
        Optional<String> result = dialog.showAndWait();
        
        result.ifPresent(type -> {
            // Call service to create graph
            try {
                // This would need to be implemented in your GraphService
                // First would need to select a table
                // GraphDto newGraph = graphService.createGraph(pageId, type, tableId, config);
                showInfo("Graph creation would be implemented here");
                // In a real implementation, you would refresh the page content
            } catch (Exception e) {
                showError("Error creating graph: " + e.getMessage());
            }
        });
    }

    private void handleInsertImage() {
        Tab selectedTab = editorTabs.getSelectionModel().getSelectedItem();
        if (selectedTab == null || !tabToPageIdMap.containsKey(selectedTab)) {
            showError("Please open a page first");
            return;
        }
        
        final Long pageId = tabToPageIdMap.get(selectedTab);
        
        // Create a file chooser for images
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Select Image");
        fileChooser.getExtensionFilters().addAll(
            new FileChooser.ExtensionFilter("Image Files", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        
        // Show open file dialog
        File selectedFile = fileChooser.showOpenDialog(getScene().getWindow());
        
        if (selectedFile != null) {
            try {
                // Read the image file
                byte[] imageData = Files.readAllBytes(selectedFile.toPath());
                String fileName = selectedFile.getName();
                String contentType = Files.probeContentType(selectedFile.toPath());
                
                // Save the image
                ImageDto newImage = imageService.saveImage(pageId, imageData, fileName, contentType);
                System.out.println("Created image with ID: " + newImage.getId());
                
                // Get the current content
                PageDto page = pagesMap.get(pageId);
                String content = page.getContent();
                
                // Add image marker to the content
                String imageMarker = "\n[IMAGE_" + newImage.getId() + "]\n";
                content += imageMarker;
                
                // Update the page content
                pageService.updatePage(pageId, page.getTitle(), content);
                
                // Add the image directly to the UI
                if (selectedTab.getContent() instanceof ScrollPane) {
                    ScrollPane scrollPane = (ScrollPane) selectedTab.getContent();
                    if (scrollPane.getContent() instanceof VBox) {
                        VBox pageContent = (VBox) scrollPane.getContent();
                        
                        // Create an image view
                        ImageView imageView = new ImageView(new Image(new ByteArrayInputStream(imageData)));
                        imageView.setFitWidth(400); // Set a reasonable default width
                        imageView.setPreserveRatio(true);
                        
                        // Add the image to the page content
                        VBox imageContainer = new VBox(5);
                        Label imageLabel = new Label("Image: " + fileName);
                        imageContainer.getChildren().addAll(imageLabel, imageView);
                        pageContent.getChildren().add(imageContainer);
                    } else {
                        // If the content structure is unexpected, refresh the page
                        openPage(pageService.getPage(pageId));
                    }
                } else {
                    // If the content structure is unexpected, refresh the page
                    openPage(pageService.getPage(pageId));
                }
                
                showInfo("Image inserted successfully!");
            } catch (IOException e) {
                showError("Error reading image file: " + e.getMessage());
                e.printStackTrace();
            } catch (Exception e) {
                showError("Error inserting image: " + e.getMessage());
                e.printStackTrace();
            }
        }
    }

    private void handleExport() {
        Tab selectedTab = editorTabs.getSelectionModel().getSelectedItem();
        if (selectedTab == null || !tabToPageIdMap.containsKey(selectedTab)) {
            showError("Please open a page first");
            return;
        }
        
        Long pageId = tabToPageIdMap.get(selectedTab);
        PageDto page = pagesMap.get(pageId);
        
        if (page == null) {
            showError("Page data not found");
            return;
        }
        
        // Create a directory chooser
        DirectoryChooser directoryChooser = new DirectoryChooser();
        directoryChooser.setTitle("Select Export Directory");
        
        // Show directory dialog
        File selectedDirectory = directoryChooser.showDialog(getScene().getWindow());
        
        if (selectedDirectory != null) {
            try {
                // Create export filename based on page title
                String sanitizedTitle = page.getTitle().replaceAll("[^a-zA-Z0-9.-]", "_");
                String exportPath = selectedDirectory.getAbsolutePath() + File.separator + sanitizedTitle + ".html";
                
                // Generate HTML content
                StringBuilder html = new StringBuilder();
                html.append("<!DOCTYPE html>\n");
                html.append("<html>\n");
                html.append("<head>\n");
                html.append("  <title>").append(page.getTitle()).append("</title>\n");
                html.append("  <style>\n");
                html.append("    body { font-family: Arial, sans-serif; margin: 20px; }\n");
                html.append("    h1 { color: #333; }\n");
                html.append("    .content { line-height: 1.6; }\n");
                html.append("    .table-placeholder { background-color: #f0f0f0; padding: 10px; margin: 10px 0; border-radius: 5px; }\n");
                html.append("    .image-placeholder { background-color: #e0e0e0; padding: 10px; margin: 10px 0; border-radius: 5px; }\n");
                html.append("  </style>\n");
                html.append("</head>\n");
                html.append("<body>\n");
                html.append("  <h1>").append(page.getTitle()).append("</h1>\n");
                html.append("  <div class=\"content\">\n");
                
                // Process content to handle table and image markers
                String content = page.getContent();
                String[] lines = content.split("\\n");
                
                for (String line : lines) {
                    if (line.matches("\\[TABLE_\\d+\\]")) {
                        // Extract table ID
                        String tableIdStr = line.substring(7, line.length() - 1);
                        html.append("    <div class=\"table-placeholder\">Table " + tableIdStr + " would be rendered here</div>\n");
                    } else if (line.matches("\\[IMAGE_\\d+\\]")) {
                        // Extract image ID
                        String imageIdStr = line.substring(7, line.length() - 1);
                        html.append("    <div class=\"image-placeholder\">Image " + imageIdStr + " would be rendered here</div>\n");
                    } else {
                        html.append("    ").append(line).append("<br>\n");
                    }
                }
                
                html.append("  </div>\n");
                html.append("</body>\n");
                html.append("</html>");
                
                // Write to file
                try (FileOutputStream fos = new FileOutputStream(exportPath)) {
                    fos.write(html.toString().getBytes());
                }
                
                showInfo("Page exported successfully to: " + exportPath);
            } catch (Exception e) {
                showError("Error exporting page: " + e.getMessage());
            }
        }
    }
    
    private void loadNotebooks() {
        try {
            // Initialize the tree view with the root node
            notebookTree.setRoot(new TreeItem<>(new Pair<>("My Notebooks", null)));
            notebookTree.getRoot().setExpanded(true);
            
            try {
                // Try to load notebooks
                List<NotebookDto> notebooks = notebookService.getAllNotebooks();
                
                // Add notebooks to the tree
                for (NotebookDto notebook : notebooks) {
                    notebooksMap.put(notebook.getId(), notebook);
                    
                    TreeItem<Pair<String, Long>> notebookItem = new TreeItem<>(new Pair<>(notebook.getTitle(), notebook.getId()));
                    notebookTree.getRoot().getChildren().add(notebookItem);
                    
                    // Load pages for this notebook
                    loadPages(notebook.getId(), notebookItem);
                }
            } catch (Exception e) {
                e.printStackTrace(); // Log the full stack trace
                
                // Instead of showing an error, just log it and continue with an empty notebook tree
                System.out.println("Note: No notebooks found or error loading notebooks. User can create new ones.");
                
                // Don't show error dialog as this might be a first-time user with no notebooks yet
                // Just continue with an empty notebook tree
            }
        } catch (Exception e) {
            e.printStackTrace();
            showError("Error initializing the application: " + e.getMessage());
        }
    }
    
    private void loadPages(Long notebookId, TreeItem<Pair<String, Long>> notebookItem) {
        try {
            // Clear existing pages
            notebookItem.getChildren().clear();
            
            // Load pages from service
            List<PageDto> pages = pageService.getPagesInNotebook(notebookId);
            
            for (PageDto page : pages) {
                pagesMap.put(page.getId(), page);
                TreeItem<Pair<String, Long>> pageItem = 
                    new TreeItem<>(new Pair<>(page.getTitle(), page.getId()));
                notebookItem.getChildren().add(pageItem);
            }
            
            // Expand the notebook item
            notebookItem.setExpanded(true);
        } catch (Exception e) {
            showError("Error loading pages: " + e.getMessage());
        }
    }
    
    private void savePage(Long pageId, String content) {
        try {
            PageDto page = pagesMap.get(pageId);
            if (page != null) {
                // Update the page content
                PageDto updatedPage = pageService.updatePage(pageId, page.getTitle(), content);
                if (updatedPage != null) {
                    // Update the cached page
                    pagesMap.put(pageId, updatedPage);
                    showInfo("Page saved successfully");
                } else {
                    showError("Failed to save page");
                }
            }
        } catch (Exception e) {
            showError("Error saving page: " + e.getMessage());
        }
    }
    
    private void showError(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
    
    private void showInfo(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Information");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
