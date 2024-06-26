package com.hhplus.lecture.business.repository;

import com.hhplus.lecture.business.entity.Lecture;
import org.springframework.stereotype.Repository;

import java.util.List;

public interface LectureRepository {
    Lecture getLectureById(Long lectureId);

    List<Lecture> getLectures();

    Lecture saveLecture(Lecture lecture);
}
