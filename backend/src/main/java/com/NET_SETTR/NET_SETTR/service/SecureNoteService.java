package com.NET_SETTR.NET_SETTR.service;

import com.NET_SETTR.NET_SETTR.model.Course;
import com.NET_SETTR.NET_SETTR.model.CourseNotes;
import com.NET_SETTR.NET_SETTR.model.Subscription;
import com.NET_SETTR.NET_SETTR.model.User;
import com.NET_SETTR.NET_SETTR.repository.CourseNotesRepository;
import com.NET_SETTR.NET_SETTR.repository.CourseRepository;
import com.NET_SETTR.NET_SETTR.repository.SubscriptionRepository;
import com.NET_SETTR.NET_SETTR.repository.UserRepository;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.*;
import java.nio.file.Files;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class SecureNoteService {

    @Value("${notes.base-path}")
    private String BASE_FOLDER; // E:/NET-SETTR_PROJECT/DOC/

    private final CourseNotesRepository notesRepository;
    private final CourseRepository courseRepository;
    private final SubscriptionRepository subscriptionRepository;
    private final UserRepository userRepository;

    public SecureNoteService(
            CourseNotesRepository notesRepository,
            CourseRepository courseRepository,
            SubscriptionRepository subscriptionRepository,
            UserRepository userRepository
    ) {
        this.notesRepository = notesRepository;
        this.courseRepository = courseRepository;
        this.subscriptionRepository = subscriptionRepository;
        this.userRepository = userRepository;
    }

    /**
     * Core secure streaming logic
     */
    public void streamNote(Long noteId, String loginId, HttpServletResponse response) {

        // -----------------------------
        // 1. Resolve user
        // -----------------------------
        User user = loginId.contains("@")
                ? userRepository.findByEmail(loginId)
                .orElseThrow(() -> new RuntimeException("User not found"))
                : userRepository.findByPhoneNo(loginId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // -----------------------------
        // 2. Resolve note + course
        // -----------------------------
        CourseNotes note = notesRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        Course course = courseRepository.findById(note.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // -----------------------------
        // 3. Validate subscription
        // -----------------------------
        Subscription sub = subscriptionRepository
                .findTopByUserIdAndCourseIdOrderByCreatedAtDesc(
                        user.getUserId(),
                        Math.toIntExact(course.getCourse_id())
                )
                .orElseThrow(() -> new RuntimeException("No subscription"));

        if (sub.getEndDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Subscription expired");
        }

        // -----------------------------
        // 4. Resolve actual file path
        // -----------------------------
        String folderBase = course.getCourseType().replace(" ", "_");
        String noteFolder = note.getFolderName();

        File folder = new File(BASE_FOLDER + folderBase + "/" + noteFolder + "/");

        File[] files = folder.listFiles((d, name) ->
                name.endsWith(".pdf") || name.endsWith(".png")
        );

        if (files == null || files.length == 0) {
            throw new RuntimeException("No note file found");
        }

        File file = files[0]; // single PDF or first image

        // -----------------------------
        // 5. Stream file securely
        // -----------------------------
        try {
            response.setContentType(Files.probeContentType(file.toPath()));
            response.setHeader(
                    "Content-Disposition",
                    "inline; filename=\"" + file.getName() + "\""
            );
            response.setContentLengthLong(file.length());

            try (InputStream in = new FileInputStream(file);
                 OutputStream out = response.getOutputStream()) {

                in.transferTo(out);
                out.flush();
            }

        } catch (IOException e) {
            throw new RuntimeException("Failed to stream note", e);
        }
    }

    public List<Integer> getSlideIndexes(Long noteId, String loginId) {

        validateUserAndSubscription(noteId, loginId);

        File folder = resolveNoteFolder(noteId);

        File[] slides = folder.listFiles((d, n) -> n.endsWith(".png"));
        if (slides == null) return List.of();

        return IntStream.range(0, slides.length)
                .boxed()
                .toList();
    }

    public void streamSlide(
            Long noteId,
            int index,
            String loginId,
            HttpServletResponse response
    ) {

        validateUserAndSubscription(noteId, loginId);

        File folder = resolveNoteFolder(noteId);
        File[] slides = folder.listFiles((d, n) -> n.endsWith(".png"));

        if (slides == null || index >= slides.length) {
            throw new RuntimeException("Slide not found");
        }

        File file = slides[index];

        try {
            response.setContentType("image/png");
            response.setHeader("Content-Disposition", "inline");
            response.setContentLengthLong(file.length());

            Files.copy(file.toPath(), response.getOutputStream());
            response.flushBuffer();

        } catch (IOException e) {
            throw new RuntimeException("Streaming failed", e);
        }
    }

    private void validateUserAndSubscription(Long noteId, String loginId) {

        // 1️⃣ Resolve user
        User user = loginId.contains("@")
                ? userRepository.findByEmail(loginId)
                .orElseThrow(() -> new RuntimeException("User not found"))
                : userRepository.findByPhoneNo(loginId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        // 2️⃣ Resolve note
        CourseNotes note = notesRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        // 3️⃣ Resolve course
        Course course = courseRepository.findById(note.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // 4️⃣ Validate subscription (latest record)
        Subscription sub = subscriptionRepository
                .findTopByUserIdAndCourseIdOrderByCreatedAtDesc(
                        user.getUserId(),
                        Math.toIntExact(course.getCourse_id())
                )
                .orElseThrow(() -> new RuntimeException("No subscription"));

        if (sub.getEndDate().isBefore(LocalDateTime.now())) {
            throw new RuntimeException("Subscription expired");
        }
    }

    private File resolveNoteFolder(Long noteId) {

        // 1️⃣ Resolve note
        CourseNotes note = notesRepository.findById(noteId)
                .orElseThrow(() -> new RuntimeException("Note not found"));

        // 2️⃣ Resolve course
        Course course = courseRepository.findById(note.getCourseId())
                .orElseThrow(() -> new RuntimeException("Course not found"));

        // 3️⃣ Build folder path using application.properties
        String folderBase = course.getCourseType().replace(" ", "_");
        String noteFolder = note.getFolderName();

        File folder = new File(
                BASE_FOLDER + folderBase + "/" + noteFolder + "/"
        );

        if (!folder.exists() || !folder.isDirectory()) {
            throw new RuntimeException("Note folder not found on disk");
        }

        return folder;
    }


}
