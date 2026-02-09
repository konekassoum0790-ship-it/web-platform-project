# 🌐 Science Festival - Workshop Management System

## 📌 Project Overview
This project is a full-stack web application designed to digitize the organization of the "Science Festival" event. The system streamlines the management of scientific workshops, allowing participants (students, teachers, and the general public) to browse available sessions and register for specific time slots.

It features administrative tools for workshop leaders (Facilitators) and administrators to manage schedules and validate registrations.

> **Note:** This is an academic project developed at **Polytech Nancy** as part of a team assignment.

---

## 🏗️ Technical Architecture
The core application is built on a robust n-tier architecture using the **Spring Boot** ecosystem.

### 1. Backend & Code Structure (Java Spring Boot)
The application follows the **Separation of Concerns (SoC)** principle across several layers:
* **Models (JPA Entities):** Database structure represented as Java objects (`Workshop`, `TimeSlot`, `Registration`, `Participant`).
* **Repositories:** Interfaces extending `JpaRepository` for abstracted SQL queries and CRUD operations.
* **Services (Business Logic):** Handles core logic, such as availability checks for registrations and authentication management.
* **Controllers (API & Routing):** Manages incoming HTTP requests, orchestrating data flow between Services and Thymeleaf views.

### 2. Database (MySQL)
- Relational schema managed via **Hibernate (ORM)**.
- Centralized configuration in `application.properties`.

### 3. External API Integration
- Integrated a third-party API via `AddressValidationService` to normalize and validate geographical data during registration.
- Asynchronous communication implemented using `RestTemplate` / `WebClient`.

### 4. Frontend
- **Tech Stack:** HTML5, CSS3, JavaScript, **Thymeleaf**.
- Thymeleaf handles server-side rendering and dynamic data injection.

---

## 🛠️ Key Features

### Role-Based Access Control (RBAC)
The application implements a security system with three distinct roles:
* **Participants:** Browse workshops and manage personal registrations.
* **Facilitators:** Specific views to manage assigned workshops.
* **Administrators:** Global event dashboard with validation rights.

### Workshop & Registration Workflow
* **Dynamic Catalog:** Interactive exploration of available workshops.
* **Registration Logic:** Smart forms with pre-filled data for authenticated users.
* **Validation Pipeline:** Admin interface to "Accept" or "Decline" registrations.

---

## 🚀 Future Roadmap
- **Migration to Angular:** Transitioning to a Single Page Application (SPA) architecture with a REST API.
- **Advanced Security:** Implementation of **BCrypt** password hashing and JWT/CSRF protection.
- **Containerization:** Deployment using **Docker** and Docker Compose.

---

## 👥 Credits
Developed as an academic team project at **Polytech Nancy**.

---
*Contact: [konekassoum0790@gmail.com](mailto:konekassoum0790@gmail.com) | [LinkedIn](https://www.linkedin.com/in/kassoum-kone-836255271)*
