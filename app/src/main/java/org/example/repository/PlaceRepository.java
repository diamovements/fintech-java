package org.example.repository;

import org.example.entity.PlaceEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface PlaceRepository extends JpaRepository<PlaceEntity, Long> {
    @Query("SELECT p FROM PlaceEntity p LEFT JOIN FETCH p.events WHERE p.id = :id")
    PlaceEntity findByIdWithEvents(Long id);
}
