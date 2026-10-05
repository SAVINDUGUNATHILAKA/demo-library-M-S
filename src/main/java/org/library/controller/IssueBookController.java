package org.library.controller;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import org.library.model.Book;
import org.library.model.BorrowRecord;
import org.library.model.Member;
import org.library.service.LibraryDataService;
import org.library.util.AlertUtil;
import org.library.util.NavigationManager;

import java.time.LocalDate;

public class IssueBookController {

    @FXML
    private ComboBox<Member> cmbMember;

    @FXML
    private ComboBox<Book> cmbBook;

    @FXML
    private DatePicker dpIssueDate;

    @FXML
    private DatePicker dpDueDate;

    @FXML
    private TextField txtNotes;

    // Member Info Card Preview
    @FXML
    private Label lblMemberName;

    @FXML
    private Label lblMemberPhone;

    @FXML
    private Label lblMemberEmail;

    // Book Info Card Preview
    @FXML
    private Label lblBookTitle;

    @FXML
    private Label lblBookAuthor;

    @FXML
    private Label lblBookCategory;

    @FXML
    private Label lblBookAvailable;

    @FXML
    private Button btnIssueBook;

    @FXML
    private Button btnClear;

    private final LibraryDataService dataService = LibraryDataService.getInstance();

    @FXML
    public void initialize() {
        // Populate Members and Books
        cmbMember.setItems(dataService.getMembers());
        cmbBook.setItems(dataService.getBooks());

        // Default Dates
        dpIssueDate.setValue(LocalDate.now());
        dpDueDate.setValue(LocalDate.now().plusDays(14));

        // When issue date changes, update suggested due date (+14 days)
        dpIssueDate.valueProperty().addListener((obs, oldVal, newVal) -> {
            if (newVal != null) {
                dpDueDate.setValue(newVal.plusDays(14));
            }
        });

        // Member selection listener
        cmbMember.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newMember) -> {
            if (newMember != null) {
                lblMemberName.setText(newMember.getName() + " (" + newMember.getId() + ")");
                lblMemberPhone.setText(newMember.getPhone());
                lblMemberEmail.setText(newMember.getEmail());
            } else {
                lblMemberName.setText("-");
                lblMemberPhone.setText("-");
                lblMemberEmail.setText("-");
            }
        });

        // Book selection listener
        cmbBook.getSelectionModel().selectedItemProperty().addListener((obs, oldVal, newBook) -> {
            if (newBook != null) {
                lblBookTitle.setText(newBook.getTitle());
                lblBookAuthor.setText(newBook.getAuthor());
                lblBookCategory.setText(newBook.getCategory());
                lblBookAvailable.setText(newBook.getAvailableQuantity() + " copies available (Total: " + newBook.getQuantity() + ")");
                if (newBook.getAvailableQuantity() <= 0) {
                    lblBookAvailable.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                } else {
                    lblBookAvailable.setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
                }
            } else {
                lblBookTitle.setText("-");
                lblBookAuthor.setText("-");
                lblBookCategory.setText("-");
                lblBookAvailable.setText("-");
                lblBookAvailable.setStyle("");
            }
        });
    }

    @FXML
    private void handleIssueBook(ActionEvent event) {
        Member selectedMember = cmbMember.getValue();
        Book selectedBook = cmbBook.getValue();
        LocalDate issueDate = dpIssueDate.getValue();
        LocalDate dueDate = dpDueDate.getValue();
        String notes = txtNotes.getText() != null ? txtNotes.getText().trim() : "";

        // Validations
        if (selectedMember == null) {
            AlertUtil.showWarning("Validation Error", "No Member Selected", "Please select a library member to issue the book to.");
            cmbMember.requestFocus();
            return;
        }

        if (selectedBook == null) {
            AlertUtil.showWarning("Validation Error", "No Book Selected", "Please select a book from the catalog.");
            cmbBook.requestFocus();
            return;
        }

        if (selectedBook.getAvailableQuantity() <= 0) {
            AlertUtil.showError("Stock Unavailable", "Book Out of Stock",
                "There are no available copies of '" + selectedBook.getTitle() + "' in the inventory right now.");
            return;
        }

        if (issueDate == null) {
            AlertUtil.showWarning("Validation Error", "Missing Issue Date", "Please select a valid issue date.");
            dpIssueDate.requestFocus();
            return;
        }

        if (dueDate == null) {
            AlertUtil.showWarning("Validation Error", "Missing Due Date", "Please select an expected due return date.");
            dpDueDate.requestFocus();
            return;
        }

        if (dueDate.isBefore(issueDate)) {
            AlertUtil.showWarning("Validation Error", "Invalid Due Date", "The return due date cannot be prior to the issue date.");
            dpDueDate.requestFocus();
            return;
        }

        // Confirmation Dialog
        boolean confirmed = AlertUtil.showConfirmation(
            "Confirm Book Issue",
            "Issue '" + selectedBook.getTitle() + "' to " + selectedMember.getName() + "?",
            String.format("Issue Date: %s\nDue Date: %s\nRemaining copies after issue: %d",
                issueDate, dueDate, selectedBook.getAvailableQuantity() - 1)
        );

        if (confirmed) {
            BorrowRecord record = dataService.issueBook(selectedMember, selectedBook, issueDate, dueDate, notes);

            AlertUtil.showInformation("Success", "Book Issued Successfully",
                String.format("Transaction Record ID: %s\nBook: %s\nMember: %s\nReturn Due Date: %s",
                    record.getRecordId(), record.getBookTitle(), record.getMemberName(), record.getDueDate()));

            // Refresh book availability label
            lblBookAvailable.setText(selectedBook.getAvailableQuantity() + " copies available (Total: " + selectedBook.getQuantity() + ")");
            handleClear(null);
        }
    }

    @FXML
    private void handleClear(ActionEvent event) {
        cmbMember.getSelectionModel().clearSelection();
        cmbBook.getSelectionModel().clearSelection();
        dpIssueDate.setValue(LocalDate.now());
        dpDueDate.setValue(LocalDate.now().plusDays(14));
        txtNotes.clear();
    }
}
