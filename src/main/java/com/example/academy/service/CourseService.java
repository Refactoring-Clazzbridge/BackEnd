package com.example.academy.service;

import com.example.academy.domain.Classroom;
import com.example.academy.domain.Course;
import com.example.academy.domain.StudentCourse;
import com.example.academy.dto.course.CourseAddDTO;
import com.example.academy.dto.course.CourseTitleDTO;
import com.example.academy.dto.course.CourseUpdateDTO;
import com.example.academy.dto.course.GetCourseDTO;
import com.example.academy.dto.course.SelectCourseDTO;
import com.example.academy.dto.member.CustomUserDetails;
import com.example.academy.exception.common.NotFoundException;
import com.example.academy.enums.MemberRole;
import com.example.academy.repository.mysql.ClassroomRepository;
import com.example.academy.repository.mysql.CourseRepository;
import com.example.academy.repository.mysql.StudentCourseRepository;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;

@Service
public class CourseService {

  private final CourseRepository courseRepository;
  private final ClassroomRepository classroomRepository;
  private final StudentCourseRepository studentCourseRepository;
  private final AuthService authService;

  public CourseService(CourseRepository courseRepository, ClassroomRepository classroomRepository, StudentCourseRepository studentCourseRepository, AuthService authService) {
    this.courseRepository = courseRepository;
    this.classroomRepository = classroomRepository;
    this.studentCourseRepository = studentCourseRepository;
    this.authService = authService;
  }

  public List<CourseTitleDTO> getCourseTitle() {
    return courseRepository.findAll().stream()
        .map(course -> new CourseTitleDTO(course.getTitle()))
        .collect(Collectors.toList());
  }

  public List<GetCourseDTO> getAllCourse() {
    List<Course> courses = courseRepository.findAll();
    return courses.stream()
        .map(course -> new GetCourseDTO(
            course.getId(),
            course.getInstructor() != null ? course.getInstructor().getName() : "",
            course.getClassroom() != null ? course.getClassroom().getName() : "No Classroom",
            course.getTitle(),
            course.getDescription(),
            course.getStartDate(),
            course.getEndDate(),
            course.getLayoutImageUrl()))
        .collect(Collectors.toList());
  }

  public List<GetCourseDTO> getCourse(Long id) throws Exception {
    Optional<Course> courses = courseRepository.findById(id);
    if (courses.isEmpty()) {
      throw new Exception("조회된 강의가 없습니다.");
    }
    return courses.stream()
        .map(course -> new GetCourseDTO(
            course.getId(),
            course.getInstructor() != null ? course.getInstructor().getName() : "No Instructor",
            course.getClassroom() != null ? course.getClassroom().getName() : "No Classroom",
            course.getTitle(),
            course.getDescription(),
            course.getStartDate(),
            course.getEndDate(),
            course.getLayoutImageUrl()))
        .collect(Collectors.toList());
  }

  public void deleteCourse(Long id) {
    courseRepository.deleteById(id);
  }

  public List<SelectCourseDTO> seatAllCourse() {
    List<Course> courses = courseRepository.findAll();
    return courses.stream()
        .map(course -> new SelectCourseDTO(
            course.getId(),
            course.getTitle()))
        .collect(Collectors.toList());
  }

  public void addCourse(CourseAddDTO courseAddDTO) throws Exception {
    String message = "";
    while (true) {
      if (courseRepository.existsByTitle(courseAddDTO.getTitle())) {
        message += "이미 존재하는 강의명입니다.";
      }
      if (classroomRepository.existsByName(courseAddDTO.getClassroomName())) {
        Optional<Classroom> optionalClassroom = classroomRepository.findByName(courseAddDTO.getClassroomName());
        if (optionalClassroom.isPresent()) {
          if (optionalClassroom.get().getIsOccupied()) {
            message += " 해당 강의실은 사용 중입니다.";
          }
        } else {
          message += "해당 강의실은 존재하지 않습니다.";
        }
      }
      if (message.length() > 5) {
        throw new Exception(message);
      } else {
        break;
      }
    }
    Course course = new Course();
    course.setClassroom(classroomRepository.findByName(courseAddDTO.getClassroomName()).get());
    course.setTitle(courseAddDTO.getTitle());
    course.setDescription(courseAddDTO.getDescription());
    course.setStartDate(courseAddDTO.getStartDate());
    course.setEndDate(courseAddDTO.getEndDate());
    course.setLayoutImageUrl(courseAddDTO.getLayoutImageUrl());
    courseRepository.save(course);
  }

  public void updateCourse(CourseUpdateDTO courseUpdateDTO) throws Exception {
    Course course = courseRepository.findById(courseUpdateDTO.getId()).get();
    String message = "";
    while (true) {
      if (!course.getTitle().equals(courseUpdateDTO.getTitle()) && courseRepository.existsByTitle(courseUpdateDTO.getTitle())) {
        message += "이미 존재하는 강의명입니다.";
      }
      if (classroomRepository.existsByName(courseUpdateDTO.getClassroomName())) {
        if (!course.getClassroom().getName().equals(courseUpdateDTO.getClassroomName()) && classroomRepository.findByName(courseUpdateDTO.getClassroomName()).get().getIsOccupied()) {
          message += " 해당 강의실은 사용 중입니다.";
        }
      } else {
        message += " 해당 강의실은 존재하지 않습니다.";
      }
      if (message.equals("")) {
        break;
      } else {
        throw new Exception(message);
      }
    }
    course.setClassroom(classroomRepository.findByName(courseUpdateDTO.getClassroomName()).get());
    course.setTitle(courseUpdateDTO.getTitle());
    course.setDescription(courseUpdateDTO.getDescription());
    course.setStartDate(courseUpdateDTO.getStartDate());
    course.setEndDate(courseUpdateDTO.getEndDate());
    course.setLayoutImageUrl(courseUpdateDTO.getLayoutImageUrl());
    courseRepository.save(course);
  }

  public Long getTeacherByCourseId() {
    CustomUserDetails user = authService.getAuthenticatedUser();
    if (user.getUserType().equals(MemberRole.ROLE_TEACHER)) {
      Course course = courseRepository.findByInstructorId(user.getUserId())
          .orElseThrow(() -> new NotFoundException("해당 강의가 존재하지 않습니다."));
      return course.getId();
    }
    return null;
  }

  public Long getStudentCourseId() {
    CustomUserDetails user = authService.getAuthenticatedUser();
    if (user.getUserType().equals(MemberRole.ROLE_STUDENT)) {
      StudentCourse studentCourse = studentCourseRepository.findByStudentId(user.getUserId());
      if (studentCourse == null) {
        throw new NotFoundException("해당 수강 정보가 존재하지 않습니다.");
      }
      return studentCourse.getCourse().getId();
    }
    return null;
  }
}