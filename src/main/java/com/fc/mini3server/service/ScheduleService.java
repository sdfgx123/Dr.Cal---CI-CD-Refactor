package com.fc.mini3server.service;

import com.fc.mini3server.domain.Schedule;
import com.fc.mini3server.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    public Page<Schedule> findAnnualList(Pageable pageable) {
        return scheduleRepository.findScheduleByOrderById(pageable);
    }
}
