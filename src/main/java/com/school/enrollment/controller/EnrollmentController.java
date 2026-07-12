package com.school.enrollment.controller;

import com.school.enrollment.entity.Course;
import com.school.enrollment.entity.Enrollment;
import com.school.enrollment.entity.EnrollmentAudit;
import com.school.enrollment.entity.Student;
import com.school.enrollment.service.CourseService;
import com.school.enrollment.service.EnrollmentAuditService;
import com.school.enrollment.service.EnrollmentService;
import com.school.enrollment.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/enrollments")
public class EnrollmentController {

    private final EnrollmentService enrollmentService;
    private final EnrollmentAuditService auditService;
    private final StudentService studentService;
    private final CourseService courseService;

    public EnrollmentController(EnrollmentService enrollmentService,
                               EnrollmentAuditService auditService,
                               StudentService studentService,
                               CourseService courseService) {
        this.enrollmentService = enrollmentService;
        this.auditService = auditService;
        this.studentService = studentService;
        this.courseService = courseService;
    }

    @GetMapping
    public String index(@RequestParam(value = "search", required = false) String search,
                        @RequestParam(value = "auditSearch", required = false) String auditSearch,
                        Model model) {
        List<Student> students = studentService.getAllActive();
        List<Course> courses = courseService.getAllActive();

        List<Enrollment> enrollments = enrollmentService.search(search);
        List<EnrollmentAudit> auditLogs = auditService.search(auditSearch);

        Map<Integer, Double> paidMap = new java.util.HashMap<>();
        for (Enrollment e : enrollments) {
            paidMap.put(e.getEnrollmentId(), enrollmentService.getTotalPaid(e.getEnrollmentId()));
        }

        model.addAttribute("students", students);
        model.addAttribute("courses", courses);
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("paidMap", paidMap);
        model.addAttribute("auditLogs", auditLogs);
        model.addAttribute("search", search);
        model.addAttribute("auditSearch", auditSearch);
        return "enrollments";
    }

    @PostMapping("/enroll")
    public String enroll(@RequestParam("studentId") String studentId,
                         @RequestParam(value = "courseIds", required = false) List<Integer> courseIds,
                         RedirectAttributes redirectAttributes) {
        if (studentId == null || studentId.trim().isEmpty() || courseIds == null || courseIds.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please select a student and at least one course.");
            return "redirect:/enrollments";
        }
        List<Course> courses = new ArrayList<>();
        for (int cid : courseIds) {
            Course c = courseService.get(cid);
            if (c != null) courses.add(c);
        }
        Map<String, Object> result = enrollmentService.enrollStudent(studentId, courses);
        String msg = (String) result.get("message");
        if ((boolean) result.get("success")) {
            redirectAttributes.addFlashAttribute("success", msg);
        } else {
            redirectAttributes.addFlashAttribute("error", msg);
        }
        return "redirect:/enrollments";
    }

    @PostMapping("/{id}/drop")
    public String drop(@PathVariable("id") int enrollmentId,
                       RedirectAttributes redirectAttributes) {
        try {
            enrollmentService.dropEnrollment(enrollmentId);
            redirectAttributes.addFlashAttribute("success", "Enrollment dropped successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", e.getMessage());
        }
        return "redirect:/enrollments";
    }
}
