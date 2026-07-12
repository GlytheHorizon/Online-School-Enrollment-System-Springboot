package com.school.enrollment.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "enrollment_audit")
public class EnrollmentAudit {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "audit_id")
    private int auditId;

    @Column(name = "student_id", length = 20, nullable = false)
    private String studentId;

    @Column(name = "course_id", nullable = false)
    private int courseId;

    @Column(length = 20, nullable = false)
    private String action;

    @Column(name = "action_date")
    private LocalDateTime actionDate;

    @Transient
    private String studentName;

    @Transient
    private String courseName;

    @Transient
    private String courseCode;

    public EnrollmentAudit() {}

    public int getAuditId() { return auditId; }
    public void setAuditId(int auditId) { this.auditId = auditId; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public LocalDateTime getActionDate() { return actionDate; }
    public void setActionDate(LocalDateTime actionDate) { this.actionDate = actionDate; }
    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
}
