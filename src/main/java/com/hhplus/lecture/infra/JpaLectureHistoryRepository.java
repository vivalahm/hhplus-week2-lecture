package com.hhplus.lecture.infra;

import com.hhplus.lecture.business.entity.Lecture;
import com.hhplus.lecture.business.entity.LectureHistory;
import com.hhplus.lecture.business.entity.User;
import com.hhplus.lecture.business.repository.LectureHistoryRepository;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface JpaLectureHistoryRepository extends JpaRepository<LectureHistory, Long>, LectureHistoryRepository {
    @Override
    default Long getAppliedLectureCount(Lecture lecture) {
        return countByLectureAndIsAppliedTrue(lecture);
    }

    @Override
    default boolean isAppliedLecture(User user, Lecture lecture) {
        return existsByUserAndLectureAndIsAppliedTrue(user, lecture);
    }

    @Override
    default LectureHistory saveLectureHistory(LectureHistory lectureHistory) {
        return save(lectureHistory);
    }

    @Override
    default Optional<LectureHistory> getLectureHistoryWithLock(User user, Lecture lecture) {
        return findByUserAndLectureWithLock(user, lecture);
    }

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT lh FROM LectureHistory lh WHERE lh.user = :user AND lh.lecture = :lecture")
    Optional<LectureHistory> findByUserAndLectureWithLock(@Param("user") User user, @Param("lecture") Lecture lecture);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    default LectureHistory saveLectureHistoryWithLock(LectureHistory lectureHistory) {
        return save(lectureHistory);
    }

    Long countByLectureAndIsAppliedTrue(Lecture lecture);
    Boolean existsByUserAndLectureAndIsAppliedTrue(User user, Lecture lecture);
}