package com.library.model;

import java.util.ArrayList;

/** A registered library member. Tracks the ISBNs they currently hold. */
public class Member extends Person {
    public static final int MAX_BOOKS = 3;

    private final ArrayList<String> borrowedIsbns = new ArrayList<>();

    public Member(String id, String name, String email) {
        super(id, name, email);
    }

    @Override
    public String getRole() { return "Member"; }

    public ArrayList<String> getBorrowedIsbns() { return borrowedIsbns; }

    public boolean canBorrow() { return borrowedIsbns.size() < MAX_BOOKS; }

    public void addBorrowed(String isbn) {
        if (!borrowedIsbns.contains(isbn)) borrowedIsbns.add(isbn);
    }

    public void removeBorrowed(String isbn) { borrowedIsbns.remove(isbn); }
}
