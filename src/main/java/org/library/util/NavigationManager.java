package org.library.util;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.library.controller.MainLayoutController;

import java.io.IOException;

/**
 * Manages stage scenes and center view navigation inside the main layout.
 */
public class NavigationManager {
    private static Stage primaryStage;
    private static MainLayoutController mainLayoutController;

    public static void setPrimaryStage(Stage stage) {
        primaryStage = stage;
    }

    public static Stage getPrimaryStage() {
        return primaryStage;
    }

    public static void setMainLayoutController(MainLayoutController controller) {
        mainLayoutController = controller;
    }

    public static MainLayoutController getMainLayoutController() {
        return mainLayoutController;
    }

    public static void navigateToLogin() {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationManager.class.getResource("/org/library/view/Login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root, 980, 660);
            primaryStage.setTitle("Library Management System - Sign In");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(900);
            primaryStage.setMinHeight(600);
            primaryStage.centerOnScreen();
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Navigation Error", "Could not load Login view", e.getMessage());
        }
    }

    public static void navigateToMainLayout() {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationManager.class.getResource("/org/library/view/MainLayout.fxml"));
            Parent root = loader.load();
            mainLayoutController = loader.getController();
            Scene scene = new Scene(root, 1200, 780);
            primaryStage.setTitle("Library Management System");
            primaryStage.setScene(scene);
            primaryStage.setMinWidth(1080);
            primaryStage.setMinHeight(700);
            primaryStage.centerOnScreen();
            primaryStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("Navigation Error", "Could not load Main Dashboard layout", e.getMessage());
        }
    }

    public static void navigateTo(String viewName, String title) {
        if (mainLayoutController != null) {
            mainLayoutController.switchView(viewName, title);
        }
    }
}
