package com.school.enrollment.service;

import com.school.enrollment.entity.Course;
import com.school.enrollment.entity.Enrollment;
import com.school.enrollment.entity.EnrollmentAudit;
import com.school.enrollment.entity.Student;
import com.school.enrollment.repository.CourseRepository;
import com.school.enrollment.repository.EnrollmentAuditRepository;
import com.school.enrollment.repository.EnrollmentRepository;
import com.school.enrollment.repository.PaymentRepository;
import com.school.enrollment.repository.StudentRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.*;

@Service
public class EnrollmentService {

    private final EnrollmentRepository enrollmentRepository;
    private final EnrollmentAuditRepository auditRepository;
    private final PaymentRepository paymentRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public EnrollmentService(EnrollmentRepository enrollmentRepository,
                            EnrollmentAuditRepository auditRepository,
                            PaymentRepository paymentRepository,
                            StudentRepository studentRepository,
                            CourseRepository courseRepository) {
        this.enrollmentRepository = enrollmentRepository;
        this.auditRepository = auditRepository;
        this.paymentRepository = paymentRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    @Transactional
    public Map<String, Object> enrollStudent(String studentId, List<Course> courses) {
        StringBuilder successMsg = new StringBuilder();
        StringBuilder errorMsg = new StringBuilder();
        int successCount = 0;

        for (Course course : courses) {
            try {
                if (enrollmentRepository.existsActive(studentId, course.getCourseId())) {
                    errorMsg.append("- ").append(course.getCourseCode()).append(": already enrolled\n");
                    continue;
                }
                Enrollment e = new Enrollment();
                e.setStudentId(studentId);
                e.setCourseId(course.getCourseId());
                e.setStatus("Enrolled");
                e.setEnrollmentDate(LocalDate.now());
                enrollmentRepository.save(e);

                EnrollmentAudit audit = new EnrollmentAudit();
                audit.setStudentId(studentId);
                audit.setCourseId(course.getCourseId());
                audit.setAction("ENROLLED");
                audit.setActionDate(java.time.LocalDateTime.now());
                auditRepository.save(audit);

                successMsg.append("- ").append(course.getCourseCode()).append(" (").append(course.getCourseName()).append(")\n");
                successCount++;
            } catch (Exception ex) {
                errorMsg.append("- ").append(course.getCourseCode()).append(": ").append(ex.getMessage()).append("\n");
            }
        }

        Map<String, Object> result = new HashMap<>();
        result.put("success", successCount > 0);
        result.put("successCount", successCount);
        result.put("message", buildMessage(successMsg.toString(), errorMsg.toString(), successCount));
        return result;
    }

    private String buildMessage(String success, String errors, int successCount) {
        StringBuilder full = new StringBuilder();
        if (successCount > 0) {
            full.append("Student enrolled successfully in:\n").append(success);
        }
        if (!errors.isEmpty()) {
            if (successCount > 0) full.append("\n");
            full.append("Issues:\n").append(errors);
        }
        return full.toString();
    }

    @Transactional
    public void dropEnrollment(int enrollmentId) {
        Enrollment e = enrollmentRepository.findById(enrollmentId)
                .orElseThrow(() -> new RuntimeException("Enrollment not found"));
        double paid = paymentRepository.getTotalPaid(enrollmentId);
        if (paid > 0) {
            throw new RuntimeException("Cannot drop this enrollment — payment has already been made (P"
                    + String.format("%.2f", paid) + "). Contact admin for refund.");
        }
        e.setStatus("Dropped");
        enrollmentRepository.save(e);

        EnrollmentAudit audit = new EnrollmentAudit();
        audit.setStudentId(e.getStudentId());
        audit.setCourseId(e.getCourseId());
        audit.setAction("DROPPED");
        audit.setActionDate(java.time.LocalDateTime.now());
        auditRepository.save(audit);
    }

    public List<Enrollment> getAllEnrollments() {
        List<Enrollment> list = enrollmentRepository.findAllActive();
        return populateEnrollmentDetails(list);
    }

    public List<Enrollment> getByStudent(String studentId) {
        List<Enrollment> list = enrollmentRepository.findByStudentAndActive(studentId);
        return populateEnrollmentDetails(list);
    }

    public List<Enrollment> search(String keyword) {
        List<Enrollment> list = enrollmentRepository.findAllActive();
        if (keyword == null || keyword.trim().isEmpty()) {
            return populateEnrollmentDetails(list);
        }
        String kw = keyword.trim().toLowerCase();
        List<Enrollment> filtered = new ArrayList<>();
        for (Enrollment e : populateEnrollmentDetails(list)) {
            if ((e.getStudentName() != null && e.getStudentName().toLowerCase().contains(kw)) ||
                (e.getCourseName() != null && e.getCourseName().toLowerCase().contains(kw)) ||
                (e.getCourseCode() != null && e.getCourseCode().toLowerCase().contains(kw))) {
                filtered.add(e);
            }
        }
        return filtered;
    }

    private List<Enrollment> populateEnrollmentDetails(List<Enrollment> enrollments) {
        Set<String> studentIds = new HashSet<>();
        Set<Integer> courseIds = new HashSet<>();
        for (Enrollment e : enrollments) {
            studentIds.add(e.getStudentId());
            courseIds.add(e.getCourseId());
        }

        Map<String, Student> studentMap = new HashMap<>();
        for (String sid : studentIds) {
            studentRepository.findById(sid).ifPresent(s -> studentMap.put(sid, s));
        }
        Map<Integer, Course> courseMap = new HashMap<>();
        for (int cid : courseIds) {
            courseRepository.findById(cid).ifPresent(c -> courseMap.put(cid, c));
        }

        for (Enrollment e : enrollments) {
            Student s = studentMap.get(e.getStudentId());
            if (s != null) {
                e.setStudentName(s.getFullName());
            }
            Course c = courseMap.get(e.getCourseId());
            if (c != null) {
                e.setCourseCode(c.getCourseCode());
                e.setCourseName(c.getCourseName());
                e.setUnits(c.getUnits());
                e.setTuitionPerUnit(c.getTuitionPerUnit());
            }
        }
        return enrollments;
    }

    public double getTotalPaid(int enrollmentId) {
        return paymentRepository.getTotalPaid(enrollmentId);
    }

    public boolean exists(String studentId, int courseId) {
        return enrollmentRepository.existsActive(studentId, courseId);
    }
}
