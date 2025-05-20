package maumrecord.maumrecord.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DailyEmotionResponse {
    private String date;     // yyyy-MM-dd
    private int positive;    // 긍정 퍼센트 (0~100)
}
