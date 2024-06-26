package com.hhplus.lecture.business.repository;

import com.hhplus.lecture.business.entity.Lecture;
import com.hhplus.lecture.business.entity.LectureHistory;
import com.hhplus.lecture.business.entity.User;
import org.springframework.stereotype.Repository;

public interface LectureHistoryRepository {
    Long getAppliedLectureCount(Lecture lecture);

    boolean isAppliedLecture(User user, Lecture lecture);

    LectureHistory saveLectureHistory(LectureHistory lectureHistory);

    LectureHistory getLectureHistory(User user, Lecture lecture);
}
