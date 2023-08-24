package com.fc.mini3server.service;

import com.fc.mini3server.domain.StatusEnum;
import com.fc.mini3server.domain.User;
import com.fc.mini3server.repository.UserRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@DisplayName("[AdminServiceClass] 서비스 클래스 테스트")
@ExtendWith(MockitoExtension.class)
class AdminServiceTest {

    @InjectMocks
    private AdminService adminService;
    @Mock
    private UserRepository userRepository;

    @Test
    void testApproveUser() {
        Long userId = 1L;
        User user = new User();
        user.setStatus(StatusEnum.NOTAPPROVED);

        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        assertDoesNotThrow(() -> adminService.approveUser(userId));
        assertEquals(StatusEnum.APPROVED, user.getStatus());
    }

}