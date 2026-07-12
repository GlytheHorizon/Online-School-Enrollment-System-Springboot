package com.school.enrollment.service;

import com.school.enrollment.entity.Payment;
import com.school.enrollment.repository.PaymentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class PaymentService {

    private final PaymentRepository paymentRepository;

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment makePayment(int enrollmentId, double amount, String paymentMethod, String referenceNumber) {
        Payment p = new Payment();
        p.setEnrollmentId(enrollmentId);
        p.setAmount(amount);
        p.setPaymentMethod(paymentMethod.trim());
        p.setReferenceNumber(referenceNumber.trim());
        p.setPaymentDate(LocalDate.now());
        return paymentRepository.save(p);
    }

    public String processPayments(List<double[]> enrollmentPayments, String paymentMethod, String referenceNumber) {
        StringBuilder result = new StringBuilder();
        for (double[] ep : enrollmentPayments) {
            int enrollmentId = (int) ep[0];
            double amount = ep[1];
            try {
                Payment p = new Payment();
                p.setEnrollmentId(enrollmentId);
                p.setAmount(amount);
                p.setPaymentMethod(paymentMethod.trim());
                p.setReferenceNumber(referenceNumber.trim());
                p.setPaymentDate(LocalDate.now());
                paymentRepository.save(p);
                result.append("  P").append(String.format("%.2f", amount)).append(" - OK\n");
            } catch (Exception e) {
                result.append("  Enroll #").append(enrollmentId).append(": ").append(e.getMessage()).append("\n");
            }
        }
        return result.toString();
    }

    public void delete(int paymentId) {
        paymentRepository.deleteById(paymentId);
    }

    public void deleteByReference(String referenceNumber) {
        paymentRepository.deleteByReferenceNumber(referenceNumber);
    }

    public List<Payment> getByReference(String referenceNumber) {
        return paymentRepository.findByReferenceNumber(referenceNumber);
    }

    public double getTotalPaid(int enrollmentId) {
        return paymentRepository.getTotalPaid(enrollmentId);
    }

    public List<Payment> getAll() {
        return paymentRepository.findAll();
    }

    public List<Payment> getByStudentId(String studentId) {
        return paymentRepository.findByStudentId(studentId);
    }

    public List<Payment> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAll();
        }
        String kw = keyword.trim().toLowerCase();
        List<Payment> all = getAll();
        List<Payment> filtered = new ArrayList<>();
        for (Payment p : all) {
            if ((p.getPaymentMethod() != null && p.getPaymentMethod().toLowerCase().contains(kw)) ||
                (p.getReferenceNumber() != null && p.getReferenceNumber().toLowerCase().contains(kw))) {
                filtered.add(p);
            }
        }
        return filtered;
    }

    public List<Payment> aggregatePayments(List<Payment> payments) {
        Map<String, Payment> grouped = new LinkedHashMap<>();
        for (Payment p : payments) {
            String key = p.getStudentName() + "_" + p.getReferenceNumber() + "_" + p.getPaymentMethod() + "_" + p.getPaymentDate();
            if (grouped.containsKey(key)) {
                Payment existing = grouped.get(key);
                existing.setAmount(existing.getAmount() + p.getAmount());
            } else {
                Payment clone = new Payment();
                clone.setPaymentId(p.getPaymentId());
                clone.setEnrollmentId(p.getEnrollmentId());
                clone.setStudentName(p.getStudentName());
                clone.setReferenceNumber(p.getReferenceNumber());
                clone.setPaymentMethod(p.getPaymentMethod());
                clone.setPaymentDate(p.getPaymentDate());
                clone.setAmount(p.getAmount());
                grouped.put(key, clone);
            }
        }
        return new ArrayList<>(grouped.values());
    }
}
