package com.hhplus.lecture.presentation.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.hhplus.lecture.business.dto.ApplyLectureRequest;
import com.hhplus.lecture.business.entity.Lecture;
import com.hhplus.lecture.business.service.LectureApplyServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
public class LectureControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private LectureApplyServiceImpl lectureApplyServiceImpl;

    @Autowired
    private ObjectMapper objectMapper;

    private ApplyLectureRequest applyLectureRequest;

    @BeforeEach
    void setUp() {
        applyLectureRequest = new ApplyLectureRequest();
        applyLectureRequest.setUserId(1L);
        applyLectureRequest.setLectureId(1L);
    }

    @Test
    void applyLecture_Success() throws Exception {
        // given
        willDoNothing().given(lectureApplyServiceImpl).applyLecture(any(Long.class), any(Long.class));

        // when
        ResultActions resultActions = mockMvc.perform(post("/lectures/apply")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(applyLectureRequest)));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(content().string("강의 신청이 완료되었습니다."));
    }

    @Test
    void applyLecture_Fail() throws Exception {
        // given
        doThrow(new RuntimeException("강의 신청에 실패했습니다.")).when(lectureApplyServiceImpl).applyLecture(any(Long.class), any(Long.class));

        // when
        ResultActions resultActions = mockMvc.perform(post("/lectures/apply")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(applyLectureRequest)));

        // then
        resultActions.andExpect(status().isBadRequest())
                .andExpect(content().string("강의 신청에 실패했습니다."));
    }

    @Test
    void checkApplicationStatus_true() throws Exception {
        // given
        given(lectureApplyServiceImpl.checkApplyStatus(1L, 1L)).willReturn(true);

        // when
        ResultActions resultActions = mockMvc.perform(get("/lectures/application/1")
                .param("lectureId", "1"));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(true));
    }

    @Test
    void checkApplicationStatus_false() throws Exception {
        // given
        given(lectureApplyServiceImpl.checkApplyStatus(1L, 1L)).willReturn(false);

        // when
        ResultActions resultActions = mockMvc.perform(get("/lectures/application/1")
                .param("lectureId", "1"));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value(false));
    }

    @Test
    void getLectureList() throws Exception {
        // given
        List<Lecture> lectureList = new ArrayList<>();
        Lecture lecture = new Lecture(1L, "Spring Boot 특강");
        lectureList.add(lecture);

        given(lectureApplyServiceImpl.getLectureList()).willReturn(lectureList);

        // when
        ResultActions resultActions = mockMvc.perform(get("/lectures"));

        // then
        resultActions.andExpect(status().isOk())
                .andExpect(jsonPath("$[0].title").value("Spring Boot 특강"));
    }

    @Test
    void getLectureList_empty() throws Exception {
        // given
        List<Lecture> lectureList = new ArrayList<>();

        given(lectureApplyServiceImpl.getLectureList()).willReturn(lectureList);

        // when
        ResultActions resultActions = mockMvc.perform(get("/lectures"));
    }
}