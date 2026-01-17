package com.example.online_learning.repository;

import com.example.online_learning.entity.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {
    List<Feedback> findBySubmission_SubmissionId(Long submissionId);
}
