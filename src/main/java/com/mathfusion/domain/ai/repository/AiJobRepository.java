package com.mathfusion.domain.ai.repository;

import com.mathfusion.domain.ai.entity.AiJob;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AiJobRepository extends JpaRepository<AiJob, Long> {
}