package com.sparta.spartaplanner.repository;

import com.sparta.spartaplanner.entity.User;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

// User 에 대한 Repository 로, created_at 과 updated_at 필드의 갱신에 대한 완전한 책임을 가짐.

public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    // Entity 객체를 저장하고, id 및 시간 필드가 갱신된 Entity 반환
    public User saveUser(User user) {
        // 업데이트 후 결과 위치를 받을 특수 객체 선언
        KeyHolder keyHolder = new GeneratedKeyHolder();

        // DB 지정 필드 삽입 쿼리
        String sql = "INSERT INTO user (username, password, email, created_at, updated_at) VALUES (?,?,?,?,?)";

        // 데이터 생성 시점 기록
        Timestamp now = new Timestamp(System.currentTimeMillis());

        // 기본 키 DB 에 의해 자동 생성, 시점 필드 갱신, 키 홀더 특수 객체 반환
        jdbcTemplate.update( con -> {
            PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1,user.getUsername());
            ps.setString(2,user.getPassword());
            ps.setString(3,user.getEmail());
            ps.setTimestamp(4, now);
            ps.setTimestamp(5, now);
            return ps;
        }, keyHolder);

        // 키 홀더 객체로 기본 키 확인
        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();

        // 저장된 필드들을 Entity 에 갱신
        user.setId(id);
        user.setCreatedAt(now.toLocalDateTime());
        user.setUpdatedAt(now.toLocalDateTime());

        // Entity 반환
        return user;
    }

    // 전체 데이터에 대한 Entity 리스트를 반환
    public List<User> readAllUsers() {
        String sql = "SELECT * FROM user";
        return jdbcTemplate.query(sql, (resultSet, rowNum) -> newUserEntity(resultSet));
    }

    // 키 위치의 데이터를 Entity 로 반환
    public User readUser(Long id) {
        return findById(id);
    }

    // 키 위치의 데이터를 Entity 로 갱신
    public Long updateUser(Long id, User user) {
        String sql = "UPDATE user SET username = ?, password = ?, email = ?, updated_at = ? WHERE id = ?";
        Timestamp now = new Timestamp(System.currentTimeMillis());
        jdbcTemplate.update(sql, user.getUsername(), user.getPassword(), user.getEmail(), now, id);
        return id;
    }

    // 키 위치의 데이터 Row 를 삭제
    public Long deleteUser(Long id) {
        String sql = "DELETE FROM user WHERE id = ?";
        jdbcTemplate.update(sql, id);
        return id;
    }

    // 전달된 email 주소가 user 테이블에 하나 이상 존재하는 지 확인하는 메서드
    public boolean ExistsByEmail(String email) {
        String sql = "SELECT * FROM user WHERE email = ?";
        return Boolean.TRUE.equals(jdbcTemplate.query(sql, (resultSet) -> resultSet.next() && Objects.equals(email, resultSet.getString("email")), email));
    }

    // 키 위치에서 password 필드 값을 반환하는 메서드. sql 안정성을 위해 필요한 필드 만큼 복제됨.
    public String getPassword(Long id) {
        String sql = "SELECT password FROM user WHERE id = ?";
        return jdbcTemplate.query(sql, (resultSet) -> {
            if (resultSet.next()) {
                return resultSet.getString("password");
            } else {
                return null;
            }
        }, id);
    }

    // 키 위치에서 username 필드 값을 반환하는 메서드. sql 안정성을 위해 필요한 필드 만큼 복제됨.
    public String getUsername(Long id) {
        String sql = "SELECT username FROM user WHERE id = ?";
        return jdbcTemplate.query(sql, (resultSet) -> {
            if (resultSet.next()) {
                return resultSet.getString("username");
            } else {
                return null;
            }
        }, id);
    }

    // 키 위치에서 email 필드 값을 반환하는 메서드. sql 안정성을 위해 필요한 필드 만큼 복제됨.
    public String getEmail(Long id) {
        String sql = "SELECT email FROM user WHERE id = ?";
        return jdbcTemplate.query(sql, (resultSet) -> {
            if (resultSet.next()) {
                return resultSet.getString("email");
            } else {
                return null;
            }
        }, id);
    }

    // 키 위치의 데이터가 존재할 경우 Entity 로 반환하고, 없을 시 null 을 반환하는 메서드
    private User findById(Long id) {
        String sql = "SELECT * FROM user WHERE id = ?";
        return jdbcTemplate.query(sql, (resultSet) -> {
            if (resultSet.next()) {
                return newUserEntity(resultSet);
            } else {
                return null;
            }
        }, id);
    }

    // user 테이블의 ResultSet Row 를 Entity 로 반환하는 메서드
    private User newUserEntity(ResultSet resultSet) throws SQLException {
        Long id = resultSet.getLong("id");
        String username = resultSet.getString("username");
        String password = resultSet.getString("password");
        String email = resultSet.getString("email");
        LocalDateTime createdAt = resultSet.getTimestamp("created_at").toLocalDateTime();
        LocalDateTime updatedAt = resultSet.getTimestamp("updated_at").toLocalDateTime();
        return new User(id, username, password, email, createdAt, updatedAt);
    }

}
