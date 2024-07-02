package com.hhplus.lecture.common.exception;

public class LectureCapacityExceededException extends RuntimeException{
    public LectureCapacityExceededException(String message) {
        super(message);
    }
}
