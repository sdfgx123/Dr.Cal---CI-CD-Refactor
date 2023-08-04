package com.fc.mini3server.service;

import com.fc.mini3server._core.handler.Message;
import com.fc.mini3server._core.handler.exception.Exception400;
import com.fc.mini3server._core.handler.exception.Exception401;
import com.fc.mini3server._core.handler.exception.Exception500;
import com.fc.mini3server._core.security.JwtTokenProvider;
import com.fc.mini3server._core.security.PrincipalUserDetail;
import com.fc.mini3server.domain.*;
import com.fc.mini3server.dto.UserRequestDTO;
import com.fc.mini3server.repository.DeptRepository;
import com.fc.mini3server.repository.HospitalRepository;
import com.fc.mini3server.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.util.List;

import static com.fc.mini3server.dto.AdminRequestDTO.*;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class UserService {

    private final UserRepository userRepository;
    private final HospitalRepository hospitalRepository;
    private final DeptRepository deptRepository;

    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

    private static final String FILE_DIR = "./images/";
    private static final int MAX_FILE_SIZE = 1024 * 1024;

    public void registerNewUser(UserRequestDTO.registerDTO registerDTO) {
        try {
            Hospital hospital = hospitalRepository.findById(registerDTO.getHospital_id())
                    .orElseThrow(() -> new IllegalArgumentException("invalid hospital id : " + registerDTO.getHospital_id()));
            Dept dept = deptRepository.findById(registerDTO.getDept_id())
                    .orElseThrow(() -> new IllegalArgumentException("invalid dept id : " + registerDTO.getDept_id()));

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
        } catch (IllegalArgumentException e) {
            throw new Exception400("요청 형식이 잘못 되었습니다. 올바른 직급, 병원, 또는 부서 번호를 입력 하였는지 확인하십시오.");
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
            throw new Exception401("인증되지 않은 사용자 입니다.");
        }

        log.info("로그인 성공 / 사용자 구분 : " + user.getAuth());

        return jwtTokenProvider.create(user);
    }

    public void updatePasswordProc(UserRequestDTO.updatePasswordDTO updatePasswordDTO) {
        log.info("old : " + updatePasswordDTO.getOld_password());
        log.info("new : " + updatePasswordDTO.getNew_password());
        User user = getUser();
        log.info("user.password : " + user.getPassword());
        validateOldPassword(user, updatePasswordDTO.getOld_password());
        user.changePassword(updatePasswordDTO.getNew_password(), passwordEncoder);
        userRepository.save(user);

    }

    public User getUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        log.info("authentication : " + authentication);
        if (authentication == null) {
            throw new Exception401("인증되지 않은 유저입니다.");
        }
        if (! (authentication.getPrincipal() instanceof PrincipalUserDetail)) {
            throw new Exception400("올바른 토큰이 아닙니다.");
        }
        Long id = ((PrincipalUserDetail) authentication.getPrincipal()).getUser().getId();
        User user = userRepository.findById(id)
                .orElseThrow(() -> new Exception400("토큰 정보와 일치하는 회원이 없습니다."));
        return user;
    }

    private void validateOldPassword(User user, String oldPassword) {
        if (! passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new Exception400("입력하신 비밀번호가 일치하지 않습니다.");
        }
    }

    public void updateUserProc(UserRequestDTO.updateUserDTO updateUserDTO) {
        User user = getUser();
        setUpdatedUser(user, updateUserDTO);
    }

    private void setUpdatedUser(User user, UserRequestDTO.updateUserDTO updateUserDTO) {
        if (updateUserDTO.getName() != null) {
            user.setName(updateUserDTO.getName());
        }
        if (updateUserDTO.getDeptId() != null) {
            Dept dept = deptRepository.findById(updateUserDTO.getDeptId())
                    .orElseThrow(() -> new Exception400("제공한 dept를 통해 찾을 수 있는 부서가 없습니다."));
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
                throw new Exception500("업로드한 이미지의 크기가 1MB를 초과합니다.");
            }

            String fileName = initiateFileName();
            Path destination = Paths.get(FILE_DIR + fileName);
            Files.write(destination, decodedBytes);
            user.setProfileImageUrl(FILE_DIR + fileName);
        } catch (IOException e) {
            throw new Exception500("이미지 파일 저장 중 문제가 발생했습니다.");
        }
    }

    private String initiateFileName() {
        LocalDateTime currentTime = LocalDateTime.now();
        String timeStamp = currentTime.format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
        return "uploadedFile_" + timeStamp;
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new Exception400(String.valueOf(id), Message.INVALID_ID_PARAMETER));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Page<User> findAllUserListAdmin(Pageable pageable){
        return userRepository.findByStatusNot(StatusEnum.NOTAPPROVED, pageable);
    }

    public Page<User> findAllJoinUserListAdmin(Pageable pageable) {
        return userRepository.findByStatusIs(StatusEnum.NOTAPPROVED, pageable);
    }

    @Transactional
    public void updateUserAuth(Long id, editAuthDTO requestDTO) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new Exception400(String.valueOf(id), Message.INVALID_ID_PARAMETER));

        user.updateAuth(requestDTO.getAuth());
    }

    @Transactional
    public void approveUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new Exception400(String.valueOf(id), Message.INVALID_ID_PARAMETER));

        if (!user.getStatus().equals(StatusEnum.NOTAPPROVED))
            throw new Exception400(Message.INVALID_USER_STATUS_NOT_APPROVED);

        user.updateStatus(StatusEnum.APPROVED);
    }

    @Transactional
    public void retireUser(Long id) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new Exception400(String.valueOf(id), Message.INVALID_ID_PARAMETER));

        if (!user.getStatus().equals(StatusEnum.APPROVED))
            throw new Exception400(Message.INVALID_USER_STATUS_APPROVED);

        user.updateStatus(StatusEnum.RETIRED);
    }
}
