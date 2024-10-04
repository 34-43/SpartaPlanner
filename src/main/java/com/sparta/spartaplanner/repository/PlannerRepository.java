package com.sparta.spartaplanner.repository;

import com.sparta.spartaplanner.entity.Plan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

// Plan 에 대한 Repository 로, created_at 과 updated_at 필드의 갱신에 대한 완전한 책임을 가짐.

public class PlannerRepository {
    // 가변 쿼리 필터링을 위해, 쿼리 패러미터에 키워드를 사용할 수 있는 NamedParameterJdbcTemplate 를 추가적으로 주입
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public PlannerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    // Entity 객체를 저장하고, id 및 시간 필드가 갱신된 Entity 반환
    public Plan savePlan(Plan plan) {
        // 업데이트 후 결과 위치를 받을 특수 객체 선언
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // DB 지정 필드 삽입 쿼리
        String sql = "INSERT INTO plan (user_id, title, content, created_at, updated_at) VALUES (?,?,?,?,?)";

        // 데이터 생성 시점 기록
        Timestamp now = new Timestamp(System.currentTimeMillis());

        // 기본 키 DB 에 의해 자동 생성, 시점 필드 갱신, 키 홀더 특수 객체 반환
        jdbcTemplate.update( con -> {
            PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setLong(1, plan.getUserId());
            ps.setString(2, plan.getTitle());
            ps.setString(3, plan.getContent());
            ps.setTimestamp(4, now);
            ps.setTimestamp(5, now);
            return ps;
        }, keyHolder);

        // 키 홀더 객체로 기본 키 확인
        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();

        // 저장된 필드들을 Entity 에 갱신
        plan.setId(id);
        plan.setCreatedAt(now.toLocalDateTime());
        plan.setUpdatedAt(now.toLocalDateTime());

        // Entity 반환
        return plan;
    }

    // 쿼리에 맞는 데이터를 Entity 리스트로 반환
    public List<Plan> readAllPlans(Long userId, LocalDate date, String sort) {
        // 쿼리에 의한 조건 생성을 고려해, 항상 참인 조건을 미리 추가
        StringBuilder sbSql = new StringBuilder("SELECT * FROM plan WHERE TRUE");
        MapSqlParameterSource params = new MapSqlParameterSource();

        // 각 조건이 존재할 때, sql 및 식별 인자 추가
        if (!Objects.isNull(userId)) {
            sbSql.append(" AND user_id = :userId");
            params.addValue("userId", userId);
        }
        if (!Objects.isNull(date)) {
            sbSql.append(" AND DATE(updated_at) = :date");
            params.addValue("date", date);
        }
        if (!Objects.isNull(sort)) {
            switch (sort) {
                case "asc":
                    sbSql.append(" ORDER BY updated_at ASC");
                    break;
                case "desc":
                    sbSql.append(" ORDER BY updated_at DESC");
                    break;
            }
        }
        String sql = sbSql.toString();
        // 오버로딩된 query 메서드 중 하나로, 한번에 SELECT 쿼리 결과를 응답 Dto 리스트 타입에 맞게 map 변환함.
        return namedParameterJdbcTemplate.query(sql, params, (resultSet, rowNum) -> newPlanEntity(resultSet));
    }

    // 키 위치의 데이터를 Entity 로 반환
    public Plan readPlan(Long id) {
        return findById(id);
    }

    // 키 위치의 데이터를 Entity 로 갱신
    public Long updatePlan(Long id, Plan plan) {
        String sql = "UPDATE plan SET title = ?, content = ?, updated_at = ? WHERE id = ?";
        Timestamp now = new Timestamp(System.currentTimeMillis());
        jdbcTemplate.update(sql, plan.getTitle(), plan.getContent(), now, id);
        return id;
    }

    // 키 위치의 데이터 Row 를 삭제
    public Long deletePlan(Long id) {
        String sql = "DELETE FROM plan WHERE id = ?";
        jdbcTemplate.update(sql, id);
        return id;
    }

    // 키 위치의 데이터가 존재할 경우 Entity 로 반환하고, 없을 시 null 을 반환하는 메서드
    private Plan findById(Long id) {
        String sql = "SELECT * FROM plan WHERE id = ?";
        return jdbcTemplate.query(sql, (resultSet) -> {
            if (resultSet.next()) {
                return newPlanEntity(resultSet);
            } else {
                return null;
            }
        }, id);
    }

    // plan 테이블의 ResultSet Row 를 Entity 로 반환하는 메서드
    private Plan newPlanEntity(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        Long userId = resultSet.getLong("user_id");
        String title = resultSet.getString("title");
        String content = resultSet.getString("content");
        LocalDateTime createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
        LocalDateTime updatedAt = resultSet.getTimestamp("updated_at").toLocalDateTime();
        return new Plan(id, userId, title, content, createdAt, updatedAt);
    }
}
