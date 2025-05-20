package maumrecord.maumrecord.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class MonthlyEmotionResponse {
    private String month;   // 예: "2025.04"
    private int positive;   // 긍정 퍼센트 값 (0~100)
}
