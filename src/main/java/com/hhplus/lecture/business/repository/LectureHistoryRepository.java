package com.hhplus.lecture.business.repository;

import com.hhplus.lecture.business.entity.Lecture;
import com.hhplus.lecture.business.entity.LectureHistory;
import com.hhplus.lecture.business.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface LectureHistoryRepository extends JpaRepository<LectureHistory, Long> {
    Long countByLectureAndIsAppliedTrue(Lecture lecture);
    Optional<LectureHistory> findByUserAndLectureAndIsAppliedTrue(User user, Lecture lecture);
}
