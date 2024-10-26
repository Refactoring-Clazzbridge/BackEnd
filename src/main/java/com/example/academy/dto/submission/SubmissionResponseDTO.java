package com.example.academy.dto.submission;


import lombok.Builder;
import lombok.Data;

@Data
public class SubmissionResponseDTO {

    private Long assignmentId;
    private Long studentCourseId;
    private String content;
    private String submissionUrl;


    @Builder
    public SubmissionResponseDTO(Long assignmentId, Long studentCourseId, String content,
        String submissionUrl) {
        this.assignmentId = assignmentId;
        this.studentCourseId = studentCourseId;
        this.content = content;
        this.submissionUrl = submissionUrl;
    }
}
