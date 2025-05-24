package maumrecord.maumrecord.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserHealingLogResponse {
    private String program;   // 힐링 프로그램 이름
    private String usedAt;    // 사용 일시 로그
}