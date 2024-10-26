package com.example.academy.mapper.question;

import com.example.academy.domain.Question;
import com.example.academy.domain.StudentCourse;
import com.example.academy.dto.question.QuestionCreateDTO;
import com.example.academy.dto.question.QuestionDetailReadDTO;
import com.example.academy.dto.question.QuestionReadDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface QuestionMapper {

  QuestionMapper INSTANCE = Mappers.getMapper(QuestionMapper.class);

  // Domain to DTO
  @Mapping(source = "question.id", target = "id")
  @Mapping(source = "question.studentCourse.student.name", target = "studentName")
  @Mapping(source = "question.recommended", target = "isRecommended")
  QuestionReadDTO questionToQuestionReadDTO(Question question);


  // DTO do Domain
  @Mapping(target = "id", ignore = true)      // id는 자동 생성되므로 무시
  @Mapping(target = "createdAt", ignore = true)
  @Mapping(target = "aiAnswer", ignore = true)
  @Mapping(target = "teacherAnswer", ignore = true)
  @Mapping(target = "answeredAt", ignore = true)
  @Mapping(target = "recommended", ignore = true)
  @Mapping(source = "studentCourse", target = "studentCourse")
  Question questionCreateDTOToQuestion(QuestionCreateDTO questionCreateDTO,
      StudentCourse studentCourse);

  @Mapping(source = "question.studentCourse.student.name", target = "studentName")
  @Mapping(source = "question.studentCourse.course.instructor.name", target = "teacherName")
  @Mapping(source = "question.recommended", target = "isRecommended")
  QuestionDetailReadDTO questionToQuestionDetailReadDTO(Question question);

}
