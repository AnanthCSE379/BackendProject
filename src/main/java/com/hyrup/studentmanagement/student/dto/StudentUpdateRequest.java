package com.hyrup.studentmanagement.student.dto;

import com.hyrup.studentmanagement.student.model.Gender;
import com.hyrup.studentmanagement.student.model.StudentStatus;
import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Digits;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.PastOrPresent;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.time.LocalDate;

public class StudentUpdateRequest {

    @NotBlank(message = "Student code is required")
    @Size(min = 3, max = 40, message = "Student code must be between 3 and 40 characters")
    private String studentCode;

    @NotBlank(message = "First name is required")
    @Size(min = 1, max = 80, message = "First name must be between 1 and 80 characters")
    private String firstName;

    @NotBlank(message = "Last name is required")
    @Size(min = 1, max = 80, message = "Last name must be between 1 and 80 characters")
    private String lastName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email is invalid")
    private String email;

    @Pattern(regexp = "^$|^[+0-9()\\-\\s]{7,30}$", message = "Phone number format is invalid")
    private String phone;

    @Past(message = "Date of birth must be in the past")
    private LocalDate dateOfBirth;

    private Gender gender;

    @Size(max = 255, message = "Address line 1 cannot exceed 255 characters")
    private String addressLine1;

    @Size(max = 255, message = "Address line 2 cannot exceed 255 characters")
    private String addressLine2;

    @Size(max = 100, message = "City cannot exceed 100 characters")
    private String city;

    @Size(max = 100, message = "State cannot exceed 100 characters")
    private String state;

    @Size(max = 20, message = "Postal code cannot exceed 20 characters")
    private String postalCode;

    @Size(max = 100, message = "Country cannot exceed 100 characters")
    private String country;

    @Size(max = 120, message = "Emergency contact name cannot exceed 120 characters")
    private String emergencyContactName;

    @Pattern(regexp = "^$|^[+0-9()\\-\\s]{7,30}$", message = "Emergency contact phone format is invalid")
    private String emergencyContactPhone;

    @Size(max = 80, message = "Emergency contact relation cannot exceed 80 characters")
    private String emergencyContactRelation;

    @NotBlank(message = "Course name is required")
    @Size(max = 120, message = "Course name cannot exceed 120 characters")
    private String courseName;

    @Size(max = 120, message = "Major cannot exceed 120 characters")
    private String major;

    @NotNull(message = "Academic year is required")
    @Min(value = 1, message = "Academic year must be at least 1")
    @Max(value = 10, message = "Academic year cannot exceed 10")
    private Integer academicYear;

    @NotNull(message = "Enrollment date is required")
    @PastOrPresent(message = "Enrollment date must be in the past or present")
    private LocalDate enrollmentDate;

    private LocalDate expectedGraduationDate;

    @DecimalMin(value = "0.00", message = "GPA cannot be lower than 0.00")
    @DecimalMax(value = "4.00", message = "GPA cannot exceed 4.00")
    @Digits(integer = 1, fraction = 2, message = "GPA must have up to 2 decimal places")
    private BigDecimal gpa;

    @Min(value = 0, message = "Credits completed cannot be negative")
    private Integer creditsCompleted;

    @NotNull(message = "Student status is required")
    private StudentStatus status;

    @Size(max = 1500, message = "Notes cannot exceed 1500 characters")
    private String notes;

    public String getStudentCode() {
        return studentCode;
    }

    public void setStudentCode(String studentCode) {
        this.studentCode = studentCode;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public LocalDate getDateOfBirth() {
        return dateOfBirth;
    }

    public void setDateOfBirth(LocalDate dateOfBirth) {
        this.dateOfBirth = dateOfBirth;
    }

    public Gender getGender() {
        return gender;
    }

    public void setGender(Gender gender) {
        this.gender = gender;
    }

    public String getAddressLine1() {
        return addressLine1;
    }

    public void setAddressLine1(String addressLine1) {
        this.addressLine1 = addressLine1;
    }

    public String getAddressLine2() {
        return addressLine2;
    }

    public void setAddressLine2(String addressLine2) {
        this.addressLine2 = addressLine2;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getPostalCode() {
        return postalCode;
    }

    public void setPostalCode(String postalCode) {
        this.postalCode = postalCode;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getEmergencyContactName() {
        return emergencyContactName;
    }

    public void setEmergencyContactName(String emergencyContactName) {
        this.emergencyContactName = emergencyContactName;
    }

    public String getEmergencyContactPhone() {
        return emergencyContactPhone;
    }

    public void setEmergencyContactPhone(String emergencyContactPhone) {
        this.emergencyContactPhone = emergencyContactPhone;
    }

    public String getEmergencyContactRelation() {
        return emergencyContactRelation;
    }

    public void setEmergencyContactRelation(String emergencyContactRelation) {
        this.emergencyContactRelation = emergencyContactRelation;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getMajor() {
        return major;
    }

    public void setMajor(String major) {
        this.major = major;
    }

    public Integer getAcademicYear() {
        return academicYear;
    }

    public void setAcademicYear(Integer academicYear) {
        this.academicYear = academicYear;
    }

    public LocalDate getEnrollmentDate() {
        return enrollmentDate;
    }

    public void setEnrollmentDate(LocalDate enrollmentDate) {
        this.enrollmentDate = enrollmentDate;
    }

    public LocalDate getExpectedGraduationDate() {
        return expectedGraduationDate;
    }

    public void setExpectedGraduationDate(LocalDate expectedGraduationDate) {
        this.expectedGraduationDate = expectedGraduationDate;
    }

    public BigDecimal getGpa() {
        return gpa;
    }

    public void setGpa(BigDecimal gpa) {
        this.gpa = gpa;
    }

    public Integer getCreditsCompleted() {
        return creditsCompleted;
    }

    public void setCreditsCompleted(Integer creditsCompleted) {
        this.creditsCompleted = creditsCompleted;
    }

    public StudentStatus getStatus() {
        return status;
    }

    public void setStatus(StudentStatus status) {
        this.status = status;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }
}
