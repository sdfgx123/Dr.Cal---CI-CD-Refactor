package com.fc.mini3server.repository;

import com.fc.mini3server.domain.CategoryEnum;
import com.fc.mini3server.domain.Hospital;
import com.fc.mini3server.domain.Schedule;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


public interface ScheduleRepositoryCustom {
    Page<Schedule> findByHospitalAndCategoryIsOrderById(Hospital hospital, CategoryEnum category, Pageable pageable);
}
