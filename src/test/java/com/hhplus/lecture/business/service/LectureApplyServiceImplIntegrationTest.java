package com.hhplus.lecture.business.service;

import com.hhplus.lecture.business.entity.Lecture;
import com.hhplus.lecture.business.entity.LectureHistory;
import com.hhplus.lecture.business.entity.User;
import com.hhplus.lecture.business.repository.LectureHistoryRepository;
import com.hhplus.lecture.business.repository.LectureRepository;
import com.hhplus.lecture.business.repository.UserRepository;
import com.hhplus.lecture.common.exception.LectureNotFoundException;
import com.hhplus.lecture.common.exception.UserNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
public class LectureApplyServiceImplIntegrationTest {
    @Autowired
    private LectureApplyService lectureApplyService;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LectureHistoryRepository lectureHistoryRepository;

    @Test
    @Transactional
    @DisplayName("강의 신청 통합 테스트 - 유저 정보 없음")
    public void testApplyLecture_UserNotFound() {
        // Given
        Long userId = 999L; // 존재하지 않는 사용자 ID
        Lecture lecture = new Lecture("Test Lecture", LocalDateTime.parse("2024-04-10T13:00:00"), 30);
        lectureRepository.saveLecture(lecture);
        Long lectureId = lecture.getId();

        // When & Then
        assertThrows(UserNotFoundException.class, () -> {
            lectureApplyService.applyLecture(userId, lectureId);
        });

    }

    @Test
    @Transactional
    @DisplayName("강의 신청 통합 테스트 - 강의 정보 없음")
    public void testApplyLecture_LectureNotFound() {
        // Given
        User user = new User("Test User"); // "Test User
        userRepository.saveUser(user);
        Long userId = user.getId();
        Long lectureId = 999L; // 존재하지 않는 강의 ID

        // When & Then
        assertThrows(LectureNotFoundException.class, () -> {
            lectureApplyService.applyLecture(userId, lectureId);
        });

    }

