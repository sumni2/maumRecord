package maumrecord.maumrecord.dto;

import lombok.*;

import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DashboardResponse {

    private Stats stats;
    private List<UserTrendDto> userTrends;
    private List<HealingUsageDto> healingUsage;

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Stats {
        private Long userCount;
        private Long todayJournalCount;
        private Long unansweredCount;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class UserTrendDto {
        private String date;
        private Long signups;
        private Long activeUsers;
    }

    @Getter
    @Setter
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class HealingUsageDto {
        private String date;
        private Map<String, Long> usage; // "명상": 8, "요가": 4 같은 형식
    }
}
