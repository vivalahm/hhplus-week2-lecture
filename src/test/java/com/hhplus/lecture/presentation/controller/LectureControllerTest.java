package com.hhplus.lecture.presentation.controller;

import com.hhplus.lecture.business.dto.ApplyLectureRequest;

import com.hhplus.lecture.business.entity.Lecture;
import com.hhplus.lecture.business.service.LectureService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(LectureController.class)
public class LectureControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LectureService lectureService;

    private ApplyLectureRequest applyLectureRequest;

    @BeforeEach
    void setUp() {
        applyLectureRequest = new ApplyLectureRequest();
        applyLectureRequest.setUserId(1L);
        applyLectureRequest.setLectureId(1L);
    }

    @Test
    void applyLecture_Success() throws Exception {
        mockMvc.perform(post("/lectures/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": 1, \"lectureId\": 1}"))
                .andExpect(status().isOk())
                .andExpect(content().string("강의 신청이 완료되었습니다."));
    }

    @Test
    void applyLecture_Fail() throws Exception {
        doThrow(new RuntimeException("강의 신청에 실패했습니다.")).when(lectureService).applyLecture(any(Long.class), any(Long.class));

        mockMvc.perform(post("/lectures/apply")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"userId\": 1, \"lectureId\": 1}"))
                .andExpect(status().isBadRequest())
                .andExpect(content().string("강의 신청에 실패했습니다."));
    }

    @Test
    void checkApplicationStatus_true() throws Exception {
        given(lectureService.checkApplyStatus(1L, 1L)).willReturn(true);

        mockMvc.perform(get("/lectures/application/1")
                        .param("lectureId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true));
    }

    @Test
    void checkApplicationStatus_false() throws Exception {
        given(lectureService.checkApplyStatus(1L, 1L)).willReturn(false);

        mockMvc.perform(get("/lectures/application/1")
                        .param("lectureId", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(false));
    }

    @Test
    void getLectureList() throws Exception {
        List<Lecture> lectureList = new ArrayList<>();
        Lecture lecture = new Lecture();
        lecture.setId(1L);
        lecture.setTitle("Spring Boot 특강");
        lectureList.add(lecture);

        given(lectureService.getLectureList()).willReturn(lectureList);

        mockMvc.perform(get("/lectures"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Spring Boot 특강"));
    }

    @Test
    void getLectureList_empty() throws Exception {
        List<Lecture> lectureList = new ArrayList<>();

        given(lectureService.getLectureList()).willReturn(lectureList);

        mockMvc.perform(get("/lectures"))
                .andExpect(status().isOk())
                .andExpect(content().string("[]"));
    }
}