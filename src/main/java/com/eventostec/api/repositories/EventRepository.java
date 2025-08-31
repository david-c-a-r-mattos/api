package com.eventostec.api.repositories;

import com.eventostec.api.domain.event.Event;
import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;

public interface EventRepository extends JpaRepository<Event, UUID>
{
    
}
