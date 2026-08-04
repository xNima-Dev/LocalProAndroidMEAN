<h1 align="center">📱 LocalPro - Location-Based Service Marketplace 🚀</h1>

<p align="center">
  <strong>An on-demand service marketplace connecting customers with local professionals in real-time.</strong>
</p>

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" alt="Platform: Android" />
  <img src="https://img.shields.io/badge/Language-Java-ED8B00?style=for-the-badge&logo=openjdk&logoColor=white" alt="Language: Java" />
  <img src="https://img.shields.io/badge/Architecture-MVVM-FFCA28?style=for-the-badge" alt="Architecture: MVVM" />
  <img src="https://img.shields.io/badge/Dependency_Injection-Hilt-blue?style=for-the-badge" alt="DI: Hilt" />
  <img src="https://img.shields.io/badge/Backend-Node.js-339933?style=for-the-badge&logo=nodedotjs&logoColor=white" alt="Backend: Node.js" />
  <img src="https://img.shields.io/badge/Database-MongoDB-47A248?style=for-the-badge&logo=mongodb&logoColor=white" alt="Database: MongoDB" />
</p>

**LocalPro** is a scalable, location-based mobile marketplace engineered to connect local service providers (handymen, technicians, freelancers) with nearby clients. Built with a robust **Android Frontend (Java/MVVM)** and a modern **MEAN/Node.js + MongoDB Backend**, it seamlessly integrates Relational & NoSQL database architecture for high-speed data processing and real-time syncing.

---

## ✨ Key Features

### 👤 Customer App
* **Smart Search & Discovery:** Browse and search for local professionals by category (e.g., Plumbers, Electricians, Cleaners).
* **Frictionless Booking:** Streamlined booking flow to request services instantly.
* **Live Job Tracking:** Real-time tracking of the service provider on a map (Google Maps integration) with ETA calculation powered by OSRM routing.
* **Status Updates:** Live state management (Pending, Accepted, On the way, Arrived, Completed) via Firebase Cloud Messaging (FCM).

### 🛠 Provider App
* **Interactive Dashboard:** Manage incoming job requests, active bookings, and past history.
* **Actionable Notifications:** Accept or decline job requests instantly.
* **Profile Management:** Dynamic profile updates to showcase skills, hourly rates, and verification status.

---

## 📐 Hybrid System Architecture

The application combines **Android MVVM** on the client side with a decoupled **RESTful Node/Express + MongoDB API** backend for maximum scalability.

```text
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
```

---

## 🚀 Current Progress (Completed)

- [x] **Core Authentication:** Secure login and registration flows.
- [x] **Dual Dashboard Implementation:** Distinct, fully styled UI/UX for both Customers and Providers.
- [x] **MVVM Refactoring:** Completely migrated both Customer and Provider flows from legacy tight-coupled views to clean MVVM components.
- [x] **Live Booking Flow:** End-to-end booking capability (Customer requests -> Provider accepts).
- [x] **Real-Time Tracking:** Google Maps integration showing user location and rendering OSRM driving routes.
- [x] **Firebase Integration:** `MyFirebaseMessagingService` configured and registered for real-time push events.
- [x] **Dependency Injection:** Centralized API instantiation and Repository patterns managed via Hilt.

---

## 🧠 Future Scope (AI & Automation)

The next major phase of LocalPro focuses on implementing intelligent features to automate and enhance the user experience:

- [ ] **AI-Powered Job Matching:** A smart recommendation engine to automatically suggest the most suitable providers based on job description, historical data, and user ratings.
- [ ] **Automated Diagnostics:** An AI assistant for customers to diagnose their issues (e.g., "My sink is leaking from the U-bend") to automatically categorize the problem and estimate costs before booking.
- [ ] **In-App Messaging & Live Chat:** Seamless communication channel between customer and provider using WebSockets/Socket.io.
- [ ] **Payment Gateway Integration:** Secure in-app payments (Stripe/PayHere) upon job completion.
- [ ] **MongoDB Geospatial Indexing:** `$near` / `$geoWithin` for faster distance-based queries on the backend.
- [ ] **Backend FCM Wiring:** Complete the backend logic to push real-time FCM payloads to the Android client.

---

## ⚙️ Installation & Local Setup

### Prerequisites
- Android Studio Ladybug | 2024.2.1 or higher
- Node.js v18+ & MongoDB Installed locally or MongoDB Atlas Connection String
- JDK 17 or higher

### Android Client Setup
1. **Clone the repository:**
   ```bash
   git clone https://github.com/xNima-Dev/LocalProAndroidMEAN.git
   ```
2. **Open in Android Studio:** Select `File > Open` and choose the cloned directory.
3. **Configure API Keys:**
   * Create a `local.properties` file in the root directory (if not present).
   * Add your Google Maps API Key:
     ```properties
     MAPS_API_KEY=YOUR_GOOGLE_MAPS_API_KEY
     ```
4. **Configure Firebase:**
   * Create a Firebase project.
   * Register the Android app (`com.localpro.localproandroid`).
   * Download `google-services.json` and place it in the `/app` directory.
5. **Build and Run:** Sync Gradle and deploy the application to your emulator or physical device.

---

### 👨‍💻 Developer
**Nimsara Kavidu**
*Enterprise Fullstack & Mobile Engineer*
- GitHub: [@xNima-Dev](https://github.com/xNima-Dev)
- LinkedIn: [Nimsara Kavidu](https://www.linkedin.com/in/nimsara-kavindhu-014ba7301)
- 📧 nimsarakavindhu@gmail.com
