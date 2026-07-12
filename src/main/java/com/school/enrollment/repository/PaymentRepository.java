package com.school.enrollment.repository;

import com.school.enrollment.entity.Payment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PaymentRepository extends JpaRepository<Payment, Integer> {

    List<Payment> findByEnrollmentIdOrderByPaymentDateDesc(int enrollmentId);

    @Query("SELECT COALESCE(SUM(p.amount), 0) FROM Payment p WHERE p.enrollmentId = :enrollmentId")
    double getTotalPaid(@Param("enrollmentId") int enrollmentId);

    List<Payment> findByReferenceNumber(String referenceNumber);

    void deleteByReferenceNumber(String referenceNumber);

    @Query("SELECT p FROM Payment p WHERE p.enrollmentId IN " +
           "(SELECT e.enrollmentId FROM Enrollment e WHERE e.studentId = :studentId) " +
           "ORDER BY p.paymentDate DESC")
    List<Payment> findByStudentId(@Param("studentId") String studentId);
}
