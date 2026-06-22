package com.mathfusion.domain.ai.repository;

import com.mathfusion.domain.ai.entity.AiJob;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AiJobRepository extends JpaRepository<AiJob, Long> {
    Optional<AiJob> findByIdAndUserEmail(Long id, String email);

    @EntityGraph(attributePaths = "user")
    Optional<AiJob> findWithUserById(Long id);
}
