# LABORATORY MANAGEMENT SYSTEM (LMS)
### College Academic Micro-Project &bull; Java MVC &bull; Servlets &bull; JDBC &bull; MySQL

---

## 1. Project Description
The **Laboratory Management System (LMS)** is a web-based college academic management application designed to streamline practical examinations, continuous laboratory assessments, laboratory equipment audits, departmental announcements, task deadlines, and automated student attendance tracking. 

Built using pure **Java Servlets**, **JDBC**, and the **Model-View-Controller (MVC)** architectural pattern, this application does not use external heavyweight frameworks like Spring Boot or React. It provides three dedicated role portals:
1. **Admin Portal**: Institutional laboratory management, user account provisioning (students & faculty), and comprehensive audit reports.
2. **Faculty Portal**: Continuous evaluation marks entry, marks updates, task assignments with deadlines, practical exam scheduling with venue selection, and report generation.
3. **Student Portal**: Automatic login-based daily attendance, marks lookup, task deadline tracking, exam schedule viewing, and announcements.

---

## 2. Key Features

### Role-Based Access Control (RBAC) & Security
- **HTTP Session Authentication**: Strict session management with automatic expiration and logout invalidation.
- **Servlet Filters**: `AuthenticationFilter` intercepts requests to ensure unauthenticated users cannot access internal portals and prevents cross-role privilege escalation.
- **SHA-256 Password Hashing**: Passwords are securely hashed before persisting in the MySQL database.
- **SQL Injection Prevention**: Centralized JDBC DAO classes utilizing `PreparedStatement` exclusively.

### Admin Capabilities
- **Overview Dashboard**: Real-time counter metrics for Total Students, Total Faculty, Active Laboratories, and Today's Attendance.
- **Laboratory Management**: Add, update, view, and delete laboratories (Code, Name, Room, Department, Seating Capacity, Equipment Details, Description).
- **User Management**: Unified management with tabbed filtering for Students and Faculty members. Complete CRUD support.
- **Laboratory Facility Inspector**: Detailed inspection of equipment inventory, seat capacities, and scheduled practical exams.
- **Institutional Reports**: Printable directory reports for Students, Faculty, Laboratories, and System Users.

### Faculty Capabilities
- **Post Practical Marks**: Select student by Roll Number, assign subject, assessment title, marks scored, and maximum marks.
- **Search & Update Marks**: Search student evaluations by roll number/name, modify marks with instant validation, or delete incorrect records.
- **Department Notices**: Broadcast circulars and lab safety instructions to students.
- **Task Deadlines**: Post lab assignments with submission deadlines and task descriptions.
- **Schedule Practical Examinations**: Allocate laboratory venues, date, start time, and end time.
- **Academic Reports**: Dynamic reports for student marks, daily laboratory attendance, and lab infrastructure with browser print/PDF export.

### Student Capabilities
- **Automated Login Attendance**: First login of the calendar day automatically marks the student as `PRESENT`. Duplicate records on the same day are strictly prevented.
- **Attendance History Log**: View complete chronological attendance logs with date and exact login timestamp.
- **Real-Time Marks Viewer**: Track practical performance, total scores, and percentage evaluations.
- **Task Deadline Tracker**: View upcoming lab assignment deadlines with overdue warnings.
- **Lab Examination Schedule**: Check dates, timings, allocated lab room numbers, and faculty in-charge.

---

## 3. Technology Stack

| Component | Technology Used | Version / Description |
|---|---|---|
| **Language** | Java (JDK) | Java 21 LTS |
| **Web Controller** | Java Servlets | Jakarta Servlet API 6.0 |
| **Data Persistence** | JDBC | `java.sql` with MySQL Connector/J 8.3 |
| **Database** | MySQL | MySQL Community Server 8.0+ |
| **Build Tool** | Apache Maven | Maven 3.9+ |
| **Web Server** | Apache Tomcat | Tomcat 10.1+ (Jakarta EE compatible) |
| **Frontend** | HTML5, CSS3, Tailwind CSS | Custom CSS + Tailwind utility styling |
| **JSON Serialization** | Google Gson | Gson 2.10.1 (Lightweight REST data transfer) |

---

## 4. MVC Architecture Explanation

The project strictly follows the **Model-View-Controller (MVC)** software architecture:

```
[ Browser / Client ]
      │  ▲
      │  │ (HTML Views / AJAX JSON)
      ▼  │
[ Controller: Java Servlets ]
   (LoginServlet, MarksServlet, LaboratoryServlet, etc.)
      │  ▲
      │  │ (Java POJOs)
      ▼  │
[ Model & DAO Layer ]
   - Model: User, Student, Faculty, Laboratory, Marks, Attendance, etc.
   - DAO: DBConnection, UserDAO, MarksDAO, AttendanceDAO, etc.
      │  ▲
      │  │ (JDBC PreparedStatements)
      ▼  │
[ MySQL Database (laboratory_management) ]
```

