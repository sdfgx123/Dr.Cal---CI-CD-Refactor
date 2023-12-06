package com.fc.mini3server.service;

import com.fc.mini3server.domain.Work;
import com.fc.mini3server.repository.WorkRepository;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.assertj.core.api.Assertions.assertThat;


@SpringBootTest
@Transactional
@DisplayName("스케줄 서비스 테스트")
public class ScheduleServiceTest {

    @Autowired
    EntityManager em;
    @Autowired
    JPAQueryFactory queryFactory;
    @Autowired
    ScheduleService scheduleService;
    @Autowired
    AdminService adminService;
    @Autowired
    WorkRepository workRepository;

    @Test
    @DisplayName("레디스 분산 락을 구현 후 동시성 이슈 테스트")
    public void testStartWorkUsingRedisWithLock() throws InterruptedException {
        Long userId = 142L;
        int threadCnt = 10;
        ExecutorService service = Executors.newFixedThreadPool(threadCnt);
        CountDownLatch latch = new CountDownLatch(threadCnt);

        for (int i=0; i<threadCnt; i++) {
            service.execute(() -> {
                try {
                    scheduleService.startWorkUsingRedis(userId);
                } finally {
                    latch.countDown();
                }
            });
        }
        System.out.println("ON EXECUTE DONE");

        latch.await();
        service.shutdown();

        List<Work> works = workRepository.findByUserId(userId);
        if (works == null) {
            System.out.println("NULL");

        }
        int expectedCnt = 1;

        assertThat(works.size()).isEqualTo(expectedCnt);
    }



}
