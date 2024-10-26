package com.example.academy.service;

import com.example.academy.domain.Course;
import com.example.academy.domain.Member;
import com.example.academy.domain.Question;
import com.example.academy.domain.StudentCourse;
import com.example.academy.dto.question.QuestionCreateDTO;
import com.example.academy.dto.question.QuestionDetailReadDTO;
import com.example.academy.dto.question.QuestionReadDTO;
import com.example.academy.dto.question.QuestionToggleRecommendedDTO;
import com.example.academy.dto.question.QuestionUpdateDTO;
import com.example.academy.dto.question.TeacherAnswerCreateDTO;
import com.example.academy.dto.question.TeacherAnswerUpdateDTO;
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
  private final QuestionMapper questionMapper = QuestionMapper.INSTANCE;
  private final CourseRepository courseRepository;

  @Autowired
  public QuestionService(QuestionRepository questionRepository, MemberRepository memberRepository,
      StudentCourseRepository studentCourseRepository, CourseRepository courseRepository) {
    this.questionRepository = questionRepository;
    this.memberRepository = memberRepository;
    this.studentCourseRepository = studentCourseRepository;
    this.courseRepository = courseRepository;
  }

  public List<QuestionReadDTO> getAllQuestions() {
    List<Question> questions = questionRepository.findAll();
    List<QuestionReadDTO> questionReadDTOs = new ArrayList<>();
    for (Question question : questions) {
      questionReadDTOs.add(questionMapper.questionToQuestionReadDTO(question));
    }
    return questionReadDTOs;
  }

  public QuestionDetailReadDTO getQuestionDetailById(Long id) {
    Question question = questionRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Question not found"));

    return questionMapper.questionToQuestionDetailReadDTO(question);
  }

  public QuestionReadDTO createQuestion(QuestionCreateDTO questionCreateDTO) {
    Member student = memberRepository.findById(questionCreateDTO.getMemberId())
        .orElseThrow(() -> new RuntimeException("User not found"));
    Course course = courseRepository.findById(questionCreateDTO.getCourseId())
        .orElseThrow(() -> new RuntimeException("Course not found"));
    StudentCourse studentCourse = studentCourseRepository.findByStudentIdAndCourseId(
            student.getId(), course.getId())
        .orElseThrow(() -> new RuntimeException("Student course not found"));

    Question newQuestion = questionMapper.questionCreateDTOToQuestion(questionCreateDTO,
        studentCourse);
    Question savedQuestion = questionRepository.save(newQuestion);

    return questionMapper.questionToQuestionReadDTO(savedQuestion);
  }

  public QuestionReadDTO updateQuestion(QuestionUpdateDTO questionUpdateDTO) {
    Question existingQuestion = questionRepository.findById(questionUpdateDTO.getId())
        .orElseThrow(() -> new RuntimeException("Question not found"));

    existingQuestion.updateContent(questionUpdateDTO.getContent());
    questionRepository.save(existingQuestion);

    return questionMapper.questionToQuestionReadDTO(existingQuestion);
  }

  public QuestionReadDTO recommendQuestion(
      QuestionToggleRecommendedDTO questionToggleRecommendedDTO) {
    Question existingQuestion = questionRepository.findById(questionToggleRecommendedDTO.getId())
        .orElseThrow();

    existingQuestion.toggleRecommended(questionToggleRecommendedDTO.isRecommended());
    questionRepository.save(existingQuestion);

    return questionMapper.questionToQuestionReadDTO(existingQuestion);
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


  public String getTeacherAnswerById(Long id) {
    Question existingQuestion = questionRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Question not found"));

    return existingQuestion.getTeacherAnswer();
  }

  public QuestionDetailReadDTO createTeacherAnswer(TeacherAnswerCreateDTO teacherAnswerCreateDTO) {
    Question existingQuestion = questionRepository.findById(teacherAnswerCreateDTO.getQuestionId())
        .orElseThrow(() -> new RuntimeException("Question not found"));

    existingQuestion.updateTeacherAnswer(teacherAnswerCreateDTO.getTeacherAnswer());
    Question createdQuestion = questionRepository.save(existingQuestion);

    return questionMapper.questionToQuestionDetailReadDTO(createdQuestion);
  }

  public QuestionDetailReadDTO updateTeacherAnswer(TeacherAnswerUpdateDTO teacherAnswerUpdateDTO) {
    Question existingQuestion = questionRepository.findById(teacherAnswerUpdateDTO.getQuestionId())
        .orElseThrow(() -> new RuntimeException("Question not found"));

    existingQuestion.updateTeacherAnswer(teacherAnswerUpdateDTO.getTeacherAnswer());
    Question updatedQuestion = questionRepository.save(existingQuestion);

    return questionMapper.questionToQuestionDetailReadDTO(updatedQuestion);
  }

  public QuestionDetailReadDTO deleteTeacherAnswer(Long id) {
    Question existingQuestion = questionRepository.findById(id)
        .orElseThrow(() -> new RuntimeException("Question not found"));

    existingQuestion.updateTeacherAnswer(null);
    Question updatedQuestion = questionRepository.save(existingQuestion);

    return questionMapper.questionToQuestionDetailReadDTO(updatedQuestion);
  }
}
