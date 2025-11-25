
# CSE 731 Project: TinySQL 

## 1\. What is Mutation Testing?

Mutation testing is a fault-based software testing technique used to evaluate the quality of a test suite. It involves modifying a program in small ways (creating "mutants"), such as changing a logical operator or deleting a statement and running the test suite against these mutants.

If the test suite fails (detects the error), the mutant is considered **killed**. If the test suite passes despite the code change, the mutant **survived**, indicating a gap in test coverage. It essentially tests the tests.

## 2\. Objective

The primary objective of this project is to build a functional, lightweight SQL database engine ("TinySQL") and apply rigorous software testing techniques. Specifically, we aim to:

1.  Develop a source code rich in control flow and logic (Java).
2.  Implement **Unit and Integration Testing** using JUnit 5.
3.  Perform **Mutation Testing** using **PITest** to analyze test suite efficacy at the unit level.
4.  Demonstrate **Integration Level Mutation** by manually modeling specific integration faults (IMCD, IPEX, IREM).
5.  Apply **Fuzz Testing** using **JQF** to detect edge-case failures in the tokenizer and aggregator logic.

## 3\. Libraries and Frameworks Used

  * **Language:** Java 21
  * **Build Tool:** Maven 3.11+
  * **Unit/Integration Testing:** JUnit 5 (Jupiter)
  * **Mutation Testing:** PITest (v1.15.0)
  * **Fuzz Testing:** JQF (Java QuickCheck Fuzzing v2.1)

Here is the combined and formatted section for your `README.md`.

## 4\. Details of Source Code

 **TinySQL**, is a relational database engine with disk persistence capabilities. It supports essential SQL functionalities including **SELECT**, **CREATE**, **INSERT**, **JOIN**, and **WHERE** clause filtering. The project consists of approximately **1000+ lines of code**.


### Core Components

The architecture is modularized into the following packages:

  * **Engine (`com.tinysql.engine`):** Contains the core processing logic.
      * `Executor`: The central controller that orchestrates data operations (`SELECT`, `INSERT`, `CREATE`) and manages memory/disk interaction.
      * `JoinProcessor`: Implements a Nested-Loop algorithm to handle Equi-Joins between tables.
      * `Aggregator`: Performs mathematical aggregations on datasets (`SUM`, `AVG`, `MAX`, `MIN`).
      * `ConditionEvaluator`: A logic-heavy component responsible for parsing and processing `WHERE` clause conditionals against row data.
  * **Storage (`com.tinysql.storage`):** Manages Input/Output operations, specifically saving and loading tables as CSV files to ensure data persistence.
  * **Tokenizer (`com.tinysql.tokenizer`):** A lexical analyzer that breaks raw SQL input strings into distinct tokens for parsing.
  * **Model (`com.tinysql.model`):** Defines the data structures representing the database schema: `Table`, `Row`, `Column`, and `DataType`.
  * **Transaction (`com.tinysql.transaction`):** Includes basic stubs for transaction management and ACID property support.

### Supported Data Types
The engine supports a strict set of data types for defining table schemas:

* **`INT`**: Integer values (mapped to Java `Integer`).
* **`TEXT`**: String literals (mapped to Java `String`).
* **`BOOL`**: Boolean values (`true`/`false`).
* **`FLOAT`**: Floating-point numbers (mapped to Java `Float`).
* **`DOUBLE`**: Double-precision numbers (mapped to Java `Double`).

### Supported Commands & Syntax

The TinySQL engine supports a strictly typed subset of SQL commands. Below are the valid functionalities and syntax patterns derived from the source code.

#### 1\. CREATE TABLE

Defines a new table schema in memory and initializes a CSV file for persistence.

  * **Functionality:** Creates a new table with specified column names and enforced data types.
  * **Supported Types:** `INT`, `DOUBLE`, `FLOAT`, `TEXT`, `BOOL`.
  * **Syntax:**
    ```sql
    CREATE TABLE <table_name> (<col_name> <type>, <col_name> <type>, ...)
    ```
  * **Example:**
    ```sql
    CREATE TABLE users (id INT, name TEXT, active BOOL, balance DOUBLE)
    ```

#### 2\. INSERT INTO

