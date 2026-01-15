package com.NET_SETTR.NET_SETTR.dto;

public class SyllabusResponse {

    private Integer syllabusId;
    private String name;
    private String fileUrl;

    public SyllabusResponse(Integer syllabusId, String name, String fileUrl) {
        this.syllabusId = syllabusId;
        this.name = name;
        this.fileUrl = fileUrl;
    }

    public Integer getSyllabusId() {
        return syllabusId;
    }

    public String getName() {
        return name;
    }

    public String getFileUrl() {
        return fileUrl;
    }
}
