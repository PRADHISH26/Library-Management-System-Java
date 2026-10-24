package com.library.service;

import com.library.model.Book;
import com.library.model.Member;
import com.library.model.Transaction;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Core library service. Books and members live in HashMaps for O(1) lookup;
 * transactions are kept in an ArrayList as a chronological log.
 */
public class Library {
    private final HashMap<String, Book> books = new HashMap<>();
    private final HashMap<String, Member> members = new HashMap<>();
    private final ArrayList<Transaction> transactions = new ArrayList<>();
    private int nextMemberSeq = 1;
    private int nextTxnSeq = 1;

    // ---- Books ----

    public void addBook(String isbn, String title, String author, String genre, int copies) {
        Book existing = books.get(isbn);
        if (existing != null) {
            existing.setAvailableCopies(existing.getAvailableCopies() + copies);
        } else {
            books.put(isbn, new Book(isbn, title, author, genre, copies));
        }
    }

    public Book getBook(String isbn) { return books.get(isbn); }

    public ArrayList<Book> listBooks() { return new ArrayList<>(books.values()); }

    /** Case-insensitive search over title, author and genre. */
    public ArrayList<Book> searchBooks(String keyword) {
        ArrayList<Book> hits = new ArrayList<>();
        String needle = keyword.toLowerCase();
        for (Book b : books.values()) {
            if (b.getTitle().toLowerCase().contains(needle)
                    || b.getAuthor().toLowerCase().contains(needle)
                    || b.getGenre().toLowerCase().contains(needle)) {
                hits.add(b);
            }
        }
        return hits;
    }

    // ---- Members ----

    public Member registerMember(String name, String email) {
        String id = "M" + String.format("%03d", nextMemberSeq++);
        Member m = new Member(id, name, email);
        members.put(id, m);
        return m;
    }

    public Member getMember(String id) { return members.get(id); }

    public ArrayList<Member> listMembers() { return new ArrayList<>(members.values()); }

    // ---- Borrow / return ----

    /** Borrows a book. Throws IllegalStateException with a readable reason on failure. */
    public Transaction borrowBook(String memberId, String isbn) {
        Member member = members.get(memberId);
        if (member == null) throw new IllegalStateException("No member with id " + memberId);
        Book book = books.get(isbn);
        if (book == null) throw new IllegalStateException("No book with ISBN " + isbn);
        if (!member.canBorrow())
            throw new IllegalStateException(member.getName() + " already holds the max " + Member.MAX_BOOKS + " books");
        if (member.getBorrowedIsbns().contains(isbn))
            throw new IllegalStateException(member.getName() + " already has a copy of this book");
        if (!book.isAvailable())
            throw new IllegalStateException("No copies of '" + book.getTitle() + "' are available right now");

        book.borrowCopy();
        member.addBorrowed(isbn);
        String txnId = "T" + String.format("%04d", nextTxnSeq++);
        Transaction txn = new Transaction(txnId, isbn, memberId, LocalDate.now());
        transactions.add(txn);
        return txn;
    }

    /** Returns a borrowed book; returns the fine charged (0 when on time). */
    public double returnBook(String memberId, String isbn) {
        Member member = members.get(memberId);
        if (member == null) throw new IllegalStateException("No member with id " + memberId);
        Transaction open = null;
        for (Transaction t : transactions) {
            if (t.isActive() && t.getMemberId().equals(memberId) && t.getIsbn().equals(isbn)) {
                open = t;
                break;
            }
        }
        if (open == null)
            throw new IllegalStateException("No active loan of " + isbn + " for member " + memberId);

        double fine = open.close(LocalDate.now());
        Book book = books.get(isbn);
        if (book != null) book.returnCopy();
        member.removeBorrowed(isbn);
        return fine;
    }

    public ArrayList<Transaction> listTransactions() { return new ArrayList<>(transactions); }

    public ArrayList<Transaction> activeTransactions() {
        ArrayList<Transaction> open = new ArrayList<>();
        for (Transaction t : transactions) if (t.isActive()) open.add(t);
        return open;
    }

    // ---- Persistence hooks (used by FileStore) ----

    void loadBook(Book b) { books.put(b.getIsbn(), b); }
    void loadMember(Member m) {
        members.put(m.getId(), m);
        int seq = Integer.parseInt(m.getId().substring(1)) + 1;
        if (seq > nextMemberSeq) nextMemberSeq = seq;
    }
    void loadTransaction(Transaction t) {
        transactions.add(t);
        int seq = Integer.parseInt(t.getId().substring(1)) + 1;
        if (seq > nextTxnSeq) nextTxnSeq = seq;
    }
}
