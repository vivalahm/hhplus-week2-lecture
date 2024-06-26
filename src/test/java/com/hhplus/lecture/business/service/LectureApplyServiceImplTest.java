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
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LectureApplyServiceImplTest {
    @InjectMocks
    private LectureApplyServiceImpl lectureApplyServiceImpl;

    @Mock
    private UserRepository userRepository;

    @Mock
    private LectureRepository lectureRepository;

    @Mock
    private LectureHistoryRepository lectureHistoryRepository;

    @Test
    @DisplayName("강의 신청 테스트 - 유저 정보 없음")
    public void applyLecture_userNotFound() {
        //Given
        Long userId = 1L;
        Long lectureId = 1L;

        when(userRepository.getUserById(userId)).thenReturn(null);

        //When & Then
        assertThrows(UserNotFoundException.class, () -> {
            lectureApplyServiceImpl.applyLecture(userId, lectureId);
        });

        verify(userRepository, times(1)).getUserById(userId);
    }

    @Test
    @DisplayName("강의 신청 테스트 - 강의 정보 없음")
    public void applyLecture_lectureNotFound() {
        //Given
        Long userId = 1L;
        Long lectureId = 1L;

        User user = new User(userId, "홍길동");

        when(userRepository.getUserById(userId)).thenReturn(user);
        when(lectureRepository.getLectureById(lectureId)).thenReturn(null);

        //When & Then
        assertThrows(LectureNotFoundException.class, () -> {
            lectureApplyServiceImpl.applyLecture(userId, lectureId);
        });

        verify(userRepository, times(1)).getUserById(userId);
        verify(lectureRepository, times(1)).getLectureById(lectureId);
    }


    @Test
    @DisplayName("강의 신청 테스트 - 이미 신청한 강의")
    public void applyLecture_alreadyApplied() {
        //Given
        Long userId = 1L;
        Long lectureId = 1L;

        User user = new User(userId, "홍길동");

        Lecture lecture = new Lecture(lectureId, "항해플러스", LocalDateTime.parse("2024-04-10T13:00:00"), 30);

        when(userRepository.getUserById(userId)).thenReturn(user);
        when(lectureRepository.getLectureById(lectureId)).thenReturn(lecture);
        when(lectureHistoryRepository.isAppliedLecture(user, lecture)).thenReturn(true);

        //When & Then
        assertThrows(IllegalStateException.class, () -> {
            lectureApplyServiceImpl.applyLecture(userId, lectureId);
        }, "이미 신청한 강의입니다.");

        verify(userRepository, times(1)).getUserById(userId);
        verify(lectureRepository, times(1)).getLectureById(lectureId);
        verify(lectureHistoryRepository, times(1)).isAppliedLecture(user, lecture);
    }


    @Test
    @DisplayName("강의 신청 테스트 - 강의 신청 일자가 아닐 때")
    public void applyLecture_notApplyDate() {
        //Given
        Long userId = 1L;
        Long lectureId = 1L;

        User user = new User(userId, "홍길동");


        Lecture lecture = new Lecture(lectureId, "항해플러스", LocalDateTime.now().plusDays(1), 30);

        when(userRepository.getUserById(userId)).thenReturn(user);
        when(lectureRepository.getLectureById(lectureId)).thenReturn(lecture);

        //When
        assertThrows(IllegalStateException.class, () -> {
            lectureApplyServiceImpl.applyLecture(userId, lectureId);
        }, "강의 신청 가능한 날짜가 아닙니다.");


        //Then
        verify(userRepository, times(1)).getUserById(userId);
        verify(lectureRepository, times(1)).getLectureById(lectureId);
    }

    @Test
    @DisplayName("강의 신청 테스트 - 수강 인원이 모두 찼을 때")
    public void applyLecture_lectureIsFull() {
        //Given
        Long userId = 1L;
        Long lectureId = 1L;

        User user = new User(userId, "홍길동");

        Lecture lecture = new Lecture(lectureId, "항해플러스", LocalDateTime.parse("2024-04-10T13:00:00"), 30);

        when(userRepository.getUserById(userId)).thenReturn(user);
        when(lectureRepository.getLectureById(lectureId)).thenReturn(lecture);
        when(lectureHistoryRepository.isAppliedLecture(user, lecture)).thenReturn(false);
        when(lectureHistoryRepository.getAppliedLectureCount(lecture)).thenReturn(30L);


        //When & Then
        assertThrows(IllegalStateException.class, () -> {
            lectureApplyServiceImpl.applyLecture(userId, lectureId);
        }, "정원이 초과되었습니다.");

        verify(userRepository, times(1)).getUserById(userId);
        verify(lectureRepository, times(1)).getLectureById(lectureId);
        verify(lectureHistoryRepository, times(1)).isAppliedLecture(user, lecture);
        verify(lectureHistoryRepository, times(1)).getAppliedLectureCount(lecture);
    }

    @Test
    @DisplayName("강의 신청 테스트 - 정상적인 강의 신청")
    public void applyLecture() {
        //Given
        Long userId = 1L;
        Long lectureId = 1L;

        User user = new User(userId, "홍길동");

        Lecture lecture = new Lecture(lectureId, "항해플러스", LocalDateTime.parse("2024-04-10T13:00:00"), 30);

        LectureHistory lectureHistory = new LectureHistory(user, lecture, LocalDateTime.now(),true);

        when(userRepository.getUserById(userId)).thenReturn(user);
        when(lectureRepository.getLectureById(lectureId)).thenReturn(lecture);
        when(lectureHistoryRepository.isAppliedLecture(user, lecture)).thenReturn(false);
        when(lectureHistoryRepository.getAppliedLectureCount(lecture)).thenReturn(29L);
        doReturn(lectureHistory).when(lectureHistoryRepository).saveLectureHistory(any(LectureHistory.class));



        //When & Then
        // LectureHistory가 정상적으로 저장되었는지 확인
        assertEquals(lectureHistory, lectureApplyServiceImpl.applyLecture(userId, lectureId));


        verify(userRepository, times(1)).getUserById(userId);
        verify(lectureRepository, times(1)).getLectureById(lectureId);
        verify(lectureHistoryRepository, times(1)).isAppliedLecture(user, lecture);
        verify(lectureHistoryRepository, times(1)).getAppliedLectureCount(lecture);
        verify(lectureHistoryRepository, times(1)).saveLectureHistory(any(LectureHistory.class));    }

    @Test
    @DisplayName("강의 신청 여부 확인 테스트 - 유저 정보 없음")
    public void checkApplyStatus_userNotFound() {
        //Given
        Long userId = 1L;
        Long lectureId = 1L;

        when(userRepository.getUserById(userId)).thenReturn(null);

        //When & Then
        assertThrows(UserNotFoundException.class, () -> {
            lectureApplyServiceImpl.checkApplyStatus(userId, lectureId);
        });

        verify(userRepository, times(1)).getUserById(userId);
    }

    @Test
    @DisplayName("강의 신청 여부 확인 테스트 - 강의 정보 없음")
    public void checkApplyStatus_lectureNotFound() {
        //Given
        Long userId = 1L;
        Long lectureId = 1L;

        User user = new User(userId, "홍길동");

        when(userRepository.getUserById(userId)).thenReturn(user);
        when(lectureRepository.getLectureById(lectureId)).thenReturn(null);

        //When & Then
        assertThrows(LectureNotFoundException.class, () -> {
            lectureApplyServiceImpl.checkApplyStatus(userId, lectureId);
        });

        verify(userRepository, times(1)).getUserById(userId);
        verify(lectureRepository, times(1)).getLectureById(lectureId);
    }

    @Test
    @DisplayName("강의 신청 여부 확인 테스트 - 정상적인 강의 신청 여부 확인")
    public void checkApplyStatus_lectureIsApplied() {
        //Given
        Long userId = 1L;
        Long lectureId = 1L;

        User user = new User(userId, "홍길동");

        Lecture lecture = new Lecture(lectureId, "항해플러스", LocalDateTime.parse("2024-04-10T13:00:00"), 30);

        when(userRepository.getUserById(userId)).thenReturn(user);
        when(lectureRepository.getLectureById(lectureId)).thenReturn(lecture);
        when(lectureHistoryRepository.isAppliedLecture(user, lecture)).thenReturn(true);

        //When
        boolean isApplied = lectureApplyServiceImpl.checkApplyStatus(userId, lectureId);

        //Then
        assertTrue(isApplied);

        verify(userRepository, times(1)).getUserById(userId);
        verify(lectureRepository, times(1)).getLectureById(lectureId);
        verify(lectureHistoryRepository, times(1)).isAppliedLecture(user, lecture);
    }

    @Test
    @DisplayName("강의 신청 여부 확인 테스트 - 강의 신청하지 않음")
    public void checkApplyStatus_lectureIsNotApplied() {
        //Given
        Long userId = 1L;
        Long lectureId = 1L;

        User user = new User(userId, "홍길동");

        Lecture lecture = new Lecture(lectureId, "항해플러스", LocalDateTime.parse("2024-04-10T13:00:00"), 30);

        when(userRepository.getUserById(userId)).thenReturn(user);
        when(lectureRepository.getLectureById(lectureId)).thenReturn(lecture);
        when(lectureHistoryRepository.isAppliedLecture(user, lecture)).thenReturn(false);

        //When
        boolean isApplied = lectureApplyServiceImpl.checkApplyStatus(userId, lectureId);

        //Then
        assertFalse(isApplied);

        verify(userRepository, times(1)).getUserById(userId);
    }

    @Test
    @DisplayName("강의 목록 조회 테스트 - 강의 목록이 없을 때")
    public void findByIdList_empty() {
        //Given
        when(lectureRepository.getLectures()).thenReturn(List.of());

        //When & Then
        assertThrows(LectureNotFoundException.class, () -> {
            lectureApplyServiceImpl.getLectureList();
        });

        verify(lectureRepository, times(1)).getLectures();
    }

    @Test
    @DisplayName("강의 목록 조회 테스트")
    public void findByIdList() {
        //Given
        Lecture lecture1 = new Lecture(1L, "항해플러스", LocalDateTime.parse("2024-04-10T13:00:00"), 30);

        Lecture lecture2 = new Lecture(2L, "자바 스터디", LocalDateTime.parse("2024-04-10T13:00:00"), 30);

        when(lectureRepository.getLectures()).thenReturn(List.of(lecture1, lecture2));

        //When
        List<Lecture> lectureList = lectureApplyServiceImpl.getLectureList();

        //Then
        assertEquals(2, lectureList.size());

        verify(lectureRepository, times(1)).getLectures();
    }



}