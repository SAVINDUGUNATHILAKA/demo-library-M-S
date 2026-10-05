package org.library.controller;

import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;
import org.library.util.AlertUtil;
import org.library.util.NavigationManager;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

public class MainLayoutController {

    @FXML
    private StackPane contentArea;

    @FXML
    private Label lblHeaderTitle;

    @FXML
    private Label lblHeaderSubtitle;

    @FXML
    private Label lblCurrentDateTime;

    @FXML
    private Button btnNavDashboard;

    @FXML
    private Button btnNavAddBook;

    @FXML
    private Button btnNavAddMember;

    @FXML
    private Button btnNavManageMembers;

    @FXML
    private Button btnNavIssueBook;

    @FXML
    private Button btnNavReturnBook;

    @FXML
    private Button btnNavHistory;

    @FXML
    private Button btnNavLogout;

    private final Map<String, Button> navButtonMap = new HashMap<>();

    @FXML
    public void initialize() {
        NavigationManager.setMainLayoutController(this);

        // Map FXML names to their corresponding nav buttons for active highlight
        navButtonMap.put("DashboardHome.fxml", btnNavDashboard);
        navButtonMap.put("AddBook.fxml", btnNavAddBook);
        navButtonMap.put("AddMember.fxml", btnNavAddMember);
        navButtonMap.put("ManageMembers.fxml", btnNavManageMembers);
        navButtonMap.put("IssueBook.fxml", btnNavIssueBook);
        navButtonMap.put("ReturnBook.fxml", btnNavReturnBook);
        navButtonMap.put("BorrowingHistory.fxml", btnNavHistory);

        // Start live clock in header
        DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("EEE, dd MMM yyyy • hh:mm:ss a");
        Timeline clock = new Timeline(new KeyFrame(Duration.ZERO, e ->
            lblCurrentDateTime.setText(LocalDateTime.now().format(timeFormatter))),
            new KeyFrame(Duration.seconds(1))
        );
        clock.setCycleCount(Animation.INDEFINITE);
        clock.play();

        // Load default view (Dashboard Home)
        switchView("DashboardHome.fxml", "Dashboard Overview");
    }

    public void switchView(String fxmlFileName, String title) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/org/library/view/" + fxmlFileName));
            Parent view = loader.load();

            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);

            lblHeaderTitle.setText(title);
            updateHeaderSubtitle(fxmlFileName);
            updateActiveNavButton(fxmlFileName);
        } catch (IOException e) {
            e.printStackTrace();
            AlertUtil.showError("View Error", "Could not load view: " + fxmlFileName, e.getMessage());
        }
    }

    private void updateHeaderSubtitle(String fxmlFileName) {
        switch (fxmlFileName) {
            case "DashboardHome.fxml":
                lblHeaderSubtitle.setText("Welcome back! Here is a summary of the library circulation and status.");
                break;
            case "AddBook.fxml":
                lblHeaderSubtitle.setText("Register new book titles, categories, and inventory counts.");
                break;
            case "AddMember.fxml":
                lblHeaderSubtitle.setText("Register a new member to allow book loans.");
                break;
            case "ManageMembers.fxml":
                lblHeaderSubtitle.setText("Search, view, update, or remove registered library patrons.");
                break;
            case "IssueBook.fxml":
                lblHeaderSubtitle.setText("Assign an available book copy to a registered library member.");
                break;
            case "ReturnBook.fxml":
                lblHeaderSubtitle.setText("Process returned books, check return deadlines, and calculate overdue fines.");
                break;
            case "BorrowingHistory.fxml":
                lblHeaderSubtitle.setText("Comprehensive log of all book borrowings, returns, and overdue statuses.");
                break;
            default:
                lblHeaderSubtitle.setText("Library Management System");
        }
    }

    private void updateActiveNavButton(String fxmlFileName) {
        for (Button btn : navButtonMap.values()) {
            btn.getStyleClass().remove("nav-button-active");
            if (!btn.getStyleClass().contains("nav-button")) {
                btn.getStyleClass().add("nav-button");
            }
        }
        Button activeBtn = navButtonMap.get(fxmlFileName);
        if (activeBtn != null) {
            activeBtn.getStyleClass().add("nav-button-active");
        }
    }

    @FXML
    private void onNavDashboard(ActionEvent event) {
        switchView("DashboardHome.fxml", "Dashboard Overview");
    }

    @FXML
    private void onNavAddBook(ActionEvent event) {
        switchView("AddBook.fxml", "Add New Book");
    }

    @FXML
    private void onNavAddMember(ActionEvent event) {
        switchView("AddMember.fxml", "Register New Member");
    }

    @FXML
    private void onNavManageMembers(ActionEvent event) {
        switchView("ManageMembers.fxml", "Manage Library Members");
    }

    @FXML
    private void onNavIssueBook(ActionEvent event) {
        switchView("IssueBook.fxml", "Issue Book to Member");
    }

    @FXML
    private void onNavReturnBook(ActionEvent event) {
        switchView("ReturnBook.fxml", "Return Book & Fine Calculation");
    }

    @FXML
    private void onNavHistory(ActionEvent event) {
        switchView("BorrowingHistory.fxml", "Borrowing Records & History");
    }

    @FXML
    private void onNavLogout(ActionEvent event) {
        boolean confirm = AlertUtil.showConfirmation(
            "Confirm Logout",
            "Are you sure you want to log out?",
            "You will be returned to the sign-in screen."
        );
        if (confirm) {
            NavigationManager.navigateToLogin();
        }
    }
}
