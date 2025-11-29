# E-Commerce
# 🛒 E-Commerce Platform

A full-featured and production-ready E-Commerce web application with **Spring Boot backend** and **React frontend**, offering secure authentication, fast product browsing, shopping cart, payment processing, and complete order management. The system is built for **high performance and scalability**, using **Redis caching** and **Kafka-based asynchronous communication** for critical events like order placement and email notifications.

---



### **Frontend**
- React
- Redux Toolkit
- Axios (API communication)

### **Backend**
- Spring Boot (REST APIs)
- Spring Security
- Spring Data JPA / Hibernate
- MySQL / PostgreSQL (Relational DB)

### **Caching & Messaging**
- **Redis** — Distributed caching for fast cart access and cart persistence
- **Kafka** — Message-based asynchronous events for:
  - Order event processing
  - Email notifications
  - Payment status updates

### **Authentication**
- JSON Web Tokens (**JWT**)

### **Payment Processing**
- **PayPal API** integration


### **Other Tools & Libraries**
- BCrypt (password hashing)
- Lombok
- Maven
- Docker support (optional)

---

##  Core Features

###  Authentication & Users
- User registration and login
- JWT-powered secure authorization
- Profile and saved address management
- Change/Reset password via email

### Product & Shopping
- Product catalog with search and category filters
- Product details with images, ratings and reviews
- Add / update / delete cart items
- Cart stored in Redis for fast performance

### Payments & Checkout
- shipping address and delivery information
- Order summary and confirmation page
- Automated email notification after successful payment  
  _(triggered via Kafka event)_

### Orders
- Order history and tracking
- Kafka-driven order processing workflow
- Stock management on order confirmation

### 🔐 Security
- BCrypt password hashing
- Secure API endpoints
- User settings with password and email update functionality



