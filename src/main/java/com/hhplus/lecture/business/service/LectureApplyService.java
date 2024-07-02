package com.hhplus.lecture.business.service;

import com.hhplus.lecture.business.entity.Lecture;
import com.hhplus.lecture.business.entity.LectureHistory;

import java.util.List;

public interface LectureApplyService {
    LectureHistory applyLecture(Long userId, Long lectureId);
    boolean checkApplyStatus(Long userId, Long lectureId);
    List<Lecture> getLectureList();
}
