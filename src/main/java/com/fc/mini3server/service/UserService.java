package com.fc.mini3server.service;

import com.fc.mini3server._core.handler.exception.Exception400;
import com.fc.mini3server._core.handler.exception.Exception401;
import com.fc.mini3server._core.handler.exception.Exception500;
import com.fc.mini3server._core.security.JwtTokenProvider;
import com.fc.mini3server._core.security.PrincipalUserDetail;
import com.fc.mini3server.domain.*;
import com.fc.mini3server.dto.UserRequestDTO;
import com.fc.mini3server.dto.UserResponseDTO;
import com.fc.mini3server.repository.*;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Primary;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DayOfWeek;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.Base64;
import java.util.List;
import java.util.stream.Collectors;

import static com.fc.mini3server._core.handler.Message.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final HospitalRepository hospitalRepository;
    private final DeptRepository deptRepository;
    private final WorkRepository workRepository;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    private static final String FILE_DIR = "./images/";
    private static final int MAX_FILE_SIZE = 1024 * 1024;

    public void registerNewUser(UserRequestDTO.registerDTO registerDTO) {
        try {
            Hospital hospital = hospitalRepository.findById(registerDTO.getHospitalId())
                    .orElseThrow(() -> new Exception400(HOSPITAL_NOT_FOUND));
            Dept dept = deptRepository.findById(registerDTO.getDeptId())
                    .orElseThrow(() -> new Exception400(DEPT_NOT_FOUND));

            Long empNo = initiateEmpNo();
            LocalDate hireDate = LocalDate.now();
            int annual = 15;
            int duty = 3;

            UserRequestDTO.saveDTO saveDTO = UserRequestDTO.saveDTO.builder()
                    .empNo(empNo)
                    .hireDate(hireDate)
                    .email(registerDTO.getEmail())
                    .password(registerDTO.getPassword())
                    .phone(registerDTO.getPhone())
                    .name(registerDTO.getName())
                    .hospital(hospital)
                    .dept(dept)
                    .annual(annual)
                    .duty(duty)
                    .level(registerDTO.getLevel())
                    .build();
            userRepository.save(saveDTO.toEntity(passwordEncoder));
            log.info("회원가입 발생 | 이메일 : " + registerDTO.getEmail());
        } catch (IllegalArgumentException e) {
            throw new Exception400(INVALID_REGISTER_FORMAT);
        }
    }

    private Long initiateEmpNo() {
        Long initialEmpNo = 100000001L;
        User lastUser = userRepository.findTopByOrderByEmpNoDesc();
        if (lastUser == null) {
            return initialEmpNo;
        }
        return lastUser.getEmpNo() + 1;
    }

    public String login(UserRequestDTO.loginDTO loginDTO) {

        UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword());
        Authentication authentication = authenticationManager.authenticate(authToken);
        PrincipalUserDetail userDetail = (PrincipalUserDetail) authentication.getPrincipal();
        User user = userDetail.getUser();

        if (user.getStatus() != StatusEnum.APPROVED) {
            throw new Exception401(INVALID_USER_NOT_APPROVED);
        }

        log.info("로그인 발생 | 사용자 구분 | 이메일 : " + user.getAuth() + " | " + user.getEmail());
        return jwtTokenProvider.create(user);
    }

    public void updatePasswordProc(UserRequestDTO.updatePasswordDTO updatePasswordDTO) {
        User user = getUser();
        validateOldPassword(user, updatePasswordDTO.getOldPassword());
        user.changePassword(updatePasswordDTO.getNewPassword(), passwordEncoder);
        userRepository.save(user);
        log.info("비밀번호 변경 발생 | 변경 대상 유저 : " + user.getEmail());
    }

    public User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null) {
            throw new Exception401(INVALID_USER_NOT_APPROVED);
        }
        if (! (authentication.getPrincipal() instanceof PrincipalUserDetail)) {
            throw new Exception400(INVALID_NOT_VALID_TOKEN);
        }
        Long id = ((PrincipalUserDetail) authentication.getPrincipal()).getUser().getId();
        User user = userRepository.findById(id)
                .orElseThrow(() -> new Exception400(INVALID_NO_TOKEN_MATCHED_WITH_USER));
        return user;
    }

