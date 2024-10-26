package com.example.academy.service;

import com.example.academy.dto.member.CustomUserDetails;
import com.example.academy.repository.mysql.CourseRepository;
import com.example.academy.repository.mysql.StudentCourseRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class StudentCourseService {

    private final StudentCourseRepository studentCourseRepository;
    private final CourseRepository courseRepository;
    private final AuthService authService;

    public StudentCourseService(StudentCourseRepository studentCourseRepository,
        CourseRepository courseRepository,
        AuthService authService) {
        this.studentCourseRepository = studentCourseRepository;
        this.courseRepository = courseRepository;
        this.authService = authService;
    }


    public Long getCourseId() {
        CustomUserDetails user = authService.getAuthenticatedUser();

        return studentCourseRepository.findByStudentId(user.getUserId()).getCourse()
            .getId();
    }

}
