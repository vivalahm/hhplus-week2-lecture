package com.hhplus.lecture.business.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "lecture")
@Getter
@NoArgsConstructor
public class Lecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private LocalDateTime openDate;
    private Integer maxAttendees;

    @OneToMany(mappedBy = "lecture", fetch = FetchType.LAZY)
    private List<LectureHistory> lectureMasters;

    public Lecture(String title, LocalDateTime openDate, Integer maxAttendees) {
        this.title = title;
        this.openDate = openDate;
        this.maxAttendees = maxAttendees;
    }

    public Lecture(Long id, String title, LocalDateTime openDate, Integer maxAttendees) {
        this.id = id;
        this.title = title;
        this.openDate = openDate;
        this.maxAttendees = maxAttendees;
    }

    public Lecture(Long id, String title) {
        this.id = id;
        this.title = title;
    }

    public boolean isAfterOpenDate(LocalDateTime now) {
        return now.isAfter(openDate);
    }

    public boolean isFull(long appliedCount) {
        return appliedCount >= maxAttendees;
    }

}