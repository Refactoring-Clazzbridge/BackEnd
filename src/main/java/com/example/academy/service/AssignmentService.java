package com.example.academy.service;

import com.example.academy.domain.Assignment;
import com.example.academy.domain.Course;
import com.example.academy.domain.StudentCourse;
import com.example.academy.dto.assignment.AssignmentRequestDTO;
import com.example.academy.dto.assignment.AssignmentResponseDTO;
import com.example.academy.dto.member.CustomUserDetails;
import com.example.academy.enums.MemberRole;
import com.example.academy.exception.common.NotFoundException;
import com.example.academy.exception.common.UnauthorizedException;
import com.example.academy.mapper.assignment.AssignmentMapper;
import com.example.academy.repository.mysql.AssignmentRepository;
import com.example.academy.repository.mysql.CourseRepository;
import com.example.academy.repository.mysql.StudentCourseRepository;
import java.util.ArrayList;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class AssignmentService {

    private final AssignmentRepository assignmentRepository;
    private final CourseRepository courseRepository;
    private final StudentCourseRepository studentCourseRepository;

    private final AuthService authService;

    public AssignmentService(AssignmentRepository assignmentRepository,
        CourseRepository courseRepository, AuthService authService,
        StudentCourseRepository studentCourseRepository) {
        this.assignmentRepository = assignmentRepository;
        this.courseRepository = courseRepository;
        this.authService = authService;
        this.studentCourseRepository = studentCourseRepository;
    }

    public List<AssignmentResponseDTO> getAllAssignments() {
        return AssignmentMapper.toDtoList(assignmentRepository.findAll());
    }

    public List<AssignmentResponseDTO> getAssignmentsByCourseId(Long courseId) {
        if (courseId == null) {
            throw new NotFoundException("courseId를 입력해 주세요.");
        }
        courseRepository.findById(courseId)
            .orElseThrow(() -> new NotFoundException("해당 강의가 존재하지 않습니다."));

        List<Assignment> assignments = assignmentRepository.findAllByCourseId(courseId);

        return AssignmentMapper.toDtoList(assignments);
    }

    public List<AssignmentResponseDTO> getAssignmentsForCurrentUser() {
        CustomUserDetails user = authService.getAuthenticatedUser();

        StudentCourse studentCourse = studentCourseRepository.findByStudentId(user.getUserId());

        if (studentCourse == null) {
            throw new NotFoundException("수강 중인 강의를 찾을 수 없습니다.");
        }

        List<Assignment> assignments = assignmentRepository.getAssignmentsByCourseId(
            studentCourse.getCourse().getId());

        return AssignmentMapper.toDtoList(assignments);
    }

    @Transactional
    public AssignmentResponseDTO save(AssignmentRequestDTO assignmentRequestDTO) {

        CustomUserDetails user = authService.getAuthenticatedUser();
        if (!user.getUserType().equals(MemberRole.ROLE_TEACHER)) {
            throw new UnauthorizedException("과제 등록 권한이없습니다.");
        }

        Course course = courseRepository.findById(assignmentRequestDTO.getCourseId())
            .orElseThrow(() -> new NotFoundException("해당 코스를 찾을 수 없습니다."));

        Assignment assignment = new Assignment();
        assignment.setCourse(course);
        assignment.setTitle(assignmentRequestDTO.getTitle());
        assignment.setDescription(assignmentRequestDTO.getDescription());
        assignment.setDueDate(assignmentRequestDTO.getDueDate());

        assignmentRepository.save(assignment);

        // 과제를 데이터베이스에 저장
        return AssignmentMapper.toDto(assignment);
    }


}
