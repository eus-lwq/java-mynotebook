package com.notebook;

import com.notebook.ui.NotebookUI;
import javafx.application.Application;
import javafx.stage.Stage;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication
public class NotebookApplication extends Application {
    private static ConfigurableApplicationContext springContext;

    public static ConfigurableApplicationContext getSpringContext() {
        return springContext;
    }

    @Override
    public void init() {
        try {
            springContext = org.springframework.boot.SpringApplication.run(NotebookApplication.class);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void start(Stage primaryStage) {
        try {
            NotebookUI ui = springContext.getBean(NotebookUI.class);
            ui.start(primaryStage);
        } catch (Exception e) {
            e.printStackTrace();
            System.exit(1);
        }
    }

    @Override
    public void stop() {
        springContext.close();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
