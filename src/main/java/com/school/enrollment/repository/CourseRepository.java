package com.school.enrollment.repository;

import com.school.enrollment.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Integer> {

    List<Course> findByActiveTrueOrderByCourseCodeAsc();

    @Query("SELECT c FROM Course c WHERE c.active = true AND (" +
           "c.courseCode LIKE %:keyword% OR c.courseName LIKE %:keyword%) " +
           "ORDER BY c.courseCode")
    List<Course> searchActive(@Param("keyword") String keyword);

    @Query("SELECT COUNT(e) > 0 FROM Enrollment e WHERE e.courseId = :courseId AND e.status = 'Enrolled'")
    boolean hasActiveEnrollments(@Param("courseId") int courseId);
}
