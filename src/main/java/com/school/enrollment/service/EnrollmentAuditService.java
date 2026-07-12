package com.school.enrollment.service;

import com.school.enrollment.entity.Course;
import com.school.enrollment.entity.EnrollmentAudit;
import com.school.enrollment.entity.Student;
import com.school.enrollment.repository.CourseRepository;
import com.school.enrollment.repository.EnrollmentAuditRepository;
import com.school.enrollment.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class EnrollmentAuditService {

    private final EnrollmentAuditRepository auditRepository;
    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;

    public EnrollmentAuditService(EnrollmentAuditRepository auditRepository,
                                  StudentRepository studentRepository,
                                  CourseRepository courseRepository) {
        this.auditRepository = auditRepository;
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
    }

    public List<EnrollmentAudit> getAll() {
        return populateDetails(auditRepository.findAllByOrderByActionDateDesc());
    }

    public List<EnrollmentAudit> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAll();
        }
        String kw = keyword.trim().toLowerCase();
        List<EnrollmentAudit> all = populateDetails(auditRepository.findAllByOrderByActionDateDesc());
        List<EnrollmentAudit> filtered = new ArrayList<>();
        for (EnrollmentAudit a : all) {
            if ((a.getStudentName() != null && a.getStudentName().toLowerCase().contains(kw)) ||
                (a.getCourseName() != null && a.getCourseName().toLowerCase().contains(kw)) ||
                (a.getCourseCode() != null && a.getCourseCode().toLowerCase().contains(kw)) ||
                (a.getAction() != null && a.getAction().toLowerCase().contains(kw))) {
                filtered.add(a);
            }
        }
        return filtered;
    }

    public List<EnrollmentAudit> getByStudent(String studentId) {
        return populateDetails(auditRepository.findByStudentIdOrderByActionDateDesc(studentId));
    }

    private List<EnrollmentAudit> populateDetails(List<EnrollmentAudit> audits) {
        Set<String> studentIds = new HashSet<>();
        Set<Integer> courseIds = new HashSet<>();
        for (EnrollmentAudit a : audits) {
            studentIds.add(a.getStudentId());
            courseIds.add(a.getCourseId());
        }

        Map<String, Student> studentMap = new HashMap<>();
        for (String sid : studentIds) {
            studentRepository.findById(sid).ifPresent(s -> studentMap.put(sid, s));
        }
        Map<Integer, Course> courseMap = new HashMap<>();
        for (int cid : courseIds) {
            courseRepository.findById(cid).ifPresent(c -> courseMap.put(cid, c));
        }

        for (EnrollmentAudit a : audits) {
            Student s = studentMap.get(a.getStudentId());
            if (s != null) {
                a.setStudentName(s.getFullName());
            }
            Course c = courseMap.get(a.getCourseId());
            if (c != null) {
                a.setCourseCode(c.getCourseCode());
                a.setCourseName(c.getCourseName());
            }
        }
        return audits;
    }
}
