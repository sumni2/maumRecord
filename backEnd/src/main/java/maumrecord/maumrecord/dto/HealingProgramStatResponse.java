package maumrecord.maumrecord.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class HealingProgramStatResponse {
    private String name;
    private int value;
}
