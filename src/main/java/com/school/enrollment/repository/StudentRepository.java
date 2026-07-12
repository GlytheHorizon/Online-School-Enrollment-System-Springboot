package com.school.enrollment.repository;

import com.school.enrollment.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentRepository extends JpaRepository<Student, String> {

    List<Student> findByActiveTrueOrderByLastNameAscFirstNameAsc();

    List<Student> findByActiveFalseOrderByLastNameAscFirstNameAsc();

    List<Student> findAllByOrderByLastNameAscFirstNameAsc();

    @Query("SELECT s FROM Student s WHERE s.active = true AND (" +
           "s.studentId LIKE %:keyword% OR s.firstName LIKE %:keyword% OR " +
           "s.lastName LIKE %:keyword% OR s.email LIKE %:keyword%) " +
           "ORDER BY s.lastName, s.firstName")
    List<Student> searchActive(@Param("keyword") String keyword);

    @Query("SELECT s FROM Student s WHERE s.active = false AND (" +
           "s.studentId LIKE %:keyword% OR s.firstName LIKE %:keyword% OR " +
           "s.lastName LIKE %:keyword% OR s.email LIKE %:keyword%) " +
           "ORDER BY s.lastName, s.firstName")
    List<Student> searchInactive(@Param("keyword") String keyword);

    @Query("SELECT COUNT(e) > 0 FROM Enrollment e WHERE e.studentId = :studentId AND e.status = 'Enrolled'")
    boolean hasActiveEnrollments(@Param("studentId") String studentId);

    @Query("SELECT COUNT(p) > 0 FROM Payment p JOIN Enrollment e ON p.enrollmentId = e.enrollmentId WHERE e.studentId = :studentId")
    boolean hasPayments(@Param("studentId") String studentId);
}
