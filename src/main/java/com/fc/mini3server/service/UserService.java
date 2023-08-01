package com.fc.mini3server.service;

import com.fc.mini3server._core.handler.Message;
import com.fc.mini3server._core.handler.exception.Exception400;
import com.fc.mini3server._core.handler.exception.Exception401;
import com.fc.mini3server._core.security.JwtTokenProvider;
import com.fc.mini3server._core.security.PrincipalUserDetail;
import com.fc.mini3server.domain.Dept;
import com.fc.mini3server.domain.Hospital;
import com.fc.mini3server.domain.StatusEnum;
import com.fc.mini3server.domain.User;
import com.fc.mini3server.dto.UserRequestDTO;
import com.fc.mini3server.repository.DeptRepository;
import com.fc.mini3server.repository.HospitalRepository;
import com.fc.mini3server.repository.UserRepository;
import lombok.RequiredArgsConstructor;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

import static com.fc.mini3server.dto.AdminRequestDTO.*;

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
        Hospital hospital = hospitalRepository.findById(registerDTO.getHospital_id())
                        .orElseThrow(() -> new IllegalArgumentException("invalid hospital id : " + registerDTO.getHospital_id()));
        Dept dept = deptRepository.findById(registerDTO.getDept_id())
                        .orElseThrow(() -> new IllegalArgumentException("invalid dept id : " + registerDTO.getDept_id()));

        Long empNo = initiateEmpNo();
        LocalDate hireDate = LocalDate.now();

        UserRequestDTO.saveDTO saveDTO = UserRequestDTO.saveDTO.builder()
                        .empNo(empNo)
                        .hireDate(hireDate)
                        .email(registerDTO.getEmail())
                        .password(registerDTO.getPassword())
                        .phone(registerDTO.getPhone())
                        .name(registerDTO.getName())
                        .hospital(hospital)
                        .dept(dept)
                        .build();
        userRepository.save(saveDTO.toEntity(passwordEncoder));
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

        return jwtTokenProvider.create(user);
    }

    public User findById(Long id) {
        return userRepository.findById(id)
                .orElseThrow(() -> new Exception400(String.valueOf(id), Message.INVALID_ID_PARAMETER));
    }

    public List<User> findAll() {
        return userRepository.findAll();
    }

    public Page<User> findAll(Pageable pageable){
        return userRepository.findAllByOrderByIdDesc(pageable);
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
