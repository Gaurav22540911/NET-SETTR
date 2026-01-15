package com.NET_SETTR.NET_SETTR.controller;

import com.NET_SETTR.NET_SETTR.dto.SyllabusResponse;
import com.NET_SETTR.NET_SETTR.service.SyllabusService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/syllabus")
@CrossOrigin("*")
public class SyllabusController {

    private final SyllabusService syllabusService;

    public SyllabusController(SyllabusService syllabusService) {
        this.syllabusService = syllabusService;
    }

    @GetMapping
    public List<SyllabusResponse> getAllSyllabus() {
        return syllabusService.getAllSyllabus();
    }
}
