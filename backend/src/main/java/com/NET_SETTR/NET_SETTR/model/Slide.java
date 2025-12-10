package com.NET_SETTR.NET_SETTR.model;

import jakarta.persistence.*;

@Entity
@Table(name = "slides")
public class Slide {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer slideId;

    private Integer courseId;
    private String slideTitle;
    private String filePath;
    private Integer slideNumber;
    private String imagePath;
    private Integer displayOrder;
    private Boolean isActive = true;

    // Getters & Setters
    public Integer getSlideId() { return slideId; }
    public void setSlideId(Integer slideId) { this.slideId = slideId; }

    public Integer getCourseId() { return courseId; }
    public void setCourseId(Integer courseId) { this.courseId = courseId; }

    public String getSlideTitle() { return slideTitle; }
    public void setSlideTitle(String slideTitle) { this.slideTitle = slideTitle; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public Integer getSlideNumber() { return slideNumber; }
    public void setSlideNumber(Integer slideNumber) { this.slideNumber = slideNumber; }

    public String getImagePath() { return imagePath; }
    public void setImagePath(String imagePath) { this.imagePath = imagePath; }

    public Integer getDisplayOrder() { return displayOrder; }
    public void setDisplayOrder(Integer displayOrder) { this.displayOrder = displayOrder; }

    public Boolean getActive() { return isActive; }
    public void setActive(Boolean active) { isActive = active; }
}
