package com.hhplus.lecture.business.service;

import com.hhplus.lecture.business.entity.Lecture;
import com.hhplus.lecture.business.entity.LectureHistory;
import com.hhplus.lecture.business.entity.User;
import com.hhplus.lecture.business.repository.LectureHistoryRepository;
import com.hhplus.lecture.business.repository.LectureRepository;
import com.hhplus.lecture.business.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
public class LectureServiceIntegrationTest {
    @Autowired
    private LectureService lectureService;

    @Autowired
    private LectureRepository lectureRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private LectureHistoryRepository lectureHistoryRepository;

    @Test
    @DisplayName("강의 신청 통합 테스트 - 유저 정보 없음")
    public void testApplyLecture_UserNotFound() {
        // Given
        Long userId = 999L; // 존재하지 않는 사용자 ID
        Lecture lecture = new Lecture();
        lecture.setTitle("Test Lecture");
        lecture.setOpenDate(LocalDateTime.parse("2024-04-10T11:00:00"));
        lecture.setMaxAttendees(30);
        lectureRepository.save(lecture);
        Long lectureId = lecture.getId();

        // When & Then
        assertThrows(NoSuchElementException.class, () -> {
            lectureService.applyLecture(userId, lectureId);
        });

        lectureRepository.deleteAll();
    }

    @Test
    @DisplayName("강의 신청 통합 테스트 - 강의 정보 없음")
    public void testApplyLecture_LectureNotFound() {
        // Given
        User user = new User();
        user.setName("Test User");
        userRepository.save(user);
        Long userId = user.getId();
        Long lectureId = 999L; // 존재하지 않는 강의 ID

        // When & Then
        assertThrows(NoSuchElementException.class, () -> {
            lectureService.applyLecture(userId, lectureId);
        });

        userRepository.deleteAll();
    }

