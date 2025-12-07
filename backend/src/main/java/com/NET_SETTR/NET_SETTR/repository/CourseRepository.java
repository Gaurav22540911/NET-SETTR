package com.NET_SETTR.NET_SETTR.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import com.NET_SETTR.NET_SETTR.model.Course;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseRepository extends JpaRepository<Course, Long> {

    @Query("SELECT DISTINCT c.courseType FROM Course c WHERE c.isActive = true")
    List<String> findDistinctCourseTypes();

    List<Course> findByCourseType(String courseType);
}


