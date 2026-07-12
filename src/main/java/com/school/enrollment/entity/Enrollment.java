package com.school.enrollment.entity;

import jakarta.persistence.*;
import java.time.LocalDate;

@Entity
@Table(name = "enrollments")
public class Enrollment {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "enrollment_id")
    private int enrollmentId;

    @Column(name = "student_id", length = 20, nullable = false)
    private String studentId;

    @Column(name = "course_id", nullable = false)
    private int courseId;

    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;

    @Column(length = 20)
    private String status;

    @Transient
    private String studentName;

    @Transient
    private String courseName;

    @Transient
    private String courseCode;

    @Transient
    private int units;

    @Transient
    private double tuitionPerUnit;

    public Enrollment() {}

    public int getEnrollmentId() { return enrollmentId; }
    public void setEnrollmentId(int enrollmentId) { this.enrollmentId = enrollmentId; }
    public String getStudentId() { return studentId; }
    public void setStudentId(String studentId) { this.studentId = studentId; }
    public int getCourseId() { return courseId; }
    public void setCourseId(int courseId) { this.courseId = courseId; }
    public LocalDate getEnrollmentDate() { return enrollmentDate; }
    public void setEnrollmentDate(LocalDate enrollmentDate) { this.enrollmentDate = enrollmentDate; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getStudentName() { return studentName; }
    public void setStudentName(String studentName) { this.studentName = studentName; }
    public String getCourseName() { return courseName; }
    public void setCourseName(String courseName) { this.courseName = courseName; }
    public String getCourseCode() { return courseCode; }
    public void setCourseCode(String courseCode) { this.courseCode = courseCode; }
    public int getUnits() { return units; }
    public void setUnits(int units) { this.units = units; }
    public double getTuitionPerUnit() { return tuitionPerUnit; }
    public void setTuitionPerUnit(double tuitionPerUnit) { this.tuitionPerUnit = tuitionPerUnit; }

    public double getTotalTuition() {
        return units * tuitionPerUnit;
    }
}
