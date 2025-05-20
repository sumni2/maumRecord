package maumrecord.maumrecord.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DiaryResultResponse {
    private String longSummary;  // 요약 내용
    private String emotion;      // sevenSentiment
    private int positive;        // 긍정 지수
    private int negative;        // 부정 지수
}
