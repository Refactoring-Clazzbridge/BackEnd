package com.example.academy.service;

import com.example.academy.domain.Course;
import com.example.academy.domain.Member;
import com.example.academy.domain.Question;
import com.example.academy.domain.StudentCourse;
import com.example.academy.dto.answer.AnswerReadDTO;
import com.example.academy.dto.question.QuestionCreateDTO;
import com.example.academy.dto.question.QuestionDetailReadDTO;
import com.example.academy.dto.question.QuestionReadDTO;
import com.example.academy.dto.question.QuestionToggleRecommendedDTO;
import com.example.academy.dto.question.QuestionUpdateDTO;
import com.example.academy.exception.post.PostEmptyException;
import com.example.academy.exception.post.PostNotFoundException;
import com.example.academy.mapper.question.QuestionMapper;
import com.example.academy.repository.mysql.CourseRepository;
import com.example.academy.repository.mysql.MemberRepository;
import com.example.academy.repository.mysql.QuestionRepository;
import com.example.academy.repository.mysql.StudentCourseRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
public class QuestionService {

  private final QuestionRepository questionRepository;
  private final MemberRepository memberRepository;
  private final StudentCourseRepository studentCourseRepository;
  private final CourseRepository courseRepository;
  private final AnswerService answerService;
  private final QuestionMapper questionMapper = QuestionMapper.INSTANCE;

  @Autowired
  public QuestionService(QuestionRepository questionRepository, MemberRepository memberRepository,
      StudentCourseRepository studentCourseRepository, CourseRepository courseRepository, AnswerService answerService) {
    this.questionRepository = questionRepository;
    this.memberRepository = memberRepository;
    this.studentCourseRepository = studentCourseRepository;
    this.courseRepository = courseRepository;
    this.answerService = answerService;
  }

  public List<QuestionReadDTO> getAllQuestions() {
    List<Question> questions = questionRepository.findAll();
    List<QuestionReadDTO> questionReadDTOs = new ArrayList<>();
    for (Question question : questions) {
      boolean isSolved = !question.getAnswers().isEmpty();
      questionReadDTOs.add(questionMapper.questionToQuestionReadDTO(question, isSolved));
    }
    return questionReadDTOs;
  }

  public QuestionDetailReadDTO getQuestionDetailById(Long id) {
    Question question = questionRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("존재하지 않는 질문입니다 ID: " + id));

    // 연관된 답변도 조회
    List<AnswerReadDTO> answers = answerService.getAnswersByQuestionId(id);
    return questionMapper.questionToQuestionDetailReadDTO(question, answers);
  }

  public QuestionReadDTO createQuestion(QuestionCreateDTO questionCreateDTO) {
    Member student = memberRepository.findById(questionCreateDTO.getMemberId())
        .orElseThrow(() -> new RuntimeException("존재하지 않는 회원입니다 ID: " + questionCreateDTO.getMemberId()));
    Course course = courseRepository.findById(questionCreateDTO.getCourseId())
        .orElseThrow(() -> new RuntimeException("존재하지 않는 강의입니다 ID: " + questionCreateDTO.getCourseId()));
    StudentCourse studentCourse = studentCourseRepository.findByStudentIdAndCourseId(
            student.getId(), course.getId())
        .orElseThrow(() -> new RuntimeException("회원이 수강"));

    Question newQuestion = questionMapper.questionCreateDTOToQuestion(questionCreateDTO, studentCourse);
    Question savedQuestion = questionRepository.save(newQuestion);

    return questionMapper.questionToQuestionReadDTO(savedQuestion, false);
  }

  public QuestionReadDTO updateQuestion(QuestionUpdateDTO questionUpdateDTO) {
    Question existingQuestion = questionRepository.findById(questionUpdateDTO.getId())
        .orElseThrow(() -> new RuntimeException("Question not found"));

    existingQuestion.updateContent(questionUpdateDTO.getContent());
    questionRepository.save(existingQuestion);

    boolean isSolved = !existingQuestion.getAnswers().isEmpty();
    return questionMapper.questionToQuestionReadDTO(existingQuestion, isSolved);
  }

  public QuestionReadDTO recommendQuestion(QuestionToggleRecommendedDTO questionToggleRecommendedDTO) {
    Question existingQuestion = questionRepository.findById(questionToggleRecommendedDTO.getId())
        .orElseThrow();

    existingQuestion.toggleRecommended(questionToggleRecommendedDTO.isRecommended());
    questionRepository.save(existingQuestion);

    boolean isSolved = !existingQuestion.getAnswers().isEmpty();
    return questionMapper.questionToQuestionReadDTO(existingQuestion, isSolved);
  }

  public void deleteQuestion(List<Long> ids) {
    if (ids.isEmpty()) {
      throw new PostEmptyException();
    }

    for (Long id : ids) {
      Question deletedQuestion = questionRepository.findById(id)
          .orElseThrow(() -> new PostNotFoundException(id));
      questionRepository.delete(deletedQuestion);
    }
  }
}
