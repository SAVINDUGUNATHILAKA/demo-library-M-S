package org.library.model;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

/**
 * Model representing a book borrowing record/transaction.
 */
public class BorrowRecord {
    private String recordId;
    private String memberId;
    private String memberName;
    private String bookId;
    private String bookTitle;
    private LocalDate issueDate;
    private LocalDate dueDate;
    private LocalDate returnDate;
    private String status; // "Borrowed", "Returned", "Overdue"
    private double fine;
    private String notes;

    public BorrowRecord() {
    }

    public BorrowRecord(String recordId, String memberId, String memberName, String bookId, String bookTitle,
                        LocalDate issueDate, LocalDate dueDate, LocalDate returnDate, String status, double fine, String notes) {
        this.recordId = recordId;
        this.memberId = memberId;
        this.memberName = memberName;
        this.bookId = bookId;
        this.bookTitle = bookTitle;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.status = status;
        this.fine = fine;
        this.notes = notes;
    }

    public String getRecordId() {
        return recordId;
    }

    public void setRecordId(String recordId) {
        this.recordId = recordId;
    }

    public String getMemberId() {
        return memberId;
    }

    public void setMemberId(String memberId) {
        this.memberId = memberId;
    }

    public String getMemberName() {
        return memberName;
    }

    public void setMemberName(String memberName) {
        this.memberName = memberName;
    }

    public String getBookId() {
        return bookId;
    }

    public void setBookId(String bookId) {
        this.bookId = bookId;
    }

    public String getBookTitle() {
        return bookTitle;
    }

    public void setBookTitle(String bookTitle) {
        this.bookTitle = bookTitle;
    }

    public LocalDate getIssueDate() {
        return issueDate;
    }

    public void setIssueDate(LocalDate issueDate) {
        this.issueDate = issueDate;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDate getReturnDate() {
        return returnDate;
    }

    public void setReturnDate(LocalDate returnDate) {
        this.returnDate = returnDate;
    }

    public String getStatus() {
        // Automatically check if an active borrow is overdue based on current date
        if (returnDate == null && dueDate != null && LocalDate.now().isAfter(dueDate)) {
            return "Overdue";
        }
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public double getFine() {
        return fine;
    }

    public void setFine(double fine) {
        this.fine = fine;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    /**
     * Calculates overdue days against return date or today if not yet returned.
     */
    public long getOverdueDays() {
        LocalDate checkDate = returnDate != null ? returnDate : LocalDate.now();
        if (dueDate != null && checkDate.isAfter(dueDate)) {
            return ChronoUnit.DAYS.between(dueDate, checkDate);
        }
        return 0;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s borrowed '%s' (Due: %s)", recordId, memberName, bookTitle, dueDate);
    }
}
