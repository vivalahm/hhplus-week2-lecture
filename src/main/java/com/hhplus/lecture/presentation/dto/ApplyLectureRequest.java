package com.hhplus.lecture.presentation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplyLectureRequest {
    private Long userId;
    private Long lectureId;
}
