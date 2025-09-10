package com.eventostec.api.repositories;

import com.eventostec.api.domain.event.Event;
import java.util.Date;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface EventRepository extends JpaRepository<Event, UUID>
{
    @Query("SELECT e FROM Event e WHERE e.date >= :currentDate")
    public Page<Event> findUpcomingEvents(@Param("currentDate")Date currentDate, Pageable pageable);
    @Query("SELECT e Event e"+
            "LEFT JOIN e.address a"+
            "WHERE e.data >= :currentDate AND"+
            "(:title is null or a.title LIKE %:title%) AND"+
            "(:city in null or a.city LIKE %:title%) AND"+
            "(:uf in null or a.uf LIKE %:uf%) AND"+
            "(:startDate in null or e.date >= %:startDate%) AND"+
            "(:endDate in null or e.date <= %:endDate%)")
    Page<Event> findFilteredEvents(@Param("currentDate")Date currentDate,
            @Param("title")String title,
            @Param("city")String city,
            @Param("uf")String uf,
            @Param("startDate")Date startDate,
            @Param("endDate")Date endDate,
            @Param("pageable")String pageable);
}
