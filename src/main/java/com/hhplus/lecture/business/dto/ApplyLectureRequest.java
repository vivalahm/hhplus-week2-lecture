package com.hhplus.lecture.business.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ApplyLectureRequest {
    private Long UserId;
    private Long LectureId;
}
