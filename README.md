# HostelKeep

HostelKeep is a comprehensive, role-based Hostel Management Android Application built with Kotlin, Jetpack Compose, and Firebase (Authentication, Firestore, Storage). 

## 🚀 Download APK
You can download and install the latest APK build directly from the link provided by the administrator. 

> **Note:** The APK is distributed privately. Please download it only from the shared link provided to you.

## ✨ Features
- **Role-Based Authentication**: Custom dashboards tailored for Admin, Warden, Student, Security, and Parent roles.
- **Room Allocation**: Transaction-safe backend operations preventing double-allocations and capacity overloads.
- **Attendance & Outpass**: Secure QR-based entry/exit, role-restricted leave approvals, and statistical attendance dashboards.
- **Live Sync**: Real-time Firebase Firestore connections resolving dynamic data instantaneously across devices without mock fallbacks.
- **Completely Secure**: Production-grade Firebase Security Rules keeping unauthorized access strictly prohibited.

## 🛠 Tech Stack
- **UI:** Jetpack Compose (Material 3)
- **Architecture:** MVVM + Clean Repository Pattern (StateFlow, Coroutines)
- **Backend:** Firebase Cloud Firestore & Authentication
- **Navigation:** Jetpack Navigation Compose

## 💻 Development
To run this project locally:
1. Clone the repository.
2. Provide your own `google-services.json` inside the `app/` directory.
3. Sync Gradle and build the project in Android Studio.