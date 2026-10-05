package org.library.service;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.library.model.Book;
import org.library.model.BorrowRecord;
import org.library.model.Member;

import java.time.LocalDate;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * Singleton service providing state management, mock data, and business logic
 * for books, members, and borrowing transactions.
 */
public class LibraryDataService {
    private static LibraryDataService instance;

    private final ObservableList<Book> books = FXCollections.observableArrayList();
    private final ObservableList<Member> members = FXCollections.observableArrayList();
    private final ObservableList<BorrowRecord> borrowRecords = FXCollections.observableArrayList();

    private final AtomicInteger bookSeq = new AtomicInteger(1005);
    private final AtomicInteger memberSeq = new AtomicInteger(106);
    private final AtomicInteger recordSeq = new AtomicInteger(2005);

    private LibraryDataService() {
        initSampleData();
    }

    public static synchronized LibraryDataService getInstance() {
        if (instance == null) {
            instance = new LibraryDataService();
        }
        return instance;
    }

    private void initSampleData() {
        // Sample Books
        books.addAll(
            new Book("B1001", "Clean Code: Agile Software Craftsmanship", "Robert C. Martin", "Technology", 2008, 6, 4),
            new Book("B1002", "Effective Java (3rd Edition)", "Joshua Bloch", "Technology", 2018, 5, 3),
            new Book("B1003", "To Kill a Mockingbird", "Harper Lee", "Fiction", 1960, 4, 3),
            new Book("B1004", "A Brief History of Time", "Stephen Hawking", "Science", 1988, 3, 2),
            new Book("B1005", "The Great Gatsby", "F. Scott Fitzgerald", "Literature", 1925, 4, 4),
            new Book("B1006", "Sapiens: A Brief History of Humankind", "Yuval Noah Harari", "History", 2011, 5, 4)
        );

        // Sample Members
        members.addAll(
            new Member("M101", "Amara Perera", "amara.perera@gmail.com", "0771234567", "12 Temple Rd, Colombo 03", LocalDate.now().minusMonths(6)),
            new Member("M102", "Kavindu Silva", "kavindu.s@outlook.com", "0719876543", "45 Galle Face Terrace, Colombo", LocalDate.now().minusMonths(4)),
            new Member("M103", "Dinithi Fernando", "dinithi.f@yahoo.com", "0765544332", "88 Kandy Road, Kiribathgoda", LocalDate.now().minusMonths(3)),
            new Member("M104", "Nuwan Jayasinghe", "nuwan.j@gmail.com", "0723344556", "101 Peradeniya Road, Kandy", LocalDate.now().minusMonths(2)),
            new Member("M105", "Samanthi Wickrama", "samanthi.w@hotmail.com", "0788877665", "23 Negombo Rd, Ja-Ela", LocalDate.now().minusWeeks(3))
        );

        // Sample Borrow Records
        // Record 1: Borrowed & Overdue
        borrowRecords.add(new BorrowRecord(
            "BR-2001", "M101", "Amara Perera", "B1001", "Clean Code: Agile Software Craftsmanship",
            LocalDate.now().minusDays(20), LocalDate.now().minusDays(6), null, "Overdue", 3.00, "First notice sent"
        ));

        // Record 2: Currently Borrowed (active, not overdue)
        borrowRecords.add(new BorrowRecord(
            "BR-2002", "M102", "Kavindu Silva", "B1002", "Effective Java (3rd Edition)",
            LocalDate.now().minusDays(5), LocalDate.now().plusDays(9), null, "Borrowed", 0.00, "Borrowed for academic research"
        ));

        // Record 3: Returned on time
        borrowRecords.add(new BorrowRecord(
            "BR-2003", "M103", "Dinithi Fernando", "B1003", "To Kill a Mockingbird",
            LocalDate.now().minusDays(15), LocalDate.now().minusDays(1), LocalDate.now().minusDays(2), "Returned", 0.00, "Returned in pristine condition"
        ));

        // Record 4: Currently Borrowed & Overdue
        borrowRecords.add(new BorrowRecord(
            "BR-2004", "M104", "Nuwan Jayasinghe", "B1004", "A Brief History of Time",
            LocalDate.now().minusDays(25), LocalDate.now().minusDays(11), null, "Overdue", 5.50, "Second notice sent via email"
        ));

        // Record 5: Returned with fine
        borrowRecords.add(new BorrowRecord(
            "BR-2005", "M105", "Samanthi Wickrama", "B1002", "Effective Java (3rd Edition)",
            LocalDate.now().minusDays(30), LocalDate.now().minusDays(16), LocalDate.now().minusDays(12), "Returned", 2.00, "Fine paid at counter"
        ));
    }

