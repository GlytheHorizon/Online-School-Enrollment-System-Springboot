package com.school.enrollment.repository;

import com.school.enrollment.entity.EnrollmentAudit;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentAuditRepository extends JpaRepository<EnrollmentAudit, Integer> {

    List<EnrollmentAudit> findAllByOrderByActionDateDesc();

    @Query("SELECT a FROM EnrollmentAudit a WHERE " +
           "a.studentId LIKE %:keyword% OR a.action LIKE %:keyword% " +
           "ORDER BY a.actionDate DESC")
    List<EnrollmentAudit> search(@Param("keyword") String keyword);

    List<EnrollmentAudit> findByStudentIdOrderByActionDateDesc(String studentId);
}
