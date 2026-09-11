package example;

import java.time.LocalDate;
import java.util.*;

/**
 * A comprehensive library management system demonstrating various Java concepts
 */
public class LibrarySystem {
    // Class fields
    private String libraryName;
    private String address;
    private List<Book> books;
    private List<Member> members;
    private Map<String, Set<Book>> borrowedBooks;
    private static int totalLibraries = 0;

    // Constructor
    public LibrarySystem(String libraryName, String address) {
        this.libraryName = libraryName;
        this.address = address;
        this.books = new ArrayList<>();
        this.members = new ArrayList<>();
        this.borrowedBooks = new HashMap<>();
        totalLibraries++;
    }

    // Methods
    public void addBook(String title, String author, String isbn, int publicationYear) {
        Book newBook = new Book(title, author, isbn, publicationYear);
        books.add(newBook);
        System.out.println("Added book: " + title);
    }

    public void registerMember(String name, String email, String phoneNumber) {
        Member newMember = new Member(name, email, phoneNumber);
        members.add(newMember);
        borrowedBooks.put(newMember.getMemberId(), new HashSet<>());
        System.out.println("Registered member: " + name + " with ID: " + newMember.getMemberId());
    }

    public boolean borrowBook(String memberId, String isbn) {
        Member member = findMemberById(memberId);
        Book book = findBookByIsbn(isbn);

        if (member != null && book != null && book.isAvailable()) {
            book.setAvailable(false);
            book.setBorrowDate(LocalDate.now());
            borrowedBooks.get(memberId).add(book);
            member.incrementBorrowedBooks();
            System.out.println(member.getName() + " borrowed: " + book.getTitle());
            return true;
        }
        return false;
    }

    public boolean returnBook(String memberId, String isbn) {
        Member member = findMemberById(memberId);
        Book book = findBookByIsbn(isbn);

        if (member != null && book != null && !book.isAvailable()) {
            book.setAvailable(true);
            book.setBorrowDate(null);
            borrowedBooks.get(memberId).remove(book);
            member.decrementBorrowedBooks();
            System.out.println(member.getName() + " returned: " + book.getTitle());
            return true;
        }
        return false;
    }

    public void displayLibraryInfo() {
        System.out.println("=== " + libraryName + " ===");
        System.out.println("Address: " + address);
        System.out.println("Total Books: " + books.size());
        System.out.println("Total Members: " + members.size());
        System.out.println("Available Books: " + getAvailableBookCount());
    }

    private Member findMemberById(String memberId) {
        return members.stream()
                .filter(member -> member.getMemberId().equals(memberId))
                .findFirst()
                .orElse(null);
    }

    private Book findBookByIsbn(String isbn) {
        return books.stream()
                .filter(book -> book.getIsbn().equals(isbn))
                .findFirst()
                .orElse(null);
    }

    private long getAvailableBookCount() {
        return books.stream().filter(Book::isAvailable).count();
    }

    // Getters and setters
    public String getLibraryName() {
        return libraryName;
    }

    public String getAddress() {
        return address;
    }

    public static int getTotalLibraries() {
        return totalLibraries;
    }

    // Inner class - Book
    public class Book {
        private String title;
        private String author;
        private String isbn;
        private int publicationYear;
        private boolean available;
        private LocalDate borrowDate;
        private BookCategory category;

        public Book(String title, String author, String isbn, int publicationYear) {
            this.title = title;
            this.author = author;
            this.isbn = isbn;
            this.publicationYear = publicationYear;
            this.available = true;
            this.category = new BookCategory();
        }

        public void displayBookInfo() {
            System.out.println("Title: " + title);
            System.out.println("Author: " + author);
            System.out.println("ISBN: " + isbn);
            System.out.println("Publication Year: " + publicationYear);
            System.out.println("Available: " + (available ? "Yes" : "No"));
            if (borrowDate != null) {
                System.out.println("Borrowed on: " + borrowDate);
            }
        }

        public boolean isOverdue() {
            if (borrowDate != null && !available) {
                return LocalDate.now().isAfter(borrowDate.plusDays(14));
            }
            return false;
        }

        public void setCategory(String categoryName, String description) {
            this.category.setCategoryName(categoryName);
            this.category.setDescription(description);
        }

        // Getters and setters
        public String getTitle() {
            return title;
        }

        public String getAuthor() {
            return author;
        }

        public String getIsbn() {
            return isbn;
        }

        public int getPublicationYear() {
            return publicationYear;
        }

        public boolean isAvailable() {
            return available;
        }

        public void setAvailable(boolean available) {
            this.available = available;
        }

        public LocalDate getBorrowDate() {
            return borrowDate;
        }

        public void setBorrowDate(LocalDate borrowDate) {
            this.borrowDate = borrowDate;
        }

        // Inner class within Book
        public class BookCategory {
            private String categoryName;
            private String description;
            private int popularityRank;

            public BookCategory() {
                this.categoryName = "General";
                this.description = "General category";
                this.popularityRank = 0;
            }

            public void updatePopularityRank() {
                // Simple logic to update rank based on library's borrowed books
                long categoryBorrowCount = LibrarySystem.this.books.stream()
                        .filter(book -> !book.isAvailable() &&
                                book.category.categoryName.equals(this.categoryName))
                        .count();
                this.popularityRank = (int) categoryBorrowCount;
            }

