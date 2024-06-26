package com.hhplus.lecture.infra;

import com.hhplus.lecture.business.entity.Lecture;
import com.hhplus.lecture.business.entity.LectureHistory;
import com.hhplus.lecture.business.entity.User;
import com.hhplus.lecture.business.repository.LectureHistoryRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaLectureHistoryRepository extends JpaRepository<LectureHistory, Long>, LectureHistoryRepository {
    @Override
    default Long getAppliedLectureCount(Lecture lecture){
        return countByLectureAndIsAppliedTrue(lecture);
    }

    @Override
    default boolean isAppliedLecture(User user, Lecture lecture){
        return existsByUserAndLectureAndIsAppliedTrue(user, lecture);
    }

    @Override
    default LectureHistory saveLectureHistory(LectureHistory lectureHistory){
        return save(lectureHistory);
    }

    @Override
    default LectureHistory getLectureHistory(User user, Lecture lecture){
        return findByUserAndLecture(user, lecture);
    }

    Long countByLectureAndIsAppliedTrue(Lecture lecture);
    Boolean existsByUserAndLectureAndIsAppliedTrue(User user, Lecture lecture);
    LectureHistory findByUserAndLecture(User user, Lecture lecture);


}
