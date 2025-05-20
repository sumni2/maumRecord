package maumrecord.maumrecord.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import maumrecord.maumrecord.domain.*;
import maumrecord.maumrecord.dto.HealingFeedbackRequest;
import maumrecord.maumrecord.repository.UserRepository;
import maumrecord.maumrecord.repository.DiaryRepository;
import maumrecord.maumrecord.repository.HealingRepository;
import maumrecord.maumrecord.repository.HealingFeedbackRepository;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;


import java.util.List;

@Service
@Transactional
@RequiredArgsConstructor

public class HealingFeedbackService {

    private final HealingFeedbackRepository healingFeedbackRepository;
    private final HealingRepository healingRepository;
    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;

    public void saveFeedback(Authentication authentication, HealingFeedbackRequest dto) {

        // 사용자 정보 가져오기
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        // 다이어리 id 가져오기
        Diary diary = diaryRepository.findTopByUserIdOrderByCreatedAtDesc(user.getId())
                .orElseThrow(() -> new RuntimeException("다이어리를 찾을 수 없습니다."));

        // FeedbackType 매핑
        HealingFeedback.FeedbackType feedbackType = switch (dto.getFeedback()) {
            case "좋았다" -> HealingFeedback.FeedbackType.GOOD;
            case "별로였다" -> HealingFeedback.FeedbackType.BAD;
            default -> throw new IllegalArgumentException("잘못된 피드백입니다.");
        };

        // HealingProgram 매핑
        List<HealingProgram> programs = healingRepository.findAllByCategory(dto.getCategory());
        if (programs.isEmpty()) {
            throw new RuntimeException("해당 카테고리에 대한 힐링 프로그램이 없습니다.");
        }
        HealingProgram program = programs.get(0);

        HealingFeedback feedback = HealingFeedback.builder()
                .user(user)
                .diary(diary)
                .feedback(feedbackType)
                .healingProgram(program)
                .build();

        healingFeedbackRepository.save(feedback);
    }
}