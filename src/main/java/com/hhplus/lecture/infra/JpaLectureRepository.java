package com.hhplus.lecture.infra;

import com.hhplus.lecture.business.entity.Lecture;
import com.hhplus.lecture.business.repository.LectureRepository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JpaLectureRepository extends JpaRepository<Lecture, Long>, LectureRepository {
    @Override
    default Lecture getLectureById(Long lectureId) {
        return findById(lectureId).orElse(null);
    }

    @Override
    default List<Lecture> getLectures() {
        return findAll();
    }

    @Override
    default Lecture saveLecture(Lecture lecture) {
        return save(lecture);
    }

}
