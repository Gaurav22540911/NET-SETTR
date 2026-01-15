package com.NET_SETTR.NET_SETTR.service;

import com.NET_SETTR.NET_SETTR.dto.SyllabusResponse;
import com.NET_SETTR.NET_SETTR.model.Syllabus;
import com.NET_SETTR.NET_SETTR.repository.SyllabusRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class SyllabusService {

    private final SyllabusRepository syllabusRepository;

    public SyllabusService(SyllabusRepository syllabusRepository) {
        this.syllabusRepository = syllabusRepository;
    }

    public List<SyllabusResponse> getAllSyllabus() {
        return syllabusRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private SyllabusResponse mapToResponse(Syllabus syllabus) {
        return new SyllabusResponse(
                syllabus.getSyllabusId(),
                syllabus.getName(),
                syllabus.getFilePath()   // already public URL
        );
    }
}
