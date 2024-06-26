package com.hhplus.lecture.presentation.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class LectureApplyResponse {
    private boolean status;

    public LectureApplyResponse(boolean status) {
        this.status = status;
    }
}
