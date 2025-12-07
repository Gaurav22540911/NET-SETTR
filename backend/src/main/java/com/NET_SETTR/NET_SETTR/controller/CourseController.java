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
    public List<Course> getCourses(@RequestParam(required = false) String type) {
        if (type != null && !type.isEmpty()) {
            return courseService.getCoursesByType(type);
        }
        return courseService.getAllCourses();
    }

    @GetMapping("/types")
    public List<String> getCourseTypes() {
        return courseService.getCourseTypes();
    }
}
