package com.NET_SETTR.NET_SETTR.repository;

import com.NET_SETTR.NET_SETTR.model.Slide;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SlideRepository extends JpaRepository<Slide, Integer> {

    List<Slide> findByCourseIdAndIsActiveOrderByDisplayOrderAsc(Integer courseId, Boolean isActive);

    List<Slide> findByCourseIdOrderByDisplayOrderAsc(Integer courseId);

}

