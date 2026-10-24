# Library Management System (Java)

A terminal-based library management system written in plain Java. Built to
demonstrate core Java concepts:

- **OOP** - abstract `Person` base class with `Member` inheritance and
  polymorphic roles, encapsulation in `Book` / `Transaction`
- **ArrayList** - transaction log, search results, member borrowing lists
- **HashMap** - O(1) lookup of books (by ISBN) and members (by ID)
- **File I/O** - the whole library persists to plain-text files in `data/`
  and reloads on startup
- **Date & Time API** (`java.time`) - issue dates, 14-day due dates and
  overdue fines computed with `LocalDate` and `ChronoUnit`

## Features

- Add and list books (ISBN, title, author, genre, copy counts)
- Case-insensitive search across title, author and genre
- Register and list members (auto IDs, max 3 books per member)
- Borrow a book: validates member, availability and duplicate loans;
  prints the due date (14 days)
- Return a book: charges a fine of 2/day for every overdue day
- View active loans (with days left / overdue), full transaction history
  and a member's current books
- Auto-saves after every change

## Run it

Needs any JDK 8+ (uses only the standard library).

```bash
# compile
javac -d out $(find src -name "*.java")

# run
java -cp out com.library.app.LibraryApp
```

Or with a JDK 11+ you can run it straight from source, no compile step:

```bash
java src/com/library/app/LibraryApp.java
```

Data files are written to `data/` in the working directory. Delete that
folder to start with an empty library.

## Project layout

```
src/com/library/
  app/LibraryApp.java      terminal menu
  model/Person.java        abstract base class
  model/Member.java        a registered member
  model/Book.java          catalog entry with copy counts
  model/Transaction.java   one borrow/return record (dates + fine)
  service/Library.java     business logic (HashMap catalog, ArrayList log)
  service/FileStore.java   load/save to plain-text files
```
