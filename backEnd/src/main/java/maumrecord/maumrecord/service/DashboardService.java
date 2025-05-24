package maumrecord.maumrecord.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import maumrecord.maumrecord.domain.*;
import maumrecord.maumrecord.dto.*;
import maumrecord.maumrecord.repository.*;
import org.springframework.stereotype.Service;



import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class DashboardService {
    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;
    private final UserInquiryRepository userInquiryRepository;
    private final UserActivityLogRepository userActivityLogRepository;
    private final HealingRepository healingRepository;
    private final HealingFeedbackRepository feedbackRepo;


    // 관리자 대시보드 화면 필요 데이터
    public DashboardResponse getAdminDashboardData() {

        LocalDate today = LocalDate.now();
        LocalDateTime startOfDay = today.atStartOfDay();
        LocalDateTime endOfDay = today.plusDays(1).atStartOfDay();

        // Stats
        DashboardResponse.Stats stats = DashboardResponse.Stats.builder()
                .userCount(userRepository.count())
                .todayJournalCount(diaryRepository.countByCreatedAtBetween(startOfDay, endOfDay))
                .unansweredCount(userInquiryRepository.countByStatus(UserInquiry.InquiryStatus.PENDING))
                .build();

        // User Trends
        List<DashboardResponse.UserTrendDto> userTrends = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay();
            userTrends.add(DashboardResponse.UserTrendDto.builder()
                    .date(date.toString())
                    .signups(userRepository.countByCreatedAtBetween(start, end))
                    .activeUsers(userActivityLogRepository.countDistinctUsersByActivityTimeBetween(start, end))
                    .build());
        }

        // Healing Usage
        List<DashboardResponse.HealingUsageDto> healingUsageList = new ArrayList<>();
        for (int i = 6; i >= 0; i--) {
            LocalDate date = LocalDate.now().minusDays(i);
            LocalDateTime start = date.atStartOfDay();
            LocalDateTime end = date.plusDays(1).atStartOfDay();

            List<Diary> diaries = diaryRepository.findByCreatedAtBetween(start, end);
            // 프로그램 ID → 제목 → 사용 횟수 집계
            Map<String, Long> usageMap = new HashMap<>();
            for (Diary diary : diaries) {
                for (Long programId : diary.getHealingProgramIds()) {
                    healingRepository.findById(programId).ifPresent(program -> {
                        String title = program.getTitle();
                        usageMap.put(title, usageMap.getOrDefault(title, 0L) + 1);
                    });
                }
            }

            healingUsageList.add(DashboardResponse.HealingUsageDto.builder()
                    .date(date.toString())
                    .usage(usageMap)
                    .build());

        }
        return DashboardResponse.builder()
                .stats(stats)
                .userTrends(userTrends)
                .healingUsage(healingUsageList)
                .build();
    }

    public List<MonthlyEmotionResponse> getMonthlyPositiveRates(User user, int limit) {
        List<Diary> diaries = diaryRepository.findByUser(user);

        // yyyy.MM 형식으로 그룹
        Map<String, List<Diary>> grouped = diaries.stream()
                .collect(Collectors.groupingBy(
                        d -> d.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy.MM")),
                        TreeMap::new,
                        Collectors.toList()
                ));

        // 최신 순 정렬 후 limit 적용 → 다시 오름차순으로 정렬
        return grouped.entrySet().stream()
                .sorted(Map.Entry.<String, List<Diary>>comparingByKey().reversed())
                .limit(limit)
                .map(entry -> {
                    String month = entry.getKey();
                    List<Diary> diaryList = entry.getValue();
                    double avg = diaryList.stream()
                            .mapToInt(Diary::getPositive)
                            .average()
                            .orElse(0);
                    return MonthlyEmotionResponse.builder() // 평균값 계산
                            .month(month)
                            .positive((int) avg)
                            .build();
                })
                .sorted(Comparator.comparing(MonthlyEmotionResponse::getMonth)) // 오름차순 정렬
                .collect(Collectors.toList());
    }

    public List<DailyEmotionResponse> getDailyPositiveRates(User user, int days) {
        LocalDate today = LocalDate.now();
        LocalDate startDate = today.minusDays(days - 1);

        LocalDateTime start = startDate.atStartOfDay();
        LocalDateTime end = today.atTime(23, 59, 59);

        List<Diary> diaries = diaryRepository.findByUserAndCreatedAtBetween(user, start, end);

        return diaries.stream()
                .collect(Collectors.groupingBy(
                        d -> d.getCreatedAt().toLocalDate().toString(), // yyyy-MM-dd
                        Collectors.toList()
                ))
                .entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .map(entry -> {
                    String date = entry.getKey();
                    double avg = entry.getValue().stream()
                            .mapToInt(Diary::getPositive)
                            .average()
                            .orElse(0);
                    return DailyEmotionResponse.builder()
                            .date(date)
                            .positive((int) avg)
                            .build();
                })
                .collect(Collectors.toList());
    }
    public List<HealingProgramStatResponse> getUserHealingProgramStats(User user) {
        List<Diary> diaries = diaryRepository.findByUser(user); // 기존 DiaryRepository

        // 모든 힐링 프로그램 ID 수집
        List<Long> allProgramIds = diaries.stream()
                .flatMap(d -> d.getHealingProgramIds().stream())
                .collect(Collectors.toList());

        // 빈 경우 빠르게 리턴
        if (allProgramIds.isEmpty()) return Collections.emptyList();

        // ID 별로 개수 집계
        Map<Long, Long> idCounts = allProgramIds.stream()
                .collect(Collectors.groupingBy(id -> id, Collectors.counting()));

        // ID → HealingProgram 이름 변환 (한 번에 조회)
        List<HealingProgram> programs = healingRepository.findAllById(idCounts.keySet());

        // 이름 기준 응답 생성
        return programs.stream()
                .map(p -> HealingProgramStatResponse.builder()
                        .name(p.getTitle())
                        .value(idCounts.getOrDefault(p.getId(), 0L).intValue())
                        .build())
                .sorted(Comparator.comparingInt(HealingProgramStatResponse::getValue).reversed())
                .collect(Collectors.toList());
    }

    public List<MonthlyFeedbackResponse> getMonthlyFeedbackCounts(User user, int limit) {
        // 1) 기간 계산: limit 개월 전 1일 00:00 부터, 이번 달 말 23:59:59 까지
        LocalDate today = LocalDate.now();
        LocalDate firstDayThisMonth = today.withDayOfMonth(1);
        LocalDate startMonth = firstDayThisMonth.minusMonths(limit - 1);
        LocalDateTime start = startMonth.atStartOfDay();
        LocalDateTime end = today.withDayOfMonth(today.lengthOfMonth())
                .atTime(23, 59, 59);

        // 2) 해당 기간의 모든 피드백 조회
        List<HealingFeedback> all = feedbackRepo.findByUserAndCreatedAtBetween(user, start, end);

        // 3) 월별 그룹핑 및 건수 집계
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy.MM");
        Map<String, Long> counts = all.stream()
                .collect(Collectors.groupingBy(
                        fb -> fb.getCreatedAt().format(fmt),
                        TreeMap::new,            // 키 정렬용: 과거→현재
                        Collectors.counting()
                ));

        // 4) limit 개월 치가 모자라다면 0건인 달도 채워주기
        TreeMap<String, Long> filled = new TreeMap<>();
        for (int i = 0; i < limit; i++) {
            String m = startMonth.plusMonths(i).format(fmt);
            filled.put(m, counts.getOrDefault(m, 0L));
        }

        // 5) DTO 변환
        return filled.entrySet().stream()
                .map(e -> MonthlyFeedbackResponse.builder()
                        .month(e.getKey())
                        .percent(e.getValue())
                        .build())
                .collect(Collectors.toList());
    }
    public List<UserHealingLogResponse> getUserHealingLogs(Long userId) {
        List<Diary> diaries = diaryRepository.findByUserIdOrderByCreatedAtDesc(userId);

        return diaries.stream()
                .flatMap(diary -> {
                    List<Long> ids = diary.getHealingProgramIds();
                    List<HealingProgram> programs = healingRepository.findAllById(ids);

                    return programs.stream()
                            .map(p -> UserHealingLogResponse.builder()
                                    .program(p.getTitle()) // 또는 getName()
                                    .usedAt(diary.getCreatedAt().toString())
                                    .build());
                })
                .collect(Collectors.toList());
    }
}