    @Test
    @Transactional
    @DisplayName("강의 신청 통합 테스트 - 특강 정원이 초과되었을 때")
    public void testApplyLecture_LectureIsFull() {
        // Given
        User user = new User("Test User");
        userRepository.saveUser(user);
        Long userId = user.getId();

        Lecture lecture = new Lecture("Test Lecture", LocalDateTime.parse("2024-04-10T13:00:00"), 30);
        lectureRepository.saveLecture(lecture);
        Long lectureId = lecture.getId();

        // 강의 정원을 채우기 위해 30명의 사용자를 신청 처리
        for (int i = 0; i < 30; i++) {
            User tempUser = new User("Temp User " + i);
            userRepository.saveUser(tempUser);

            LectureHistory lectureHistory = new LectureHistory(tempUser, lecture, LocalDateTime.parse("2024-04-10T13:00:00"), true);

            lectureHistoryRepository.saveLectureHistory(lectureHistory);
        }

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            lectureApplyService.applyLecture(userId, lectureId);
        });

    }

    @Test
    @Transactional
    @DisplayName("강의 신청 통합 테스트 - 강의 신청 가능 날짜가 아닐 때")
    public void testApplyLecture_NotOpenDate() {
        // Given
        User user = new User("Test User");
        userRepository.saveUser(user);
        Long userId = user.getId();

        Lecture lecture = new Lecture("Test Lecture", LocalDateTime.now().plusDays(1), 30);
        lectureRepository.saveLecture(lecture);
        Long lectureId = lecture.getId();

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            lectureApplyService.applyLecture(userId, lectureId);
        }, "강의 신청 가능한 날짜가 아닙니다.");
    }

    @Test
    @Transactional
    @DisplayName("강의 신청 통합 테스트 - 동일 사용자가 중복 신청할 때")
    public void testApplyLecture_UserAlreadyApplied() {
        // Given
        User user = new User("Test User");
        userRepository.saveUser(user);
        Long userId = user.getId();

        Lecture lecture = new Lecture("Test Lecture", LocalDateTime.parse("2024-04-10T13:00:00"), 30);
        lectureRepository.saveLecture(lecture);
        Long lectureId = lecture.getId();

        LectureHistory lectureHistory = new LectureHistory(user, lecture, LocalDateTime.parse("2024-04-10T13:00:00"), true);
        lectureHistoryRepository.saveLectureHistory(lectureHistory);

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            lectureApplyService.applyLecture(userId, lectureId);
        }, "이미 수강중인 강의입니다.");

    }

    @Test
    @Transactional
    @DisplayName("강의 신청 통합 테스트 - 정상적인 강의 신청")
    public void testApplyLecture_Success() {
        // Given
        User user = new User("Test User");
        userRepository.saveUser(user);
        Long userId = user.getId();

        Lecture lecture = new Lecture("Test Lecture", LocalDateTime.parse("2024-04-10T13:00:00"), 30);
        lectureRepository.saveLecture(lecture);
        Long lectureId = lecture.getId();

        // When
        LectureHistory foundLectureHistory = lectureApplyService.applyLecture(userId, lectureId);

        // Then

        assertNotNull(foundLectureHistory);
        assertEquals(userId, foundLectureHistory.getUser().getId());
        assertEquals(lectureId, foundLectureHistory.getLecture().getId());
        assertTrue(foundLectureHistory.getIsApplied());
    }

    @Test
    @Transactional
    @DisplayName("강의 신청 여부 확인 통합 테스트 - 유저 정보 없음")
    public void testCheckApplicationStatus_UserNotFound() {
        // Given
        Long userId = 999L; // 존재하지 않는 사용자 ID
        Lecture lecture = new Lecture("Test Lecture", LocalDateTime.parse("2024-04-10T13:00:00"), 30);
        lectureRepository.saveLecture(lecture);
        Long lectureId = lecture.getId();

        // When & Then
        assertThrows(UserNotFoundException.class, () -> {
            lectureApplyService.checkApplyStatus(userId, lectureId);
        });

    }

    @Test
    @Transactional
    @DisplayName("강의 신청 여부 확인 통합 테스트 - 강의 정보 없음")
    public void testCheckApplicationStatus_LectureNotFound() {
        // Given
        User user = new User("Test User");
        userRepository.saveUser(user);
        Long userId = user.getId();
        Long lectureId = 999L; // 존재하지 않는 강의 ID

        // When & Then
        assertThrows(LectureNotFoundException.class, () -> {
            lectureApplyService.checkApplyStatus(userId, lectureId);
        });

    }

    @Test
    @Transactional
    @DisplayName("강의 신청 여부 확인 통합 테스트 - 정상적인 강의 신청 여부 확인")
    public void testCheckApplicationStatus_Success() {
        // Given
        User user = new User("Test User");
        userRepository.saveUser(user);
        Long userId = user.getId();

        Lecture lecture = new Lecture("Test Lecture", LocalDateTime.parse("2024-04-10T13:00:00"), 30);
        lectureRepository.saveLecture(lecture);
        Long lectureId = lecture.getId();

        LectureHistory lectureHistory = new LectureHistory(user, lecture, LocalDateTime.parse("2024-04-10T13:00:00"), true);
        lectureHistoryRepository.saveLectureHistory(lectureHistory);

        // When
        boolean result = lectureApplyService.checkApplyStatus(userId, lectureId);

        // Then
        assertTrue(result);
    }

    @Test
    @Transactional
    @DisplayName("강의 목록 조회 통합 테스트")
    public void testGetLectureList() {
        // Given
        Lecture lecture1 = new Lecture("Lecture 1", LocalDateTime.parse("2024-04-10T13:00:00"), 30);
        lectureRepository.saveLecture(lecture1);

        Lecture lecture2 = new Lecture("Lecture 2", LocalDateTime.parse("2024-04-10T13:00:00"), 30);
        lectureRepository.saveLecture(lecture2);

        // When
        List<Lecture> lectureList = lectureApplyService.getLectureList();

        // Then
        assertEquals(2, lectureList.size());
    }
}
