package org.library.controller;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.library.model.Book;
import org.library.service.LibraryDataService;
import org.library.util.AlertUtil;
import org.library.util.ValidationUtil;

public class AddBookController {

    @FXML
    private TextField txtBookId;

    @FXML
    private TextField txtTitle;

    @FXML
    private TextField txtAuthor;

    @FXML
    private ComboBox<String> cmbCategory;

    @FXML
    private TextField txtPublishedYear;

    @FXML
    private TextField txtQuantity;

    @FXML
    private Button btnAddBook;

    @FXML
    private Button btnClear;

    @FXML
    private TableView<Book> tblBooksPreview;

    @FXML
    private TableColumn<Book, String> colBookId;

    @FXML
    private TableColumn<Book, String> colTitle;

    @FXML
    private TableColumn<Book, String> colAuthor;

    @FXML
    private TableColumn<Book, String> colCategory;

    @FXML
    private TableColumn<Book, Integer> colQuantity;

    private final LibraryDataService dataService = LibraryDataService.getInstance();

    @FXML
    public void initialize() {
        // Initialize Categories ComboBox
        ObservableList<String> categories = FXCollections.observableArrayList(
            "Technology & Computer Science",
            "Science & Mathematics",
            "Fiction & Literature",
            "History & Philosophy",
            "Business & Economics",
            "Biographies & Memoirs",
            "Arts & Design",
            "General Reference"
        );
        cmbCategory.setItems(categories);
        cmbCategory.getSelectionModel().selectFirst();

        // Auto-suggest next Book ID
        txtBookId.setText(dataService.generateNextBookId());

        // Setup preview table
        setupTable();
    }

    private void setupTable() {
        colBookId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTitle.setCellValueFactory(new PropertyValueFactory<>("title"));
        colAuthor.setCellValueFactory(new PropertyValueFactory<>("author"));
        colCategory.setCellValueFactory(new PropertyValueFactory<>("category"));
        colQuantity.setCellValueFactory(new PropertyValueFactory<>("quantity"));

        tblBooksPreview.setItems(dataService.getBooks());
    }

    @FXML
    private void handleAddBook(ActionEvent event) {
        String bookId = txtBookId.getText() != null ? txtBookId.getText().trim() : "";
        String title = txtTitle.getText() != null ? txtTitle.getText().trim() : "";
        String author = txtAuthor.getText() != null ? txtAuthor.getText().trim() : "";
        String category = cmbCategory.getValue();
        String yearStr = txtPublishedYear.getText() != null ? txtPublishedYear.getText().trim() : "";
        String qtyStr = txtQuantity.getText() != null ? txtQuantity.getText().trim() : "";

        // Validations
        if (!ValidationUtil.isNotEmpty(bookId)) {
            AlertUtil.showWarning("Validation Error", "Invalid Book ID", "Book ID / ISBN cannot be blank.");
            txtBookId.requestFocus();
            return;
        }

        if (dataService.findBookById(bookId) != null) {
            AlertUtil.showWarning("Validation Error", "Duplicate Book ID", "A book with ID '" + bookId + "' already exists in the catalog.");
            txtBookId.requestFocus();
            return;
        }

        if (!ValidationUtil.isNotEmpty(title)) {
            AlertUtil.showWarning("Validation Error", "Invalid Book Title", "Book title is required.");
            txtTitle.requestFocus();
            return;
        }

        if (!ValidationUtil.isNotEmpty(author)) {
            AlertUtil.showWarning("Validation Error", "Invalid Author", "Author name is required.");
            txtAuthor.requestFocus();
            return;
        }

        if (!ValidationUtil.isNotEmpty(category)) {
            AlertUtil.showWarning("Validation Error", "Invalid Category", "Please select a category.");
            cmbCategory.requestFocus();
            return;
        }

        if (!ValidationUtil.isValidYear(yearStr)) {
            AlertUtil.showWarning("Validation Error", "Invalid Year", "Published year must be a 4-digit valid year (e.g., 2024).");
            txtPublishedYear.requestFocus();
            return;
        }

        if (!ValidationUtil.isPositiveInteger(qtyStr)) {
            AlertUtil.showWarning("Validation Error", "Invalid Quantity", "Quantity must be a positive whole number greater than 0.");
            txtQuantity.requestFocus();
            return;
        }

        int year = Integer.parseInt(yearStr);
        int quantity = Integer.parseInt(qtyStr);

        Book newBook = new Book(bookId, title, author, category, year, quantity);
        dataService.addBook(newBook);

        AlertUtil.showInformation("Success", "Book Added Successfully",
            String.format("Book '%s' (ID: %s) has been added to the library catalog with %d copies.", title, bookId, quantity));

        handleClear(null);
    }

    @FXML
    private void handleClear(ActionEvent event) {
        txtBookId.setText(dataService.generateNextBookId());
        txtTitle.clear();
        txtAuthor.clear();
        cmbCategory.getSelectionModel().selectFirst();
        txtPublishedYear.clear();
        txtQuantity.clear();
        txtTitle.requestFocus();
    }
}
