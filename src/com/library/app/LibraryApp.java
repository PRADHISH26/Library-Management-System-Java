package com.library.app;

import com.library.model.Book;
import com.library.model.Member;
import com.library.model.Transaction;
import com.library.service.FileStore;
import com.library.service.Library;

import java.util.ArrayList;
import java.util.Scanner;

/** Terminal-based menu app for the library management system. */
public class LibraryApp {
    private final Library library = new Library();
    private final FileStore store = new FileStore("data");
    private final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        new LibraryApp().run();
    }

    private void run() {
        store.load(library);
        System.out.println("====================================");
        System.out.println("   LIBRARY MANAGEMENT SYSTEM");
        System.out.println("====================================");
        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            System.out.println();
            switch (choice) {
                case "1": addBook(); break;
                case "2": listBooks(); break;
                case "3": searchBooks(); break;
                case "4": registerMember(); break;
                case "5": listMembers(); break;
                case "6": borrowBook(); break;
                case "7": returnBook(); break;
                case "8": listActiveLoans(); break;
                case "9": listAllTransactions(); break;
                case "10": viewMemberBooks(); break;
                case "0": running = false; break;
                default: System.out.println("Unknown option, try again.");
            }
        }
        save();
        System.out.println("Data saved. Goodbye!");
    }

    private void printMenu() {
        System.out.println();
        System.out.println("---------- MENU ----------");
        System.out.println(" 1. Add a book");
        System.out.println(" 2. List all books");
        System.out.println(" 3. Search books");
        System.out.println(" 4. Register a member");
        System.out.println(" 5. List members");
        System.out.println(" 6. Borrow a book");
        System.out.println(" 7. Return a book");
        System.out.println(" 8. View active loans");
        System.out.println(" 9. View all transactions");
        System.out.println("10. View a member's books");
        System.out.println(" 0. Save & exit");
        System.out.print("Choose: ");
    }

    private void addBook() {
        String isbn = prompt("ISBN: ");
        String title = prompt("Title: ");
        String author = prompt("Author: ");
        String genre = prompt("Genre: ");
        int copies = promptInt("Copies: ");
        if (copies <= 0) { System.out.println("Copies must be at least 1."); return; }
        library.addBook(isbn, title, author, genre, copies);
        System.out.println("Book saved: " + library.getBook(isbn));
        save();
    }

    private void listBooks() {
        ArrayList<Book> books = library.listBooks();
        if (books.isEmpty()) { System.out.println("No books in the catalog yet."); return; }
        books.sort((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()));
        for (Book b : books) System.out.println(" " + b);
    }

    private void searchBooks() {
        String keyword = prompt("Search title/author/genre: ");
        ArrayList<Book> hits = library.searchBooks(keyword);
        if (hits.isEmpty()) { System.out.println("No matches for '" + keyword + "'."); return; }
        for (Book b : hits) System.out.println(" " + b);
    }

    private void registerMember() {
        String name = prompt("Name: ");
        String email = prompt("Email: ");
        Member m = library.registerMember(name, email);
        System.out.println("Registered: " + m);
        save();
    }

    private void listMembers() {
        ArrayList<Member> members = library.listMembers();
        if (members.isEmpty()) { System.out.println("No members registered yet."); return; }
        members.sort((a, b) -> a.getId().compareTo(b.getId()));
        for (Member m : members)
            System.out.println(" " + m + " | holding " + m.getBorrowedIsbns().size() + " book(s)");
    }

    private void borrowBook() {
        String memberId = prompt("Member ID: ");
        String isbn = prompt("ISBN: ");
        try {
            Transaction txn = library.borrowBook(memberId, isbn);
            System.out.println("Borrowed! Due back by "
                    + txn.getDueDate().format(java.time.format.DateTimeFormatter.ofPattern("dd MMM yyyy"))
                    + " (" + Transaction.LOAN_DAYS + " days).");
            save();
        } catch (IllegalStateException e) {
            System.out.println("Cannot borrow: " + e.getMessage());
        }
    }

    private void returnBook() {
        String memberId = prompt("Member ID: ");
        String isbn = prompt("ISBN: ");
        try {
            double fine = library.returnBook(memberId, isbn);
            if (fine > 0) System.out.printf("Returned. Overdue fine: %.2f%n", fine);
            else System.out.println("Returned on time. No fine.");
            save();
        } catch (IllegalStateException e) {
            System.out.println("Cannot return: " + e.getMessage());
        }
    }

    private void listActiveLoans() {
        ArrayList<Transaction> open = library.activeTransactions();
        if (open.isEmpty()) { System.out.println("No active loans."); return; }
        for (Transaction t : open) System.out.println(" " + t);
    }

    private void listAllTransactions() {
        ArrayList<Transaction> all = library.listTransactions();
        if (all.isEmpty()) { System.out.println("No transactions yet."); return; }
        for (Transaction t : all) System.out.println(" " + t);
    }

    private void viewMemberBooks() {
        String memberId = prompt("Member ID: ");
        Member m = library.getMember(memberId);
        if (m == null) { System.out.println("No member with id " + memberId); return; }
        if (m.getBorrowedIsbns().isEmpty()) {
            System.out.println(m.getName() + " has no books right now.");
            return;
        }
        System.out.println(m.getName() + " is holding:");
        for (String isbn : m.getBorrowedIsbns()) {
            Book b = library.getBook(isbn);
            System.out.println("  - " + (b == null ? isbn : b.getTitle() + " [" + isbn + "]"));
        }
    }

    private String prompt(String label) {
        System.out.print(label);
        return scanner.nextLine().trim();
    }

    private int promptInt(String label) {
        try {
            return Integer.parseInt(prompt(label));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void save() {
        try {
            store.save(library);
        } catch (java.io.IOException e) {
            System.out.println("Warning: could not save data (" + e.getMessage() + ")");
        }
    }
}
