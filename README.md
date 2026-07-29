# 📱 LocalPro - Location-Based Service Marketplace

[![Android](https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white)](https://developer.android.com/)
[![Java](https://img.shields.io/badge/Language-Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white)](https://www.java.com/)
[![MongoDB](https://img.shields.io/badge/Database-MongoDB-47A248?style=for-the-badge&logo=mongodb&logoColor=white)](https://www.mongodb.com/)
[![Node.js](https://img.shields.io/badge/Backend-Node.js-339933?style=for-the-badge&logo=nodedotjs&logoColor=white)](https://nodejs.org/)
[![Architecture](https://img.shields.io/badge/Architecture-MVVM%20%26%20REST-blue?style=for-the-badge)](https://developer.android.com/topic/architecture)

**LocalPro** is a scalable, location-based mobile marketplace engineered to connect local service providers (handymen, technicians, freelancers) with nearby clients. Built with a robust **Android Frontend (Java/MVVM)** and a modern **MEAN/Node.js + MongoDB Backend**, it seamlessly integrates Relational & NoSQL database architecture for high-speed data processing and real-time syncing.

---

## ✨ Key Features

- 📍 **Location-Based Discovery:** Dynamic discovery of service providers based on real-time geolocation.
- 👤 **Dual Role System:** Unified experience for both **Service Seekers** and **Service Providers**.
- 🍃 **Scalable NoSQL Schema:** Flexible Mongo Data Models to handle complex service listings, reviews, and user profiles.
- 🛠️ **Service Booking Engine:** Real-time request flows, status tracking, and booking history.
- 🔐 **Secure Authentication & REST APIs:** JWT/Firebase authenticated RESTful endpoints for secure client-server communication.

---

## 📐 Hybrid System Architecture

The application combines **Android MVVM** on the client side with a decoupled **RESTful Node/Express + MongoDB API** backend for maximum scalability.

┌─────────────────────────────────────────────────────────┐
│                    UI Layer (View)                      │
│            Activities / Fragments / XML                 │
└──────────────────────────┬──────────────────────────────┘
                           │ Observes LiveData / ViewModel
┌──────────────────────────▼──────────────────────────────┐
│                    ViewModel Layer                      │
│          Manages UI Logic & State Separation            │
└──────────────────────────┬──────────────────────────────┘
                           │ Network / Repository Layer
┌──────────────────────────▼──────────────────────────────┐
│                  RESTful API Client                     │
│               (Retrofit / Volley HTTP)                  │
└──────────────────────────┬──────────────────────────────┘
                           │ JSON Requests over HTTP
┌──────────────────────────▼──────────────────────────────┐
│                  Node.js / Express API                  │
│             Business Logic & Authentication             │
└───────────────┬─────────────────────────┬───────────────┘
                │                         │
┌───────────────▼──────────┐   ┌──────────▼───────────────┐
│   MongoDB (NoSQL Store)  │   │  MySQL / Firebase Sync   │
│ Service Catalog & Users  │   │ Auth & Relational Logs   │
└──────────────────────────┘   └──────────────────────────┘


---

## 🛠️ Tech Stack & Database Setup

- **Mobile Client:** Android SDK (Java), MVVM Pattern, XML Layouts, Material Design Components.
- **Backend Services:** Node.js, Express.js REST APIs.
- **Database Architecture:** 
  - **MongoDB:** Flexible document storage for complex service catalogs, user profiles, and activity logs.
  - **MySQL / Firebase:** Relational mapping for auth states and transactional logs.
- **Tools:** Android Studio, MongoDB Compass, Postman, Git, GitHub.

---

## 🚀 Getting Started

To get a local copy up and running, follow these simple steps:

### Prerequisites
- Android Studio Ladybug | 2024.2.1 or higher
- Node.js v18+ & MongoDB Installed locally or MongoDB Atlas Connection String
- JDK 17 or higher

### Installation & Local Setup

1. **Clone the repository:**
   ```bash
   git clone [https://github.com/xNima-Dev/LocalProAndroidMEAN.git](https://github.com/xNima-Dev/LocalProAndroidMEAN.git)
Backend Setup (Node.js & MongoDB):

Bash
cd backend
npm install
# Configure your MONGO_URI in .env file
npm start
Android Client Setup:

Open the root folder in Android Studio.

Sync project with Gradle files.

Set your local API base URL (e.g., http://10.0.2.2:5000/api) in the config file.

Build and Run on Emulator/Physical Device.

📌 Future Roadmap[ ] In-App Real-Time Messaging using WebSockets/Socket.io.

[ ] Payment Gateway Integration (Stripe/PayHere).

[ ] MongoDB Geospatial Indexing ($near / $geoWithin) for faster distance-based queries.

👨‍💻 Developer
Nimsara Kavidu

Enterprise Fullstack & Mobile Engineer

GitHub: @xNima-Dev

LinkedIn: Nimsara Kavidu
