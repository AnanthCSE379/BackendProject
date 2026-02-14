package com.hyrup.studentmanagement.student.service;

import com.hyrup.studentmanagement.common.dto.PagedResponse;
import com.hyrup.studentmanagement.common.exception.BadRequestException;
import com.hyrup.studentmanagement.common.exception.ConflictException;
import com.hyrup.studentmanagement.common.exception.ResourceNotFoundException;
import com.hyrup.studentmanagement.student.dto.StudentCreateRequest;
import com.hyrup.studentmanagement.student.dto.StudentResponse;
import com.hyrup.studentmanagement.student.dto.StudentUpdateRequest;
import com.hyrup.studentmanagement.student.model.Student;
import com.hyrup.studentmanagement.student.model.StudentStatus;
import com.hyrup.studentmanagement.student.repository.StudentRepository;
import com.hyrup.studentmanagement.student.specification.StudentSpecification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
public class StudentService {

    private static final Set<String> ALLOWED_SORT_COLUMNS = Set.of(
            "id",
            "studentCode",
            "firstName",
            "lastName",
            "email",
            "courseName",
            "academicYear",
            "enrollmentDate",
            "gpa",
            "status",
            "createdAt",
            "updatedAt"
    );

    private final StudentRepository studentRepository;

    public StudentService(StudentRepository studentRepository) {
        this.studentRepository = studentRepository;
    }

    @Transactional
    public StudentResponse create(StudentCreateRequest request) {
        String normalizedEmail = normalizeEmail(request.getEmail());
        String normalizedStudentCode = normalizeCode(request.getStudentCode());

        if (studentRepository.existsByEmail(normalizedEmail)) {
            throw new ConflictException("A student with this email already exists");
        }

        if (studentRepository.existsByStudentCode(normalizedStudentCode)) {
            throw new ConflictException("A student with this student code already exists");
        }

        validateBusinessRules(request.getEnrollmentDate(), request.getExpectedGraduationDate());

        Student student = new Student();
        applyCreateData(student, request, normalizedEmail, normalizedStudentCode);

        Student saved = studentRepository.save(student);
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public StudentResponse getById(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        return toResponse(student);
    }

    @Transactional(readOnly = true)
    public PagedResponse<StudentResponse> list(int page,
                                               int size,
                                               String sortBy,
                                               String sortDirection,
                                               String search,
                                               String courseName,
                                               Integer academicYear,
                                               StudentStatus status) {
        String sortColumn = sanitizeSortBy(sortBy);
        Sort.Direction direction = "asc".equalsIgnoreCase(sortDirection) ? Sort.Direction.ASC : Sort.Direction.DESC;

        Pageable pageable = PageRequest.of(page, size, Sort.by(direction, sortColumn));

        Page<Student> result = studentRepository.findAll(
                StudentSpecification.withFilters(search, courseName, academicYear, status),
                pageable
        );

        List<StudentResponse> content = result.getContent().stream().map(this::toResponse).toList();

        return new PagedResponse<>(
                content,
                result.getNumber(),
                result.getSize(),
                result.getTotalElements(),
                result.getTotalPages(),
                result.isFirst(),
                result.isLast()
        );
    }

    @Transactional
    public StudentResponse update(Long id, StudentUpdateRequest request) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        String normalizedEmail = normalizeEmail(request.getEmail());
        String normalizedStudentCode = normalizeCode(request.getStudentCode());

        if (studentRepository.existsByEmailAndIdNot(normalizedEmail, id)) {
            throw new ConflictException("A student with this email already exists");
        }

        if (studentRepository.existsByStudentCodeAndIdNot(normalizedStudentCode, id)) {
            throw new ConflictException("A student with this student code already exists");
        }

        validateBusinessRules(request.getEnrollmentDate(), request.getExpectedGraduationDate());

        applyUpdateData(student, request, normalizedEmail, normalizedStudentCode);

        Student updated = studentRepository.save(student);
        return toResponse(updated);
    }

    @Transactional
    public void delete(Long id) {
        Student student = studentRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Student not found with id: " + id));

        studentRepository.delete(student);
    }

    private String sanitizeSortBy(String sortBy) {
        if (sortBy == null || sortBy.isBlank()) {
            return "createdAt";
        }
        return ALLOWED_SORT_COLUMNS.contains(sortBy) ? sortBy : "createdAt";
    }

    private String normalizeEmail(String email) {
        return email.trim().toLowerCase();
    }

    private String normalizeCode(String studentCode) {
        return studentCode.trim().toUpperCase();
    }