Adds a single row of data to the specified table.

  * **Functionality:** Parses input values, matches them to the schema, and appends them to the table storage. Internal IDs are auto-incremented.
  * **Syntax:**
    ```sql
    INSERT INTO <table_name> VALUES <val1> <val2> <val3> ...
    ```
  * **Example:**
    ```sql
    INSERT INTO users VALUES 1 Alice true 500.50
    ```

#### 3\. SELECT (Data Retrieval)

Retrieves rows from a table, optionally filtering them with a `WHERE` clause.

  * **Functionality:** Fetches all columns (`*`) for rows matching the specific criteria.
  * **Supported Operators:** `=`, `!=`, `>`, `<`, `>=`, `<=`.
  * **Syntax:**
    ```sql
    SELECT * FROM <table_name> [WHERE <column> <operator> <value>]
    ```
  * **Example:**
    ```sql
    SELECT * FROM users WHERE balance > 100.0
    ```

#### 4\. JOIN

Performs a join operation between two tables.

  * **Functionality:** Combines rows from two tables using a Nested-Loop Join algorithm where the specified columns match (Equi-Join).
  * **Syntax:**
    ```sql
    JOIN <table1> <table2> ON <col1_in_t1> <col2_in_t2>
    ```
  * **Example:**
    ```sql
    JOIN users orders ON id user_id
    ```

#### 5\. Aggregate Functions

Performs calculations on a specific column across all matching rows.

  * **Functionality:** Computes a single result value based on the selected mathematical function.
  * **Supported Functions:** `COUNT`, `SUM`, `AVG`, `MIN`, `MAX`.
  * **Syntax:**
    ```sql
    SELECT <FUNCTION>(<column>) FROM <table_name> [WHERE <column> <operator> <value>]
    ```
  * **Examples:**
    ```sql
    SELECT COUNT(id) FROM users
    SELECT AVG(balance) FROM users WHERE active = true
    ```


## 5\. Detailed Testing Strategy

### A. Unit Testing

We performed comprehensive JUnit-based unit testing across all core packages.

Unit tests were written for the following components:

1. **com.tinysql.engine**  
   - Execution logic, query operators, aggregators, and evaluator classes.

2. **com.tinysql.model**  
   - Table, Row, Column, Schema, and in-memory data representations.

3. **com.tinysql.storage**  
   - StorageManager, file I/O, page-level read/write operations, and persistence logic.

4. **com.tinysql.tokenizer**  
   - SQL Tokenizer, token classification, keyword detection, literal parsing, and operator handling.

5. **com.tinysql.transaction**  
   - Transaction lifecycle, locks, commit/rollback behavior, and isolation assumptions.

6. **com.tinysql.util**  
   - Utility helpers including type conversion, comparisons, and general-purpose helpers.


### B. Integration Testing

Integration tests were designed to validate how major subsystems interact when executing complete SQL workflows.

**Targets**
- `com.tinysql.engine.Executor`
- `com.tinysql.engine.JoinProcessor`
- `com.tinysql.storage.StorageManager`
- `com.tinysql.model.Table` (as part of end-to-end flow)
---

These enhanced Unit and Mutation Testing strategies ensure both correctness of individual components and robustness of the overall test suite.

### C. Mutation Testing (Automated & Manual)

1.  **Unit Level (Automated via PITest):** We ran PITest against the `com.tinysql.*` packages. PITest automatically generates mutants by altering bytecode.
2.  **Integration Level (Manual Modeling):** As per project requirements, we manually designed integration mutants to verify if our integration tests could catch faults in component interfaces:
      * **IMCD (Integration Method Call Deletion):** We created a mutant where the `storage.saveTable()` call was deleted to test persistence verification.
      * **IPEX (Integration Parameter Exchange):** We swapped column parameters in the `executeJoin` method to test if the test suite detects mismatched join keys.
      * **IREM (Integration Return Expression Modification):** We modified `executeSelect` to return empty lists to test data flow verification.

### D. Fuzz Testing

We used **JQF (Java QuickCheck Fuzzing)** to perform generative testing.

  * **Tokenizer Fuzzing:** Generates random strings to ensure the tokenizer does not crash on malformed SQL.
  * **Aggregator Fuzzing:** Generates random `Row` objects with mixed types (`Double`, `String`, `Integer`) to test the robustness of math functions.
  * **CLI Fuzzing:** Feeds random command inputs to the `Main` class.