- **Model Layer (`com.lms.model`, `com.lms.dao`)**:
  - `model`: POJO classes encapsulating data entities and attributes.
  - `dao`: Data Access Objects that encapsulate JDBC database operations, PreparedStatements, transactions, and resource management (`DBConnection.close`).
- **View Layer (`src/main/webapp/`)**:
  - Plain HTML5 pages styled with responsive CSS.
  - No database logic or raw Java scriptlets inside HTML.
- **Controller Layer (`com.lms.controller`)**:
  - Java Servlets extending `HttpServlet`.
  - Process HTTP `GET` and `POST` requests, validate parameters, invoke corresponding DAO operations, and return appropriate redirects or structured JSON responses.

---

## 5. Folder Structure

```
LaboratoryManagementSystem/
│
├── pom.xml                               # Maven build configuration
├── database.sql                          # MySQL schema & sample seed data
├── setup_database.bat                    # Helper script to initialize MySQL
├── run_server.bat                        # Helper script to launch Tomcat 10
├── README.md                             # Project documentation
│
├── src/
│   └── main/
│       ├── java/
│       │   └── com/lms/
│       │       ├── model/                # Entity / POJO classes
│       │       │   ├── User.java
│       │       │   ├── Student.java
│       │       │   ├── Faculty.java
│       │       │   ├── Admin.java
│       │       │   ├── Laboratory.java
│       │       │   ├── Marks.java
│       │       │   ├── Notice.java
│       │       │   ├── Task.java
│       │       │   ├── LabExam.java
│       │       │   └── Attendance.java
│       │       │
│       │       ├── dao/                  # Database Access Objects
│       │       │   ├── DBConnection.java
│       │       │   ├── UserDAO.java
│       │       │   ├── AdminDAO.java
│       │       │   ├── StudentDAO.java
│       │       │   ├── FacultyDAO.java
│       │       │   ├── LaboratoryDAO.java
│       │       │   ├── MarksDAO.java
│       │       │   ├── NoticeDAO.java
│       │       │   ├── TaskDAO.java
│       │       │   ├── LabExamDAO.java
│       │       │   └── AttendanceDAO.java
│       │       │
│       │       ├── controller/           # Servlet Controllers
│       │       │   ├── LoginServlet.java
│       │       │   ├── LogoutServlet.java
│       │       │   ├── RegisterServlet.java
│       │       │   ├── AdminServlet.java
│       │       │   ├── FacultyServlet.java
│       │       │   ├── StudentServlet.java
│       │       │   ├── LaboratoryServlet.java
│       │       │   ├── UserManagementServlet.java
│       │       │   ├── MarksServlet.java
│       │       │   ├── NoticeServlet.java
│       │       │   ├── TaskServlet.java
│       │       │   ├── LabExamServlet.java
│       │       │   └── ReportServlet.java
│       │       │
│       │       ├── filter/               # Security & Role Filter
│       │       │   └── AuthenticationFilter.java
│       │       │
│       │       └── util/                 # Utility Classes
│       │           └── PasswordUtil.java # SHA-256 Hashing
│       │
│       ├── resources/
│       │   └── db.properties             # Centralized DB configuration
│       │
│       └── webapp/                       # Web Presentation Layer
│           ├── WEB-INF/
│           │   └── web.xml               # Web application deployment descriptor
│           │
│           ├── index.html                # Welcome landing page
│           ├── login.html                # Common authentication portal
│           ├── register.html             # Student/Faculty registration page
│           │
│           ├── css/
│           │   ├── style.css             # Global UI styles & components
│           │   ├── login.css             # Login page specific styles
│           │   ├── register.css          # Register page styling
│           │   ├── dashboard.css         # Sidebar & dashboard layout
│           │   └── forms.css             # Form controls & inputs
│           │
│           ├── admin/                    # Admin views
│           │   ├── dashboard.html
│           │   ├── laboratories.html
│           │   ├── users.html
│           │   ├── lab-details.html
│           │   └── reports.html
│           │
│           ├── faculty/                  # Faculty views
│           │   ├── dashboard.html
│           │   ├── marks.html
│           │   ├── update-marks.html
│           │   ├── notices.html
│           │   ├── tasks.html
│           │   ├── lab-exams.html
│           │   └── reports.html
│           │
│           └── student/                  # Student views
│               ├── dashboard.html
│               ├── marks.html
│               ├── notices.html
│               ├── tasks.html
│               ├── lab-exams.html
│               └── attendance.html
```

---

