package com.NET_SETTR.NET_SETTR.controller;

import com.NET_SETTR.NET_SETTR.model.Slide;
import com.NET_SETTR.NET_SETTR.repository.SlideRepository;
import com.NET_SETTR.NET_SETTR.service.SlideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/slides")
@CrossOrigin("*")
public class SlideController {

    @Autowired
    SlideRepository slideRepository;

    private final SlideService slideService;

    public SlideController(SlideService slideService) {
        this.slideService = slideService;
    }

    // Get all slides for a course
//    @GetMapping("/{courseId}")
//    public List<Slide> getSlides(@PathVariable Integer courseId) {
//        return slideRepository.findByCourseIdOrderByDisplayOrderAsc(courseId);
//    }

    @GetMapping("/{courseId}")
    public List<Slide> getSlides(@PathVariable Integer courseId) {
        return slideRepository.findByCourseIdOrderByDisplayOrderAsc(courseId);
    }



    // View a specific slide
    @GetMapping("/view/{slideId}")
    public Slide viewSlide(@PathVariable Integer slideId) {
        return slideService.getSlideById(slideId);
    }
}

