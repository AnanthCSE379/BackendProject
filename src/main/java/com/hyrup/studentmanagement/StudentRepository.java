package com.hyrup.studentmanagement;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, Long> {

    boolean existsByStudentId(String studentId);

    boolean existsByEmail(String email);

    boolean existsByStudentIdAndIdNot(String studentId, Long id);

    boolean existsByEmailAndIdNot(String email, Long id);
}
