package com.hhplus.lecture.exception;

public class LectureNotFoundException extends RuntimeException{
    public LectureNotFoundException(String message) {
        super(message);
    }
}
