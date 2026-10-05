# Atlas Library Management System (JavaFX)

A modern, desktop-based Library Management System developed with **JavaFX**, **FXML**, **Scene Builder**, and clean **CSS** styling. Designed specifically for small-to-medium libraries to streamline books, members, and circulation workflows.

---

## 🌟 Key Features & Implemented Interfaces

### 1. 🔐 Login Page (`Login.fxml`)
- **Credentials Input**: Username and password fields with clean placeholder prompts.
- **Validation & Error Handling**: Inline error messaging and JavaFX Alert dialogs for missing or invalid credentials.
- **Default Credentials**: `admin` / `admin123` or `librarian` / `librarian`.
- **Keyboard Shortcut**: Pressing `Enter` automatically submits the login form.
- **Clear / Reset**: Clears input fields and resets error states.

### 2. 📊 Main Dashboard (`DashboardHome.fxml` inside `MainLayout.fxml`)
- **Collapsible Navigation Sidebar**:
  - Direct routing to Dashboard, Add Book, Add Member, Manage Members, Issue Book, Return Book, and Borrowing History.
  - Active screen highlighted with indicator bar.
  - Profile card displaying librarian session status.
  - Secure **Logout** option with confirmation dialog.
- **Real-Time KPI Metric Cards**:
  - 📚 **Total Books** in catalog
  - 👥 **Total Registered Members**
  - 📖 **Currently Borrowed Books**
  - ⚠️ **Overdue Books** requiring attention
- **Quick Action Bar**: 1-click shortcuts to Issue, Return, Add Member, or Add Book.
- **Recent Circulation Table**: Instant preview of the latest 5 transactions with color-coded status badges.

### 3. ➕ Add Book Page (`AddBook.fxml`)
- **Fields**:
  - Book ID / ISBN (auto-suggested next ID, unique check)
  - Book Title
  - Author Name
  - Category (ComboBox: Fiction, Science, Technology, History, etc.)
  - Published Year (validates 4-digit valid calendar year)
  - Quantity (validates positive integer)
- **Validation**: Strict validation with JavaFX Alert dialogs.
- **Side Catalog Preview**: Real-time TableView displaying existing books in the catalog.
- **Clear Button**: Resets form and generates the next incremental ID.

### 4. 👤 Add Member Page (`AddMember.fxml`)
- **Fields**:
  - Member ID (auto-suggested next ID, unique check)
  - Full Name
  - Email Address (regex validation)
  - Phone Number (phone format validation)
  - Postal / Residential Address
- **Live Enrollment Roster**: TableView on the right displaying recently enrolled patrons.
- **Clear Button**: Resets form and increments ID counter.

### 5. 👥 View / Manage Members Page (`ManageMembers.fxml`)
- **Interactive TableView**: Displays Member ID, Full Name, Email, Phone, Address, and Enrollment Date.
- **Real-Time Search Bar**: Dynamically filters members as you type by Member ID, Name, Email, or Phone.
- **Master-Detail Edit Drawer**:
  - Clicking any row loads patron details into an edit form.
  - Update patron name, email, phone, or address with 1-click.
- **Safe Member Deletion**: Triggers a **JavaFX Confirmation Alert** before removing a member.

### 6. 📖 Issue Book Page (`IssueBook.fxml`)
- **Member Selection**: ComboBox with auto-loading member details (phone, email, ID).
- **Book Selection**: ComboBox with live inventory stock verification.
- **Inventory Check**: Prevents loaning if stock is depleted (`availableQuantity <= 0`).
- **Dates Handling**:
  - Issue Date (DatePicker, default to today).
  - Due Date (DatePicker, default to +14 days).
  - Validation ensures Due Date is not before Issue Date.
- **Confirmation Alert**: Displays loan summary before committing the transaction.

### 7. 🔄 Return Book Page (`ReturnBook.fxml`)
- **Active Loans Table**: Real-time list of outstanding borrowed books with quick filter search.
- **1-Click Selection**: Clicking any active loan populates the transaction panel.
- **Live Overdue Assessment**:
  - Automatically calculates days overdue based on selected return date.
  - Real-time late fee computation ($0.50/day penalty).
  - Prominent overdue warning badge if deadline was exceeded.
- **Restocking**: Automatically increments available stock upon return completion.

### 8. 📜 Borrowing History Page (`BorrowingHistory.fxml`)
- **Audit Log TableView**:
  - Transaction Record ID, Member ID, Member Name, Book Title, Issue Date, Due Date, Return Date, Status, and Fine.
- **Dynamic Status Filter**: Dropdown to view All Records, Active Loans, Returned Books, or Overdue Books.
- **Instant Search**: Search across Member ID, Name, Book Title, or Transaction ID.
- **Color-Coded Status Badges**:
  - 🔵 `Borrowed`
  - 🟢 `Returned`
  - 🔴 `Overdue`

---

## 🛠️ Architecture & Package Structure

```
ICET PROJECT/
├── pom.xml
├── run.sh
├── README.md
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   ├── module-info.java
│   │   │   └── org/library/
│   │   │       ├── App.java
│   │   │       ├── controller/
│   │   │       │   ├── LoginController.java
│   │   │       │   ├── MainLayoutController.java
│   │   │       │   ├── DashboardHomeController.java
│   │   │       │   ├── AddBookController.java
│   │   │       │   ├── AddMemberController.java
│   │   │       │   ├── ManageMembersController.java
│   │   │       │   ├── IssueBookController.java
│   │   │       │   ├── ReturnBookController.java
│   │   │       │   └── BorrowingHistoryController.java
│   │   │       ├── model/
│   │   │       │   ├── Book.java
│   │   │       │   ├── Member.java
│   │   │       │   └── BorrowRecord.java
│   │   │       ├── service/
│   │   │       │   └── LibraryDataService.java
│   │   │       └── util/
│   │   │           ├── AlertUtil.java
│   │   │           ├── NavigationManager.java
│   │   │           └── ValidationUtil.java
│   │   └── resources/
│   │       └── org/library/
│   │           ├── css/
│   │           │   └── styles.css
│   │           └── view/
│   │               ├── Login.fxml
│   │               ├── MainLayout.fxml
│   │               ├── DashboardHome.fxml
│   │               ├── AddBook.fxml
│   │               ├── AddMember.fxml
│   │               ├── ManageMembers.fxml
│   │               ├── IssueBook.fxml
│   │               ├── ReturnBook.fxml
│   │               └── BorrowingHistory.fxml
│   └── test/
│       └── java/org/library/
│           ├── LibraryServiceTest.java
│           └── FxmlViewsTest.java
```

---

## 🚀 How to Run the Application

### Option 1: Quick Shell Script
```bash
./run.sh
```

### Option 2: Maven Command
```bash
mvn javafx:run
```

### Option 3: Run Tests
```bash
mvn test
```

---

## 🔑 Login Credentials
- **Username**: `admin`
- **Password**: `admin123`
*(Alternative: `librarian` / `librarian`)*
