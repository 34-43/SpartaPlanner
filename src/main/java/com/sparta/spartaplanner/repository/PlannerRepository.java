package com.sparta.spartaplanner.repository;

import com.sparta.spartaplanner.entity.Plan;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

public class PlannerRepository {
    private final JdbcTemplate jdbcTemplate;

    public PlannerRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public Plan save(Plan plan) {
        // 업데이트 후 결과를 받을 키 홀더 객체 선언
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // DB 지정 필드 삽입 쿼리
        String sql = "INSERT INTO plan (title, content, username, password, created_datetime, last_datetime) VALUES (?,?,?,?,?,?)";

        // 기본 키 생성하며 JDBC 업데이트 및 키 홀더 값 갱신 수행
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

        return plan;
    }

    public List<Plan> readAll() {
        String sql = "SELECT * FROM plan";

        // 오버로딩된 query 메서드 중 하나로, 한번에 SELECT 쿼리 결과를 응답 Dto 리스트 타입에 맞게 map 변환함.
        return jdbcTemplate.query(sql, (resultSet, rowNum) -> {
            // resultSet 은 각 Row 에 해당하는 객체이고, rowNum 은 목차 번호이기에 지금은 활용하지 않음.
            Long id = resultSet.getLong("id");
            String title = resultSet.getString("title");
            String content = resultSet.getString("content");
            String username = resultSet.getString("username");
            String password = "";
            LocalDateTime createdDatetime = resultSet.getTimestamp("created_datetime").toLocalDateTime();   // Dto 의 필드 타입에 맞게 변환
            LocalDateTime lastDatetime = resultSet.getTimestamp("last_datetime").toLocalDateTime();   // Dto 의 필드 타입에 맞게 변환
            return new Plan(id,title,content,username,createdDatetime,lastDatetime,password);
        });
    }

    public Plan read(Long id) {
        return findById(id);
    }

    public Long update(Long id, Plan plan) {
        String sql = "UPDATE plan SET title = ?, content = ?, username = ?, last_datetime = ? WHERE id = ?";
        jdbcTemplate.update(sql, plan.getTitle(), plan.getContent(), plan.getUsername(), Timestamp.valueOf(plan.getLastDatetime()), id);
        return id;
    }

    public Long delete(Long id) {
        String sql = "DELETE FROM plan WHERE id = ?";
        jdbcTemplate.update(sql, id);
        return id;
    }

    // JdbcTemplate 객체와 id 를 받아서, DB 에 해당 id 를 키로 하는 데이터를 Entity 형태로 반환하고, 없다면 null 을 반환합니다.
    private Plan findById(Long id) {
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
