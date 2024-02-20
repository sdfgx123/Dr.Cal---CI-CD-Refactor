package com.fc.mini3server.service;

import com.fc.mini3server.domain.Work;
import com.fc.mini3server.repository.UserRepository;
import com.fc.mini3server.repository.WorkRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.script.DefaultRedisScript;

import java.util.UUID;

@DisplayName("UserService 단위 테스트")
@ExtendWith(MockitoExtension.class)
public class UserServiceTest {

    @InjectMocks
    private ScheduleService scheduleService;

    @Mock
    private UserRepository userRepository;
    @Mock
    private WorkRepository workRepository;
    @Mock
    private RedisTemplate redisTemplate;

    @Test
    void testOnAPIUsingRedis() {
        // given
        Long userId = 142L;
        String lockKey = "lock:" + userId;
        String lockVal = UUID.randomUUID().toString();


        Mockito.when(redisTemplate.opsForValue().setIfAbsent(Mockito.eq(lockKey), Mockito.anyString(), Mockito.any())).thenReturn(true);

        Mockito.when(redisTemplate.execute(
                Mockito.any(DefaultRedisScript.class),
                Mockito.anyList(),
                Mockito.eq(lockVal)
        )).thenReturn(1L);

        // when
        scheduleService.startWorkUsingRedis(userId);

        // then
        Mockito.verify(workRepository, Mockito.times(1)).save(Mockito.any(Work.class));

    }
}
