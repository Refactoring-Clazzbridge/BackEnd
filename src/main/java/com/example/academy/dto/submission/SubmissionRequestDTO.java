package com.example.academy.dto.submission;


import lombok.Builder;
import lombok.Data;

@Data
public class SubmissionRequestDTO {

    private Long assignmentId;
    private Long studentCourseId;
    private String content;


    @Builder
    public SubmissionRequestDTO(Long assignmentId, Long studentCourseId, String content) {
        this.assignmentId = assignmentId;
        this.studentCourseId = studentCourseId;
        this.content = content;
    }
}
