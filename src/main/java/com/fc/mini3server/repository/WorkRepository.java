package com.fc.mini3server.repository;

import com.fc.mini3server.domain.Work;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;

public interface WorkRepository extends JpaRepository<Work, Long> {
    Work findByUserIdAndStartTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);
}
