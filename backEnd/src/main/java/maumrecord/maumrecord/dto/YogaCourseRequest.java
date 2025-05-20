package maumrecord.maumrecord.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class YogaCourseRequest {
    private Long poseId;
    private int time;
}
