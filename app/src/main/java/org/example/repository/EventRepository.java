package org.example.repository;

import org.example.entity.EventEntity;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EventRepository extends JpaRepository<EventEntity, Long> {
    Optional<List<EventEntity>> findAll(Specification<EventEntity> spec);
}
