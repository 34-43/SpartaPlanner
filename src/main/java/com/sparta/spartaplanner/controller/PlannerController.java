package com.sparta.spartaplanner.controller;

import com.sparta.spartaplanner.dto.PlanRequestDto;
import com.sparta.spartaplanner.dto.PlanResponseDto;
import com.sparta.spartaplanner.entity.Plan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.web.bind.annotation.*;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

@RestController
@RequestMapping("/api")
public class PlannerController {
    private final JdbcTemplate jdbcTemplate;

    public PlannerController(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // 계획 생성 API
    @PostMapping("/plan")
    public PlanResponseDto createPlan(@RequestBody PlanRequestDto planRequestDto) {
        // 요청 Dto (title, content, username, password) -> Entity
        Plan plan = new Plan(planRequestDto);

        // 업데이트 후 결과를 받을 키 홀더 객체 선언
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // DB 지정 필드 삽입 쿼리
        String sql = "INSERT INTO plan (title, content, username, password, created_datetime, last_datetime) VALUES (?,?,?,?,?,?)";

        //
        jdbcTemplate.update( con -> {
            PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1, plan.getTitle());
            ps.setString(2, plan.getContent());
            ps.setString(3, plan.getUsername());
            ps.setString(4, plan.getPassword());
            ps.setTimestamp(5, Timestamp.valueOf(plan.getCreatedDatetime()));
            ps.setTimestamp(6, Timestamp.valueOf(plan.getLastDatetime()));
            return ps;
        }, keyHolder);

        // 키 홀더로 기본 키 확인
        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();
        plan.setId(id);

        // Entity -> 응답 Dto 로 생성하여 반환
        return new PlanResponseDto(plan);
    }

    // 계획 전체 조회 API
    @GetMapping("/plan")
    public List<PlanResponseDto> getAllPlans() {
        // DB 전체 조회 쿼리
        String sql = "SELECT * FROM plan";

        // 오버로딩된 query 메서드 중 하나로, 한번에 SELECT 쿼리 결과를 응답 Dto 리스트 타입에 맞게 map 변환함.
        return jdbcTemplate.query(sql, (resultSet, rowNum) -> {
            // resultSet 은 각 Row 에 해당하는 객체이고, rowNum 은 목차 번호이기에 지금은 활용하지 않음.
            Long id = resultSet.getLong("id");
            String title = resultSet.getString("title");
            String content = resultSet.getString("content");
            String username = resultSet.getString("username");
            LocalDateTime createdDatetime = resultSet.getTimestamp("created_datetime").toLocalDateTime();   // Dto 의 필드 타입에 맞게 변환
            LocalDateTime lastDatetime = resultSet.getTimestamp("last_datetime").toLocalDateTime();   // Dto 의 필드 타입에 맞게 변환
            return new PlanResponseDto(id,title,content,username,createdDatetime,lastDatetime);
        });
    }

    // 계획 id 로 조회 API
    @GetMapping("/plan/{id}")
    public PlanResponseDto getPlan(@PathVariable Long id) {
        // id 를 키로 하는 데이터 또는 null 취득
        Plan targetPlan = findEntityOrNull(jdbcTemplate, id);
        if (targetPlan != null) {
            // 존재할 경우, Entity -> 응답 Dto 생성하여 반환
            return new PlanResponseDto(targetPlan);
        } else {
            throw new IllegalArgumentException("id에 해당하는 계획 데이터가 없습니다.");
        }
    }

    // 계획 id 로 수정 API
    @PutMapping("/plan/{id}")
    public Long updatePlan(@PathVariable Long id, @RequestBody PlanRequestDto planRequestDto) {
        // id 를 키로 하는 데이터 또는 null 취득
        Plan targetPlan = findEntityOrNull(jdbcTemplate, id);
        if (targetPlan != null) {
            // 존재할 경우, 비밀번호 비교
            if (targetPlan.getPassword().equals(planRequestDto.getPassword())) {
                // 비밀번호 맞을 경우, UPDATE 수행
                String sql = "UPDATE plan SET title = ?, content = ?, username = ?, last_datetime = ? WHERE id = ?";
                // 마지막 편집 시간 새로 조정
                LocalDateTime lastDatetime = LocalDateTime.now();
                jdbcTemplate.update(sql, planRequestDto.getTitle(), planRequestDto.getContent(), planRequestDto.getUsername(), Timestamp.valueOf(lastDatetime), id);
                return id;
            } else {
                throw new IllegalArgumentException("비밀번호가 틀렸습니다.");
            }
        } else {
            throw new IllegalArgumentException("id에 해당하는 계획 데이터가 없습니다.");
        }
    }

    // 계획 id 로 삭제 API
    @DeleteMapping("/plan/{id}")
    public Long deletePlan(@PathVariable Long id) {
        // id 를 키로 하는 데이터 존재 여부 확인
        if (findEntityOrNull(jdbcTemplate, id) != null) {
            String sql = "DELETE FROM plan WHERE id = ?";
            jdbcTemplate.update(sql, id);
            return id;
        } else {
            throw new IllegalArgumentException("id에 해당하는 계획 데이터가 없습니다.");
        }
    }

    // JdbcTemplate 객체와 id 를 받아서, DB 에 해당 id 를 키로 하는 데이터를 Entity 형태로 반환하고, 없다면 null 을 반환합니다.
    private Plan findEntityOrNull(JdbcTemplate jdbcTemplate, Long id) {
        String sql = "SELECT * FROM plan WHERE id = ?";
        return jdbcTemplate.query(sql, (resultSet) -> {
            if (resultSet.next()) {
                Plan plan = new Plan();
                plan.setId(resultSet.getLong("id"));
                plan.setTitle(resultSet.getString("title"));
                plan.setContent(resultSet.getString("content"));
                plan.setUsername(resultSet.getString("username"));
                plan.setPassword(resultSet.getString("password"));
                plan.setCreatedDatetime(resultSet.getTimestamp("created_datetime").toLocalDateTime());
                plan.setLastDatetime(resultSet.getTimestamp("last_datetime").toLocalDateTime());
                return plan;
            } else {
                return null;
            }
        }, id);
    }

}
