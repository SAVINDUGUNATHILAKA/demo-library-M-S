package org.library.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import org.library.model.BorrowRecord;
import org.library.service.LibraryDataService;
import org.library.util.NavigationManager;

import java.time.LocalDate;

public class DashboardHomeController {

    @FXML
    private Label lblTotalBooks;

    @FXML
    private Label lblTotalMembers;

    @FXML
    private Label lblBorrowedBooks;

    @FXML
    private Label lblOverdueBooks;

    @FXML
    private TableView<BorrowRecord> tblRecentActivity;

    @FXML
    private TableColumn<BorrowRecord, String> colRecordId;

    @FXML
    private TableColumn<BorrowRecord, String> colMemberName;

    @FXML
    private TableColumn<BorrowRecord, String> colBookTitle;

    @FXML
    private TableColumn<BorrowRecord, LocalDate> colIssueDate;

    @FXML
    private TableColumn<BorrowRecord, LocalDate> colDueDate;

    @FXML
    private TableColumn<BorrowRecord, String> colStatus;

    private final LibraryDataService dataService = LibraryDataService.getInstance();

    @FXML
    public void initialize() {
        setupTableColumns();
        loadDashboardData();
    }

    private void setupTableColumns() {
        colRecordId.setCellValueFactory(new PropertyValueFactory<>("recordId"));
        colMemberName.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        colBookTitle.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        colIssueDate.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        colDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Custom cell styling for Status column badges
        colStatus.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setText(null);
                    setGraphic(null);
                    setStyle("");
                } else {
                    Label badge = new Label(status);
                    badge.getStyleClass().add("badge");
                    if ("Overdue".equalsIgnoreCase(status)) {
                        badge.getStyleClass().add("badge-overdue");
                    } else if ("Returned".equalsIgnoreCase(status)) {
                        badge.getStyleClass().add("badge-returned");
                    } else {
                        badge.getStyleClass().add("badge-borrowed");
                    }
                    setGraphic(badge);
                    setText(null);
                }
            }
        });
    }

    public void loadDashboardData() {
        lblTotalBooks.setText(String.valueOf(dataService.getTotalBooksCount()));
        lblTotalMembers.setText(String.valueOf(dataService.getTotalMembersCount()));
        lblBorrowedBooks.setText(String.valueOf(dataService.getCurrentlyBorrowedCount()));
        lblOverdueBooks.setText(String.valueOf(dataService.getOverdueCount()));

        // Display recent 5 borrowing records
        ObservableList<BorrowRecord> allRecords = dataService.getBorrowRecords();
        int limit = Math.min(5, allRecords.size());
        tblRecentActivity.setItems(FXCollections.observableArrayList(allRecords.subList(0, limit)));
    }

    @FXML
    private void onQuickIssueBook(ActionEvent event) {
        NavigationManager.navigateTo("IssueBook.fxml", "Issue Book to Member");
    }

    @FXML
    private void onQuickReturnBook(ActionEvent event) {
        NavigationManager.navigateTo("ReturnBook.fxml", "Return Book & Fine Calculation");
    }

    @FXML
    private void onQuickAddMember(ActionEvent event) {
        NavigationManager.navigateTo("AddMember.fxml", "Register New Member");
    }

    @FXML
    private void onQuickAddBook(ActionEvent event) {
        NavigationManager.navigateTo("AddBook.fxml", "Add New Book");
    }

    @FXML
    private void onRefresh(ActionEvent event) {
        loadDashboardData();
    }
}
