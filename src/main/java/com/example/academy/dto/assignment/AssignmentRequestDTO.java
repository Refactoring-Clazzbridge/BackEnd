package com.example.academy.dto.assignment;

import java.time.LocalDate;
import lombok.Builder;
import lombok.Data;

@Data
public class AssignmentRequestDTO {

    private Long courseId;
    private String title;
    private String description;
    private LocalDate dueDate;

    @Builder
    public AssignmentRequestDTO(Long courseId, String title, String description,
        LocalDate dueDate) {
        this.courseId = courseId;
        this.title = title;
        this.description = description;
        this.dueDate = dueDate;
    }
}
