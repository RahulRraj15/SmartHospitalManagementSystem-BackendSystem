# 🏥 Smart Hospital Management System

A backend REST API project for managing hospital operations such as users, departments, doctors, patients, appointments, medicines, prescriptions, billing, and medical reports.

The project is developed using Java and Spring Boot with a layered architecture and MySQL database.

---

## 📌 Project Overview

The Smart Hospital Management System is designed to provide a centralized backend system for managing different hospital operations.

The application provides REST APIs for:

- User Management
- Department Management
- Doctor Management
- Patient Management
- Appointment Management
- Medicine Management
- Prescription Management
- Billing Management
- Medical Report Management

The project also includes:

- DTO-based request and response handling
- Validation
- Exception handling
- Pagination
- Sorting
- Searching
- Filtering
- JPA/Hibernate relationships
- Swagger/OpenAPI documentation
- Spring Security
- JWT Authentication

---

## 🚀 Features

### 👤 User Management

- Create user
- Get all users
- Get user by ID
- Update user
- Delete user
- Pagination
- Sorting
- Search
- Role filtering
- Status filtering
- Password encryption using BCrypt

### 🏢 Department Management

- Create department
- Get all departments
- Get department by ID
- Update department
- Delete department
- Pagination
- Sorting
- Search
- Active/inactive filtering

### 👨‍⚕️ Doctor Management

- Create doctor
- Get all doctors
- Get doctor by ID
- Update doctor
- Delete doctor
- Pagination
- Sorting
- Search
- Active/inactive filtering
- Department relationship

### 🧑‍🤝‍🧑 Patient Management

- Create patient
- Get all patients
- Get patient by ID
- Update patient
- Delete patient
- Pagination
- Sorting
- Search
- Gender filtering
- Blood group filtering
- User relationship

### 📅 Appointment Management

- Create appointment
- Get all appointments
- Get appointment by ID
- Update appointment
- Delete appointment
- Pagination
- Sorting
- Search
- Appointment status filtering
- Appointment date filtering
- Doctor relationship
- Patient relationship

### 💊 Medicine Management

- Create medicine
- Get all medicines
- Get medicine by ID
- Update medicine
- Delete medicine
- Pagination
- Sorting
- Search
- Active/inactive filtering
- Expiry date filtering
- Stock availability filtering

### 📋 Prescription Management

- Create prescription
- Get all prescriptions
- Get prescription by ID
- Get prescription by appointment
- Update prescription
- Delete prescription
- Pagination
- Sorting
- Search
- Appointment relationship
- Medicine relationship

### 💰 Billing Management

- Create bill
- Get all bills
- Get bill by ID
- Get bills by patient
- Update bill
- Delete bill
- Pagination
- Sorting
- Search
- Payment status filtering
- Payment method filtering

### 🩺 Medical Report Management

- Create medical report
- Get all medical reports
- Get medical report by ID
- Get reports by patient
- Get reports by doctor
- Update medical report
- Delete medical report
- Pagination
- Sorting
- Search
- Patient relationship
- Doctor relationship

---

# 🛠️ Technologies Used

| Technology | Purpose |
|---|---|
| Java 25 | Programming Language |
| Spring Boot 4.0.8 | Backend Framework |
| Spring MVC | REST API Development |
| Spring Data JPA | Database Access |
| Hibernate | ORM |
| MySQL | Relational Database |
| Maven | Build & Dependency Management |
| Lombok | Boilerplate Code Reduction |
| Jakarta Validation | Request Validation |
| Spring Security | Application Security |
| JWT | Authentication |
| Swagger / OpenAPI | API Documentation |
| Git | Version Control |
| GitHub | Source Code Management |

---

# 🏗️ Project Architecture

The project follows a layered architecture.

```text
Client / React Frontend
          |
          v
     REST Controller
          |
          v
      Request DTO
          |
          v
    Service Interface
          |
          v
 Service Implementation
          |
          v
        Mapper
          |
          v
      Repository
          |
          v
    JPA / Hibernate
          |
          v
      MySQL Database