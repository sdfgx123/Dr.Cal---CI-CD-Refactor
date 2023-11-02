package com.fc.mini3server.repository;

import com.fc.mini3server.domain.User;
import com.fc.mini3server.domain.Work;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface WorkRepository extends JpaRepository<Work, Long>, WorkRepositoryCustom {
    Work findFirstByUserIdAndStartTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);
    List<Work> findAllByUserIdAndStartTimeBetween(Long userId, LocalDateTime start, LocalDateTime end);
    Page<Work> findByUserAndStartTimeBetween(User user, LocalDateTime startOfweek, LocalDateTime endOfweek, Pageable pageable);
    Optional<Work> findTopByUserIdOrderByStartTimeDesc(Long userId);
}
