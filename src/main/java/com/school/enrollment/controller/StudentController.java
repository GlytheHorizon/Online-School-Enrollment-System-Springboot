package com.school.enrollment.controller;

import com.school.enrollment.entity.Enrollment;
import com.school.enrollment.entity.Student;
import com.school.enrollment.service.EnrollmentService;
import com.school.enrollment.service.StudentService;
import org.springframework.stereotype.Controller;
import java.util.Map;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/students")
public class StudentController {

    private final StudentService studentService;
    private final EnrollmentService enrollmentService;

    public StudentController(StudentService studentService, EnrollmentService enrollmentService) {
        this.studentService = studentService;
        this.enrollmentService = enrollmentService;
    }

    @GetMapping
    public String list(@RequestParam(value = "archived", defaultValue = "false") boolean archived,
                       @RequestParam(value = "search", required = false) String search,
                       Model model) {
        List<Student> students;
        if (archived) {
            students = studentService.searchInactive(search);
        } else {
            students = studentService.searchActive(search);
        }
        model.addAttribute("students", students);
        model.addAttribute("archived", archived);
        model.addAttribute("search", search);
        return "students";
    }

    @GetMapping("/{id}")
    public String details(@PathVariable("id") String studentId, Model model) {
        Student student = studentService.get(studentId);
        if (student == null) {
            return "redirect:/students";
        }
        List<Enrollment> enrollments = enrollmentService.getByStudent(studentId);
        Map<Integer, Double> paidMap = new java.util.HashMap<>();
        for (Enrollment e : enrollments) {
            paidMap.put(e.getEnrollmentId(), enrollmentService.getTotalPaid(e.getEnrollmentId()));
        }
        model.addAttribute("student", student);
        model.addAttribute("enrollments", enrollments);
        model.addAttribute("paidMap", paidMap);
        return "student-details";
    }

    @PostMapping("/register")
    public String register(@RequestParam("studentId") String studentId,
                           @RequestParam("firstName") String firstName,
                           @RequestParam("lastName") String lastName,
                           @RequestParam("email") String email,
                           @RequestParam(value = "phone", required = false) String phone,
                           @RequestParam(value = "address", required = false) String address,
                           RedirectAttributes redirectAttributes) {
        if (studentId.trim().isEmpty() || firstName.trim().isEmpty() || lastName.trim().isEmpty() || email.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Student ID, first name, last name, and email are required.");
            return "redirect:/students";
        }
        if (!studentId.matches("^\\d{4}-\\d{4}$")) {
            redirectAttributes.addFlashAttribute("error", "Student ID must be in format XXXX-XXXX (e.g., 2024-0001).");
            return "redirect:/students";
        }
        if (!email.matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            redirectAttributes.addFlashAttribute("error", "Invalid email format.");
            return "redirect:/students";
        }
        if (studentService.exists(studentId)) {
            redirectAttributes.addFlashAttribute("error", "Student ID already exists.");
            return "redirect:/students";
        }
        try {
            studentService.register(studentId, firstName, lastName, email, phone, address);
            redirectAttributes.addFlashAttribute("success", "Student registered successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error registering student: " + e.getMessage());
        }
        return "redirect:/students";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable("id") String studentId,
                         @RequestParam("firstName") String firstName,
                         @RequestParam("lastName") String lastName,
                         @RequestParam("email") String email,
                         @RequestParam(value = "phone", required = false) String phone,
                         @RequestParam(value = "address", required = false) String address,
                         RedirectAttributes redirectAttributes) {
        if (firstName.trim().isEmpty() || lastName.trim().isEmpty() || email.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "First name, last name, and email are required.");
            return "redirect:/students/" + studentId;
        }
        try {
            studentService.update(studentId, firstName, lastName, email, phone, address);
            redirectAttributes.addFlashAttribute("success", "Student updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating student: " + e.getMessage());
        }
        return "redirect:/students/" + studentId;
    }

    @PostMapping("/{id}/deactivate")
    public String deactivate(@PathVariable("id") String studentId,
                             RedirectAttributes redirectAttributes) {
        try {
            boolean hasEnrollments = studentService.hasActiveEnrollments(studentId);
            boolean hasPayments = studentService.hasPayments(studentId);
            studentService.deactivate(studentId);
            redirectAttributes.addFlashAttribute("success", "Student deactivated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deactivating student: " + e.getMessage());
        }
        return "redirect:/students";
    }

    @PostMapping("/{id}/reactivate")
    public String reactivate(@PathVariable("id") String studentId,
                             RedirectAttributes redirectAttributes) {
        try {
            studentService.reactivate(studentId);
            redirectAttributes.addFlashAttribute("success", "Student reactivated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error reactivating student: " + e.getMessage());
        }
        return "redirect:/students";
    }
}
