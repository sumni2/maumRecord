package maumrecord.maumrecord.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyFeedbackResponse {
    private String month;  // "yyyy.MM"
    private long percent;  // 해당 월의 피드백 응답 퍼센트
}
