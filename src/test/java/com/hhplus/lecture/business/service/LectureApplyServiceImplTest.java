package com.hhplus.lecture.business.service;

import com.hhplus.lecture.business.entity.Lecture;
import com.hhplus.lecture.business.entity.LectureHistory;
import com.hhplus.lecture.business.entity.User;
import com.hhplus.lecture.business.repository.LectureHistoryRepository;
import com.hhplus.lecture.business.repository.LectureRepository;
import com.hhplus.lecture.business.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;

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

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        //When & Then
        assertThrows(NoSuchElementException.class, () -> {
            lectureApplyServiceImpl.applyLecture(userId, lectureId);
        });

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("강의 신청 테스트 - 강의 정보 없음")
    public void applyLecture_lectureNotFound() {
        //Given
        Long userId = 1L;
        Long lectureId = 1L;

        User user = new User(userId, "홍길동");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(lectureRepository.findById(lectureId)).thenReturn(Optional.empty());

        //When & Then
        assertThrows(NoSuchElementException.class, () -> {
            lectureApplyServiceImpl.applyLecture(userId, lectureId);
        });

        verify(userRepository, times(1)).findById(userId);
        verify(lectureRepository, times(1)).findById(lectureId);
    }

    @Test
    @DisplayName("강의 신청 테스트 - 강의 신청 일자가 아닐 때")
    public void applyLecture_notApplyDate() {
        //Given
        Long userId = 1L;
        Long lectureId = 1L;

        User user = new User(userId, "홍길동");


        Lecture lecture = new Lecture(lectureId, "항해플러스", LocalDateTime.now().plusDays(1), 30);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(lectureRepository.findById(lectureId)).thenReturn(Optional.of(lecture));

        //When
        assertThrows(IllegalStateException.class, () -> {
            lectureApplyServiceImpl.applyLecture(userId, lectureId);
        }, "강의 신청 가능한 날짜가 아닙니다.");


        //Then
        verify(userRepository, times(1)).findById(userId);
        verify(lectureRepository, times(1)).findById(lectureId);
    }

    @Test
    @DisplayName("강의 신청 테스트 - 수강 인원이 모두 찼을 때")
    public void applyLecture_lectureIsFull() {
        //Given
        Long userId = 1L;
        Long lectureId = 1L;

        User user = new User(userId, "홍길동");

        Lecture lecture = new Lecture(lectureId, "항해플러스", LocalDateTime.parse("2024-04-10T13:00:00"), 30);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(lectureRepository.findById(lectureId)).thenReturn(Optional.of(lecture));
        when(lectureHistoryRepository.countByLectureAndIsAppliedTrue(lecture)).thenReturn(30L);


        //When & Then
        assertThrows(IllegalStateException.class, () -> {
            lectureApplyServiceImpl.applyLecture(userId, lectureId);
        }, "정원이 초과되었습니다.");

        verify(userRepository, times(1)).findById(userId);
        verify(lectureRepository, times(1)).findById(lectureId);
        verify(lectureHistoryRepository, times(1)).countByLectureAndIsAppliedTrue(lecture);
    }

    @Test
    @DisplayName("강의 신청 테스트 - 이미 신청한 강의")
    public void applyLecture_alreadyApplied() {
        //Given
        Long userId = 1L;
        Long lectureId = 1L;

        User user = new User(userId, "홍길동");

        Lecture lecture = new Lecture(lectureId, "항해플러스", LocalDateTime.parse("2024-04-10T13:00:00"), 30);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(lectureRepository.findById(lectureId)).thenReturn(Optional.of(lecture));
        when(lectureHistoryRepository.countByLectureAndIsAppliedTrue(lecture)).thenReturn(29L);
        when(lectureHistoryRepository.findByUserAndLectureAndIsAppliedTrue(user, lecture)).thenReturn(Optional.of(new LectureHistory()));

        //When & Then
        assertThrows(IllegalStateException.class, () -> {
            lectureApplyServiceImpl.applyLecture(userId, lectureId);
        }, "이미 신청한 강의입니다.");

        verify(userRepository, times(1)).findById(userId);
        verify(lectureRepository, times(1)).findById(lectureId);
        verify(lectureHistoryRepository, times(1)).countByLectureAndIsAppliedTrue(lecture);
        verify(lectureHistoryRepository, times(1)).findByUserAndLectureAndIsAppliedTrue(user, lecture);
    }

    @Test
    @DisplayName("강의 신청 테스트 - 정상적인 강의 신청")
    public void applyLecture() {
        //Given
        Long userId = 1L;
        Long lectureId = 1L;

        User user = new User(userId, "홍길동");

        Lecture lecture = new Lecture(lectureId, "항해플러스", LocalDateTime.parse("2024-04-10T13:00:00"), 30);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(lectureRepository.findById(lectureId)).thenReturn(Optional.of(lecture));
        when(lectureHistoryRepository.countByLectureAndIsAppliedTrue(lecture)).thenReturn(29L);
        when(lectureHistoryRepository.findByUserAndLectureAndIsAppliedTrue(user, lecture)).thenReturn(Optional.empty());

        //When & Then
        assertDoesNotThrow(() -> {
            lectureApplyServiceImpl.applyLecture(userId, lectureId);
        });

        verify(userRepository, times(1)).findById(userId);
        verify(lectureRepository, times(1)).findById(lectureId);
        verify(lectureHistoryRepository, times(1)).countByLectureAndIsAppliedTrue(lecture);
        verify(lectureHistoryRepository, times(1)).findByUserAndLectureAndIsAppliedTrue(user, lecture);
        verify(lectureHistoryRepository, times(1)).save(any(LectureHistory.class));
    }

    @Test
    @DisplayName("강의 신청 여부 확인 테스트 - 유저 정보 없음")
    public void checkApplyStatus_userNotFound() {
        //Given
        Long userId = 1L;
        Long lectureId = 1L;

        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        //When & Then
        assertThrows(NoSuchElementException.class, () -> {
            lectureApplyServiceImpl.checkApplyStatus(userId, lectureId);
        });

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("강의 신청 여부 확인 테스트 - 강의 정보 없음")
    public void checkApplyStatus_lectureNotFound() {
        //Given
        Long userId = 1L;
        Long lectureId = 1L;

        User user = new User(userId, "홍길동");

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(lectureRepository.findById(lectureId)).thenReturn(Optional.empty());

        //When & Then
        assertThrows(NoSuchElementException.class, () -> {
            lectureApplyServiceImpl.checkApplyStatus(userId, lectureId);
        });

        verify(userRepository, times(1)).findById(userId);
        verify(lectureRepository, times(1)).findById(lectureId);
    }

    @Test
    @DisplayName("강의 신청 여부 확인 테스트 - 정상적인 강의 신청 여부 확인")
    public void checkApplyStatus_lectureIsApplied() {
        //Given
        Long userId = 1L;
        Long lectureId = 1L;

        User user = new User(userId, "홍길동");

        Lecture lecture = new Lecture(lectureId, "항해플러스", LocalDateTime.parse("2024-04-10T13:00:00"), 30);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(lectureRepository.findById(lectureId)).thenReturn(Optional.of(lecture));
        when(lectureHistoryRepository.findByUserAndLectureAndIsAppliedTrue(user, lecture)).thenReturn(Optional.of(new LectureHistory()));

        //When
        boolean isApplied = lectureApplyServiceImpl.checkApplyStatus(userId, lectureId);

        //Then
        assertTrue(isApplied);

        verify(userRepository, times(1)).findById(userId);
        verify(lectureRepository, times(1)).findById(lectureId);
        verify(lectureHistoryRepository, times(1)).findByUserAndLectureAndIsAppliedTrue(user, lecture);
    }

    @Test
    @DisplayName("강의 신청 여부 확인 테스트 - 강의 신청하지 않음")
    public void checkApplyStatus_lectureIsNotApplied() {
        //Given
        Long userId = 1L;
        Long lectureId = 1L;

        User user = new User(userId, "홍길동");

        Lecture lecture = new Lecture(lectureId, "항해플러스", LocalDateTime.parse("2024-04-10T13:00:00"), 30);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(lectureRepository.findById(lectureId)).thenReturn(Optional.of(lecture));
        when(lectureHistoryRepository.findByUserAndLectureAndIsAppliedTrue(user, lecture)).thenReturn(Optional.empty());

        //When
        boolean isApplied = lectureApplyServiceImpl.checkApplyStatus(userId, lectureId);

        //Then
        assertFalse(isApplied);

        verify(userRepository, times(1)).findById(userId);
    }

    @Test
    @DisplayName("강의 목록 조회 테스트 - 강의 목록이 없을 때")
    public void findByIdList_empty() {
        //Given
        when(lectureRepository.findAll()).thenReturn(List.of());

        //When & Then
        assertThrows(NoSuchElementException.class, () -> {
            lectureApplyServiceImpl.getLectureList();
        });

        verify(lectureRepository, times(1)).findAll();
    }

    @Test
    @DisplayName("강의 목록 조회 테스트")
    public void findByIdList() {
        //Given
        Lecture lecture1 = new Lecture(1L, "항해플러스", LocalDateTime.parse("2024-04-10T13:00:00"), 30);

        Lecture lecture2 = new Lecture(2L, "자바 스터디", LocalDateTime.parse("2024-04-10T13:00:00"), 30);

        when(lectureRepository.findAll()).thenReturn(List.of(lecture1, lecture2));

        //When
        List<Lecture> lectureList = lectureApplyServiceImpl.getLectureList();

        //Then
        assertEquals(2, lectureList.size());

        verify(lectureRepository, times(1)).findAll();
    }



}