    private void applyCreateData(Student student,
                                 StudentCreateRequest request,
                                 String normalizedEmail,
                                 String normalizedStudentCode) {
        student.setStudentCode(normalizedStudentCode);
        student.setFirstName(request.getFirstName().trim());
        student.setLastName(request.getLastName().trim());
        student.setEmail(normalizedEmail);
        student.setPhone(trimToNull(request.getPhone()));
        student.setDateOfBirth(request.getDateOfBirth());
        student.setGender(request.getGender());
        student.setAddressLine1(trimToNull(request.getAddressLine1()));
        student.setAddressLine2(trimToNull(request.getAddressLine2()));
        student.setCity(trimToNull(request.getCity()));
        student.setState(trimToNull(request.getState()));
        student.setPostalCode(trimToNull(request.getPostalCode()));
        student.setCountry(trimToNull(request.getCountry()));
        student.setEmergencyContactName(trimToNull(request.getEmergencyContactName()));
        student.setEmergencyContactPhone(trimToNull(request.getEmergencyContactPhone()));
        student.setEmergencyContactRelation(trimToNull(request.getEmergencyContactRelation()));
        student.setCourseName(request.getCourseName().trim());
        student.setMajor(trimToNull(request.getMajor()));
        student.setAcademicYear(request.getAcademicYear().shortValue());
        student.setEnrollmentDate(request.getEnrollmentDate());
        student.setExpectedGraduationDate(request.getExpectedGraduationDate());
        student.setGpa(request.getGpa());
        student.setCreditsCompleted(request.getCreditsCompleted());
        student.setStatus(request.getStatus());
        student.setNotes(trimToNull(request.getNotes()));
    }

    private void applyUpdateData(Student student,
                                 StudentUpdateRequest request,
                                 String normalizedEmail,
                                 String normalizedStudentCode) {
        student.setStudentCode(normalizedStudentCode);
        student.setFirstName(request.getFirstName().trim());
        student.setLastName(request.getLastName().trim());
        student.setEmail(normalizedEmail);
        student.setPhone(trimToNull(request.getPhone()));
        student.setDateOfBirth(request.getDateOfBirth());
        student.setGender(request.getGender());
        student.setAddressLine1(trimToNull(request.getAddressLine1()));
        student.setAddressLine2(trimToNull(request.getAddressLine2()));
        student.setCity(trimToNull(request.getCity()));
        student.setState(trimToNull(request.getState()));
        student.setPostalCode(trimToNull(request.getPostalCode()));
        student.setCountry(trimToNull(request.getCountry()));
        student.setEmergencyContactName(trimToNull(request.getEmergencyContactName()));
        student.setEmergencyContactPhone(trimToNull(request.getEmergencyContactPhone()));
        student.setEmergencyContactRelation(trimToNull(request.getEmergencyContactRelation()));
        student.setCourseName(request.getCourseName().trim());
        student.setMajor(trimToNull(request.getMajor()));
        student.setAcademicYear(request.getAcademicYear().shortValue());
        student.setEnrollmentDate(request.getEnrollmentDate());
        student.setExpectedGraduationDate(request.getExpectedGraduationDate());
        student.setGpa(request.getGpa());
        student.setCreditsCompleted(request.getCreditsCompleted());
        student.setStatus(request.getStatus());
        student.setNotes(trimToNull(request.getNotes()));
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private void validateBusinessRules(LocalDate enrollmentDate, LocalDate expectedGraduationDate) {
        if (expectedGraduationDate != null && expectedGraduationDate.isBefore(enrollmentDate)) {
            throw new BadRequestException("Expected graduation date cannot be earlier than enrollment date");
        }
    }

    private StudentResponse toResponse(Student student) {
        StudentResponse response = new StudentResponse();
        response.setId(student.getId());
        response.setStudentCode(student.getStudentCode());
        response.setFirstName(student.getFirstName());
        response.setLastName(student.getLastName());
        response.setEmail(student.getEmail());
        response.setPhone(student.getPhone());
        response.setDateOfBirth(student.getDateOfBirth());
        response.setGender(student.getGender());
        response.setAddressLine1(student.getAddressLine1());
        response.setAddressLine2(student.getAddressLine2());
        response.setCity(student.getCity());
        response.setState(student.getState());
        response.setPostalCode(student.getPostalCode());
        response.setCountry(student.getCountry());
        response.setEmergencyContactName(student.getEmergencyContactName());
        response.setEmergencyContactPhone(student.getEmergencyContactPhone());
        response.setEmergencyContactRelation(student.getEmergencyContactRelation());
        response.setCourseName(student.getCourseName());
        response.setMajor(student.getMajor());
        response.setAcademicYear(student.getAcademicYear() == null ? null : student.getAcademicYear().intValue());
        response.setEnrollmentDate(student.getEnrollmentDate());
        response.setExpectedGraduationDate(student.getExpectedGraduationDate());
        response.setGpa(student.getGpa());
        response.setCreditsCompleted(student.getCreditsCompleted());
        response.setStatus(student.getStatus());
        response.setNotes(student.getNotes());
        response.setCreatedAt(student.getCreatedAt());
        response.setUpdatedAt(student.getUpdatedAt());
        return response;
    }
}