//    public Work getWorkInfoWithUser(User user) {
//        LocalDate today = LocalDate.now();
//        Work work = workRepository.findFirstByUserIdAndStartTimeBetween(
//                user.getId(), today.atStartOfDay(), today.atTime(23, 59, 59)
//        );
//        return work;
//    }

    public Work getWorkInfoWithUserWithQueryDSL(User user) {
        LocalDate today = LocalDate.now();
        Work work = workRepository.findFirstWorkByUserIdAndStartTimeBetween(
                user.getId(), today.atStartOfDay(), today.atTime(23, 59, 59)
        );
        return work;
    }

    public UserResponseDTO.MyPageWorkDTO getMyPageWork(User user, Pageable pageable) {
        LocalDate today = LocalDate.now();
        LocalDate startOfWeek = today.with(TemporalAdjusters.previousOrSame(DayOfWeek.MONDAY));
        LocalDate endOfWeek = today.with(TemporalAdjusters.nextOrSame(DayOfWeek.SUNDAY));
        LocalDate startOfMonth = today.withDayOfMonth(1);
        LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());

        String dayWork = parseDuration(calculateWorkTime(user, today, today));
        String weekWork = parseDuration(calculateWorkTime(user, startOfWeek, endOfWeek));
        String monthWork = parseDuration(calculateWorkTime(user, startOfMonth, endOfMonth));

        Page<Work> worksPage = workRepository.findByUserOrderByStartTimeDesc(user, pageable);
        List<UserResponseDTO.WorkDTO> works = worksPage.stream()
                .map(work -> new UserResponseDTO.WorkDTO(work.getStartTime(), work.getEndTime(), parseDuration(calculateWorkTimeForSingleWork(work))))
                .collect(Collectors.toList());

        return new UserResponseDTO.MyPageWorkDTO(dayWork, weekWork, monthWork, works);
    }

    private String parseDuration(Duration duration) {
        Long totalSeconds = duration.getSeconds();
        Long hours = totalSeconds / 3600;
        Long minutes = (totalSeconds % 3600) / 60;
        Long seconds = totalSeconds % 60;

        return String.format("%d:%02d:%02d", hours, minutes, seconds);
    }

    private Duration calculateWorkTimeForSingleWork(Work work) {
        LocalDateTime endTime = work.getEndTime() != null ? work.getEndTime() : LocalDateTime.now();
        return Duration.between(work.getStartTime(), endTime);
    }

    private Duration calculateWorkTime(User user, LocalDate start, LocalDate end) {
        Long userId = user.getId();
        List<Work> works = workRepository.findAllByUserIdAndStartTimeBetween(userId, start.atStartOfDay(), end.atTime(23, 59, 59));

        Duration totalDuration = Duration.ZERO;

        for (Work work : works) {
            LocalDateTime endTime = work.getEndTime() != null ? work.getEndTime() : LocalDateTime.now();
            Duration duration = Duration.between(work.getStartTime(), endTime);
            totalDuration = totalDuration.plus(duration);
        }

        return totalDuration;
    }

    private void validateOldPassword(User user, String oldPassword) {
        if (! passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new Exception400(INVALID_PASSWORD);
        }
    }

    public void updateUserProc(UserRequestDTO.updateUserDTO updateUserDTO) {
        User user = getUser();
        setUpdatedUser(user, updateUserDTO);
        log.info("회원정보 변경됨 | 변경 대상 유저 : " + user.getEmail());
    }

    private void setUpdatedUser(User user, UserRequestDTO.updateUserDTO updateUserDTO) {
        if (updateUserDTO.getName() != null) {
            user.setName(updateUserDTO.getName());
        }
        if (updateUserDTO.getDeptId() != null) {
            Dept dept = deptRepository.findById(updateUserDTO.getDeptId())
                    .orElseThrow(() -> new Exception400(DEPT_NOT_FOUND));
            user.setDept(dept);
        }
        if (updateUserDTO.getPhone() != null) {
            user.setPhone(updateUserDTO.getPhone());
        }
        if (updateUserDTO.getImage() != null) {
            writeImageOnServer(user, updateUserDTO);
        }
    }

    private void writeImageOnServer(User user, UserRequestDTO.updateUserDTO updateUserDTO) {
        try {
            byte[] decodedBytes = Base64.getDecoder().decode(updateUserDTO.getImage());

            if (decodedBytes.length > MAX_FILE_SIZE) {
                throw new Exception500(EXCEED_MAX_FILE_SIZE);
            }

            String fileName = initiateFileName();
            Path destination = Paths.get(FILE_DIR + fileName);
            Files.write(destination, decodedBytes);
            user.setProfileImageUrl(FILE_DIR.replaceFirst("^\\.", "") + fileName);
        } catch (IOException e) {
            throw new Exception500(IO_EXCEPTION_WHEN_FILE_UPLOADING);
        }
    }

    private String initiateFileName() {
        LocalDateTime currentTime = LocalDateTime.now();
        String timeStamp = currentTime.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return timeStamp;
    }
}
