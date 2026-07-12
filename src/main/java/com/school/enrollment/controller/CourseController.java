package com.school.enrollment.controller;

import com.school.enrollment.entity.Course;
import com.school.enrollment.service.CourseService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/courses")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public String list(@RequestParam(value = "search", required = false) String search, Model model) {
        List<Course> courses = courseService.searchActive(search);
        model.addAttribute("courses", courses);
        model.addAttribute("search", search);
        return "courses";
    }

    @PostMapping("/add")
    public String add(@RequestParam("courseCode") String courseCode,
                      @RequestParam("courseName") String courseName,
                      @RequestParam("units") int units,
                      @RequestParam("tuitionPerUnit") double tuitionPerUnit,
                      RedirectAttributes redirectAttributes) {
        if (courseCode.trim().isEmpty() || courseName.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Course code and name are required.");
            return "redirect:/courses";
        }
        if (units <= 0) {
            redirectAttributes.addFlashAttribute("error", "Units must be greater than zero.");
            return "redirect:/courses";
        }
        if (tuitionPerUnit <= 0) {
            redirectAttributes.addFlashAttribute("error", "Tuition per unit must be greater than zero.");
            return "redirect:/courses";
        }
        try {
            courseService.add(courseCode, courseName, units, tuitionPerUnit);
            redirectAttributes.addFlashAttribute("success", "Course added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error adding course: " + e.getMessage());
        }
        return "redirect:/courses";
    }

    @PostMapping("/{id}/update")
    public String update(@PathVariable("id") int courseId,
                         @RequestParam("courseCode") String courseCode,
                         @RequestParam("courseName") String courseName,
                         @RequestParam("units") int units,
                         @RequestParam("tuitionPerUnit") double tuitionPerUnit,
                         RedirectAttributes redirectAttributes) {
        if (courseCode.trim().isEmpty() || courseName.trim().isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Course code and name are required.");
            return "redirect:/courses";
        }
        try {
            courseService.update(courseId, courseCode, courseName, units, tuitionPerUnit);
            redirectAttributes.addFlashAttribute("success", "Course updated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error updating course: " + e.getMessage());
        }
        return "redirect:/courses";
    }

    @PostMapping("/{id}/deactivate")
    public String deactivate(@PathVariable("id") int courseId,
                             RedirectAttributes redirectAttributes) {
        try {
            if (courseService.hasActiveEnrollments(courseId)) {
                redirectAttributes.addFlashAttribute("error", "Cannot deactivate this course — it has active enrollments.");
                return "redirect:/courses";
            }
            courseService.deactivate(courseId);
            redirectAttributes.addFlashAttribute("success", "Course deactivated successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error deactivating course: " + e.getMessage());
        }
        return "redirect:/courses";
    }
}
