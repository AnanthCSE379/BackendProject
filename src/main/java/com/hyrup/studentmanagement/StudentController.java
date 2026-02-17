package com.hyrup.studentmanagement;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;

@RestController
@RequestMapping("/api/students")
public class StudentController {

    private final StudentRepository studentRepository;

    public StudentController(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @GetMapping
    public List<StudentResponse> getAll() {
        return studentRepository.findAll().stream().map(this::toResponse).toList();
    }

    @GetMapping("/{id}")
    public StudentResponse getById(@PathVariable Long id) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));
        return toResponse(student);
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public StudentResponse create(@Valid @RequestBody StudentRequest request) {
        if (studentRepository.existsByStudentId(request.studentId())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "studentId already exists");
        }
        if (studentRepository.existsByEmail(request.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "student email already exists");
        }

        Student student = new Student();
        applyRequest(request, student);
        return toResponse(studentRepository.save(student));
    }

    @PutMapping("/{id}")
    public StudentResponse update(@PathVariable Long id, @Valid @RequestBody StudentRequest request) {
        Student student = studentRepository.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found"));

        if (studentRepository.existsByStudentIdAndIdNot(request.studentId(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "studentId already exists");
        }
        if (studentRepository.existsByEmailAndIdNot(request.email(), id)) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "student email already exists");
        }

        applyRequest(request, student);
        return toResponse(studentRepository.save(student));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        if (!studentRepository.existsById(id)) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Student not found");
        }
        studentRepository.deleteById(id);
    }

    private void applyRequest(StudentRequest request, Student student) {
        student.setStudentId(request.studentId());
        student.setFirstName(request.firstName());
        student.setLastName(request.lastName());
        student.setEmail(request.email().trim().toLowerCase());
        student.setCourse(request.course());
        student.setAcademicYear(request.academicYear());
        student.setEnrollmentDate(request.enrollmentDate());
        student.setGpa(request.gpa());
        student.setPhone(request.phone());
        student.setAddress(request.address());
        student.setEmergencyContactName(request.emergencyContactName());
        student.setEmergencyContactPhone(request.emergencyContactPhone());
        student.setStatus(request.status());
    }

    private StudentResponse toResponse(Student student) {
        return new StudentResponse(
            student.getId(),
            student.getStudentId(),
            student.getFirstName(),
            student.getLastName(),
            student.getEmail(),
            student.getCourse(),
            student.getAcademicYear(),
            student.getEnrollmentDate(),
            student.getGpa(),
            student.getPhone(),
            student.getAddress(),
            student.getEmergencyContactName(),
            student.getEmergencyContactPhone(),
            student.getStatus()
        );
    }

    public record StudentRequest(
        @NotBlank String studentId,
        @NotBlank String firstName,
        @NotBlank String lastName,
        @NotBlank @Email String email,
        @NotBlank String course,
        @NotNull @Min(1) @Max(8) Integer academicYear,
        @NotNull @PastOrPresent LocalDate enrollmentDate,
        @NotNull @DecimalMin("0.0") @DecimalMax("4.0") BigDecimal gpa,
        @NotBlank String phone,
        @NotBlank String address,
        @NotBlank String emergencyContactName,
        @NotBlank String emergencyContactPhone,
        @NotBlank String status
    ) {
    }

    public record StudentResponse(
        Long id,
        String studentId,
        String firstName,
        String lastName,
        String email,
        String course,
        Integer academicYear,
        LocalDate enrollmentDate,
        BigDecimal gpa,
        String phone,
        String address,
        String emergencyContactName,
        String emergencyContactPhone,
        String status
    ) {
    }
}
