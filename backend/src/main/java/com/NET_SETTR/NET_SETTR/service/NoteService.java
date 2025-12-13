package com.NET_SETTR.NET_SETTR.service;

import com.NET_SETTR.NET_SETTR.dto.NoteResponse;
import com.NET_SETTR.NET_SETTR.dto.NoteSlidesResponse;
import com.NET_SETTR.NET_SETTR.model.Course;
import com.NET_SETTR.NET_SETTR.model.CourseNotes;
import com.NET_SETTR.NET_SETTR.repository.CourseNotesRepository;
import com.NET_SETTR.NET_SETTR.repository.CourseRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class NoteService {

    @Value("${notes.base-path}")
    private String BASE_FOLDER; // E:/NET-SETTR_PROJECT/DOC/

    private final CourseRepository courseRepository;
    private final CourseNotesRepository notesRepository;

    public NoteService(CourseRepository courseRepository,
                       CourseNotesRepository notesRepository) {
        this.courseRepository = courseRepository;
        this.notesRepository = notesRepository;
    }

    // ---------------------------
    // GET NOTES FOR COURSE
    // ---------------------------
    public List<NoteResponse> getNotesForCourse(Long courseId) {
        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        String folderBase = course.getCourseType().replace(" ", "_");

        List<CourseNotes> notes = notesRepository.findByCourseId(courseId);

        List<NoteResponse> result = new ArrayList<>();

        for (CourseNotes note : notes) {

            String folderPath = BASE_FOLDER + folderBase + "/" + note.getFolderName() + "/";

            File dir = new File(folderPath);
            File[] slides = dir.listFiles((d, n) -> n.endsWith(".png"));

            String preview = slides != null && slides.length > 0
                    ? "/DOC/" + folderBase + "/" + note.getFolderName() + "/" + slides[0].getName()
                    : null;

            NoteResponse dto = new NoteResponse();
            dto.setNoteId(note.getId());
            dto.setTitle(note.getNoteTitle());
            dto.setDescription(note.getNoteDescription());
            dto.setFolderName(note.getFolderName());
            dto.setPreviewImage(preview);

            result.add(dto);
        }

        return result;
    }

    // ---------------------------
    // GET SLIDES FOR SPECIFIC NOTE
    // ---------------------------
    public NoteSlidesResponse getSlidesForNote(Long courseId, Long noteId) {

        Course course = courseRepository.findById(courseId)
                .orElseThrow(() -> new RuntimeException("Course not found"));

        CourseNotes note = notesRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        String folderBase = course.getCourseType().replace(" ", "_");
        String folderPath = BASE_FOLDER + folderBase + "/" + note.getFolderName() + "/";

        File dir = new File(folderPath);
        File[] slides = dir.listFiles((d, n) -> n.endsWith(".png"));

        List<String> fileUrls = Arrays.stream(slides)
                .sorted()
                .map(f -> "/DOC/" + folderBase + "/" + note.getFolderName() + "/" + f.getName())
                .toList();

        NoteSlidesResponse response = new NoteSlidesResponse();
        response.setNoteId(noteId);
        response.setSlides(fileUrls);

        return response;
    }
}

