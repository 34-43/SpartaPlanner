package com.sparta.spartaplanner.controller;

import com.sparta.spartaplanner.dto.PlanFilterDto;
import com.sparta.spartaplanner.dto.PlanFormRequestDto;
import com.sparta.spartaplanner.dto.PlanViewResponseDto;
import com.sparta.spartaplanner.entity.Plan;
import com.sparta.spartaplanner.entity.User;
import com.sparta.spartaplanner.exception.FailedRequestException;
import com.sparta.spartaplanner.exception.IdNotFoundException;
import com.sparta.spartaplanner.exception.PasswordFailException;
import com.sparta.spartaplanner.repository.PlannerRepository;
import com.sparta.spartaplanner.repository.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api/plan")
public class PlannerController {
    private final PlannerRepository plannerRepository;
    private final UserRepository userRepository;

    public PlannerController(JdbcTemplate jdbcTemplate) {
        this.plannerRepository = new PlannerRepository(jdbcTemplate);
        this.userRepository = new UserRepository(jdbcTemplate);
    }

    // 계획 생성 API
    @PostMapping("")
    public PlanViewResponseDto createPlan(@RequestBody PlanFormRequestDto requestDto) {
        // 요청된 사용자가 존재하는 지 조회
        System.out.println(requestDto.getUserId());
        User targetUser = userRepository.readUser(requestDto.getUserId());
        if (Objects.isNull(targetUser)) {throw new IdNotFoundException("User", requestDto.getUserId());}

        // 요청된 사용자와 비밀번호 일치 여부 확인
        verifyUserPassword(requestDto.getUserId(),requestDto.getPassword());

        // 요청 Dto 를 토대로 저장될 Entity 생성
        Plan reqPlan = new Plan(requestDto);

        // DB 저장과 동시에, id 및 시간을 부여받은 새 Entity 획득
        Plan resPlan = plannerRepository.savePlan(reqPlan);

        // 해당 Entity 를 username 을 포함시킨 응답 Dto 로 반환
        return ResDtoWithUserInfo(resPlan);
    }

    // 계획 전체 조회 API
    @GetMapping("")
    public List<PlanViewResponseDto> getAllPlans(@ModelAttribute PlanFilterDto filter) {
        // 요청된 필터 Dto 값을 받아 repository 에 전달하여 쿼리 수행
        List<Plan> queriedPlanList = plannerRepository.readAllPlans(filter.getUserId(),filter.getDate(),filter.getSort());
        // 읽어들인 Entity 리스트 -> 응답 Dto 리스트 반환
        return queriedPlanList.stream().map(this::ResDtoWithUserInfo).toList();
    }

    // 계획 id 로 조회 API
    @GetMapping("{id}")
    public PlanViewResponseDto getPlan(@PathVariable Long id) {
        // 기본 키로 nullable 데이터 조회
        Plan plan = plannerRepository.readPlan(id);
        if (!Objects.isNull(plan)) {
            // 존재할 시, 응답 Dto 로 반환
            return ResDtoWithUserInfo(plan);
        } else {
            // 아닐 시, 예외 전달
            throw new IllegalArgumentException("Plan with id " + id + " not found");
        }
    }

    // 계획 id 로 수정 API
    @PutMapping("{id}")
    public Long updatePlan(@PathVariable Long id, @RequestBody PlanFormRequestDto requestDto) {
        // 요청 일정과 해당 사용자 존재 확인
        Long userId = verifyUserId(id);

        // 등록된 사용자와 요청 Dto 비밀번호 일치 여부 확인
        String password = requestDto.getPassword();
        verifyUserPassword(userId, password);

        // 일정 갱신 및 id 반환
        Plan requestPlan = new Plan(requestDto);
        return plannerRepository.updatePlan(id, requestPlan);
    }

    // 계획 id 로 삭제 API
    @DeleteMapping("{id}")
    public Long deletePlan(@PathVariable Long id, @RequestHeader Map<String, String> headers) {
        // 요청 일정과 해당 사용자 존재 확인
        Long userId = verifyUserId(id);

        // 등록된 사용자와 요청 헤더 비밀번호 일치 여부 확인
        String password = headers.get("password");
        verifyUserPassword(userId, password);

        // 일정 삭제 및 id 반환
        return plannerRepository.deletePlan(id);
    }

    public PlanViewResponseDto ResDtoWithUserInfo(Plan plan) {
        // 사용자 존재 조회
        Long userId = plan.getUserId();
        User targetUser = userRepository.readUser(userId);
        if (Objects.isNull(targetUser)) {throw new IdNotFoundException("User", userId);}

        // 인자로 응답 Dto 생성 후 사용자 정보 기입
        PlanViewResponseDto res = new PlanViewResponseDto(plan);
        String username = userRepository.getUsername(userId);
        String email = userRepository.getEmail(userId);
        res.setUsername(username);
        res.setEmail(email);

        // 사용자명을 포함한 Dto 반환
        return res;
    }

    private void verifyUserPassword(Long userId, String password) throws PasswordFailException {
        if (!password.equals(userRepository.getPassword(userId))) {
            throw new PasswordFailException();
        }
    }

    private Long verifyUserId(Long planId) {
        // 요청 일정 존재 조회
        Plan targetPlan = plannerRepository.readPlan(planId);
        if (Objects.isNull(targetPlan)) {throw new IdNotFoundException("Plan", planId);}

        // 요청 일정에 등록된 사용자 존재 조회
        Long userId = targetPlan.getUserId();
        User targetUser = userRepository.readUser(userId);
        if (Objects.isNull(targetUser)) {throw new IdNotFoundException("User", userId);}

        // 사용자 id 반환
        return userId;
    }

    @ExceptionHandler(FailedRequestException.class)
    public ResponseEntity<String> handleFailedRequestException(FailedRequestException e) {
        return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE).body(e.getMessage());
    }

}
