package org.library.controller;

import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import org.library.model.Member;
import org.library.service.LibraryDataService;
import org.library.util.AlertUtil;
import org.library.util.ValidationUtil;

import java.time.LocalDate;

public class ManageMembersController {

    @FXML
    private TextField txtSearch;

    @FXML
    private TableView<Member> tblMembers;

    @FXML
    private TableColumn<Member, String> colMemberId;

    @FXML
    private TableColumn<Member, String> colName;

    @FXML
    private TableColumn<Member, String> colEmail;

    @FXML
    private TableColumn<Member, String> colPhone;

    @FXML
    private TableColumn<Member, String> colAddress;

    @FXML
    private TableColumn<Member, LocalDate> colRegDate;

    // Edit Panel Fields
    @FXML
    private TextField txtEditId;

    @FXML
    private TextField txtEditName;

    @FXML
    private TextField txtEditEmail;

    @FXML
    private TextField txtEditPhone;

    @FXML
    private TextArea txtEditAddress;

    @FXML
    private Button btnUpdate;

    @FXML
    private Button btnDelete;

    @FXML
    private Button btnCancelEdit;

    @FXML
    private Label lblMemberCount;

    private final LibraryDataService dataService = LibraryDataService.getInstance();
    private Member selectedMember;

    @FXML
    public void initialize() {
        setupTableColumns();
        setupSearchAndFilter();
        setupRowSelectionListener();
        updateMemberCount();
        setEditPanelEnabled(false);
    }

    private void setupTableColumns() {
        colMemberId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colName.setCellValueFactory(new PropertyValueFactory<>("name"));
        colEmail.setCellValueFactory(new PropertyValueFactory<>("email"));
        colPhone.setCellValueFactory(new PropertyValueFactory<>("phone"));
        colAddress.setCellValueFactory(new PropertyValueFactory<>("address"));
        colRegDate.setCellValueFactory(new PropertyValueFactory<>("registeredDate"));
    }

    private void setupSearchAndFilter() {
        FilteredList<Member> filteredData = new FilteredList<>(dataService.getMembers(), p -> true);

        txtSearch.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(member -> {
                if (newValue == null || newValue.trim().isEmpty()) {
                    return true;
                }
                String lowerCaseFilter = newValue.toLowerCase().trim();

                if (member.getId() != null && member.getId().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (member.getName() != null && member.getName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (member.getEmail() != null && member.getEmail().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (member.getPhone() != null && member.getPhone().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
            updateMemberCount();
        });

        SortedList<Member> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(tblMembers.comparatorProperty());
        tblMembers.setItems(sortedData);
    }

    private void setupRowSelectionListener() {
        tblMembers.getSelectionModel().selectedItemProperty().addListener((obs, oldSelection, newSelection) -> {
            if (newSelection != null) {
                selectedMember = newSelection;
                populateEditFields(newSelection);
                setEditPanelEnabled(true);
            } else {
                selectedMember = null;
                clearEditFields();
                setEditPanelEnabled(false);
            }
        });
    }

    private void populateEditFields(Member member) {
        txtEditId.setText(member.getId());
        txtEditName.setText(member.getName());
        txtEditEmail.setText(member.getEmail());
        txtEditPhone.setText(member.getPhone());
        txtEditAddress.setText(member.getAddress());
    }

    private void clearEditFields() {
        txtEditId.clear();
        txtEditName.clear();
        txtEditEmail.clear();
        txtEditPhone.clear();
        txtEditAddress.clear();
    }

    private void setEditPanelEnabled(boolean enabled) {
        txtEditName.setDisable(!enabled);
        txtEditEmail.setDisable(!enabled);
        txtEditPhone.setDisable(!enabled);
        txtEditAddress.setDisable(!enabled);
        btnUpdate.setDisable(!enabled);
        btnDelete.setDisable(!enabled);
        btnCancelEdit.setDisable(!enabled);
    }

    private void updateMemberCount() {
        int count = tblMembers.getItems() != null ? tblMembers.getItems().size() : 0;
        lblMemberCount.setText("Showing " + count + " Member" + (count == 1 ? "" : "s"));
    }

    @FXML
    private void handleUpdateMember(ActionEvent event) {
        if (selectedMember == null) {
            AlertUtil.showWarning("No Selection", "No Member Selected", "Please select a member from the table to edit.");
            return;
        }

        String name = txtEditName.getText() != null ? txtEditName.getText().trim() : "";
        String email = txtEditEmail.getText() != null ? txtEditEmail.getText().trim() : "";
        String phone = txtEditPhone.getText() != null ? txtEditPhone.getText().trim() : "";
        String address = txtEditAddress.getText() != null ? txtEditAddress.getText().trim() : "";

        if (!ValidationUtil.isNotEmpty(name)) {
            AlertUtil.showWarning("Validation Error", "Invalid Name", "Member full name cannot be blank.");
            txtEditName.requestFocus();
            return;
        }

        if (!ValidationUtil.isValidEmail(email)) {
            AlertUtil.showWarning("Validation Error", "Invalid Email", "Please enter a valid email address.");
            txtEditEmail.requestFocus();
            return;
        }

        if (!ValidationUtil.isValidPhone(phone)) {
            AlertUtil.showWarning("Validation Error", "Invalid Phone", "Please enter a valid phone number.");
            txtEditPhone.requestFocus();
            return;
        }

        if (!ValidationUtil.isNotEmpty(address)) {
            AlertUtil.showWarning("Validation Error", "Invalid Address", "Address cannot be blank.");
            txtEditAddress.requestFocus();
            return;
        }

        selectedMember.setName(name);
        selectedMember.setEmail(email);
        selectedMember.setPhone(phone);
        selectedMember.setAddress(address);

        dataService.updateMember(selectedMember);
        tblMembers.refresh();

        AlertUtil.showInformation("Update Successful", "Member Updated",
            "Member '" + selectedMember.getName() + "' (ID: " + selectedMember.getId() + ") details updated successfully.");
    }

    @FXML
    private void handleDeleteMember(ActionEvent event) {
        if (selectedMember == null) {
            AlertUtil.showWarning("No Selection", "No Member Selected", "Please select a member from the table to delete.");
            return;
        }

        boolean confirmed = AlertUtil.showConfirmation(
            "Confirm Member Deletion",
            "Delete Member: " + selectedMember.getName() + " (" + selectedMember.getId() + ")?",
            "Are you sure you want to permanently delete this member? This action cannot be reversed."
        );

        if (confirmed) {
            String name = selectedMember.getName();
            String id = selectedMember.getId();
            dataService.deleteMember(id);
            tblMembers.getSelectionModel().clearSelection();
            updateMemberCount();

            AlertUtil.showInformation("Member Deleted", "Deleted Successfully",
                "Member '" + name + "' (ID: " + id + ") has been removed from the registry.");
        }
    }

    @FXML
    private void handleCancelEdit(ActionEvent event) {
        tblMembers.getSelectionModel().clearSelection();
    }
}
