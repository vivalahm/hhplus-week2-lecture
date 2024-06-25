package com.hhplus.lecture.business.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "lecture_history")
@NoArgsConstructor
@Getter
public class LectureHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id")
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lecture_id")
    private Lecture lecture;

    private LocalDateTime applyDate;
    private Boolean isApplied;

    public LectureHistory(User user, Lecture lecture, LocalDateTime applyDate, Boolean isApplied) {
        this.user = user;
        this.lecture = lecture;
        this.applyDate = applyDate;
        this.isApplied = isApplied;
    }

    public static LectureHistory apply(User user, Lecture lecture) {
        LectureHistory lectureHistory = new LectureHistory(user, lecture, LocalDateTime.now(), true);
        return lectureHistory;
    }
}
