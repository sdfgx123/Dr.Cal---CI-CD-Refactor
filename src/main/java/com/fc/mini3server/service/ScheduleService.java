package com.fc.mini3server.service;

import com.fc.mini3server._core.handler.exception.Exception400;
import com.fc.mini3server.domain.Schedule;
import com.fc.mini3server.dto.AdminRequestDTO;
import com.fc.mini3server.repository.ScheduleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@RequiredArgsConstructor
@Service
public class ScheduleService {
    private final ScheduleRepository scheduleRepository;
    public Page<Schedule> findAnnualList(Pageable pageable) {
        return scheduleRepository.findScheduleByOrderById(pageable);
    }

    @Transactional
    public void updateScheduleEvaluation(Long id, AdminRequestDTO.editEvaluationDTO requestDTO) {
        Schedule schedule = scheduleRepository.findById(id).orElseThrow(
                () -> new Exception400(String.valueOf(id), "해당 아이디가 존재하지 않습니다."));

        schedule.updateEvaluation(requestDTO.getEvaluation());
    }
}
