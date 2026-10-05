package org.library.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.library.model.BorrowRecord;
import org.library.service.LibraryDataService;
import org.library.util.AlertUtil;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class ReturnBookController {

    @FXML
    private TextField txtSearchRecord;

    @FXML
    private TableView<BorrowRecord> tblBorrowedBooks;

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

    // Return Processing Detail Panel
    @FXML
    private Label lblSelectedRecordId;

    @FXML
    private Label lblMemberName;

    @FXML
    private Label lblMemberId;

    @FXML
    private Label lblBookTitle;

    @FXML
    private Label lblBookId;

    @FXML
    private Label lblBorrowedDate;

    @FXML
    private Label lblDueDate;

    @FXML
    private DatePicker dpReturnDate;

    @FXML
    private Label lblOverdueStatus;

    @FXML
    private Label lblOverdueDays;

    @FXML
    private Label lblCalculatedFine;

    @FXML
    private Button btnReturnBook;

    @FXML
    private Button btnClear;

    private final LibraryDataService dataService = LibraryDataService.getInstance();
    private BorrowRecord selectedRecord;
    private final double FINE_PER_DAY = 0.50; // $0.50 per day overdue

    @FXML
    public void initialize() {
        setupTable();
        dpReturnDate.setValue(LocalDate.now());

        dpReturnDate.valueProperty().addListener((obs, oldVal, newVal) -> {
            updateOverdueCalculation();
        });

        clearDetails();
        loadActiveBorrowedBooks();
    }

    private void setupTable() {
        colRecordId.setCellValueFactory(new PropertyValueFactory<>("recordId"));
        colMemberName.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        colBookTitle.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        colIssueDate.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        colDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));

        colStatus.setCellFactory(col -> new TableCell<>() {
            @Override
            protected void updateItem(String status, boolean empty) {
                super.updateItem(status, empty);
                if (empty || status == null) {
                    setGraphic(null);
                    setText(null);
                } else {
                    Label badge = new Label(status);
                    badge.getStyleClass().add("badge");
                    if ("Overdue".equalsIgnoreCase(status)) {
                        badge.getStyleClass().add("badge-overdue");
                    } else {
                        badge.getStyleClass().add("badge-borrowed");
                    }
                    setGraphic(badge);
                    setText(null);
                }
            }
        });

        tblBorrowedBooks.getSelectionModel().selectedItemProperty().addListener((obs, oldRec, newRec) -> {
            if (newRec != null) {
                selectedRecord = newRec;
                populateDetails(newRec);
            } else {
                selectedRecord = null;
                clearDetails();
            }
        });
    }

    private void loadActiveBorrowedBooks() {
        // Only show records that haven't been returned yet
        FilteredList<BorrowRecord> activeList = new FilteredList<>(dataService.getBorrowRecords(), r -> r.getReturnDate() == null);

        txtSearchRecord.textProperty().addListener((obs, oldVal, query) -> {
            activeList.setPredicate(record -> {
                if (record.getReturnDate() != null) return false;
                if (query == null || query.trim().isEmpty()) return true;

                String lower = query.toLowerCase().trim();
                return (record.getRecordId() != null && record.getRecordId().toLowerCase().contains(lower))
                    || (record.getMemberName() != null && record.getMemberName().toLowerCase().contains(lower))
                    || (record.getMemberId() != null && record.getMemberId().toLowerCase().contains(lower))
                    || (record.getBookTitle() != null && record.getBookTitle().toLowerCase().contains(lower))
                    || (record.getBookId() != null && record.getBookId().toLowerCase().contains(lower));
            });
        });

        tblBorrowedBooks.setItems(activeList);
    }

    private void populateDetails(BorrowRecord record) {
        lblSelectedRecordId.setText(record.getRecordId());
        lblMemberName.setText(record.getMemberName());
        lblMemberId.setText(record.getMemberId());
        lblBookTitle.setText(record.getBookTitle());
        lblBookId.setText(record.getBookId());
        lblBorrowedDate.setText(record.getIssueDate() != null ? record.getIssueDate().toString() : "-");
        lblDueDate.setText(record.getDueDate() != null ? record.getDueDate().toString() : "-");

        updateOverdueCalculation();
        btnReturnBook.setDisable(false);
    }

    private void updateOverdueCalculation() {
        if (selectedRecord == null || selectedRecord.getDueDate() == null) {
            lblOverdueStatus.setText("None");
            lblOverdueStatus.setStyle("-fx-text-fill: #64748b; -fx-font-weight: normal;");
            lblOverdueDays.setText("0 days");
            lblCalculatedFine.setText("$0.00");
            return;
        }

        LocalDate returnDate = dpReturnDate.getValue() != null ? dpReturnDate.getValue() : LocalDate.now();
        LocalDate dueDate = selectedRecord.getDueDate();

        if (returnDate.isAfter(dueDate)) {
            long days = ChronoUnit.DAYS.between(dueDate, returnDate);
            double fine = days * FINE_PER_DAY;
            lblOverdueStatus.setText("OVERDUE (" + days + " days late)");
            lblOverdueStatus.setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
            lblOverdueDays.setText(days + " days");
            lblCalculatedFine.setText(String.format("$%.2f", fine));
        } else {
            lblOverdueStatus.setText("On Time (No Penalty)");
            lblOverdueStatus.setStyle("-fx-text-fill: #10b981; -fx-font-weight: bold;");
            lblOverdueDays.setText("0 days");
            lblCalculatedFine.setText("$0.00");
        }
    }

    private void clearDetails() {
        lblSelectedRecordId.setText("None selected");
        lblMemberName.setText("-");
        lblMemberId.setText("-");
        lblBookTitle.setText("-");
        lblBookId.setText("-");
        lblBorrowedDate.setText("-");
        lblDueDate.setText("-");
        lblOverdueStatus.setText("-");
        lblOverdueStatus.setStyle("");
        lblOverdueDays.setText("0 days");
        lblCalculatedFine.setText("$0.00");
        btnReturnBook.setDisable(true);
    }

    @FXML
    private void handleReturnBook(ActionEvent event) {
        if (selectedRecord == null) {
            AlertUtil.showWarning("No Selection", "No Borrow Record Selected", "Please select a borrowed record from the table to process the return.");
            return;
        }

        LocalDate returnDate = dpReturnDate.getValue();
        if (returnDate == null) {
            AlertUtil.showWarning("Validation Error", "Missing Return Date", "Please specify the return date.");
            dpReturnDate.requestFocus();
            return;
        }

        if (selectedRecord.getIssueDate() != null && returnDate.isBefore(selectedRecord.getIssueDate())) {
            AlertUtil.showWarning("Validation Error", "Invalid Return Date", "Return date cannot be earlier than the issue date (" + selectedRecord.getIssueDate() + ").");
            dpReturnDate.requestFocus();
            return;
        }

        long overdueDays = 0;
        double fine = 0.00;
        if (returnDate.isAfter(selectedRecord.getDueDate())) {
            overdueDays = ChronoUnit.DAYS.between(selectedRecord.getDueDate(), returnDate);
            fine = overdueDays * FINE_PER_DAY;
        }

        String confirmMsg = String.format("Return book '%s' borrowed by %s?\nReturn Date: %s\nOverdue Days: %d\nFine Charged: $%.2f",
            selectedRecord.getBookTitle(), selectedRecord.getMemberName(), returnDate, overdueDays, fine);

        boolean confirm = AlertUtil.showConfirmation("Confirm Book Return", "Process Book Return", confirmMsg);

        if (confirm) {
            dataService.returnBook(selectedRecord, returnDate, fine);

            AlertUtil.showInformation("Success", "Book Returned Successfully",
                String.format("Book '%s' has been checked back into the library catalog.\nInventory has been restocked.\nFine: $%.2f",
                    selectedRecord.getBookTitle(), fine));

            tblBorrowedBooks.getSelectionModel().clearSelection();
            loadActiveBorrowedBooks();
            clearDetails();
        }
    }

    @FXML
    private void handleClear(ActionEvent event) {
        tblBorrowedBooks.getSelectionModel().clearSelection();
        txtSearchRecord.clear();
        dpReturnDate.setValue(LocalDate.now());
        clearDetails();
    }
}
