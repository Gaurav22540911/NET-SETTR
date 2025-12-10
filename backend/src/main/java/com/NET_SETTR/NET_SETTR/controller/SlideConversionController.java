package com.NET_SETTR.NET_SETTR.controller;

import com.NET_SETTR.NET_SETTR.service.PptConversionService;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/slides")
@CrossOrigin("*")
public class SlideConversionController {

    private final PptConversionService pptConversionService;

    public SlideConversionController(PptConversionService pptConversionService) {
        this.pptConversionService = pptConversionService;
    }

    @PostMapping("/convert")
    public Map<String, Object> convertPptToImages(@RequestBody Map<String, String> requestBody) throws Exception {
        Integer courseId = Integer.parseInt(requestBody.get("courseId"));
        String pptPath = requestBody.get("pptPath");

        int totalSlides = pptConversionService.convertPptToImages(courseId, pptPath);

        return Map.of(
                "status", "success",
                "slidesGenerated", totalSlides
        );
    }
}
