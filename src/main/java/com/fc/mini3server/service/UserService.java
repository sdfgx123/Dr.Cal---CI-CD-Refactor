package com.fc.mini3server.service;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fc.mini3server._core.handler.Message;
import com.fc.mini3server._core.handler.exception.Exception400;
import com.fc.mini3server._core.handler.exception.Exception401;
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

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static com.fc.mini3server.dto.AdminRequestDTO.*;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final HospitalRepository hospitalRepository;
    private final DeptRepository deptRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;

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

    private User getUser() {
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
                .orElseThrow(() -> new Exception400("입력한 비밀번호와 일치하는 회원이 없습니다."));
        return user;
//        return ((PrincipalUserDetail) authentication.getPrincipal()).getUser();
    }

    private void validateOldPassword(User user, String oldPassword) {
        if (! passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new Exception400("입력하신 비밀번호가 일치하지 않습니다.");
        }
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
    public void updateUserStatus(Long id, editStatusDTO requestDTO) {
        User user = userRepository.findById(id).orElseThrow(
                () -> new Exception400(String.valueOf(id), Message.INVALID_ID_PARAMETER));

        user.updateStatus(requestDTO.getStatus());
    }
}
