# Blood Bank Management System 🩸

**Author:** Ravi Kumar

A robust, enterprise-level desktop application built with Java Swing and MySQL, designed to manage blood stock, donor registrations, and patient issuances. This project moves beyond standard CRUD operations by implementing advanced healthcare data protocols, multi-threaded background processes, and ACID-compliant database transactions.

## 🚀 Key Features

### Security & Access Control
* **Role-Based Access Control (RBAC):** Distinct dashboards and security clearances for 'Admin' and 'Staff'.
* **Strict Authentication:** Implemented case-sensitive login validation using MySQL `BINARY` operators.

### Smart Data Management
* **Temporal Mutability:** System allows for updating dynamic donor data (Age, Medical History) for returning donors while retaining historical integrity.
* **Database Syncing:** Natively syncs and updates the most recent donation dates using advanced `JOIN` and aggregate `MAX()` queries.

### Analytics & Inventory
* **Real-Time Dashboard:** Utilizes SQL `GROUP BY` aggregations to provide a real-time inventory count.
* **Smart Alerts:** Automatically flags critically low blood groups with visual warnings.
* **Automated Expiry Sweeps:** Utilizes a Java `ScheduledExecutorService` (Multi-threading) to run silent background sweeps that automatically flag expired blood bags based on `CURDATE()`.

### 🚨 Emergency Lookback Protocol (Contact Tracing)
An advanced healthcare feature that utilizes **ACID Transactions**. If a donor reports an infection post-donation, the system executes a single transaction that:
1. Flags the donor's medical file with a critical warning.
2. Instantly purges all of their 'Available' inventory.
3. Executes a relational `JOIN` query to generate a contact-tracing list of any patients who received the tainted blood.

## 💻 Tech Stack
* **Frontend:** Java Swing (AWT/Swing Layout Managers)
* **Backend:** Java (JDK 21)
* **Database:** MySQL
* **Tools:** Eclipse IDE, MySQL Workbench

## ⚙️ Setup Instructions
1. Clone the repository to your local machine.
2. Open MySQL Workbench and import the `database.sql` file to recreate the schema and test data.
3. Update the `DBConnection.java` file with your local MySQL username and password.
4. Run `LoginScreen.java` to start the application.