    @Test
    @DisplayName("강의 신청 통합 테스트 - 특강 정원이 초과되었을 때")
    public void testApplyLecture_LectureIsFull() {
        // Given
        User user = new User();
        user.setName("Test User");
        userRepository.save(user);
        Long userId = user.getId();

        Lecture lecture = new Lecture();
        lecture.setTitle("Test Lecture");
        lecture.setOpenDate(LocalDateTime.parse("2024-04-10T11:00:00"));
        lecture.setMaxAttendees(30);
        lectureRepository.save(lecture);
        Long lectureId = lecture.getId();

        // 강의 정원을 채우기 위해 30명의 사용자를 신청 처리
        for (int i = 0; i < 30; i++) {
            User tempUser = new User();
            tempUser.setName("Temp User " + i);
            userRepository.save(tempUser);

            LectureHistory lectureHistory = new LectureHistory();
            lectureHistory.setUser(tempUser);
            lectureHistory.setLecture(lecture);
            lectureHistory.setApplyDate(LocalDateTime.parse("2024-04-10T11:00:00"));
            lectureHistory.setIsApplied(true);
            lectureHistoryRepository.save(lectureHistory);
        }

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            lectureService.applyLecture(userId, lectureId);
        });

        lectureHistoryRepository.deleteAll();
        lectureRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("강의 신청 통합 테스트 - 강의 신청 가능 날짜가 아닐 때")
    public void testApplyLecture_NotOpenDate() {
        // Given
        User user = new User();
        user.setName("Test User");
        userRepository.save(user);
        Long userId = user.getId();

        Lecture lecture = new Lecture();
        lecture.setTitle("Test Lecture");
        lecture.setOpenDate(LocalDateTime.parse("2024-04-10T13:00:00"));
        lecture.setMaxAttendees(30);
        lectureRepository.save(lecture);
        Long lectureId = lecture.getId();

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            lectureService.applyLecture(userId, lectureId);
        }, "강의 신청 가능한 날짜가 아닙니다.");

        lectureRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("강의 신청 통합 테스트 - 동일 사용자가 중복 신청할 때")
    public void testApplyLecture_UserAlreadyApplied() {
        // Given
        User user = new User();
        user.setName("Test User");
        userRepository.save(user);
        Long userId = user.getId();

        Lecture lecture = new Lecture();
        lecture.setTitle("Test Lecture");
        lecture.setOpenDate(LocalDateTime.parse("2024-04-10T11:00:00"));
        lecture.setMaxAttendees(30);
        lectureRepository.save(lecture);
        Long lectureId = lecture.getId();

        LectureHistory lectureHistory = new LectureHistory();
        lectureHistory.setUser(user);
        lectureHistory.setLecture(lecture);
        lectureHistory.setApplyDate(LocalDateTime.parse("2024-04-10T11:00:00"));
        lectureHistory.setIsApplied(true);
        lectureHistoryRepository.save(lectureHistory);

        // When & Then
        assertThrows(IllegalStateException.class, () -> {
            lectureService.applyLecture(userId, lectureId);
        }, "이미 수강중인 강의입니다.");

        lectureHistoryRepository.deleteAll();
        lectureRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("강의 신청 통합 테스트 - 정상적인 강의 신청")
    public void testApplyLecture_Success() {
        // Given
        User user = new User();
        user.setName("Test User");
        userRepository.save(user);
        Long userId = user.getId();

        Lecture lecture = new Lecture();
        lecture.setTitle("Test Lecture");
        lecture.setOpenDate(LocalDateTime.parse("2024-04-10T11:00:00"));
        lecture.setMaxAttendees(30);
        lectureRepository.save(lecture);
        Long lectureId = lecture.getId();

        // When
        lectureService.applyLecture(userId, lectureId);

        // Then
        LectureHistory foundLectureHistory = lectureHistoryRepository.findByUserAndLectureAndIsAppliedTrue(user, lecture).orElse(null);
        assertNotNull(foundLectureHistory);
        assertEquals(user, foundLectureHistory.getUser());
        assertEquals(lecture, foundLectureHistory.getLecture());

        lectureHistoryRepository.deleteAll();
        lectureRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("강의 신청 여부 확인 통합 테스트 - 유저 정보 없음")
    public void testCheckApplicationStatus_UserNotFound() {
        // Given
        Long userId = 999L; // 존재하지 않는 사용자 ID
        Lecture lecture = new Lecture();
        lecture.setTitle("Test Lecture");
        lecture.setOpenDate(LocalDateTime.parse("2024-04-10T11:00:00"));
        lecture.setMaxAttendees(30);
        lectureRepository.save(lecture);
        Long lectureId = lecture.getId();

        // When & Then
        assertThrows(NoSuchElementException.class, () -> {
            lectureService.checkApplyStatus(userId, lectureId);
        });

        lectureRepository.deleteAll();
    }

    @Test
    @DisplayName("강의 신청 여부 확인 통합 테스트 - 강의 정보 없음")
    public void testCheckApplicationStatus_LectureNotFound() {
        // Given
        User user = new User();
        user.setName("Test User");
        userRepository.save(user);
        Long userId = user.getId();
        Long lectureId = 999L; // 존재하지 않는 강의 ID

        // When & Then
        assertThrows(NoSuchElementException.class, () -> {
            lectureService.checkApplyStatus(userId, lectureId);
        });

        userRepository.deleteAll();
    }

    @Test
    @DisplayName("강의 신청 여부 확인 통합 테스트 - 정상적인 강의 신청 여부 확인")
    public void testCheckApplicationStatus_Success() {
        // Given
        User user = new User();
        user.setName("Test User");
        userRepository.save(user);
        Long userId = user.getId();

        Lecture lecture = new Lecture();
        lecture.setTitle("Test Lecture");
        lecture.setOpenDate(LocalDateTime.parse("2024-04-10T13:00:00"));
        lecture.setMaxAttendees(30);
        lectureRepository.save(lecture);
        Long lectureId = lecture.getId();

        LectureHistory lectureHistory = new LectureHistory();
        lectureHistory.setUser(user);
        lectureHistory.setLecture(lecture);
        lectureHistory.setApplyDate(LocalDateTime.parse("2024-04-10T13:00:00"));
        lectureHistory.setIsApplied(true);
        lectureHistoryRepository.save(lectureHistory);

        // When
        boolean result = lectureService.checkApplyStatus(userId, lectureId);

        // Then
        assertTrue(result);

        lectureHistoryRepository.deleteAll();
        lectureRepository.deleteAll();
        userRepository.deleteAll();
    }

    @Test
    @DisplayName("강의 목록 조회 통합 테스트")
    public void testGetLectureList() {
        // Given
        Lecture lecture1 = new Lecture();
        lecture1.setTitle("Lecture 1");
        lecture1.setOpenDate(LocalDateTime.parse("2024-04-10T11:00:00"));
        lecture1.setMaxAttendees(30);
        lectureRepository.save(lecture1);

        Lecture lecture2 = new Lecture();
        lecture2.setTitle("Lecture 2");
        lecture2.setOpenDate(LocalDateTime.parse("2024-04-10T13:00:00"));
        lecture2.setMaxAttendees(30);
        lectureRepository.save(lecture2);

        // When
        List<Lecture> lectureList = lectureService.getLectureList();

        // Then
        assertEquals(2, lectureList.size());

        lectureRepository.deleteAll();
    }
}