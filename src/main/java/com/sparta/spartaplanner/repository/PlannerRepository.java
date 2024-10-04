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

public class PlannerRepository {
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    public PlannerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate);
    }

    public Plan savePlan(Plan plan) {
        // 업데이트 후 결과를 받을 키 홀더 객체 선언
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // DB 지정 필드 삽입 쿼리
        String sql = "INSERT INTO plan (user_id, title, content, created_at, updated_at) VALUES (?,?,?,?,?)";

        Timestamp now = new Timestamp(System.currentTimeMillis());

        // 기본 키 생성하며 JDBC 업데이트 및 키 홀더 값 갱신 수행
        jdbcTemplate.update( con -> {
            PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setLong(1, plan.getUserId());
            ps.setString(2, plan.getTitle());
            ps.setString(3, plan.getContent());
            ps.setTimestamp(4, now);
            ps.setTimestamp(5, now);
            return ps;
        }, keyHolder);

        // 키 홀더로 기본 키 확인
        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();

        plan.setId(id);
        plan.setCreatedAt(now.toLocalDateTime());
        plan.setUpdatedAt(now.toLocalDateTime());

        return plan;
    }

    public List<Plan> readAllPlans(Long userId, LocalDate date, String sort) {
        // 쿼리에 의한 조건 생성을 고려해, 항상 참인 조건을 미리 추가
        StringBuilder sbSql = new StringBuilder("SELECT * FROM plan WHERE TRUE");
        MapSqlParameterSource params = new MapSqlParameterSource();

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

    public Plan readPlan(Long id) {
        return findById(id);
    }

    public Long updatePlan(Long id, Plan plan) {
        String sql = "UPDATE plan SET title = ?, content = ?, updated_at = ? WHERE id = ?";
        Timestamp now = new Timestamp(System.currentTimeMillis());
        jdbcTemplate.update(sql, plan.getTitle(), plan.getContent(), now, id);
        return id;
    }

    public Long deletePlan(Long id) {
        String sql = "DELETE FROM plan WHERE id = ?";
        jdbcTemplate.update(sql, id);
        return id;
    }

    // JdbcTemplate 객체와 id 를 받아서, DB 에 해당 id 를 키로 하는 데이터를 Entity 형태로 반환하고, 없다면 null 을 반환합니다.
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
