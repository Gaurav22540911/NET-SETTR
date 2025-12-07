package com.NET_SETTR.NET_SETTR.service;


import com.NET_SETTR.NET_SETTR.model.Course;
import com.NET_SETTR.NET_SETTR.repository.CourseRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CourseService {

    private final CourseRepository courseRepository;

    public CourseService(CourseRepository courseRepository) {
        this.courseRepository = courseRepository;
    }

    public List<Course> getAllCourses() {
        return courseRepository.findAll();
    }

    public List<String> getCourseTypes() {
        return courseRepository.findDistinctCourseTypes();
    }

    public List<Course> getCoursesByType(String type) {
        return courseRepository.findByCourseType(type);
    }
}
