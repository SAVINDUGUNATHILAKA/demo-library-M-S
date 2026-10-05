package org.library;

import javafx.application.Application;
import javafx.stage.Stage;
import org.library.util.NavigationManager;

/**
 * Main application launcher for the Library Management System.
 */
public class App extends Application {

    @Override
    public void start(Stage primaryStage) {
        NavigationManager.setPrimaryStage(primaryStage);
        NavigationManager.navigateToLogin();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
