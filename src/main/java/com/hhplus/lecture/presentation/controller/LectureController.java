package com.hhplus.lecture.presentation.controller;

import com.hhplus.lecture.business.dto.ApplyLectureRequest;
import com.hhplus.lecture.business.dto.LectureApplyResponse;
import com.hhplus.lecture.business.entity.Lecture;
import com.hhplus.lecture.business.service.LectureApplyServiceImpl;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/lectures")
public class LectureController {
    private final LectureApplyServiceImpl lectureApplyServiceImpl;

    /**
     * 강의 신청
     * @param request
     * @return ResponseEntity
     */
    @PostMapping("/apply")
    public ResponseEntity<String> applyLecture(@RequestBody ApplyLectureRequest request) {
        try {
            lectureApplyServiceImpl.applyLecture(request.getUserId(), request.getLectureId());
            return ResponseEntity.ok("강의 신청이 완료되었습니다.");
        }catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * 강의 신청 여부 확인
     * @param userId
     * @param lectureId
     * @return ResponseEntity
     */
    @GetMapping("/application/{userId}")
    public ResponseEntity<LectureApplyResponse> checkApplyStatus(@PathVariable Long userId, @RequestParam Long lectureId) {
        boolean status = lectureApplyServiceImpl.checkApplyStatus(userId, lectureId);
        return ResponseEntity.ok(new LectureApplyResponse(status));
    }

    /**
     * 강의 목록 조회
     * @return ResponseEntity
     */
    @GetMapping
    public ResponseEntity<List<Lecture>> getLectureList() {
        List<Lecture> lectureList = lectureApplyServiceImpl.getLectureList();
        return ResponseEntity.ok(lectureList);
    }
}
