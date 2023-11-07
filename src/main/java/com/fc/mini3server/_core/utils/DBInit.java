package com.fc.mini3server._core.utils;

import com.fc.mini3server.domain.*;
import com.fc.mini3server.repository.DeptRepository;
import com.fc.mini3server.repository.HospitalRepository;
import com.fc.mini3server.repository.ScheduleRepository;
import com.fc.mini3server.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
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
    private final PasswordEncoder passwordEncoder;

    //@Bean
    CommandLineRunner initDB(){
        return args -> {
            Hospital hospital1 = Hospital.builder()
                    .name("서울대학교 병원")
                    .build();
            Hospital hospital2 = Hospital.builder()
                    .name("연세 세브란스 병원")
                    .build();
            Hospital hospital3 = Hospital.builder()
                    .name("고려대학교 안암병원")
                    .build();
            hospitalRepository.saveAll(Arrays.asList(hospital1, hospital2, hospital3));

            Dept dept10 = Dept.builder()
                    .name("응급의학과")
                    .hospital(hospital1)
                    .build();
            Dept dept11 = Dept.builder()
                    .name("내과")
                    .hospital(hospital1)
                    .build();
            Dept dept12 = Dept.builder()
                    .name("외과")
                    .hospital(hospital1)
                    .build();
            Dept dept13 = Dept.builder()
                    .name("산부인과")
                    .hospital(hospital1)
                    .build();
            Dept dept14 = Dept.builder()
                    .name("가정의학과")
                    .hospital(hospital1)
                    .build();
            Dept dept15 = Dept.builder()
                    .name("피부과")
                    .hospital(hospital1)
                    .build();
            Dept dept16 = Dept.builder()
                    .name("마취통증의학과")
                    .hospital(hospital1)
                    .build();
            Dept dept17 = Dept.builder()
                    .name("안과")
                    .hospital(hospital1)
                    .build();
            Dept dept18 = Dept.builder()
                    .name("이비인후과")
                    .hospital(hospital1)
                    .build();
            Dept dept19 = Dept.builder()
                    .name("신경외과")
                    .hospital(hospital1)
                    .build();
            Dept dept20 = Dept.builder()
                    .name("정신건강의학과")
                    .hospital(hospital1)
                    .build();

            Dept dept21 = Dept.builder()
                    .name("응급의학과")
                    .hospital(hospital2)
                    .build();
            Dept dept22 = Dept.builder()
                    .name("내과")
                    .hospital(hospital2)
                    .build();
            Dept dept23 = Dept.builder()
                    .name("외과")
                    .hospital(hospital2)
                    .build();
            Dept dept24 = Dept.builder()
                    .name("산부인과")
                    .hospital(hospital2)
                    .build();
            Dept dept25 = Dept.builder()
                    .name("성형외과")
                    .hospital(hospital2)
                    .build();
            Dept dept26 = Dept.builder()
                    .name("신경과")
                    .hospital(hospital2)
                    .build();
            Dept dept27 = Dept.builder()
                    .name("영상의학과")
                    .hospital(hospital2)
                    .build();
            Dept dept28 = Dept.builder()
                    .name("이비인후과")
                    .hospital(hospital2)
                    .build();
            Dept dept29 = Dept.builder()
                    .name("정형외과")
                    .hospital(hospital2)
                    .build();
            Dept dept30 = Dept.builder()
                    .name("피부과")
                    .hospital(hospital2)
                    .build();
            Dept dept31 = Dept.builder()
                    .name("피부과")
                    .hospital(hospital2)
                    .build();

            Dept dept32 = Dept.builder()
                    .name("응급의학과")
                    .hospital(hospital3)
                    .build();
            Dept dept33 = Dept.builder()
                    .name("소화기내과")
                    .hospital(hospital3)
                    .build();
            Dept dept34 = Dept.builder()
                    .name("흉부외과")
                    .hospital(hospital3)
                    .build();
            Dept dept35 = Dept.builder()
                    .name("정신건강의학과")
                    .hospital(hospital3)
                    .build();
            Dept dept36 = Dept.builder()
                    .name("순환기내과")
                    .hospital(hospital3)
                    .build();
            Dept dept37 = Dept.builder()
                    .name("간담췌외과")
                    .hospital(hospital3)
                    .build();
            Dept dept38 = Dept.builder()
                    .name("산부인과")
                    .hospital(hospital3)
                    .build();
            Dept dept39 = Dept.builder()
                    .name("신경과")
                    .hospital(hospital3)
                    .build();
            Dept dept40 = Dept.builder()
                    .name("정형외과")
                    .hospital(hospital3)
                    .build();
            Dept dept41 = Dept.builder()
                    .name("핵의학과")
                    .hospital(hospital3)
                    .build();
            Dept dept42 = Dept.builder()
                    .name("가정의학과")
                    .hospital(hospital3)
                    .build();

            deptRepository.saveAll(Arrays.asList(
                    dept10, dept11, dept12, dept13, dept14, dept15, dept16, dept17, dept18, dept19, dept20,
                    dept21, dept22, dept23, dept24, dept25, dept26, dept27, dept28, dept29, dept30,
                    dept31, dept32, dept33, dept34, dept35, dept36, dept37, dept38, dept39, dept40,
                    dept41, dept42
                    ));

            long empNo = 10000000L;
            User user01 = User.builder()
                    .empNo(++empNo)
                    .name("서울대관리자")
                    .email("admin@doctorcal.com")
                    .password(passwordEncoder.encode("1234"))
                    .phone("01012345678")
                    .hospital(hospital1)
                    .dept(dept10)
                    .level(LevelEnum.INTERN)
                    .auth(AuthEnum.ADMIN)
                    .status(StatusEnum.APPROVED)
                    .annual(15)
                    .duty(3)
                    .hiredDate(LocalDate.of(2023,8,10))
                    .resignedDate(null)
                    .build();
            User user02 = User.builder()
                    .empNo(++empNo)
                    .name("연세대관리자")
                    .email("admin2@doctorcal.com")
                    .password(passwordEncoder.encode("1234"))
                    .phone("01012345678")
                    .hospital(hospital2)
                    .dept(dept21)
                    .level(LevelEnum.INTERN)
                    .auth(AuthEnum.ADMIN)
                    .status(StatusEnum.APPROVED)
                    .annual(15)
                    .duty(3)
                    .hiredDate(LocalDate.of(2023,8,10))
                    .resignedDate(null)
                    .build();
            User user03 = User.builder()
                    .empNo(++empNo)
                    .name("고려대관리자")
                    .email("admin3@doctorcal.com")
                    .password(passwordEncoder.encode("1234"))
                    .phone("01012345678")
                    .hospital(hospital3)
                    .dept(dept32)
                    .level(LevelEnum.INTERN)
                    .auth(AuthEnum.ADMIN)
                    .status(StatusEnum.APPROVED)
                    .annual(15)
                    .duty(3)
                    .hiredDate(LocalDate.of(2023,8,10))
                    .resignedDate(null)
                    .build();
            User user1 = User.builder()
                    .empNo(++empNo)
                    .name("이익준")
                    .email("ikjun@doctorcal.com")
                    .password(passwordEncoder.encode("1234"))
                    .phone("01012345678")
                    .hospital(hospital1)
                    .dept(dept10)
                    .level(LevelEnum.FELLOW)
                    .auth(AuthEnum.USER)
                    .status(StatusEnum.APPROVED)
                    .annual(15)
                    .duty(3)
                    .hiredDate(LocalDate.of(2002,7,13))
                    .resignedDate(null)
                    .build();
            User user2 = User.builder()
                    .empNo(++empNo)
                    .name("김준완")
                    .email("junwan@doctorcal.com")
                    .password(passwordEncoder.encode("1234"))
                    .phone("01012345678")
                    .hospital(hospital1)
                    .dept(dept14)
                    .level(LevelEnum.FELLOW)
                    .auth(AuthEnum.USER)
                    .status(StatusEnum.APPROVED)
                    .annual(15)
                    .duty(3)
                    .hiredDate(LocalDate.of(2004,11,7))
                    .resignedDate(null)
                    .build();
            User user3 = User.builder()
                    .empNo(++empNo)
                    .name("안정원")
                    .email("jungwon@doctorcal.com")
                    .password(passwordEncoder.encode("1234"))
                    .phone("01012345678")
                    .hospital(hospital1)
                    .dept(dept11)
                    .level(LevelEnum.RESIDENT)
                    .auth(AuthEnum.USER)
                    .status(StatusEnum.APPROVED)
                    .annual(15)
                    .duty(3)
                    .hiredDate(LocalDate.of(2009,3,3))
                    .resignedDate(null)
                    .build();

            User user4 = User.builder()
                    .empNo(++empNo)
                    .name("장윤복")
                    .email("younbok@doctorcal.com")
                    .password(passwordEncoder.encode("1234"))
                    .phone("01011115228")
                    .hospital(hospital1)
                    .dept(dept10)
                    .level(LevelEnum.PK)
                    .auth(AuthEnum.USER)
                    .status(StatusEnum.NOTAPPROVED)
                    .annual(15)
                    .duty(3)
                    .hiredDate(LocalDate.of(2023,8,3))
                    .resignedDate(null)
                    .build();
            User user5 = User.builder()
                    .empNo(++empNo)
                    .name("장홍도")
                    .email("hongdojjang@doctorcal.com")
                    .password(passwordEncoder.encode("1234"))
                    .phone("01022225228")
                    .hospital(hospital1)
                    .dept(dept10)
                    .level(LevelEnum.PK)
                    .auth(AuthEnum.USER)
                    .status(StatusEnum.NOTAPPROVED)
                    .annual(15)
                    .duty(3)
                    .hiredDate(LocalDate.of(2023,8,3))
                    .resignedDate(null)
                    .build();
            User user6 = User.builder()
                    .empNo(++empNo)
                    .name("정로사")
                    .email("losajeong@doctorcal.com")
                    .password(passwordEncoder.encode("1234"))
                    .phone("01024425123")
                    .hospital(hospital1)
                    .dept(dept10)
                    .level(LevelEnum.FELLOW)
                    .auth(AuthEnum.USER)
                    .status(StatusEnum.RETIRED)
                    .annual(15)
                    .duty(3)
                    .hiredDate(LocalDate.of(1998,1,2))
                    .resignedDate(LocalDate.of(2020, 12,30))
                    .build();

            User user7 = User.builder()
                    .empNo(++empNo)
                    .name("양석형")
                    .email("sukhyung@doctorcal.com")
                    .password(passwordEncoder.encode("1234"))
                    .phone("01012345678")
                    .hospital(hospital2)
                    .dept(dept21)
                    .level(LevelEnum.FELLOW)
                    .auth(AuthEnum.USER)
                    .status(StatusEnum.APPROVED)
                    .annual(15)
                    .duty(3)
                    .hiredDate(LocalDate.of(2002,7,14))
                    .resignedDate(null)
                    .build();
            User user8 = User.builder()
                    .empNo(++empNo)
                    .name("채송화")
                    .email("songhwa@doctorcal.com")
                    .password(passwordEncoder.encode("1234"))
                    .phone("01012345678")
                    .hospital(hospital2)
                    .dept(dept22)
                    .level(LevelEnum.FELLOW)
                    .auth(AuthEnum.USER)
                    .status(StatusEnum.APPROVED)
                    .annual(15)
                    .duty(3)
                    .hiredDate(LocalDate.of(2003,11,7))
                    .resignedDate(null)
                    .build();
            User user9 = User.builder()
                    .empNo(++empNo)
                    .name("장겨울")
                    .email("winter@doctorcal.com")
                    .password(passwordEncoder.encode("1234"))
                    .phone("01012345678")
                    .hospital(hospital2)
                    .dept(dept23)
                    .level(LevelEnum.RESIDENT)
                    .auth(AuthEnum.USER)
                    .status(StatusEnum.APPROVED)
                    .annual(15)
                    .duty(3)
                    .hiredDate(LocalDate.of(2015,3,2))
                    .resignedDate(null)
                    .build();
            User user10 = User.builder()
                    .empNo(++empNo)
                    .name("이익주")
                    .email("ikjoo@doctorcal.com")
                    .password(passwordEncoder.encode("1234"))
                    .phone("01012345678")
                    .hospital(hospital1)
                    .dept(dept10)
                    .level(LevelEnum.FELLOW)
                    .auth(AuthEnum.USER)
                    .status(StatusEnum.APPROVED)
                    .annual(15)
                    .duty(3)
                    .hiredDate(LocalDate.of(2002,7,15))
                    .resignedDate(null)
                    .build();
            User user11 = User.builder()
                    .empNo(++empNo)
                    .name("김준수")
                    .email("junsoo@doctorcal.com")
                    .password(passwordEncoder.encode("1234"))
                    .phone("01012345678")
                    .hospital(hospital1)
                    .dept(dept14)
                    .level(LevelEnum.PK)
                    .auth(AuthEnum.USER)
                    .status(StatusEnum.APPROVED)
                    .annual(15)
                    .duty(3)
                    .hiredDate(LocalDate.of(2014,11,7))
                    .resignedDate(null)
                    .build();
            User user12 = User.builder()
                    .empNo(++empNo)
                    .name("도재학")
                    .email("jaehack@doctorcal.com")
                    .password(passwordEncoder.encode("1234"))
                    .phone("01012345678")
                    .hospital(hospital1)
                    .dept(dept11)
                    .level(LevelEnum.RESIDENT)
                    .auth(AuthEnum.USER)
                    .status(StatusEnum.APPROVED)
                    .annual(15)
                    .duty(3)
                    .hiredDate(LocalDate.of(2005,3,5))
                    .resignedDate(null)
                    .build();
            User user13 = User.builder()
                    .empNo(++empNo)
                    .name("김다영")
                    .email("dayoung@doctorcal.com")
                    .password(passwordEncoder.encode("1234"))
                    .phone("01012345678")
                    .hospital(hospital1)
                    .dept(dept10)
                    .level(LevelEnum.RESIDENT)
                    .auth(AuthEnum.USER)
                    .status(StatusEnum.APPROVED)
                    .annual(15)
                    .duty(3)
                    .hiredDate(LocalDate.of(2012,7,30))
                    .resignedDate(null)
                    .build();
            User user14 = User.builder()
                    .empNo(++empNo)
                    .name("윤가희")
                    .email("gahui@doctorcal.com")
                    .password(passwordEncoder.encode("1234"))
                    .phone("01012345678")
                    .hospital(hospital1)
                    .dept(dept14)
                    .level(LevelEnum.INTERN)
                    .auth(AuthEnum.USER)
                    .status(StatusEnum.APPROVED)
                    .annual(15)
                    .duty(3)
                    .hiredDate(LocalDate.of(2023,1,7))
                    .resignedDate(null)
                    .build();
            User user15 = User.builder()
                    .empNo(++empNo)
                    .name("한현희")
                    .email("hyunhui@doctorcal.com")
                    .password(passwordEncoder.encode("1234"))
                    .phone("01012345678")
                    .hospital(hospital1)
                    .dept(dept11)
                    .level(LevelEnum.RESIDENT)
                    .auth(AuthEnum.USER)
                    .status(StatusEnum.APPROVED)
                    .annual(15)
                    .duty(3)
                    .hiredDate(LocalDate.of(2019,3,3))
                    .resignedDate(null)
                    .build();

            userRepository.saveAll(Arrays.asList( user01, user02, user03,
                    user1, user2, user3, user4, user5, user6, user7, user8, user9,
                    user10, user11, user12, user13, user14, user15));

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
            Schedule schedule3 = Schedule.builder()
                    .user(user3)
                    .hospital(hospital1)
                    .category(CategoryEnum.ANNUAL)
                    .startDate(LocalDate.of(2023, 8, 3))
                    .endDate(LocalDate.of(2023, 8, 5))
                    .evaluation(EvaluationEnum.APPROVED)
                    .reason("휴가")
                    .build();
            Schedule schedule4 = Schedule.builder()
                    .user(user4)
                    .hospital(hospital1)
                    .category(CategoryEnum.ANNUAL)
                    .startDate(LocalDate.of(2023, 8, 4))
                    .endDate(LocalDate.of(2023, 8, 4))
                    .evaluation(EvaluationEnum.APPROVED)
                    .reason("휴가")
                    .build();
            Schedule schedule5 = Schedule.builder()
                    .user(user5)
                    .hospital(hospital1)
                    .category(CategoryEnum.ANNUAL)
                    .startDate(LocalDate.of(2023, 8, 5))
                    .endDate(LocalDate.of(2023, 8, 5))
                    .evaluation(EvaluationEnum.APPROVED)
                    .reason("휴가")
                    .build();
            Schedule schedule6 = Schedule.builder()
                    .user(user7)
                    .hospital(hospital2)
                    .category(CategoryEnum.ANNUAL)
                    .startDate(LocalDate.of(2023, 8, 3))
                    .endDate(LocalDate.of(2023, 8, 5))
                    .evaluation(EvaluationEnum.APPROVED)
                    .reason("휴가")
                    .build();
            Schedule schedule7 = Schedule.builder()
                    .user(user8)
                    .hospital(hospital2)
                    .category(CategoryEnum.ANNUAL)
                    .startDate(LocalDate.of(2023, 8, 4))
                    .endDate(LocalDate.of(2023, 8, 4))
                    .evaluation(EvaluationEnum.APPROVED)
                    .reason("휴가")
                    .build();
            Schedule schedule8 = Schedule.builder()
                    .user(user9)
                    .hospital(hospital2)
                    .category(CategoryEnum.ANNUAL)
                    .startDate(LocalDate.of(2023, 8, 5))
                    .endDate(LocalDate.of(2023, 8, 5))
                    .evaluation(EvaluationEnum.APPROVED)
                    .reason("휴가")
                    .build();

            Schedule schedule21 = Schedule.builder()
                    .user(user1)
                    .hospital(hospital1)
                    .category(CategoryEnum.DUTY)
                    .startDate(LocalDate.of(2023, 8, 4))
                    .endDate(LocalDate.of(2023, 8, 4))
                    .evaluation(EvaluationEnum.APPROVED)
                    .build();

            Schedule schedule22 = Schedule.builder()
                    .user(user2)
                    .hospital(hospital1)
                    .category(CategoryEnum.DUTY)
                    .startDate(LocalDate.of(2023, 8, 5))
                    .endDate(LocalDate.of(2023, 8, 5))
                    .evaluation(EvaluationEnum.APPROVED)
                    .build();
            Schedule schedule23 = Schedule.builder()
                    .user(user7)
                    .hospital(hospital2)
                    .category(CategoryEnum.DUTY)
                    .startDate(LocalDate.of(2023, 8, 5))
                    .endDate(LocalDate.of(2023, 8, 5))
                    .evaluation(EvaluationEnum.APPROVED)
                    .build();
            Schedule schedule24 = Schedule.builder()
                    .user(user7)
                    .hospital(hospital1)
                    .category(CategoryEnum.DUTY)
                    .startDate(LocalDate.of(2023, 8, 6))
                    .endDate(LocalDate.of(2023, 8, 6))
                    .evaluation(EvaluationEnum.APPROVED)
                    .build();

            Schedule schedule27 = Schedule.builder()
                    .user(user1)
                    .hospital(hospital1)
                    .category(CategoryEnum.DUTY)
                    .startDate(LocalDate.of(2023, 8, 9))
                    .endDate(LocalDate.of(2023, 8, 9))
                    .evaluation(EvaluationEnum.APPROVED)
                    .build();
            Schedule schedule28 = Schedule.builder()
                    .user(user2)
                    .hospital(hospital1)
                    .category(CategoryEnum.DUTY)
                    .startDate(LocalDate.of(2023, 8, 10))
                    .endDate(LocalDate.of(2023, 8, 10))
                    .evaluation(EvaluationEnum.APPROVED)
                    .build();
            Schedule schedule29 = Schedule.builder()
                    .user(user1)
                    .hospital(hospital1)
                    .category(CategoryEnum.DUTY)
                    .startDate(LocalDate.of(2023, 8, 9))
                    .endDate(LocalDate.of(2023, 8, 10))
                    .evaluation(EvaluationEnum.STANDBY)
                    .build();

            scheduleRepository.saveAll(Arrays.asList(schedule1, schedule2, schedule3, schedule4, schedule5,
                    schedule6, schedule7, schedule8,
                    schedule21, schedule22, schedule23, schedule24,
                    schedule27, schedule28, schedule29));
        };
    }
}
