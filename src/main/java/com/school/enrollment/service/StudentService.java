package com.school.enrollment.service;

import com.school.enrollment.entity.Student;
import com.school.enrollment.repository.StudentRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class StudentService {

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    public Student register(String studentId, String firstName, String lastName,
                           String email, String phone, String address) {
        Student student = new Student(studentId.trim(), firstName.trim(), lastName.trim(),
                                      email.trim(), phone.trim(), address.trim());
        student.setRegistrationDate(LocalDate.now());
        return studentRepository.save(student);
    }

    public Student update(String studentId, String firstName, String lastName,
                         String email, String phone, String address) {
        Student s = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));
        s.setFirstName(firstName.trim());
        s.setLastName(lastName.trim());
        s.setEmail(email.trim());
        s.setPhone(phone.trim());
        s.setAddress(address.trim());
        return studentRepository.save(s);
    }

    public void deactivate(String studentId) {
        Student s = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));
        s.setActive(false);
        studentRepository.save(s);
    }

    public void reactivate(String studentId) {
        Student s = studentRepository.findById(studentId)
                .orElseThrow(() -> new RuntimeException("Student not found: " + studentId));
        s.setActive(true);
        studentRepository.save(s);
    }

    public Student get(String studentId) {
        return studentRepository.findById(studentId).orElse(null);
    }

    public List<Student> getAllActive() {
        return studentRepository.findByActiveTrueOrderByLastNameAscFirstNameAsc();
    }

    public List<Student> getAllInactive() {
        return studentRepository.findByActiveFalseOrderByLastNameAscFirstNameAsc();
    }

    public List<Student> searchActive(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllActive();
        }
        return studentRepository.searchActive(keyword.trim());
    }

    public List<Student> searchInactive(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAllInactive();
        }
        return studentRepository.searchInactive(keyword.trim());
    }

    public boolean hasActiveEnrollments(String studentId) {
        return studentRepository.hasActiveEnrollments(studentId);
    }

    public boolean hasPayments(String studentId) {
        return studentRepository.hasPayments(studentId);
    }

    public boolean exists(String studentId) {
        return studentRepository.existsById(studentId);
    }

    public void delete(String studentId) {
        if (hasPayments(studentId)) {
            throw new RuntimeException("Cannot delete student — payment records exist. Deactivate instead.");
        }
        studentRepository.deleteById(studentId);
    }
}
