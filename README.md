# 📚 Core Modular Library & Resource Management System

A high-performance, object-oriented backend library ecosystem built entirely in **Java**. This system features a strict multi-tier architecture that completely decouples business logic processors (Controllers) from structural entities and custom Abstract Data Types (ADTs). The architecture manages real-time material circulation workflows, secure multi-role administrative access control matrices, room scheduling structures with transactional backup features, and performance analytical reporting tools.

---

## 👥 Meet the Contributors

This backend engine was engineered as a collaborative team project. Individual architectural components are explicitly attributed below based on development ownership:

* **Adam Ho Swee En**
  * Core Session Router & Global Authentication Gate (`LoginControl`)
  * System Administrative Access Matrices & Change-State Tracking (`AdminControl`)
  * Dynamic Input Ingestion & New User Validation Pipelines (`RegisterControl`)

* **Elisha Loh Tien Rong**
  * Advanced Resource Scheduling Engine & Multi-Criteria Trackers (`RoomBookingControl`)
  * Algorithmic Data Compilers & Performance Metric Generators (`ReportControl`)

* **Toh Ming Yang**
  * Transaction Execution Pipelines & Asset Circulation Utilities (`TransactionControl`)

* **Darren Ong Der Ren**
  * Material Lifecycle Management, State Trackers, and Catalog Engines (`MaintainBookControl`)

---

## 🏗️ Architectural Layout & Design Patterns

The codebase is engineered strictly upon foundational software engineering principles to guarantee high cohesion and absolute separation of concerns:

1. **Separation of Concerns (SoC)**: Operational business logistics are completely isolated inside a dedicated Controller tier (`control.*`). This ensures that entity data models (`entity.*`) act purely as clean, encapsulated objects independent of processing logic.
2. **Command Pattern Mappings (State Rollbacks)**: Both `AdminControl` and `RoomBookingControl` feature historical change-state tracking ledgers (`UndoEntry` and `roomUndoStack`). These allow for structural modification tracing and granular multi-step operation reversals (Undo features).
3. **State Machine Processing**: Physical asset items dynamically shift between structural phases (`Available` $\rightarrow$ `Borrowed` $\rightarrow$ `Waitlisted`) strictly validated against user ceiling criteria and active ledger dependencies before records are committed or wiped.

---

## 📂 Project Directory Structure

```text
/Library-Management-System/
│
└── src/
    └── control/
        ├── LoginControl.java          # Global Session router and cross-controller factory manager
        ├── AdminControl.java          # User manipulation, role-access matrices, and administrative undo ledger
        ├── RegisterControl.java       # Pre-ingestion validation validation pipelines for client signups
        ├── UserControl.java           # Secure user profile updating and client credential manipulation
        ├── MaintainBookControl.java   # Inventory lifecycle management and search indexing operations
        ├── TransactionControl.java    # Live checkouts, multi-level waitlist queues, and fine updates
        ├── RoomBookingControl.java    # Spatial scheduling, time-slot collision validation, and rollback stacks
        └── ReportControl.java         # Multi-metric analytical compilers and list optimization sorters
