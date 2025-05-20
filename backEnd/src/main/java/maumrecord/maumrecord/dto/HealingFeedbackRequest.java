package maumrecord.maumrecord.dto;

import maumrecord.maumrecord.domain.HealingFeedback;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.Builder;

@Getter
@Setter
@Builder
@AllArgsConstructor
public class HealingFeedbackRequest {
//    private HealingFeedback.FeedbackType feedback;
    private String feedback;
    private String category;
}
