# 📋 Attendance Management App

An open-source Android application developed in **Kotlin** for attendance management in organizations and educational institutions. The application provides role-based access, employee management, attendance tracking, scheduling, and secure user authentication through a modern and modular architecture.

> This project was developed as a learning experience and continues to evolve with new features and improvements.

---

## ✨ Features

- 🔐 User authentication
- 👥 Role-based access (Administrator, Manager, Employee)
- 📝 Attendance registration
- 📅 Schedule management
- 👨‍💼 Employee management
- 👤 User management
- 💾 Local database using Room
- 🌐 API integration using Retrofit
- 📱 Native Android application built with Kotlin

---

## 🏗 Architecture

The project follows a modular structure to improve maintainability and scalability.

```
app/
├── db/
│   ├── dao/
│   ├── entity/
│   └── UsuarioDatabase.kt
│
├── retrofit/
│   ├── api/
│   ├── request/
│   └── response/
│
├── util/
│
└── view/
    ├── admin/
    ├── empleado/
    └── gerente/
```

---

## 🛠 Technologies

- Kotlin
- Android SDK
- Room Database
- Retrofit
- SQLite
- Material Design Components
- Gradle

---

## 👨‍💼 User Roles

### Administrator

- Manage users
- Manage employees
- View attendance records
- Configure schedules

### Manager

- Register attendance
- Manage employees
- View reports

### Employee

- Check attendance
- View assigned schedule
- Access personal information

---

## 🚀 Installation

1. Clone the repository

```bash
git clone https://github.com/etawaaesperu/proyecto-app-de-asistencia.git
```

2. Open the project using Android Studio.

3. Sync Gradle dependencies.

4. Build and run the application on an emulator or Android device.

---

## 📂 Project Status

🟢 Active

The project is fully functional and serves as a foundation for future improvements including:

- Cloud synchronization
- Push notifications
- Attendance reports
- Dashboard analytics
- Improved UI/UX
- Unit testing

---

## 🤝 Contributing

Contributions, issues and feature requests are welcome.

If you'd like to improve the project, feel free to fork the repository and submit a Pull Request.

---

## 📄 License

This project is licensed under the Apache 2.0 License.

---

## 👨‍💻 Author

Developed by **Etawaaesperu**

GitHub:
https://github.com/etawaaesperu
