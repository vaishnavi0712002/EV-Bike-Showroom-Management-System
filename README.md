# EV Bike Showroom Management System

A desktop Java Swing application (with MySQL database) for managing an
Electric Bike showroom: bike inventory, customers, sales/billing, and
test-ride bookings.

## Features
- Login screen (staff/admin authentication)
- Dashboard with navigation
- **Bike Management** — add / update / delete / view EV bike inventory
- **Customer Management** — add / update / delete / view customers
- **Sales / Billing** — record a sale, auto-decrease stock, view sales history
- **Test Ride Booking** — schedule and track test rides

## Tech Stack
- Java (Swing GUI, JDBC)
- MySQL 8.x

## Project Structure
```
EVBikeShowroom/
├── database/
│   └── schema.sql          -- run this first to create the DB & tables
├── src/
│   ├── Main.java
│   ├── db/
│   │   └── DBConnection.java
│   └── ui/
│       ├── LoginFrame.java
│       ├── DashboardFrame.java
│       ├── BikeManagementFrame.java
│       ├── CustomerManagementFrame.java
│       ├── SalesFrame.java
│       └── TestRideFrame.java
├── lib/                     -- put mysql-connector-j-x.x.x.jar here
└── README.md
```

## Setup Instructions

### 1. Create the database
Open MySQL Workbench / CLI and run:
```
mysql -u root -p < database/schema.sql
```
This creates the `evbikeshowroom` database, all tables, and seeds sample data.
Default login: **admin / admin123**

### 2. Download the MySQL JDBC driver
Download `mysql-connector-j-8.x.x.jar` from:
https://dev.mysql.com/downloads/connector/j/

Place the jar file inside the `lib/` folder.

### 3. Update DB credentials
Open `src/db/DBConnection.java` and update if needed:
```java
private static final String URL = "jdbc:mysql://localhost:3306/evbikeshowroom?useSSL=false&serverTimezone=UTC";
private static final String USER = "root";
private static final String PASSWORD = "root"; // your MySQL password
```

### 4. Compile
From the project root:
```
javac -cp "lib/mysql-connector-j-8.x.x.jar" -d bin src/Main.java src/db/*.java src/ui/*.java
```

### 5. Run
```
java -cp "bin:lib/mysql-connector-j-8.x.x.jar" Main
```
(On Windows use a semicolon instead of a colon: `bin;lib/mysql-connector-j-8.x.x.jar`)

## Using an IDE (Eclipse / IntelliJ / NetBeans)
1. Import as a Java project, pointing the source root to `src/`.
2. Add `lib/mysql-connector-j-8.x.x.jar` to the project's build path / libraries.
3. Run `Main.java`.

## Notes
- Table stock automatically decreases by 1 whenever a sale is recorded.
- All CRUD screens support add / update / delete with a live table view.
- You can extend this project further by adding: invoice/PDF generation,
  role-based access (staff vs admin), or charts for sales reports.
