package com.hhplus.lecture.business.service;

import com.hhplus.lecture.business.entity.Lecture;

import java.util.List;

public interface LectureApplyService {
    void applyLecture(Long userId, Long lectureId);
    boolean checkApplyStatus(Long userId, Long lectureId);
    List<Lecture> getLectureList();
}