## 6\. Results and Analysis

### Mutation Testing Results (PITest)

Below is the summary of the PITest coverage report generated after running the suite.

| Metric | Score |
| :--- | :--- |
| **Mutation Score** | **82%** |
| **Line Coverage** | **94%** |
| **Mutants Killed** | **348/426]** |
| **Mutations with no coverage** | **28 (Test strength 87%)** |

**Visual Report**<img width="977" height="465" alt="1" src="https://github.com/user-attachments/assets/3a6f91ab-3425-4429-a744-ffe9eb887755" />

<img width="1115" height="502" alt="2" src="https://github.com/user-attachments/assets/309600d9-de91-4c88-ac51-b8eaecb3d177" />


### Fuzz Testing Results

We ran the fuzzer for 30 seconds per method.

  * **Tokenizer:** No crashes detected. Handled malformed inputs by throwing handled exceptions rather than crashing.
  * **Aggregator:** Robust against `ClassCastException` due to strict type checking implemented in the `Aggregator` class.

**Fuzz Execution Output:**
*(Place a screenshot of the terminal showing the JQF run)*

-----

## 8\. Steps To Run

### Prerequisites

  * Java 21
  * Maven

### Build the Project

Compile the code and the tests, and build the executable JAR.

```bash
mvn clean package
```

### Run Unit and Integration Tests

```bash
mvn test
```

### Run Mutation Testing (PITest)

This generates the mutation report in `target/pit-reports/`.

```bash
mvn org.pitest:pitest-maven:mutationCoverage
```

### Run Fuzz Testing (JQF)

Run specific fuzz drivers for 30 seconds each.

```bash
# Fuzz the Tokenizer
mvn jqf:fuzz -Dclass=com.tinysql.fuzz.TokenizerFuzz -Dmethod=fuzzTokenizer -Dtime=30s

# Fuzz the Aggregator
mvn jqf:fuzz -Dclass=com.tinysql.fuzz.AggregatorFuzz -Dmethod=fuzzAggregator -Dtime=30s

# Fuzz the Main CLI
mvn jqf:fuzz -Dclass=com.tinysql.fuzz.MainFuzz -Dmethod=fuzzCLI -Dtime=30s
```
<img width="1920" height="757" alt="3" src="https://github.com/user-attachments/assets/ae6e5fee-3d7f-4597-9121-1a998bb4c797" />

<img width="1920" height="757" alt="4" src="https://github.com/user-attachments/assets/dc49267d-5fab-4961-80e1-671d4fbbdbef" />

-----

## 9\. Individual Contributions

The work was divided equally between the team members:

**Ananthakrishna K (IMT2022086)**

  * **Unit Testing:** Wrote and improved JUnit tests based on mutations for `Engine`,`Model` and `Storage` modules.
  * **Integration Mutation:** Designed the **IPEX** (Parameter Exchange) and **IMCD** (Method Call Deletion) mutation scenarios and corresponding tests.
  * **Documentation:** Authored the corresponding section of the report.
  * **Fuzz Testing**: Explored `Fuzz` testing.

**Aditya Priyadarshi (IMT2022075)**
  * **Unit Testing:** Wrote and improved JUnit tests based on mutations for `Tokenizer`, `Transaction` and `Main` modules.
  * **Integration Mutation:** Designed the **IREM** (Return Expression Modification) mutation scenario.
  * **Build System:** Configured `pom.xml` for PITest and Shade plugins.
  * **Documentation:** Authored the corresponding section of the report.
  * **Fuzz Testing**: Explored `Fuzz` testing.
-----

## 10\. AI Use Declaration

We verify that we used LLMs for the following purposes in this project:

  * **Source Code Generation:** Assisting in generating boilerplate code for the `Tokenizer` and standard Getters/Setters.
  * **JUnit Syntax:** Helping generate the initial syntax for parameterized JUnit tests.
  * **Report Assistance:** Assisting in structuring this README file and the final project report.

**Note:** The core logic for test case generation, the selection of mutation operators, and the design of the integration mutants were performed entirely by us.






