package com.school.enrollment.controller;

import com.school.enrollment.repository.CourseRepository;
import com.school.enrollment.repository.EnrollmentRepository;
import com.school.enrollment.repository.PaymentRepository;
import com.school.enrollment.repository.StudentRepository;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    private final StudentRepository studentRepository;
    private final CourseRepository courseRepository;
    private final EnrollmentRepository enrollmentRepository;
    private final PaymentRepository paymentRepository;

    public HomeController(StudentRepository studentRepository,
                          CourseRepository courseRepository,
                          EnrollmentRepository enrollmentRepository,
                          PaymentRepository paymentRepository) {
        this.studentRepository = studentRepository;
        this.courseRepository = courseRepository;
        this.enrollmentRepository = enrollmentRepository;
        this.paymentRepository = paymentRepository;
    }

    @GetMapping("/")
    public String index() {
        return "redirect:/info";
    }

    @GetMapping("/info")
    public String info(Model model) {
        model.addAttribute("currentPage", "info");
        model.addAttribute("totalStudents", studentRepository.count());
        model.addAttribute("totalCourses", courseRepository.count());
        model.addAttribute("totalEnrollments", enrollmentRepository.count());
        model.addAttribute("totalPayments", paymentRepository.count());
        return "info";
    }
}
