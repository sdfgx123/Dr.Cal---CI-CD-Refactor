package com.fc.mini3server.repository;

import com.fc.mini3server.domain.User;
import com.fc.mini3server.domain.Work;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WorkRepository extends JpaRepository<Work, Long> {
    Work findByUserIdAndStartTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);
    List<Work> findByUserIdAndStartTimeBetween(User user, LocalDateTime start, LocalDateTime end);
    Page<Work> findByUserAndStartTimeBetween(User user, LocalDate startOfweek, LocalDate endOfweek, Pageable pageable);
    Optional<Work> findTopByUserIdOrderByStartTimeDesc(Long userId);
}
