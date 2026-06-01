package com.ruttu.project_02_backend.scheduler;

import com.ruttu.project_02_backend.repository.prod.auth.OauthMngtRepository;
import com.ruttu.project_02_backend.repository.prod.auth.TokenMngtRepository;
import com.ruttu.project_02_backend.repository.prod.feed.PostCommentRepository;
import com.ruttu.project_02_backend.repository.prod.feed.PostReportRepository;
import com.ruttu.project_02_backend.repository.prod.feed.PostRepository;
import com.ruttu.project_02_backend.repository.prod.report.UserMonthlyReportRepository;
import com.ruttu.project_02_backend.repository.prod.report.UserWeeklyReportRepository;
import com.ruttu.project_02_backend.repository.prod.routine.UserRoutineRepository;
import com.ruttu.project_02_backend.repository.prod.user.UserAddressRepository;
import com.ruttu.project_02_backend.repository.prod.user.UserRepository;
import com.ruttu.project_02_backend.repository.stats.UserDailyStatsRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class UserCleanupScheduler {

    private final UserRepository userRepository;
    private final OauthMngtRepository oauthMngtRepository;
    private final TokenMngtRepository tokenMngtRepository;
    private final UserAddressRepository userAddressRepository;
    private final UserRoutineRepository userRoutineRepository;
    private final UserDailyStatsRepository userDailyStatsRepository;
    private final UserWeeklyReportRepository userWeeklyReportRepository;
    private final UserMonthlyReportRepository userMonthlyReportRepository;
    private final PostCommentRepository postCommentRepository;
    private final PostReportRepository postReportRepository;
    private final PostRepository postRepository;

    @Scheduled(cron = "0 */1 * * * *") // 매일 0시(자정)에 실행
    @Transactional // 탈퇴 후 30일 이내 로그인하면 데이터 복구, 30일 이후엔 데이터 삭제
    public void cleanupUsers() {

        // 🔥 30일동안 데이터 보관
        Instant limit = Instant.now().minus(30, ChronoUnit.DAYS);

        List<Long> userIds =
                userRepository.findExpiredUserIds(limit);

        if (userIds.isEmpty()) return;

        log.info("삭제 대상 userIds = {}", userIds);

        // 1️⃣ 관련 데이터 bulk 삭제
        oauthMngtRepository.deleteByUserIdIn(userIds);
        tokenMngtRepository.deleteByUserIdIn(userIds);

        userAddressRepository.deleteByUserIdIn(userIds);
        userRoutineRepository.deleteByUserIdIn(userIds);

        userDailyStatsRepository.deleteByUserIdIn(userIds);
        userWeeklyReportRepository.deleteByUserIdIn(userIds);
        userMonthlyReportRepository.deleteByUserIdIn(userIds);

        postCommentRepository.deleteByUserIdIn(userIds);
        postReportRepository.deleteByUserIdIn(userIds);
        postRepository.deleteByUserIdIn(userIds);

        // 2️⃣ 마지막 user 삭제
        userRepository.deleteAllById(userIds);

        log.info("삭제 완료 userIds = {}", userIds);
    }
}