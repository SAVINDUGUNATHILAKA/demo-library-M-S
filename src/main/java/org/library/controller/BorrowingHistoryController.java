package org.library.controller;

import javafx.collections.FXCollections;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.library.model.BorrowRecord;
import org.library.service.LibraryDataService;

import java.time.LocalDate;

public class BorrowingHistoryController {

    @FXML
    private TextField txtSearch;

    @FXML
    private ComboBox<String> cmbStatusFilter;

    @FXML
    private TableView<BorrowRecord> tblHistory;

    @FXML
    private TableColumn<BorrowRecord, String> colRecordId;

    @FXML
    private TableColumn<BorrowRecord, String> colMemberId;

    @FXML
    private TableColumn<BorrowRecord, String> colMemberName;

    @FXML
    private TableColumn<BorrowRecord, String> colBookTitle;

    @FXML
    private TableColumn<BorrowRecord, LocalDate> colIssueDate;

    @FXML
    private TableColumn<BorrowRecord, LocalDate> colDueDate;

    @FXML
    private TableColumn<BorrowRecord, LocalDate> colReturnDate;

    @FXML
    private TableColumn<BorrowRecord, String> colStatus;

    @FXML
    private TableColumn<BorrowRecord, Double> colFine;

    @FXML
    private Label lblTotalRecords;

    @FXML
    private Label lblBorrowedCount;

    @FXML
    private Label lblOverdueCount;

    @FXML
    private Label lblReturnedCount;

    private final LibraryDataService dataService = LibraryDataService.getInstance();
    private FilteredList<BorrowRecord> filteredData;

    @FXML
    public void initialize() {
        setupStatusFilterComboBox();
        setupTableColumns();
        setupFilteringAndSorting();
        updateSummaryCounts();
    }

    private void setupStatusFilterComboBox() {
        cmbStatusFilter.setItems(FXCollections.observableArrayList(
            "All Records",
            "Borrowed (Active)",
            "Returned",
            "Overdue"
        ));
        cmbStatusFilter.getSelectionModel().selectFirst();
    }

    private void setupTableColumns() {
        colRecordId.setCellValueFactory(new PropertyValueFactory<>("recordId"));
        colMemberId.setCellValueFactory(new PropertyValueFactory<>("memberId"));
        colMemberName.setCellValueFactory(new PropertyValueFactory<>("memberName"));
        colBookTitle.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        colIssueDate.setCellValueFactory(new PropertyValueFactory<>("issueDate"));
        colDueDate.setCellValueFactory(new PropertyValueFactory<>("dueDate"));
        colReturnDate.setCellValueFactory(new PropertyValueFactory<>("returnDate"));
        colStatus.setCellValueFactory(new PropertyValueFactory<>("status"));
        colFine.setCellValueFactory(new PropertyValueFactory<>("fine"));

        // Format Return Date: Show "Not Returned" if null
        colReturnDate.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(LocalDate item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setText(null);
                } else if (item == null) {
                    setText("Not Returned Yet");
                    setStyle("-fx-text-fill: #94a3b8; -fx-font-style: italic;");
                } else {
                    setText(item.toString());
                    setStyle("");
                }
            }
        });

        // Status badge styling
        colStatus.setCellFactory(column -> new TableCell<>() {
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

        // Format Fine
        colFine.setCellFactory(column -> new TableCell<>() {
            @Override
            protected void updateItem(Double fine, boolean empty) {
                super.updateItem(fine, empty);
                if (empty || fine == null) {
                    setText(null);
                } else if (fine > 0) {
                    setText(String.format("$%.2f", fine));
                    setStyle("-fx-text-fill: #ef4444; -fx-font-weight: bold;");
                } else {
                    setText("$0.00");
                    setStyle("-fx-text-fill: #64748b;");
                }
            }
        });
    }

    private void setupFilteringAndSorting() {
        filteredData = new FilteredList<>(dataService.getBorrowRecords(), p -> true);

        // Filter listener for text search
        txtSearch.textProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        // Filter listener for status combo
        cmbStatusFilter.valueProperty().addListener((obs, oldVal, newVal) -> applyFilters());

        SortedList<BorrowRecord> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tblHistory.comparatorProperty());
        tblHistory.setItems(sortedData);
    }

    private void applyFilters() {
        String query = txtSearch.getText() != null ? txtSearch.getText().toLowerCase().trim() : "";
        String statusFilter = cmbStatusFilter.getValue();

        filteredData.setPredicate(record -> {
            // Check status condition
            if (statusFilter != null && !statusFilter.equals("All Records")) {
                if (statusFilter.startsWith("Borrowed") && !"Borrowed".equalsIgnoreCase(record.getStatus())) {
                    return false;
                } else if (statusFilter.equalsIgnoreCase("Returned") && !"Returned".equalsIgnoreCase(record.getStatus())) {
                    return false;
                } else if (statusFilter.equalsIgnoreCase("Overdue") && !"Overdue".equalsIgnoreCase(record.getStatus())) {
                    return false;
                }
            }

            // Check text query
            if (query.isEmpty()) {
                return true;
            }

            return (record.getRecordId() != null && record.getRecordId().toLowerCase().contains(query))
                || (record.getMemberId() != null && record.getMemberId().toLowerCase().contains(query))
                || (record.getMemberName() != null && record.getMemberName().toLowerCase().contains(query))
                || (record.getBookTitle() != null && record.getBookTitle().toLowerCase().contains(query))
                || (record.getBookId() != null && record.getBookId().toLowerCase().contains(query));
        });

        updateSummaryCounts();
    }

    private void updateSummaryCounts() {
        int total = dataService.getBorrowRecords().size();
        long borrowed = dataService.getBorrowRecords().stream().filter(r -> "Borrowed".equalsIgnoreCase(r.getStatus())).count();
        long overdue = dataService.getBorrowRecords().stream().filter(r -> "Overdue".equalsIgnoreCase(r.getStatus())).count();
        long returned = dataService.getBorrowRecords().stream().filter(r -> "Returned".equalsIgnoreCase(r.getStatus())).count();

        lblTotalRecords.setText(String.valueOf(total));
        lblBorrowedCount.setText(String.valueOf(borrowed));
        lblOverdueCount.setText(String.valueOf(overdue));
        lblReturnedCount.setText(String.valueOf(returned));
    }

    @FXML
    private void handleResetFilter(ActionEvent event) {
        txtSearch.clear();
        cmbStatusFilter.getSelectionModel().selectFirst();
    }
}