            public void displayCategoryInfo() {
                System.out.println("Category: " + categoryName);
                System.out.println("Description: " + description);
                System.out.println("Popularity Rank: " + popularityRank);
            }

            // Getters and setters
            public String getCategoryName() {
                return categoryName;
            }

            public void setCategoryName(String categoryName) {
                this.categoryName = categoryName;
            }

            public String getDescription() {
                return description;
            }

            public void setDescription(String description) {
                this.description = description;
            }

            public int getPopularityRank() {
                return popularityRank;
            }
        }
    }

    // Static nested class - Member
    public static class Member {
        private String memberId;
        private String name;
        private String email;
        private String phoneNumber;
        private LocalDate membershipDate;
        private int borrowedBooksCount;
        private MembershipLevel membershipLevel;
        private static int memberCounter = 0;

        public Member(String name, String email, String phoneNumber) {
            this.memberId = "MEM" + String.format("%04d", ++memberCounter);
            this.name = name;
            this.email = email;
            this.phoneNumber = phoneNumber;
            this.membershipDate = LocalDate.now();
            this.borrowedBooksCount = 0;
            this.membershipLevel = new MembershipLevel();
        }

        public void displayMemberInfo() {
            System.out.println("Member ID: " + memberId);
            System.out.println("Name: " + name);
            System.out.println("Email: " + email);
            System.out.println("Phone: " + phoneNumber);
            System.out.println("Member since: " + membershipDate);
            System.out.println("Books currently borrowed: " + borrowedBooksCount);
            membershipLevel.displayLevelInfo();
        }

        public void incrementBorrowedBooks() {
            borrowedBooksCount++;
            membershipLevel.updateLevel(this);
        }

        public void decrementBorrowedBooks() {
            if (borrowedBooksCount > 0) {
                borrowedBooksCount--;
            }
        }

        public boolean canBorrowMore() {
            return borrowedBooksCount < membershipLevel.getMaxBooksAllowed();
        }

        // Getters and setters
        public String getMemberId() {
            return memberId;
        }

        public String getName() {
            return name;
        }

        public String getEmail() {
            return email;
        }

        public String getPhoneNumber() {
            return phoneNumber;
        }

        public LocalDate getMembershipDate() {
            return membershipDate;
        }

        public int getBorrowedBooksCount() {
            return borrowedBooksCount;
        }

        // Static nested class within Member
        public static class MembershipLevel {
            private String levelName;
            private int maxBooksAllowed;
            private int borrowPeriodDays;
            private double finePerDay;

            public MembershipLevel() {
                this.levelName = "Bronze";
                this.maxBooksAllowed = 3;
                this.borrowPeriodDays = 14;
                this.finePerDay = 0.50;
            }

            public void updateLevel(Member member) {
                int totalBooksEverBorrowed = member.getBorrowedBooksCount();
                long membershipDays = java.time.temporal.ChronoUnit.DAYS.between(
                        member.getMembershipDate(), LocalDate.now());

                if (membershipDays > 365 && totalBooksEverBorrowed > 50) {
                    setToGoldLevel();
                } else if (membershipDays > 180 && totalBooksEverBorrowed > 20) {
                    setToSilverLevel();
                } else {
                    setToBronzeLevel();
                }
            }

            private void setToBronzeLevel() {
                this.levelName = "Bronze";
                this.maxBooksAllowed = 3;
                this.borrowPeriodDays = 14;
                this.finePerDay = 0.50;
            }

            private void setToSilverLevel() {
                this.levelName = "Silver";
                this.maxBooksAllowed = 5;
                this.borrowPeriodDays = 21;
                this.finePerDay = 0.25;
            }

            private void setToGoldLevel() {
                this.levelName = "Gold";
                this.maxBooksAllowed = 10;
                this.borrowPeriodDays = 30;
                this.finePerDay = 0.10;
            }

            public void displayLevelInfo() {
                System.out.println("Membership Level: " + levelName);
                System.out.println("Max Books Allowed: " + maxBooksAllowed);
                System.out.println("Borrow Period: " + borrowPeriodDays + " days");
                System.out.println("Fine per day: $" + finePerDay);
            }

            public double calculateFine(int daysOverdue) {
                return daysOverdue * finePerDay;
            }

            // Getters
            public String getLevelName() {
                return levelName;
            }

            public int getMaxBooksAllowed() {
                return maxBooksAllowed;
            }

            public int getBorrowPeriodDays() {
                return borrowPeriodDays;
            }

            public double getFinePerDay() {
                return finePerDay;
            }
        }
    }

    // Main method for demonstration
    public static void main(String[] args) {
        LibrarySystem library = new LibrarySystem("Central City Library", "123 Main Street");

        // Add some books
        library.addBook("The Great Gatsby", "F. Scott Fitzgerald", "978-0-7432-7356-5", 1925);
        library.addBook("To Kill a Mockingbird", "Harper Lee", "978-0-06-112008-4", 1960);
        library.addBook("1984", "George Orwell", "978-0-452-28423-4", 1949);

        // Register members
        library.registerMember("Alice Johnson", "alice@email.com", "555-0101");
        library.registerMember("Bob Smith", "bob@email.com", "555-0102");

        // Display library info
        library.displayLibraryInfo();

        System.out.println("\nTotal libraries created: " + LibrarySystem.getTotalLibraries());
    }
}