    public ObservableList<Book> getBooks() {
        return books;
    }

    public ObservableList<Member> getMembers() {
        return members;
    }

    public ObservableList<BorrowRecord> getBorrowRecords() {
        return borrowRecords;
    }

    public String generateNextBookId() {
        return "B" + bookSeq.incrementAndGet();
    }

    public String generateNextMemberId() {
        return "M" + memberSeq.incrementAndGet();
    }

    public String generateNextRecordId() {
        return "BR-" + recordSeq.incrementAndGet();
    }

    // Books management
    public void addBook(Book book) {
        books.add(book);
    }

    public Book findBookById(String id) {
        if (id == null) return null;
        return books.stream().filter(b -> b.getId().equalsIgnoreCase(id.trim())).findFirst().orElse(null);
    }

    // Members management
    public void addMember(Member member) {
        members.add(member);
    }

    public void updateMember(Member updated) {
        for (int i = 0; i < members.size(); i++) {
            if (members.get(i).getId().equalsIgnoreCase(updated.getId())) {
                members.set(i, updated);
                break;
            }
        }
    }

    public boolean deleteMember(String id) {
        return members.removeIf(m -> m.getId().equalsIgnoreCase(id));
    }

    public Member findMemberById(String id) {
        if (id == null) return null;
        return members.stream().filter(m -> m.getId().equalsIgnoreCase(id.trim())).findFirst().orElse(null);
    }

    // Issue book
    public BorrowRecord issueBook(Member member, Book book, LocalDate issueDate, LocalDate dueDate, String notes) {
        if (book.getAvailableQuantity() <= 0) {
            throw new IllegalStateException("Book is out of stock!");
        }
        // decrement available stock
        book.setAvailableQuantity(book.getAvailableQuantity() - 1);

        String recId = generateNextRecordId();
        BorrowRecord record = new BorrowRecord(
            recId,
            member.getId(),
            member.getName(),
            book.getId(),
            book.getTitle(),
            issueDate,
            dueDate,
            null,
            "Borrowed",
            0.00,
            notes != null ? notes : ""
        );
        borrowRecords.add(0, record);
        return record;
    }

    // Return book
    public void returnBook(BorrowRecord record, LocalDate returnDate, double fine) {
        record.setReturnDate(returnDate);
        record.setStatus("Returned");
        record.setFine(fine);

        // restock book quantity
        Book book = findBookById(record.getBookId());
        if (book != null) {
            book.setAvailableQuantity(Math.min(book.getQuantity(), book.getAvailableQuantity() + 1));
        }

        // Trigger refresh
        int idx = borrowRecords.indexOf(record);
        if (idx >= 0) {
            borrowRecords.set(idx, record);
        }
    }

    // Dashboard metrics
    public int getTotalBooksCount() {
        return books.stream().mapToInt(Book::getQuantity).sum();
    }

    public int getTotalDistinctBooksCount() {
        return books.size();
    }

    public int getTotalMembersCount() {
        return members.size();
    }

    public long getCurrentlyBorrowedCount() {
        return borrowRecords.stream().filter(r -> r.getReturnDate() == null).count();
    }

    public long getOverdueCount() {
        return borrowRecords.stream().filter(r -> r.getReturnDate() == null && r.getDueDate() != null && LocalDate.now().isAfter(r.getDueDate())).count();
    }
}
