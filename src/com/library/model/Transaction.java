package com.library.model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/** One borrow/return record. Uses the java.time Date & Time API for dates and fines. */
public class Transaction {
    public static final int LOAN_DAYS = 14;
    public static final double FINE_PER_DAY = 2.0; // currency units per overdue day
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd MMM yyyy");

    private final String id;
    private final String isbn;
    private final String memberId;
    private final LocalDate issueDate;
    private final LocalDate dueDate;
    private LocalDate returnDate; // null while the loan is active
    private double fine;

    public Transaction(String id, String isbn, String memberId, LocalDate issueDate) {
        this.id = id;
        this.isbn = isbn;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.dueDate = issueDate.plusDays(LOAN_DAYS);
    }

    /** Full constructor used when loading saved transactions from file. */
    public Transaction(String id, String isbn, String memberId,
                       LocalDate issueDate, LocalDate dueDate,
                       LocalDate returnDate, double fine) {
        this.id = id;
        this.isbn = isbn;
        this.memberId = memberId;
        this.issueDate = issueDate;
        this.dueDate = dueDate;
        this.returnDate = returnDate;
        this.fine = fine;
    }

    public String getId() { return id; }
    public String getIsbn() { return isbn; }
    public String getMemberId() { return memberId; }
    public LocalDate getIssueDate() { return issueDate; }
    public LocalDate getDueDate() { return dueDate; }
    public LocalDate getReturnDate() { return returnDate; }
    public double getFine() { return fine; }

    public boolean isActive() { return returnDate == null; }

    /** Days overdue as of the given date; 0 when on time. */
    public long overdueDays(LocalDate asOf) {
        if (asOf.isAfter(dueDate)) return ChronoUnit.DAYS.between(dueDate, asOf);
        return 0;
    }

    /** Closes the loan and computes the overdue fine (0 when returned on time). */
    public double close(LocalDate date) {
        this.returnDate = date;
        this.fine = overdueDays(date) * FINE_PER_DAY;
        return fine;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append(String.format("%s | book %s | member %s | issued %s | due %s",
                id, isbn, memberId, issueDate.format(FMT), dueDate.format(FMT)));
        if (isActive()) {
            long left = ChronoUnit.DAYS.between(LocalDate.now(), dueDate);
            if (left >= 0) sb.append(" | ACTIVE (").append(left).append(" days left)");
            else sb.append(" | ACTIVE (OVERDUE by ").append(-left).append(" days)");
        } else {
            sb.append(" | returned ").append(returnDate.format(FMT));
            sb.append(String.format(" | fine %.2f", fine));
        }
        return sb.toString();
    }
}
