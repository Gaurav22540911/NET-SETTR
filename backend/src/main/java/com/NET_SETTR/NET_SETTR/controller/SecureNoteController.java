package com.NET_SETTR.NET_SETTR.controller;

import com.NET_SETTR.NET_SETTR.service.SecureNoteService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/notes")
@CrossOrigin("*")
public class SecureNoteController {

    private final SecureNoteService secureNoteService;

    public SecureNoteController(SecureNoteService secureNoteService) {
        this.secureNoteService = secureNoteService;
    }

    /**
     * 🔐 Open note (PDF or first slide) inline
     */
    @GetMapping("/{noteId}/view")
    public void viewNote(
            @PathVariable Long noteId,
            @RequestParam String loginId,
            HttpServletResponse response
    ) {
        secureNoteService.streamNote(noteId, loginId, response);
    }

    /**
     * 🔐 Legacy single-file stream (optional)
     */
    @GetMapping("/stream")
    public void streamNote(
            @RequestParam Long noteId,
            @RequestParam String loginId,
            HttpServletResponse response
    ) {
        secureNoteService.streamNote(noteId, loginId, response);
    }

    /**
     * 🔐 Get slide indexes for a note
     */
    @GetMapping("/{noteId}/slides")
    public List<Integer> getSlideIndexes(
            @PathVariable Long noteId,
            @RequestParam String loginId
    ) {
        return secureNoteService.getSlideIndexes(noteId, loginId);
    }

    /**
     * 🔐 Stream a specific slide
     */
    @GetMapping("/{noteId}/slides/{index}")
    public void streamSlide(
            @PathVariable Long noteId,
            @PathVariable int index,
            @RequestParam String loginId,
            HttpServletResponse response
    ) {
        secureNoteService.streamSlide(noteId, index, loginId, response);
    }
}
