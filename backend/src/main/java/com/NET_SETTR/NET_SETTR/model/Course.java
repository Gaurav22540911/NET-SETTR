package com.NET_SETTR.NET_SETTR.model;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "courses")
public class Course {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long course_id;

    @Column(name ="course_name")
    private String courseName;

    @Column(length = 1000)
    private String course_description;

    @Column(name = "course_type")
    private String courseType;

    private String image_url;

    private Boolean isActive;

    @Column(precision = 10, scale = 2)
    private BigDecimal amount;

//    @Column(name = "active_device_id")
//    private String activeDeviceId;

//    @Column(name = "last_device_login_at")
//    private LocalDateTime lastDeviceLoginAt;

// getters + setters

    public Long getCourse_id() {
        return course_id;
    }

    public void setCourse_id(Long course_id) {
        this.course_id = course_id;
    }

    public String getCourse_description() {
        return course_description;
    }

    public void setCourse_description(String course_description) {
        this.course_description = course_description;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }

    public Boolean getActive() {
        return isActive;
    }

    public void setActive(Boolean active) {
        isActive = active;
    }

    public String getCourseName() {
        return courseName;
    }

    public void setCourseName(String courseName) {
        this.courseName = courseName;
    }

    public String getCourseType() {
        return courseType;
    }

    public void setCourseType(String courseType) {
        this.courseType = courseType;
    }

//    public String getActiveDeviceId() {
//        return activeDeviceId;
//    }
//
//    public void setActiveDeviceId(String activeDeviceId) {
//        this.activeDeviceId = activeDeviceId;
//    }

//    public LocalDateTime getLastDeviceLoginAt() {
//        return lastDeviceLoginAt;
//    }
//
//    public void setLastDeviceLoginAt(LocalDateTime lastDeviceLoginAt) {
//        this.lastDeviceLoginAt = lastDeviceLoginAt;
   // }
}
//