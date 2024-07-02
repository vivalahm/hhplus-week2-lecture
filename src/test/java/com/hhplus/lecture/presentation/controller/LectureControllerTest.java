package com.hhplus.lecture.presentation.controller;

import com.hhplus.lecture.business.entity.LectureHistory;
import com.hhplus.lecture.business.entity.User;
import com.hhplus.lecture.presentation.dto.ApplyLectureRequest;

import com.hhplus.lecture.business.entity.Lecture;
import com.hhplus.lecture.business.service.LectureApplyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LectureController.class)
public class LectureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LectureApplyServiceImpl lectureApplyServiceImpl;

    private ApplyLectureRequest applyLectureRequest;

    @BeforeEach
    void setUp() {
        applyLectureRequest = new ApplyLectureRequest();
        applyLectureRequest.setUserId(1L);
        applyLectureRequest.setLectureId(1L);
    }

    @Test
    void applyLecture_Success() throws Exception {
        User user = new User(1L, "testUser");
        Lecture lecture = new Lecture(1L, "testLecture");
        given(lectureApplyServiceImpl.applyLecture(1L, 1L)).willReturn(new LectureHistory(user, lecture, LocalDateTime.now(), true));
        mockMvc.perform(post("/lectures/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": 1, \"lectureId\": 1}"))
                .andExpect(status().isOk())
                .andExpect(content().string("강의 신청이 완료되었습니다."));
        verify(lectureApplyServiceImpl).applyLecture(1L, 1L);
    }

    @Test
    void applyLecture_Fail1() throws Exception {
        User user = new User(1L, "testUser");
        Lecture lecture = new Lecture(1L, "testLecture");
        given(lectureApplyServiceImpl.applyLecture(1L, 1L)).willReturn(new LectureHistory(user, lecture, LocalDateTime.now(), false));
        mockMvc.perform(post("/lectures/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": 1, \"lectureId\": 1}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("강의 신청에 실패했습니다."));
        verify(lectureApplyServiceImpl).applyLecture(1L, 1L);
    }

    @Test
    void applyLecture_Fail2() throws Exception {
        doThrow(new RuntimeException("강의 신청에 실패했습니다.")).when(lectureApplyServiceImpl).applyLecture(any(Long.class), any(Long.class));

        mockMvc.perform(post("/lectures/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": 1, \"lectureId\": 1}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("강의 신청에 실패했습니다."));
        verify(lectureApplyServiceImpl).applyLecture(1L, 1L);
    }

    @Test
    void checkApplicationStatus_true() throws Exception {
        given(lectureApplyServiceImpl.checkApplyStatus(1L, 1L)).willReturn(true);

        mockMvc.perform(get("/lectures/application/1")
                        .param("lectureId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true));
        verify(lectureApplyServiceImpl).checkApplyStatus(1L, 1L);
    }

    @Test
    void checkApplicationStatus_false() throws Exception {
        given(lectureApplyServiceImpl.checkApplyStatus(1L, 1L)).willReturn(false);

        mockMvc.perform(get("/lectures/application/1")
                        .param("lectureId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(false));
        verify(lectureApplyServiceImpl).checkApplyStatus(1L, 1L);
    }

    @Test
    void getLectureList() throws Exception {
        List<Lecture> lectureList = new ArrayList<>();
        Lecture lecture = new Lecture(1L, "Spring Boot 특강");
        lectureList.add(lecture);

        given(lectureApplyServiceImpl.getLectureList()).willReturn(lectureList);

        mockMvc.perform(get("/lectures"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Spring Boot 특강"));
        verify(lectureApplyServiceImpl).getLectureList();
    }

    @Test
    void getLectureList_empty() throws Exception {
        List<Lecture> lectureList = new ArrayList<>();

        given(lectureApplyServiceImpl.getLectureList()).willReturn(lectureList);

        mockMvc.perform(get("/lectures"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
        verify(lectureApplyServiceImpl).getLectureList();
    }
}