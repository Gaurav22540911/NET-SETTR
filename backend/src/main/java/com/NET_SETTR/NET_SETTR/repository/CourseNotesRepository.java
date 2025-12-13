package com.NET_SETTR.NET_SETTR.repository;

import com.NET_SETTR.NET_SETTR.model.CourseNotes;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CourseNotesRepository extends JpaRepository<CourseNotes, Long> {
    List<CourseNotes> findByCourseId(Long courseId);
}

