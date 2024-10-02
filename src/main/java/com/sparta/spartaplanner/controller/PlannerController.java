package com.sparta.spartaplanner.controller;

import com.sparta.spartaplanner.dto.PlanRequestDto;
import com.sparta.spartaplanner.dto.PlanResponseDto;
import com.sparta.spartaplanner.entity.Plan;
import com.sparta.spartaplanner.repository.PlannerRepository;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@RestController
@RequestMapping("/api")
public class PlannerController {
    private final PlannerRepository repository;

    public PlannerController(JdbcTemplate jdbcTemplate) {
        this.repository = new PlannerRepository(jdbcTemplate);
    }

    // 계획 생성 API
    @PostMapping("/plan")
    public PlanResponseDto createPlan(@RequestBody PlanRequestDto planRequestDto) {
        // 요청 Dto (title, content, username, password) -> Entity
        Plan reqPlan = new Plan(planRequestDto);

        // repository 에 저장 작업 위임 후, 저장된 Entity 반환
        Plan resPlan = repository.save(reqPlan);

        // Entity -> 응답 Dto 로 생성하여 반환
        return new PlanResponseDto(resPlan);
    }

    // 계획 전체 조회 API
    @GetMapping("/plan")
    public List<PlanResponseDto> getAllPlans() {
        // repository 에 전체 조회 위임하여 얻은 Entity List 를 응답 Dto List 로 변환하여 반환
        return repository.readAll().stream().map(PlanResponseDto::new).toList();
    }

    // 계획 id 로 조회 API
    @GetMapping("/plan/{id}")
    public PlanResponseDto getPlan(@PathVariable Long id) {
        // repository 에 조회 위임하여 nullable Entity 취득
        Plan plan = repository.read(id);
        // 취득한 Entity 의 null 여부에 따라 예외 발생 또는 응답 Dto 반환
        if (!Objects.isNull(plan)) {
            return new PlanResponseDto(plan);
        } else {
            throw new IllegalArgumentException("Plan not found");
        }
    }

    // 계획 id 로 수정 API
    @PutMapping("/plan/{id}")
    public Long updatePlan(@PathVariable Long id, @RequestBody PlanRequestDto planRequestDto) {
        // id 를 키로 하는 데이터 또는 null 취득
        Plan targetPlan = repository.read(id);
        Plan reqPlan = new Plan(planRequestDto);
        if (targetPlan != null) {
            // 존재할 경우, 비밀번호 비교 후 진행
            if (targetPlan.getPassword().equals(reqPlan.getPassword())) {
                // 요청 Entity 의 최근 편집 시간 갱신
                reqPlan.setLastDatetime(LocalDateTime.now());
                // repository 수정 위임 후 반환된 id 반환
                return repository.update(id, reqPlan);
            } else {
                throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
            }
        } else {
            throw new IllegalArgumentException("id에 해당하는 계획 데이터가 없습니다.");
        }
    }

    // 계획 id 로 삭제 API
    @DeleteMapping("/plan/{id}")
    public Long deletePlan(@PathVariable Long id, @RequestHeader Map<String, String> headers) {
        // id 를 키로 하는 데이터 취득
        Plan targetPlan = repository.read(id);
        if (targetPlan != null) {
            // 존재할 경우, 비밀번호 비교 후 진행
            String password = headers.get("password");
            if (targetPlan.getPassword().equals(password)) {
                return repository.delete(id);
            } else {
                throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
            }
        } else {
            throw new IllegalArgumentException("id에 해당하는 계획 데이터가 없습니다.");
        }
    }

}
