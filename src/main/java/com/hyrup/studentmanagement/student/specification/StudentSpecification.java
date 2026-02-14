package com.hyrup.studentmanagement.student.specification;

import com.hyrup.studentmanagement.student.model.Student;
import com.hyrup.studentmanagement.student.model.StudentStatus;
import org.springframework.data.jpa.domain.Specification;

public final class StudentSpecification {

    private StudentSpecification() {
    }

    public static Specification<Student> withFilters(String search,
                                                     String courseName,
                                                     Integer academicYear,
                                                     StudentStatus status) {
        return Specification.where(search(search))
                .and(courseNameEquals(courseName))
                .and(academicYearEquals(academicYear))
                .and(statusEquals(status));
    }

    private static Specification<Student> search(String search) {
        return (root, query, criteriaBuilder) -> {
            if (search == null || search.isBlank()) {
                return criteriaBuilder.conjunction();
            }

            String like = "%" + search.trim().toLowerCase() + "%";

            return criteriaBuilder.or(
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("firstName").as(String.class)), like),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("lastName").as(String.class)), like),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("email").as(String.class)), like),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("studentCode").as(String.class)), like),
                    criteriaBuilder.like(criteriaBuilder.lower(root.get("major").as(String.class)), like)
            );
        };
    }

    private static Specification<Student> courseNameEquals(String courseName) {
        return (root, query, criteriaBuilder) -> {
            if (courseName == null || courseName.isBlank()) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(
                    criteriaBuilder.lower(root.get("courseName").as(String.class)),
                    courseName.trim().toLowerCase()
            );
        };
    }

    private static Specification<Student> academicYearEquals(Integer academicYear) {
        return (root, query, criteriaBuilder) -> {
            if (academicYear == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("academicYear"), academicYear.shortValue());
        };
    }

    private static Specification<Student> statusEquals(StudentStatus status) {
        return (root, query, criteriaBuilder) -> {
            if (status == null) {
                return criteriaBuilder.conjunction();
            }
            return criteriaBuilder.equal(root.get("status"), status);
        };
    }
}
