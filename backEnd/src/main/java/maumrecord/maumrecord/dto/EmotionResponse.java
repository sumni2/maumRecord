package maumrecord.maumrecord.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmotionResponse {
    // 7가지 분류 감정
    private String emotion;
    // 일기 텍스트
    private String longSummary;
    // 빈문자열도 가능
    private String memo;
}
