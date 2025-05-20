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
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.nio.file.AccessDeniedException;
import java.util.List;
import java.util.Map;
import java.util.Set;

@SecurityRequirement(name = "bearerAuth")
@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Tag(name = "로그인 회원 관련 기능")
public class UserController {
    private final UserService userService;
    private final InquiryService inquiryService;
    private final UserDetailService userDetailService;
    private final HealingService healingService;
    private final DashboardService dashboardService;

    @DeleteMapping(value="/delete")
    @Operation(summary = "회원탈퇴")
    public ResponseEntity<String> deleteUser(Authentication authentication)
    {
        userService.deleteUser(authentication);
        return ResponseEntity.ok("회원탈퇴 완료");
    }

    @GetMapping(value = "/profile")
    @Operation(summary = "회원 정보 확인")
    public UserResponse selectUser(Authentication authentication){
        User user= userDetailService.loadUserByUsername(authentication.getName());
        return new UserResponse(user.getEmail(),user.getNickName(),user.getImage(),user.getRole());
    }

    @PatchMapping(value = "/update")
    @Operation(summary = "회원 정보 수정")
    public ResponseEntity<String> updateUser(UserRequest request,Authentication authentication){
        userService.updateUser(request,authentication.getName());
        return ResponseEntity.ok("회원 정보 수정 완료");
    }

    @PostMapping(value = "/inquiry")
    @Operation(summary = "1대1 문의 전송")
    public ResponseEntity<String> newInquiry(InquiryRequest request, Authentication authentication){
        inquiryService.newInquiry(authentication,request);
        return ResponseEntity.ok("1대1 문의 접수 완료");
    }

    @GetMapping(value = "/my-inquiries")
    @Operation(summary = "내 문의 내역")
    public List<InquiryResponse> findInquiries(Authentication authentication){
        return inquiryService.findInquires(authentication);
    }

    @GetMapping(value = "/my-inquiries/{id}")
    @Operation(summary = "내 문의 확인")
    public InquiryWithAnswer findInquiryById(Authentication authentication, @PathVariable Long id) throws AccessDeniedException {
        return inquiryService.findUserInquiryById(authentication, id);
    }

    @GetMapping(value = "/healing")
    @Operation(summary = "힐링 프로그램 전체 조회")
    public List<HealingDTO> healings(){
        return healingService.healingList("all");
    }

    @GetMapping(value = "/healing/music")
    @Operation(summary = "음악 전체 조회")
    public List<HealingDTO> musics(){
        return healingService.healingList("Music");
    }

    @GetMapping(value = "/healing/meditation")
    @Operation(summary = "명상 전체 조회")
    public List<HealingDTO> meditations(){
        return healingService.healingList("Meditation");
    }

    @GetMapping(value = "/healing/{id}")
    @Operation(summary = "특정 힐링 프로그램 조회")
    public HealingProgram healing(@PathVariable Long id){
        return healingService.findHealingProgram(id);
    }

    @GetMapping(value = "/healing/yoga/courses")
    @Operation(summary = "요가 코스 전체 조회")
    public List<String> yogaCourses(){
        return healingService.yogaCourseList();
    }

    @GetMapping(value = "/healing/yoga/courses/{title}")
    @Operation(summary = "특정 요가 코스 조회")
    public List<YogaCourseElement> yogaCourse(@PathVariable String title){
        return healingService.findYogaCourse(title);
    }

    @GetMapping("/dashboard/emotion/monthly")
    @Operation(summary = "월별 긍정 지수 조회")
    public ResponseEntity<List<MonthlyEmotionResponse>> getMonthlyEmotions(
            @RequestParam(defaultValue = "6") int limit,
            @AuthenticationPrincipal User user) {

        List<MonthlyEmotionResponse> response = dashboardService.getMonthlyPositiveRates(user, limit);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dashboard/emotion/daily")
    @Operation(summary = "일별 긍정 지수 조회")
    public ResponseEntity<List<DailyEmotionResponse>> getDailyEmotions(
            @RequestParam(defaultValue = "30") int days,
            @AuthenticationPrincipal User user) {

        List<DailyEmotionResponse> response = dashboardService.getDailyPositiveRates(user, days);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dashboard/healing-programs")
    @Operation(summary = "개인 힐링 프로그램 사용 통계")
    public ResponseEntity<List<HealingProgramStatResponse>> getUserHealingStats(
            @AuthenticationPrincipal User user) {

        List<HealingProgramStatResponse> response = dashboardService.getUserHealingProgramStats(user);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/dashboard/feedback")
    public ResponseEntity<List<MonthlyFeedbackResponse>> getFeedbackTrend(
            @RequestParam(defaultValue = "12") int limit,
            @AuthenticationPrincipal User user) {

        List<MonthlyFeedbackResponse> list =
                dashboardService.getMonthlyFeedbackCounts(user, limit);
        return ResponseEntity.ok(list);
    }
}
