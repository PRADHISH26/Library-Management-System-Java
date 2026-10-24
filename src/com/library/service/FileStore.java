package com.library.service;

import com.library.model.Book;
import com.library.model.Member;
import com.library.model.Transaction;

import java.io.*;
import java.nio.file.*;
import java.time.LocalDate;

/**
 * File I/O: persists the whole library to plain-text files in ./data and
 * reloads them on startup. Line formats (pipe-delimited):
 *   books.txt        isbn|title|author|genre|totalCopies|availableCopies
 *   members.txt      id|name|email|isbn1,isbn2,...
 *   transactions.txt id|isbn|memberId|issueDate|dueDate|returnDate(-)|fine
 */
public class FileStore {
    private final Path dir;

    public FileStore(String directory) {
        this.dir = Paths.get(directory);
    }

    public void save(Library lib) throws IOException {
        Files.createDirectories(dir);

        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(dir.resolve("books.txt")))) {
            for (Book b : lib.listBooks()) {
                out.println(String.join("|", b.getIsbn(), b.getTitle(), b.getAuthor(),
                        b.getGenre(), String.valueOf(b.getTotalCopies()),
                        String.valueOf(b.getAvailableCopies())));
            }
        }
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(dir.resolve("members.txt")))) {
            for (Member m : lib.listMembers()) {
                out.println(String.join("|", m.getId(), m.getName(), m.getEmail(),
                        String.join(",", m.getBorrowedIsbns())));
            }
        }
        try (PrintWriter out = new PrintWriter(Files.newBufferedWriter(dir.resolve("transactions.txt")))) {
            for (Transaction t : lib.listTransactions()) {
                out.println(String.join("|", t.getId(), t.getIsbn(), t.getMemberId(),
                        t.getIssueDate().toString(), t.getDueDate().toString(),
                        t.getReturnDate() == null ? "-" : t.getReturnDate().toString(),
                        String.valueOf(t.getFine())));
            }
        }
    }

    /** Loads saved state into the library; silently starts fresh when no files exist. */
    public void load(Library lib) {
        Path booksFile = dir.resolve("books.txt");
        if (Files.exists(booksFile)) {
            for (String line : readLines(booksFile)) {
                String[] p = line.split("\\|", -1);
                Book b = new Book(p[0], p[1], p[2], p[3], Integer.parseInt(p[4]));
                b.setAvailableCopies(Integer.parseInt(p[5]));
                lib.loadBook(b);
            }
        }
        Path membersFile = dir.resolve("members.txt");
        if (Files.exists(membersFile)) {
            for (String line : readLines(membersFile)) {
                String[] p = line.split("\\|", -1);
                Member m = new Member(p[0], p[1], p[2]);
                if (p.length > 3 && !p[3].isEmpty()) {
                    for (String isbn : p[3].split(",")) m.addBorrowed(isbn);
                }
                lib.loadMember(m);
            }
        }
        Path txnFile = dir.resolve("transactions.txt");
        if (Files.exists(txnFile)) {
            for (String line : readLines(txnFile)) {
                String[] p = line.split("\\|", -1);
                LocalDate returned = p[5].equals("-") ? null : LocalDate.parse(p[5]);
                lib.loadTransaction(new Transaction(p[0], p[1], p[2],
                        LocalDate.parse(p[3]), LocalDate.parse(p[4]), returned,
                        Double.parseDouble(p[6])));
            }
        }
    }

    private java.util.List<String> readLines(Path file) {
        try {
            return Files.readAllLines(file);
        } catch (IOException e) {
            System.out.println("Warning: could not read " + file + " (" + e.getMessage() + ")");
            return java.util.Collections.emptyList();
        }
    }
}
