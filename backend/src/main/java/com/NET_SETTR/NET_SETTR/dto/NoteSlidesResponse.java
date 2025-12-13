package com.NET_SETTR.NET_SETTR.dto;

import lombok.Data;
import java.util.List;

@Data
public class NoteSlidesResponse {
    private Long noteId;
    private List<String> slides;

    public Long getNoteId() {
        return noteId;
    }

    public void setNoteId(Long noteId) {
        this.noteId = noteId;
    }

    public List<String> getSlides() {
        return slides;
    }

    public void setSlides(List<String> slides) {
        this.slides = slides;
    }
}
