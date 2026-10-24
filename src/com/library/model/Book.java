package com.library.model;

/** A book in the catalog, tracked by ISBN with copy counts (OOP: encapsulation). */
public class Book {
    private final String isbn;
    private String title;
    private String author;
    private String genre;
    private int totalCopies;
    private int availableCopies;

    public Book(String isbn, String title, String author, String genre, int totalCopies) {
        this.isbn = isbn;
        this.title = title;
        this.author = author;
        this.genre = genre;
        this.totalCopies = totalCopies;
        this.availableCopies = totalCopies;
    }

    public String getIsbn() { return isbn; }
    public String getTitle() { return title; }
    public String getAuthor() { return author; }
    public String getGenre() { return genre; }
    public int getTotalCopies() { return totalCopies; }
    public int getAvailableCopies() { return availableCopies; }

    public void setAvailableCopies(int availableCopies) { this.availableCopies = availableCopies; }

    public boolean isAvailable() { return availableCopies > 0; }

    /** Marks one copy as borrowed; false when no copies are left. */
    public boolean borrowCopy() {
        if (!isAvailable()) return false;
        availableCopies--;
        return true;
    }

    /** Marks one copy as returned. */
    public void returnCopy() {
        if (availableCopies < totalCopies) availableCopies++;
    }

    @Override
    public String toString() {
        return String.format("[%s] %s by %s (%s) - %d/%d available",
                isbn, title, author, genre, availableCopies, totalCopies);
    }
}
