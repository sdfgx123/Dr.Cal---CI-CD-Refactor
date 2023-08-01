package com.fc.mini3server._core.utils;

import com.fc.mini3server.domain.*;
import com.fc.mini3server.repository.DeptRepository;
import com.fc.mini3server.repository.HospitalRepository;
import com.fc.mini3server.repository.ScheduleRepository;
import com.fc.mini3server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.Arrays;

@RequiredArgsConstructor
@Component
public class DBInit {

    private final UserRepository userRepository;
    private final ScheduleRepository scheduleRepository;
    private final HospitalRepository hospitalRepository;
    private final DeptRepository deptRepository;

    @Bean
    CommandLineRunner initDB(){
        return args -> {
            Hospital hospital1 = Hospital.builder()
                    .name("강남세브란스병원")
                    .build();
            Hospital hospital2 = Hospital.builder()
                    .name("서울성모병원")
                    .build();
            hospitalRepository.saveAll(Arrays.asList(hospital1, hospital2));

            Dept dept10 = Dept.builder()
                    .name("간담체외과")
                    .hospital(hospital1)
                    .build();
            Dept dept11 = Dept.builder()
                    .name("소아과")
                    .hospital(hospital1)
                    .build();
            Dept dept12 = Dept.builder()
                    .name("신경외과")
                    .hospital(hospital1)
                    .build();
            Dept dept13 = Dept.builder()
                    .name("정형외과")
                    .hospital(hospital1)
                    .build();
            Dept dept14 = Dept.builder()
                    .name("흉부외과")
                    .hospital(hospital1)
                    .build();

            Dept dept20 = Dept.builder()
                    .name("가정의학과")
                    .hospital(hospital2)
                    .build();
            Dept dept21 = Dept.builder()
                    .name("마취통증외학과")
                    .hospital(hospital2)
                    .build();
            Dept dept22 = Dept.builder()
                    .name("종양내과")
                    .hospital(hospital2)
                    .build();
            Dept dept23 = Dept.builder()
                    .name("진단검사의학과")
                    .hospital(hospital2)
                    .build();

            deptRepository.saveAll(Arrays.asList(
                    dept10, dept11, dept12, dept13, dept14,
                    dept20, dept21, dept22, dept23));

            long empNo = 10000000L;
            User user1 = User.builder()
                    .empNo(++empNo)
                    .name("이익준")
                    .email("ikjun@doctorcal.com")
                    .password("1234")
                    .phone("01012345678")
                    .hospital(hospital1)
                    .dept(dept10)
                    .level(LevelEnum.FELLOW)
                    .auth(AuthEnum.USER)
                    .status(StatusEnum.APPROVED)
                    .annual(20)
                    .duty(3)
                    .hiredDate(LocalDate.of(2002,7,13))
                    .resignedDate(null)
                    .build();

            User user2 = User.builder()
                    .empNo(++empNo)
                    .name("김준완")
                    .email("junwan@doctorcal.com")
                    .password("1234")
                    .phone("01012345678")
                    .hospital(hospital1)
                    .dept(dept14)
                    .level(LevelEnum.FELLOW)
                    .auth(AuthEnum.USER)
                    .status(StatusEnum.APPROVED)
                    .annual(20)
                    .duty(3)
                    .hiredDate(LocalDate.of(2004,11,7))
                    .resignedDate(null)
                    .build();

            User user3 = User.builder()
                    .empNo(++empNo)
                    .name("안정원")
                    .email("jungwon@doctorcal.com")
                    .password("1234")
                    .phone("01012345678")
                    .hospital(hospital1)
                    .dept(dept11)
                    .level(LevelEnum.RESIDENT)
                    .auth(AuthEnum.USER)
                    .status(StatusEnum.APPROVED)
                    .annual(20)
                    .duty(3)
                    .hiredDate(LocalDate.of(2009,3,3))
                    .resignedDate(null)
                    .build();

            userRepository.saveAll(Arrays.asList(user1, user2, user3));

            Schedule schedule1 = Schedule.builder()
                    .user(user1)
                    .hospital(hospital1)
                    .category(CategoryEnum.ANNUAL)
                    .startDate(LocalDate.of(2023, 8, 3))
                    .endDate(LocalDate.of(2023, 8, 3))
                    .evaluation(EvaluationEnum.STANDBY)
                    .reason("휴가")
                    .build();

            Schedule schedule2 = Schedule.builder()
                    .user(user1)
                    .hospital(hospital1)
                    .category(CategoryEnum.ANNUAL)
                    .startDate(LocalDate.of(2023, 8, 14))
                    .endDate(LocalDate.of(2023, 8, 14))
                    .evaluation(EvaluationEnum.STANDBY)
                    .reason("징검다리 휴가")
                    .build();

            Schedule schedule21 = Schedule.builder()
                    .user(user1)
                    .hospital(hospital1)
                    .category(CategoryEnum.DUTY)
                    .startDate(LocalDate.of(2023, 8, 4))
                    .endDate(LocalDate.of(2023, 8, 4))
                    .evaluation(EvaluationEnum.STANDBY)
                    .build();

            Schedule schedule22 = Schedule.builder()
                    .user(user2)
                    .hospital(hospital1)
                    .category(CategoryEnum.DUTY)
                    .startDate(LocalDate.of(2023, 8, 5))
                    .endDate(LocalDate.of(2023, 8, 5))
                    .evaluation(EvaluationEnum.STANDBY)
                    .build();

            scheduleRepository.saveAll(Arrays.asList(schedule1, schedule2,
                    schedule21, schedule22));
        };
    }
}
