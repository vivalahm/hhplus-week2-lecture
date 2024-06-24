package com.hhplus.lecture.business.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;


@Entity
@Table(name = "lecture")
@RequiredArgsConstructor
@Getter
@Setter
public class Lecture {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private LocalDateTime openDate;
    private Integer maxAttendees;

    @OneToMany(mappedBy = "lecture", fetch = FetchType.LAZY)
    private List<LectureHistory> lectureMasters;

}