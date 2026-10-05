package org.library.controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.library.model.Member;
import org.library.service.LibraryDataService;
import org.library.util.AlertUtil;
import org.library.util.NavigationManager;
import org.library.util.ValidationUtil;

import java.time.LocalDate;

public class AddMemberController {

    @FXML
    private TextField txtMemberId;

    @FXML
    private TextField txtName;

    @FXML
    private TextField txtEmail;

    @FXML
    private TextField txtPhone;

    @FXML
    private TextArea txtAddress;

    @FXML
    private Button btnRegister;

    @FXML
    private Button btnClear;

    @FXML
    private Button btnViewMembers;

    @FXML
    private TableView<Member> tblMembersPreview;

    @FXML
    private TableColumn<Member, String> colMemberId;

    @FXML
    private TableColumn<Member, String> colName;

    @FXML
    private TableColumn<Member, String> colEmail;

    @FXML
    private TableColumn<Member, String> colPhone;

    private final LibraryDataService dataService = LibraryDataService.getInstance();

    @FXML
    public void initialize() {
        txtMemberId.setText(dataService.generateNextMemberId());
        setupTable();
    }

    private void setupTable() {
        colMemberId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));

        tblMembersPreview.setItems(dataService.getMembers());
    }

    @FXML
    private void handleRegister(ActionEvent event) {
        String id = txtMemberId.getText() != null ? txtMemberId.getText().trim() : "";
        String name = txtName.getText() != null ? txtName.getText().trim() : "";
        String email = txtEmail.getText() != null ? txtEmail.getText().trim() : "";
        String phone = txtPhone.getText() != null ? txtPhone.getText().trim() : "";
        String address = txtAddress.getText() != null ? txtAddress.getText().trim() : "";

        // Validations
        if (!ValidationUtil.isNotEmpty(id)) {
            AlertUtil.showWarning("Validation Error", "Missing Member ID", "Please specify a Member ID.");
            txtMemberId.requestFocus();
            return;
        }

        if (dataService.findMemberById(id) != null) {
            AlertUtil.showWarning("Validation Error", "Duplicate Member ID", "A member with ID '" + id + "' is already registered.");
            txtMemberId.requestFocus();
            return;
        }

        if (!ValidationUtil.isNotEmpty(name)) {
            AlertUtil.showWarning("Validation Error", "Missing Name", "Full name is required.");
            txtName.requestFocus();
            return;
        }

        if (!ValidationUtil.isValidEmail(email)) {
            AlertUtil.showWarning("Validation Error", "Invalid Email", "Please enter a valid email address (e.g., patron@domain.com).");
            txtEmail.requestFocus();
            return;
        }

        if (!ValidationUtil.isValidPhone(phone)) {
            AlertUtil.showWarning("Validation Error", "Invalid Phone Number", "Please enter a valid phone number (e.g., 0771234567 or +94771234567).");
            txtPhone.requestFocus();
            return;
        }

        if (!ValidationUtil.isNotEmpty(address)) {
            AlertUtil.showWarning("Validation Error", "Missing Address", "Postal or residential address is required.");
            txtAddress.requestFocus();
            return;
        }

        Member newMember = new Member(id, name, email, phone, address, LocalDate.now());
        dataService.addMember(newMember);

        AlertUtil.showInformation("Registration Successful", "Member Registered",
            String.format("Member '%s' (ID: %s) has been successfully enrolled.", name, id));

        handleClear(null);
    }

    @FXML
    private void handleClear(ActionEvent event) {
        txtMemberId.setText(dataService.generateNextMemberId());
        txtName.clear();
        txtEmail.clear();
        txtPhone.clear();
        txtAddress.clear();
        txtName.requestFocus();
    }

    @FXML
    private void handleViewAllMembers(ActionEvent event) {
        NavigationManager.navigateTo("ManageMembers.fxml", "Manage Library Members");
    }
}
