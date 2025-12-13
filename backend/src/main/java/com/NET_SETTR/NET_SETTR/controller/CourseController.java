package com.NET_SETTR.NET_SETTR.controller;

import com.NET_SETTR.NET_SETTR.dto.NoteResponse;
import com.NET_SETTR.NET_SETTR.dto.NoteSlidesResponse;
import com.NET_SETTR.NET_SETTR.model.Course;
import com.NET_SETTR.NET_SETTR.service.CourseService;
import com.NET_SETTR.NET_SETTR.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/courses")
@CrossOrigin("*")
public class CourseController {

    private final CourseService courseService;

    @Autowired
    private NoteService noteService;

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

    @GetMapping("/{courseId}/notes")
    public List<NoteResponse> getCourseNotes(@PathVariable Long courseId) {
        return noteService.getNotesForCourse(courseId);
    }

    @GetMapping("/{courseId}/notes/{noteId}/slides")
    public NoteSlidesResponse getSlides(
            @PathVariable Long courseId,
            @PathVariable Long noteId) {
        return noteService.getSlidesForNote(courseId, noteId);
    }

}
