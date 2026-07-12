package com.school.enrollment.controller;

import com.school.enrollment.entity.Enrollment;
import com.school.enrollment.entity.Payment;
import com.school.enrollment.entity.Student;
import com.school.enrollment.service.EnrollmentService;
import com.school.enrollment.service.PaymentService;
import com.school.enrollment.service.StudentService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.text.NumberFormat;
import java.util.*;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/payments")
public class PaymentController {

    private final PaymentService paymentService;
    private final EnrollmentService enrollmentService;
    private final StudentService studentService;

    public PaymentController(PaymentService paymentService,
                            EnrollmentService enrollmentService,
                            StudentService studentService) {
        this.paymentService = paymentService;
        this.enrollmentService = enrollmentService;
        this.studentService = studentService;
    }

    @GetMapping
    public String index(@RequestParam(value = "studentId", required = false) String studentId,
                        @RequestParam(value = "search", required = false) String search,
                        Model model) {
        List<Student> students = studentService.getAllActive();
        model.addAttribute("students", students);

        List<Enrollment> allEnrollments = enrollmentService.getAllEnrollments();
        Map<String, List<Enrollment>> grouped = allEnrollments.stream()
                .collect(Collectors.groupingBy(Enrollment::getStudentId, LinkedHashMap::new, Collectors.toList()));

        List<Map<String, Object>> studentBalances = new ArrayList<>();
        double grandTotalTuition = 0;
        double grandTotalPaid = 0;

        for (Map.Entry<String, List<Enrollment>> entry : grouped.entrySet()) {
            String sid = entry.getKey();
            List<Enrollment> enrs = entry.getValue();
            if (!enrs.isEmpty()) {
                String name = enrs.get(0).getStudentName();
                double totalTuition = 0;
                double totalPaid = 0;
                for (Enrollment e : enrs) {
                    totalTuition += e.getTotalTuition();
                    totalPaid += enrollmentService.getTotalPaid(e.getEnrollmentId());
                }
                double balance = Math.max(0, totalTuition - totalPaid);
                grandTotalTuition += totalTuition;
                grandTotalPaid += totalPaid;

                Map<String, Object> row = new LinkedHashMap<>();
                row.put("studentId", sid);
                row.put("studentName", name);
                row.put("totalTuition", totalTuition);
                row.put("totalPaid", totalPaid);
                row.put("balance", balance);
                studentBalances.add(row);
            }
        }

        model.addAttribute("studentBalances", studentBalances);
        model.addAttribute("grandTotalTuition", grandTotalTuition);
        model.addAttribute("grandTotalPaid", grandTotalPaid);

        List<Payment> payments;
        if (studentId != null && !studentId.trim().isEmpty()) {
            payments = paymentService.getByStudentId(studentId);
        } else {
            payments = paymentService.search(search);
        }
        List<Payment> aggregated = paymentService.aggregatePayments(payments);
        model.addAttribute("payments", aggregated);
        model.addAttribute("selectedStudentId", studentId);
        model.addAttribute("search", search);
        return "payments";
    }

    @PostMapping("/pay")
    public String pay(@RequestParam("studentId") String studentId,
                      @RequestParam("amount") double amount,
                      @RequestParam("paymentMethod") String paymentMethod,
                      @RequestParam("referenceNumber") String referenceNumber,
                      RedirectAttributes redirectAttributes) {
        if (amount <= 0) {
            redirectAttributes.addFlashAttribute("error", "Amount must be greater than zero.");
            return "redirect:/payments?studentId=" + studentId;
        }
        if (paymentMethod == null || paymentMethod.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Please select a payment method.");
            return "redirect:/payments?studentId=" + studentId;
        }
        if (referenceNumber == null || referenceNumber.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Reference number is required.");
            return "redirect:/payments?studentId=" + studentId;
        }

        try {
            List<Enrollment> studentEnrs = enrollmentService.getByStudent(studentId);
            if (studentEnrs.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "No active enrollments for this student.");
                return "redirect:/payments?studentId=" + studentId;
            }

            double totalBalance = 0;
            for (Enrollment e : studentEnrs) {
                totalBalance += e.getTotalTuition() - enrollmentService.getTotalPaid(e.getEnrollmentId());
            }

            double actualAmount = Math.min(amount, totalBalance);
            List<double[]> enrollmentPayments = new ArrayList<>();
            double remainingAmount = actualAmount;

            for (int i = 0; i < studentEnrs.size(); i++) {
                Enrollment enrollment = studentEnrs.get(i);
                double eBal = enrollment.getTotalTuition() - enrollmentService.getTotalPaid(enrollment.getEnrollmentId());
                if (eBal <= 0) continue;
                double payAmt = (i == studentEnrs.size() - 1)
                        ? Math.round(remainingAmount * 100.0) / 100.0
                        : Math.min(eBal, Math.round((eBal / totalBalance * actualAmount) * 100.0) / 100.0);
                remainingAmount -= payAmt;
                enrollmentPayments.add(new double[]{enrollment.getEnrollmentId(), payAmt});
            }

            if (enrollmentPayments.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "No applicable balance found.");
                return "redirect:/payments?studentId=" + studentId;
            }

            String result = paymentService.processPayments(enrollmentPayments, paymentMethod, referenceNumber);
            redirectAttributes.addFlashAttribute("success", "Payments recorded:\n" + result);
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error processing payment: " + e.getMessage());
        }
        return "redirect:/payments?studentId=" + studentId;
    }

    @PostMapping("/delete")
    public String delete(@RequestParam("referenceNumber") String referenceNumber,
                         @RequestParam(value = "studentId", required = false) String studentId,
                         RedirectAttributes redirectAttributes) {
        try {
            List<Payment> payments = paymentService.getByReference(referenceNumber);
            if (payments.isEmpty()) {
                redirectAttributes.addFlashAttribute("error", "No payments found with that reference number.");
                return "redirect:/payments" + (studentId != null ? "?studentId=" + studentId : "");
            }
            paymentService.deleteByReference(referenceNumber);
            redirectAttributes.addFlashAttribute("success", payments.size() + " payment record(s) deleted successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deleting payments: " + e.getMessage());
        }
        return "redirect:/payments" + (studentId != null ? "?studentId=" + studentId : "");
    }

    @GetMapping("/api/enrollment-balance")
    @ResponseBody
    public Map<String, Object> getEnrollmentBalance(@RequestParam("enrollmentId") int enrollmentId) {
        double paid = enrollmentService.getTotalPaid(enrollmentId);
        Map<String, Object> result = new HashMap<>();
        result.put("paid", paid);
        return result;
    }
}
