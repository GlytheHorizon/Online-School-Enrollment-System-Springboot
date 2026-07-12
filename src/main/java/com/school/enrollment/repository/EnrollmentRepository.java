package com.school.enrollment.repository;

import com.school.enrollment.entity.Enrollment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EnrollmentRepository extends JpaRepository<Enrollment, Integer> {

    @Query("SELECT e FROM Enrollment e WHERE e.status = 'Enrolled' ORDER BY e.enrollmentDate DESC")
    List<Enrollment> findAllActive();

    @Query("SELECT e FROM Enrollment e WHERE e.studentId = :studentId AND e.status = 'Enrolled' ORDER BY e.enrollmentDate DESC")
    List<Enrollment> findByStudentAndActive(@Param("studentId") String studentId);

    @Query("SELECT e FROM Enrollment e WHERE e.courseId = :courseId AND e.status = 'Enrolled' ORDER BY e.enrollmentDate DESC")
    List<Enrollment> findByCourseAndActive(@Param("courseId") int courseId);

    @Query("SELECT COUNT(e) > 0 FROM Enrollment e WHERE e.studentId = :studentId AND e.courseId = :courseId AND e.status = 'Enrolled'")
    boolean existsActive(@Param("studentId") String studentId, @Param("courseId") int courseId);
}
