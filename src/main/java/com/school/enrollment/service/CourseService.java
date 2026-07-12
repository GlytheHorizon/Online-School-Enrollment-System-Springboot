package com.school.enrollment.service;

import com.school.enrollment.entity.Course;
import com.school.enrollment.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public Course add(String courseCode, String courseName, int units, double tuitionPerUnit) {
        Course course = new Course(courseCode.trim().toUpperCase(), courseName.trim(), units, tuitionPerUnit);
        return courseRepository.save(course);
    }

    public Course update(int courseId, String courseCode, String courseName, int units, double tuitionPerUnit) {
        Course c = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found: " + courseId));
        c.setCourseCode(courseCode.trim().toUpperCase());
        c.setCourseName(courseName.trim());
        c.setUnits(units);
        c.setTuitionPerUnit(tuitionPerUnit);
        return courseRepository.save(c);
    }

    public void deactivate(int courseId) {
        Course c = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found: " + courseId));
        c.setActive(false);
        courseRepository.save(c);
    }

    public Course get(int courseId) {
        return courseRepository.findById(courseId).orElse(null);
    }

    public List<Course> getAllActive() {
        return courseRepository.findByActiveTrueOrderByCourseCodeAsc();
    }

    public List<Course> searchActive(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllActive();
        }
        return courseRepository.searchActive(keyword.trim());
    }

    public boolean hasActiveEnrollments(int courseId) {
        return courseRepository.hasActiveEnrollments(courseId);
    }

    public void delete(int courseId) {
        if (hasActiveEnrollments(courseId)) {
            throw new RuntimeException("Cannot delete course — it has active enrollments.");
        }
        courseRepository.deleteById(courseId);
    }
}
