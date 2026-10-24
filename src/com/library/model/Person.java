package com.library.model;

/** Abstract base class for people in the library system (OOP: abstraction + inheritance). */
public abstract class Person {
    private final String id;
    private String name;
    private String email;

    protected Person(String id, String name, String email) {
        this.id = id;
        this.name = name;
        this.email = email;
    }

    public String getId() { return id; }
    public String getName() { return name; }
    public String getEmail() { return email; }

    public void setName(String name) { this.name = name; }
    public void setEmail(String email) { this.email = email; }

    /** Every kind of person tells us their role (OOP: polymorphism). */
    public abstract String getRole();

    @Override
    public String toString() {
        return String.format("%s [%s] %s <%s>", getRole(), id, name, email);
    }
}
