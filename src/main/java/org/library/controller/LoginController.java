package org.library.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import org.library.util.AlertUtil;
import org.library.util.NavigationManager;

public class LoginController {

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    @FXML
    private Button btnLogin;

    @FXML
    private Button btnClear;

    @FXML
    private Label lblError;

    @FXML
    public void initialize() {
        lblError.setText("");
        // Allow pressing Enter key to trigger login
        txtPassword.setOnAction(this::handleLogin);
        txtUsername.setOnAction(e -> txtPassword.requestFocus());
    }

    @FXML
    private void handleLogin(ActionEvent event) {
        String username = txtUsername.getText() != null ? txtUsername.getText().trim() : "";
        String password = txtPassword.getText() != null ? txtPassword.getText().trim() : "";

        // Validation: Empty fields
        if (username.isEmpty() || password.isEmpty()) {
            lblError.setText("Please enter both username and password.");
            AlertUtil.showWarning("Login Validation", "Missing Credentials", "Username and password cannot be blank.");
            return;
        }

        // Authentication check (accepts admin/admin123 or librarian/librarian)
        if ((username.equalsIgnoreCase("admin") && password.equals("admin123"))
            || (username.equalsIgnoreCase("librarian") && password.equals("librarian"))) {
            lblError.setText("");
            NavigationManager.navigateToMainLayout();
        } else {
            lblError.setText("Invalid username or password. Please try again.");
            AlertUtil.showError("Authentication Failed", "Invalid Credentials", "The username or password you entered is incorrect.\nHint: Use admin / admin123");
        }
    }

    @FXML
    private void handleClear(ActionEvent event) {
        txtUsername.clear();
        txtPassword.clear();
        lblError.setText("");
        txtUsername.requestFocus();
    }
}
