package com.NET_SETTR.NET_SETTR.service;

import com.NET_SETTR.NET_SETTR.model.Slide;
import com.NET_SETTR.NET_SETTR.repository.SlideRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SlideService {

    private final SlideRepository slideRepository;

    public SlideService(SlideRepository slideRepository) {
        this.slideRepository = slideRepository;
    }

    public List<Slide> getSlidesByCourse(Integer courseId) {
        return slideRepository.findByCourseIdAndIsActiveOrderByDisplayOrderAsc(courseId, true);
    }

    public Slide getSlideById(Integer slideId) {
        return slideRepository.findById(slideId).orElse(null);
    }
}

