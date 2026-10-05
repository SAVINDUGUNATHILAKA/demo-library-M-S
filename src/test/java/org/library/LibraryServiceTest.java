package org.library;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.library.model.Book;
import org.library.model.BorrowRecord;
import org.library.model.Member;
import org.library.service.LibraryDataService;
import org.library.util.ValidationUtil;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

public class LibraryServiceTest {

    private LibraryDataService service;

    @BeforeEach
    public void setUp() {
        service = LibraryDataService.getInstance();
    }

    @Test
    public void testInitialSampleDataLoaded() {
        assertTrue(service.getTotalBooksCount() > 0, "Books should be loaded");
        assertTrue(service.getTotalMembersCount() > 0, "Members should be loaded");
        assertTrue(service.getBorrowRecords().size() > 0, "Borrow records should be loaded");
    }

    @Test
    public void testAddAndFindBook() {
        String testId = "BTEST-999";
        Book book = new Book(testId, "Test Architecture", "Martin Fowler", "Technology", 2023, 10);
        service.addBook(book);

        Book found = service.findBookById(testId);
        assertNotNull(found);
        assertEquals("Test Architecture", found.getTitle());
        assertEquals(10, found.getQuantity());
        assertEquals(10, found.getAvailableQuantity());
    }

    @Test
    public void testAddAndFindMember() {
        String testId = "MTEST-999";
        Member member = new Member(testId, "Sunil Shantha", "sunil@gmail.com", "0711122334", "Galle");
        service.addMember(member);

        Member found = service.findMemberById(testId);
        assertNotNull(found);
        assertEquals("Sunil Shantha", found.getName());
    }

    @Test
    public void testIssueAndReturnBook() {
        Member member = service.getMembers().get(0);
        Book book = service.getBooks().get(0);
        int initialAvailable = book.getAvailableQuantity();
        assertTrue(initialAvailable > 0);

        LocalDate issueDate = LocalDate.now();
        LocalDate dueDate = issueDate.plusDays(14);
        BorrowRecord record = service.issueBook(member, book, issueDate, dueDate, "Unit test loan");

        assertNotNull(record);
        assertEquals(initialAvailable - 1, book.getAvailableQuantity());
        assertEquals("Borrowed", record.getStatus());

        // Process Return
        LocalDate returnDate = dueDate.plusDays(3); // 3 days overdue
        double fine = 1.50; // $0.50 * 3
        service.returnBook(record, returnDate, fine);

        assertEquals("Returned", record.getStatus());
        assertEquals(returnDate, record.getReturnDate());
        assertEquals(1.50, record.getFine());
        assertEquals(initialAvailable, book.getAvailableQuantity());
    }

    @Test
    public void testValidations() {
        assertTrue(ValidationUtil.isValidEmail("librarian@library.org"));
        assertFalse(ValidationUtil.isValidEmail("invalid-email"));

        assertTrue(ValidationUtil.isValidPhone("0771234567"));
        assertTrue(ValidationUtil.isValidPhone("+94771234567"));
        assertFalse(ValidationUtil.isValidPhone("abc"));

        assertTrue(ValidationUtil.isPositiveInteger("5"));
        assertFalse(ValidationUtil.isPositiveInteger("-2"));
        assertFalse(ValidationUtil.isPositiveInteger("abc"));

        assertTrue(ValidationUtil.isValidYear("2024"));
        assertFalse(ValidationUtil.isValidYear("800"));
        assertFalse(ValidationUtil.isValidYear("3000"));
    }
}
