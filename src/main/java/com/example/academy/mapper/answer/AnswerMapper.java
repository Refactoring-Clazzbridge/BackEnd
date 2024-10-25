package com.example.academy.mapper.answer;

import com.example.academy.domain.Answer;
import com.example.academy.domain.Member;
import com.example.academy.domain.Question;
import com.example.academy.dto.answer.AnswerCreateDTO;
import com.example.academy.dto.answer.AnswerReadDTO;
import com.example.academy.dto.answer.AnswerUpdateDTO;
import java.util.List;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;

@Mapper
public interface AnswerMapper {

  AnswerMapper INSTANCE = Mappers.getMapper(AnswerMapper.class);

  // Answer 엔티티를 AnswerReadDTO로 변환
  @Mapping(source = "teacher.name", target = "teacherName")
  AnswerReadDTO answerToAnswerReadDTO(Answer answer);

  // List<Answer>를 List<AnswerReadDTO>로 변환
  List<AnswerReadDTO> answersToAnswerReadDTOs(List<Answer> answers);

  // AnswerCreateDTO를 Answer 엔티티로 변환
  @Mapping(target = "id", ignore = true)  // id는 자동 생성되므로 무시
  @Mapping(source = "question", target = "question")
  @Mapping(source = "teacher", target = "teacher")
  @Mapping(source = "answerCreateDTO.content", target = "content")  // 명확한 content 매핑
  Answer answerCreateDTOToAnswer(AnswerCreateDTO answerCreateDTO, Question question, Member teacher);

  // AnswerUpdateDTO를 Answer 엔티티로 변환
  @Mapping(target = "question", ignore = true)  // 질문 정보는 수정되지 않음
  @Mapping(target = "teacher", ignore = true)   // 교사 정보는 수정되지 않음
  Answer answerUpdateDTOToAnswer(AnswerUpdateDTO answerUpdateDTO);
}