## 6. MySQL Installation & Service
1. Download and install **MySQL Server 8.0+** from [dev.mysql.com](https://dev.mysql.com/downloads/installer/).
2. During installation, set root user password (e.g. `root` or your preferred password).
3. Ensure the MySQL Windows service (`MySQL80`) is running. You can check via PowerShell:
   ```powershell
   Get-Service MySQL80
   ```

---

## 7. Database Creation & Initialization
Import `database.sql` into MySQL using either of the following methods:

### Method A: Using MySQL Command-Line Client
```bash
mysql -u root -p < database.sql
```

### Method B: Using MySQL Workbench
1. Open **MySQL Workbench**.
2. Connect to your local MySQL instance.
3. Open `database.sql` (**File > Open SQL Script**).
4. Click the **Execute (Lightning bolt)** button.

### Method C: Using the included script
Run the provided batch file in the project folder:
```cmd
setup_database.bat
```

---

## 8. Database Credentials Configuration
All database connection parameters are centralized in:
`src/main/resources/db.properties`

```properties
db.url=jdbc:mysql://localhost:3306/laboratory_management?useSSL=false&allowPublicKeyRetrieval=true&serverTimezone=UTC
db.user=root
db.password=YOUR_MYSQL_PASSWORD
db.driver=com.mysql.cj.jdbc.Driver
```

If you modify credentials in `db.properties`, rebuild the project with Maven:
```bash
mvn clean package
```

---

## 9. How to Build with Maven
In the project root directory, run:
```bash
mvn clean package
```
This will compile all Java sources, run packaging checks, and produce the deployment archive:
`target/LaboratoryManagementSystem.war`

---

## 10. How to Deploy on Apache Tomcat
1. Download and extract **Apache Tomcat 10.1+** (e.g. `apache-tomcat-10.1.20`).
2. Copy `target/LaboratoryManagementSystem.war` into Tomcat's `webapps/` folder:
   ```bash
   cp target/LaboratoryManagementSystem.war <TOMCAT_DIR>/webapps/
   ```
3. Start Tomcat:
   - On Windows: `<TOMCAT_DIR>\bin\startup.bat`
   - On Linux/Mac: `<TOMCAT_DIR>/bin/startup.sh`

---

## 11. How to Run the Project
1. Open your web browser.
2. Navigate to:
   ```
   http://localhost:8080/LaboratoryManagementSystem/
   ```
3. You will see the welcome landing page with links to **Sign In** and **Register**.
4. You can also run `run_server.bat` to launch Tomcat and open the browser automatically.

---

## 12. Default Test Accounts
All sample accounts are preloaded with SHA-256 hashed passwords in `database.sql`:

| Role | Username | Email | Password | Details |
|---|---|---|---|---|
| **ADMIN** | `admin` | `admin@college.edu` | `admin123` | System Administrator |
| **FACULTY** | `faculty1` | `faculty1@college.edu` | `faculty123` | Dr. Aris Thorne (Associate Prof, CSE) |
| **FACULTY** | `faculty2` | `faculty2@college.edu` | `faculty123` | Prof. Elena Rostova (Assistant Prof, IT) |
| **STUDENT** | `student1` | `student1@college.edu` | `student123` | Aarav Sharma (CS2026-001, Sem 5) |
| **STUDENT** | `student2` | `student2@college.edu` | `student123` | Diya Patel (IT2026-042, Sem 5) |
| **STUDENT** | `student3` | `student3@college.edu` | `student123` | Rohan Verma (CS2026-088, Sem 5) |

*(Note: On the login page, you can also use the one-click demo autofill buttons for rapid testing during your project presentation/viva).*

---

## 13. Screenshots Section Placeholder

```
+-----------------------------------------------------------------------+
|                       [ LOGIN / LANDING SCREEN ]                      |
|                                                                       |
|  [Screenshot of Index & Login Portal with One-Click Autofill]         |
+-----------------------------------------------------------------------+
|                       [ ADMIN DASHBOARD ]                             |
|                                                                       |
|  [Screenshot of Admin Metrics, Laboratories, and User Directory]      |
+-----------------------------------------------------------------------+
|                       [ FACULTY PORTAL ]                              |
|                                                                       |
|  [Screenshot of Continuous Assessment Marks Entry & Tasks]            |
+-----------------------------------------------------------------------+
|                       [ STUDENT PORTAL ]                              |
|                                                                       |
|  [Screenshot of Auto-Attendance, Grades, and Examination Venue]       |
+-----------------------------------------------------------------------+
```

---

## 14. Future Enhancements
1. **Barcode / QR-Code Equipment Scanning**: Allow lab assistants to scan hardware assets using webcams or mobile devices.
2. **Online Assignment File Uploads**: Enable students to upload `.zip` or `.pdf` lab assignment solutions directly to the server.
3. **Automated Email / SMS Notifications**: Send deadline reminders and exam venue alerts via JavaMail API.
4. **Biometric Integration**: Interface physical biometric fingerprint devices with the attendance servlet.
