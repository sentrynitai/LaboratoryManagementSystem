package com.lms;

import com.lms.dao.*;
import com.lms.model.*;

import java.sql.Connection;
import java.util.List;

/**
 * End-to-end verification test for the Laboratory Management System.
 * Tests DB connectivity, authentication, role DAOs, and Admin notice creation.
 */
public class LmsSystemVerificationTest {

    public static void main(String[] args) {
        System.out.println("==================================================");
        System.out.println("STARTING LMS SYSTEM INTEGRITY VERIFICATION TEST");
        System.out.println("==================================================");

        int passed = 0;
        int failed = 0;

        // 1. Database Connection Test
        try (Connection conn = DBConnection.getConnection()) {
            if (conn != null && !conn.isClosed()) {
                System.out.println("✅ [PASS] Database connection established successfully.");
                passed++;
            } else {
                System.err.println("❌ [FAIL] Database connection is null or closed.");
                failed++;
            }
        } catch (Exception e) {
            System.err.println("❌ [FAIL] Database connection threw exception: " + e.getMessage());
            failed++;
        }

        // 2. User Authentication Test
        try {
            UserDAO userDAO = new UserDAO();
            User admin = userDAO.login("admin", "admin123");
            if (admin != null && "ADMIN".equalsIgnoreCase(admin.getRole())) {
                System.out.println("✅ [PASS] Admin authentication successful: " + admin.getName());
                passed++;
            } else {
                System.err.println("❌ [FAIL] Admin login failed.");
                failed++;
            }

            User faculty = userDAO.login("faculty1", "faculty123");
            if (faculty != null && "FACULTY".equalsIgnoreCase(faculty.getRole())) {
                System.out.println("✅ [PASS] Faculty authentication successful: " + faculty.getName());
                passed++;
            } else {
                System.err.println("❌ [FAIL] Faculty login failed.");
                failed++;
            }

            User student = userDAO.login("student1", "student123");
            if (student != null && "STUDENT".equalsIgnoreCase(student.getRole())) {
                System.out.println("✅ [PASS] Student authentication successful: " + student.getName());
                passed++;
            } else {
                System.err.println("❌ [FAIL] Student login failed.");
                failed++;
            }
        } catch (Exception e) {
            System.err.println("❌ [FAIL] Authentication threw exception: " + e.getMessage());
            failed++;
        }

        // 3. Laboratories DAO Test
        try {
            LaboratoryDAO labDAO = new LaboratoryDAO();
            List<Laboratory> labs = labDAO.getAllLaboratories();
            System.out.println("✅ [PASS] Laboratories retrieved: " + labs.size() + " labs present.");
            passed++;
        } catch (Exception e) {
            System.err.println("❌ [FAIL] LaboratoryDAO threw exception: " + e.getMessage());
            failed++;
        }

        // 4. Student & Faculty DAOs
        try {
            StudentDAO studentDAO = new StudentDAO();
            List<Student> students = studentDAO.getAllStudents();
            System.out.println("✅ [PASS] Students retrieved: " + students.size() + " students.");
            passed++;

            FacultyDAO facultyDAO = new FacultyDAO();
            List<Faculty> facultyList = facultyDAO.getAllFaculty();
            System.out.println("✅ [PASS] Faculty members retrieved: " + facultyList.size() + " faculty.");
            passed++;
        } catch (Exception e) {
            System.err.println("❌ [FAIL] Student/Faculty DAO threw exception: " + e.getMessage());
            failed++;
        }

        // 5. Admin Notice Creation and Retrieval Test (supports null faculty_id)
        try {
            NoticeDAO noticeDAO = new NoticeDAO();
            Notice adminNotice = new Notice();
            adminNotice.setFacultyId(null); // Admin authored
            adminNotice.setTitle("Official Notice: System Audit Complete");
            adminNotice.setDescription("All laboratory systems, practical schedules, and databases have been validated.");

            boolean added = noticeDAO.addNotice(adminNotice);
            if (added) {
                System.out.println("✅ [PASS] Admin-authored notice published successfully (NULL faculty_id).");
                passed++;
            } else {
                System.err.println("❌ [FAIL] Failed to publish admin notice.");
                failed++;
            }

            List<Notice> notices = noticeDAO.getAllNotices();
            System.out.println("✅ [PASS] Notices retrieved: " + notices.size() + " notices in total.");
            passed++;
        } catch (Exception e) {
            System.err.println("❌ [FAIL] NoticeDAO threw exception: " + e.getMessage());
            failed++;
        }

        // 6. Tasks, Exams, Marks, and Attendance DAOs
        try {
            TaskDAO taskDAO = new TaskDAO();
            System.out.println("✅ [PASS] Tasks retrieved: " + taskDAO.getAllTasks().size() + " tasks.");
            passed++;

            LabExamDAO examDAO = new LabExamDAO();
            System.out.println("✅ [PASS] Practical Exams retrieved: " + examDAO.getAllLabExams().size() + " exams.");
            passed++;

            MarksDAO marksDAO = new MarksDAO();
            System.out.println("✅ [PASS] Marks records retrieved: " + marksDAO.getAllMarks().size() + " marks.");
            passed++;

            AttendanceDAO attendanceDAO = new AttendanceDAO();
            System.out.println("✅ [PASS] Attendance entries retrieved: " + attendanceDAO.getAllAttendance().size() + " entries.");
            passed++;
        } catch (Exception e) {
            System.err.println("❌ [FAIL] Academic DAOs threw exception: " + e.getMessage());
            failed++;
        }

        System.out.println("==================================================");
        System.out.println("VERIFICATION SUMMARY: " + passed + " PASSED, " + failed + " FAILED");
        System.out.println("==================================================");

        if (failed > 0) {
            System.exit(1);
        }
    }
}
