package maumrecord.maumrecord.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import maumrecord.maumrecord.domain.*;
import maumrecord.maumrecord.dto.*;
import maumrecord.maumrecord.service.*;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api")
@Tag(name = "/api 통신")
public class ApiController {
    private final HealingFeedbackService healingFeedbackService;
    private final CalendarService calendarService;

    @PostMapping(value = "/healing/feedback")
    @Operation(summary = "힐링 프로그램 피드백")
    public ResponseEntity<Void> saveFeedback(@RequestBody HealingFeedbackRequest request, Authentication authentication) {
        healingFeedbackService.saveFeedback(authentication, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping(value = "/emotions")
    @Operation(summary = "캘릭더 감정 기록 조회")
    public ResponseEntity<Map<String, EmotionResponse>> getEmotions(Authentication authentication) {
        Map<String, EmotionResponse> response = calendarService.getEmotionRecords(authentication);
        return ResponseEntity.ok(response);
    }

    @PatchMapping("/emotions/{date}/memo")
    @Operation(summary = "감정 메모 저장")
    public ResponseEntity<Void> updateMemo(
            @PathVariable String date,
            @RequestBody CalendarRequest request,
            Authentication authentication) {

        calendarService.updateMemo(authentication, date, request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/diary/today")
    @Operation(summary = "오늘의 감정 분석 결과 조회")
    public ResponseEntity<DiaryResultResponse> getTodayDiary(Authentication authentication) {
        DiaryResultResponse response = calendarService.getTodayDiary(authentication);
        return ResponseEntity.ok(response);
    }
}
