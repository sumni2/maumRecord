package maumrecord.maumrecord.service;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import maumrecord.maumrecord.domain.Diary;
import maumrecord.maumrecord.domain.User;
import maumrecord.maumrecord.dto.CalendarRequest;
import maumrecord.maumrecord.dto.DiaryResultResponse;
import maumrecord.maumrecord.dto.EmotionResponse;
import org.springframework.stereotype.Service;
import org.springframework.security.core.Authentication;
import maumrecord.maumrecord.repository.DiaryRepository;
import maumrecord.maumrecord.repository.UserRepository;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;

@Transactional
@RequiredArgsConstructor
@Service
public class CalendarService {
    private final UserRepository userRepository;
    private final DiaryRepository diaryRepository;

    public Map<String, EmotionResponse> getEmotionRecords(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        List<Diary> records = diaryRepository.findByUser(user);

        return records.stream()
                .collect(Collectors.toMap(
                        record -> record.getCreatedAt().toLocalDate().toString(), // yyyy-MM-dd
                        record -> EmotionResponse.builder()
                                .emotion(record.getSevenSentiment().name())
                                .longSummary(record.getSummary())
                                .memo(record.getMemo() != null ? record.getMemo() : "")
                                .build(),
                        (a, b) -> b,
                        TreeMap::new // 날짜 순 정렬
                ));
    }

    public void updateMemo(Authentication authentication, String date, CalendarRequest request) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        LocalDate targetDate = LocalDate.parse(date);

        // createdAt이 해당 날짜의 일기 찾기 (00:00 ~ 23:59)
        LocalDateTime start = targetDate.atStartOfDay();
        LocalDateTime end = targetDate.atTime(23, 59, 59);

        Diary diary = diaryRepository.findFirstByUserAndCreatedAtBetween(user, start, end)
                .orElseThrow(() -> new RuntimeException("해당 날짜의 일기를 찾을 수 없습니다."));

        diary.setMemo(request.getMemo());
        diaryRepository.save(diary);
    }

    public DiaryResultResponse getTodayDiary(Authentication authentication) {
        User user = userRepository.findByEmail(authentication.getName())
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        LocalDate today = LocalDate.now();
        LocalDateTime start = today.atStartOfDay();
        LocalDateTime end = today.atTime(23, 59, 59);

        Diary diary = diaryRepository.findFirstByUserAndCreatedAtBetween(user, start, end)
                .orElseThrow(() -> new RuntimeException("오늘 작성된 일기를 찾을 수 없습니다."));

        return DiaryResultResponse.builder()
                .longSummary(diary.getSummary())
                .emotion(diary.getSevenSentiment().name())
                .positive(diary.getPositive())
                .negative(diary.getNegative())
                .build();
    }
}
