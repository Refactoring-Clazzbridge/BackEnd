package com.example.academy.service;

import com.example.academy.domain.Course;
import com.example.academy.domain.Member;
import com.example.academy.domain.StudentCourse;
import com.example.academy.domain.Submission;
import com.example.academy.dto.member.CustomUserDetails;
import com.example.academy.dto.member.StudentDTO;
import com.example.academy.dto.studentCourse.StudentCourseResponseDTO;
import com.example.academy.enums.MemberRole;
import com.example.academy.exception.common.NotFoundException;
import com.example.academy.mapper.member.MemberResponseMapper;
import com.example.academy.mapper.studentCourse.StudentCourseMapper;
import com.example.academy.repository.mysql.CourseRepository;
import com.example.academy.repository.mysql.MemberRepository;
import com.example.academy.repository.mysql.StudentCourseRepository;
import com.example.academy.repository.mysql.SubmissionRepository;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class StudentCourseService {

    private final StudentCourseRepository studentCourseRepository;
    private final CourseRepository courseRepository;
    private final SubmissionRepository submissionRepository;
    private final AuthService authService;

    public StudentCourseService(StudentCourseRepository studentCourseRepository,
        CourseRepository courseRepository,
        SubmissionRepository submissionRepository,
        AuthService authService) {
        this.studentCourseRepository = studentCourseRepository;
        this.courseRepository = courseRepository;
        this.authService = authService;
        this.submissionRepository = submissionRepository;
    }


    public Long getCourseId() {
        CustomUserDetails user = authService.getAuthenticatedUser();

        return studentCourseRepository.findByStudentId(user.getUserId()).getCourse().getId();
    }

    public StudentCourseResponseDTO getStudentCourseId() {
        CustomUserDetails user = authService.getAuthenticatedUser();

        if (user.getUserType().equals(MemberRole.ROLE_TEACHER)) {
            Course course = courseRepository.findByInstructor_Id(user.getUserId())
                .orElseThrow(() -> new NotFoundException("배정된 강의가 없습니다."));

            return StudentCourseMapper.toDto(course);
        }

        StudentCourse studentCourse = studentCourseRepository.findByStudentId(user.getUserId());

        return StudentCourseMapper.toDto(studentCourse);
    }

    public List<StudentDTO> getStudentsByCourseId(Long courseId) {
        // courseId에 해당하는 StudentCourse 엔터티 리스트를 가져와 학생(Member) 객체만 추출
        List<Member> members = studentCourseRepository.findByCourse_Id(courseId)
            .stream()
            .map(StudentCourse::getStudent)
            .toList();

        return MemberResponseMapper.toStudentDTOList(members);
    }


}
