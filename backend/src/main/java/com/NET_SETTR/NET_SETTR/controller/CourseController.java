package com.NET_SETTR.NET_SETTR.controller;

import com.NET_SETTR.NET_SETTR.model.Course;
import com.NET_SETTR.NET_SETTR.service.CourseService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin("*")
public class CourseController {

    private final CourseService courseService;

    public CourseController(CourseService courseService) {
        this.courseService = courseService;
    }

    @GetMapping
    public List<Course> getCourses() {
        return courseService.getAllCourses();
    }
}
