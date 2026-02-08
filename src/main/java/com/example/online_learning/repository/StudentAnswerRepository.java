package com.example.online_learning.repository;

import com.example.online_learning.entity.StudentAnswer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface StudentAnswerRepository extends JpaRepository<StudentAnswer, Long> {
    List<StudentAnswer> findBySubmission_SubmissionId(Long submissionId);
    
    void deleteBySubmission_SubmissionId(Long submissionId);
}
