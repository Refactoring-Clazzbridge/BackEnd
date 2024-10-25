package com.example.academy.dto.question;

import com.example.academy.dto.answer.AnswerReadDTO;
import java.util.Date;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class QuestionDetailReadDTO {

  private Long id;
  private String studentName;
  private String teacherName;
  private String content;
  private boolean isRecommended;
  private Date createdAt;
  private String aiAnswer;

  private List<AnswerReadDTO> answers;
}
