package com.hhplus.lecture.business.service;

import com.hhplus.lecture.business.entity.Lecture;
import com.hhplus.lecture.business.entity.LectureHistory;
import com.hhplus.lecture.business.entity.User;
import com.hhplus.lecture.business.repository.LectureHistoryRepository;
import com.hhplus.lecture.business.repository.LectureRepository;
import com.hhplus.lecture.business.repository.UserRepository;
import jakarta.transaction.Transactional;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

@Service
public class LectureApplyServiceImpl implements LectureApplyService {

    private final UserRepository userRepository;

    private final LectureRepository lectureRepository;

    private final LectureHistoryRepository lectureHistoryRepository;

    public LectureApplyServiceImpl(UserRepository userRepository, LectureRepository lectureRepository, LectureHistoryRepository lectureHistoryRepository) {
        this.userRepository = userRepository;
        this.lectureRepository = lectureRepository;
        this.lectureHistoryRepository = lectureHistoryRepository;
    }

    /**
     * 강의 신청
     * @param userId 사용자 ID
     * @param lectureId 강의 ID
     * @throws NoSuchElementException 사용자 또는 강의를 찾을 수 없을 때
     * @throws IllegalStateException 강의 신청 가능 날짜가 아닐 때, 정원 초과 시, 이미 신청한 강의일 때
     */
    @Transactional
    public void applyLecture(Long userId, Long lectureId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));

        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(()-> new NoSuchElementException("강의를 찾을 수 없습니다."));

        // 강의 신청 가능 날짜 확인
        //lecture.getOpenDate()보다 이전이라면 에러 발생
        if(!lecture.isAfterOpenDate(LocalDateTime.now())) {
            throw new IllegalStateException("강의 신청 가능한 날짜가 아닙니다.");
        }

        long count = lectureHistoryRepository.countByLectureAndIsAppliedTrue(lecture);
        if(lecture.isFull(count)) {
            throw new IllegalStateException("정원이 초과되었습니다.");
        }

        if(lectureHistoryRepository.findByUserAndLectureAndIsAppliedTrue(user, lecture).isPresent()){
            throw new IllegalStateException("이미 신청한 강의입니다.");
        }

        LectureHistory lectureHistory = LectureHistory.apply(user, lecture);
        lectureHistoryRepository.save(lectureHistory);
    }

    /**
     * 강의 신청 상태 확인
     * @param userId 사용자 ID
     * @param lectureId 강의 ID
     * @return 강의 신청 여부
     * @throws NoSuchElementException 사용자 또는 강의를 찾을 수 없을 때
     */
    public boolean checkApplyStatus(Long userId, Long lectureId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new NoSuchElementException("사용자를 찾을 수 없습니다."));
        Lecture lecture = lectureRepository.findById(lectureId)
                .orElseThrow(()-> new NoSuchElementException("강의를 찾을 수 없습니다."));

        return lectureHistoryRepository.findByUserAndLectureAndIsAppliedTrue(user,lecture).isPresent();
    }

    /**
     * 강의 목록 조회
     * @return 강의 목록
     * @throws NoSuchElementException 강의 목록이 없을 때
     */
    public List<Lecture> getLectureList() {
        List<Lecture> lectures = lectureRepository.findAll();
        if (lectures.isEmpty()) {
            throw new NoSuchElementException("강의 목록이 없습니다.");
        }
        return lectures;
    }
}
