package com.hhplus.lecture.business.service;

import com.hhplus.lecture.business.entity.Lecture;
import com.hhplus.lecture.business.entity.LectureHistory;
import com.hhplus.lecture.business.entity.User;
import com.hhplus.lecture.business.repository.LectureHistoryRepository;
import com.hhplus.lecture.business.repository.LectureRepository;
import com.hhplus.lecture.business.repository.UserRepository;
import com.hhplus.lecture.common.exception.*;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class LectureApplyServiceImpl implements LectureApplyService {
    private final UserRepository userRepository;
    private final LectureRepository lectureRepository;
    private final LectureHistoryRepository lectureHistoryRepository;

    @Autowired
    public LectureApplyServiceImpl(UserRepository userRepository, LectureRepository lectureRepository, LectureHistoryRepository lectureHistoryRepository) {
        this.userRepository = userRepository;
        this.lectureRepository = lectureRepository;
        this.lectureHistoryRepository = lectureHistoryRepository;
    }

    /**
     * 강의 신청
     * @param userId 사용자 ID
     * @param lectureId 강의 ID
     * @throws UserNotFoundException 사용자를 찾을 수 없을 때
     * @throws LectureNotFoundException 강의를 찾을 수 없을 때
     * @throws IllegalStateException 강의 신청 가능 날짜가 아닐 때, 정원 초과 시, 이미 신청한 강의일 때
     */
    @Override
    @Transactional
    public LectureHistory applyLecture(Long userId, Long lectureId) {
        User user = userRepository.getUserById(userId);
        if (user == null) {
            throw new UserNotFoundException("사용자를 찾을 수 없습니다.");
        }

        Lecture lecture = lectureRepository.getLectureById(lectureId);
        if (lecture == null) {
            throw new LectureNotFoundException("강의를 찾을 수 없습니다.");
        }

        if(lectureHistoryRepository.isAppliedLecture(user, lecture)){
            throw new AlreadyAppliedException("이미 신청한 강의입니다.");
        }

        // 강의 신청 가능 날짜 확인
        //lecture.getOpenDate()보다 이전이라면 에러 발생
        if(!lecture.isAfterOpenDate(LocalDateTime.now())) {
            throw new LectureNotOpenException("강의 신청 가능한 날짜가 아닙니다.");
        }

        long appliedLectureCount = lectureHistoryRepository.getAppliedLectureCount(lecture);
        if(lecture.isFull(appliedLectureCount)) {
            throw new LectureCapacityExceededException("정원이 초과되었습니다.");
        }



        LectureHistory lectureHistory = LectureHistory.apply(user, lecture);
        return lectureHistoryRepository.saveLectureHistory(lectureHistory);
    }

    /**
     * 강의 신청 상태 확인
     * @param userId 사용자 ID
     * @param lectureId 강의 ID
     * @return 강의 신청 여부
     * @throws UserNotFoundException 사용자를 찾을 수 없을 때
     * @throws LectureNotFoundException 강의를 찾을 수 없을 때
     */
    @Override
    public boolean checkApplyStatus(Long userId, Long lectureId) {
        User user = userRepository.getUserById(userId);
        if (user == null) {
            throw new UserNotFoundException("사용자를 찾을 수 없습니다.");
        }
        Lecture lecture = lectureRepository.getLectureById(lectureId);
        if (lecture == null) {
            throw new LectureNotFoundException("강의를 찾을 수 없습니다.");
        }

        return lectureHistoryRepository.isAppliedLecture(user, lecture);
    }

    /**
     * 강의 목록 조회
     * @return 강의 목록
     * @throws LectureNotFoundException 강의 목록이 없을 때
     */
    @Override
    public List<Lecture> getLectureList() {
        List<Lecture> lectures = lectureRepository.getLectures();
        if (lectures.isEmpty()) {
            throw new LectureNotFoundException("강의 목록이 없습니다.");
        }
        return lectures;
    }
}
