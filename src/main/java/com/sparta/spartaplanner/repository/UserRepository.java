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

public class UserRepository {
    private final JdbcTemplate jdbcTemplate;

    public UserRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    public User saveUser(User user) {
        KeyHolder keyHolder = new GeneratedKeyHolder();

        String sql = "INSERT INTO user (username, password, email, created_at, updated_at) VALUES (?,?,?,?,?)";

        Timestamp now = new Timestamp(System.currentTimeMillis());

        jdbcTemplate.update( con -> {
            PreparedStatement ps = con.prepareStatement(sql, PreparedStatement.RETURN_GENERATED_KEYS);
            ps.setString(1,user.getUsername());
            ps.setString(2,user.getPassword());
            ps.setString(3,user.getEmail());
            ps.setTimestamp(4, now);
            ps.setTimestamp(5, now);
            return ps;
        }, keyHolder);

        Long id = Objects.requireNonNull(keyHolder.getKey()).longValue();

        user.setId(id);
        user.setCreatedAt(now.toLocalDateTime());
        user.setUpdatedAt(now.toLocalDateTime());

        return user;
    }

    public List<User> readAllUsers() {
        String sql = "SELECT * FROM user";
        return jdbcTemplate.query(sql, (resultSet, rowNum) -> newUserEntity(resultSet));
    }

    public User readUser(Long id) {
        return findById(id);
    }

    public Long updateUser(Long id, User user) {
        String sql = "UPDATE user SET username = ?, password = ?, email = ?, updated_at = ? WHERE id = ?";
        Timestamp now = new Timestamp(System.currentTimeMillis());
        jdbcTemplate.update(sql, user.getUsername(), user.getPassword(), user.getEmail(), now, id);
        return id;
    }

    public Long deleteUser(Long id) {
        String sql = "DELETE FROM user WHERE id = ?";
        jdbcTemplate.update(sql, id);
        return id;
    }

    public boolean ExistsByEmail(String email) {
        String sql = "SELECT * FROM user WHERE email = ?";
        return Boolean.TRUE.equals(jdbcTemplate.query(sql, (resultSet) -> resultSet.next() && Objects.equals(email, resultSet.getString("email")), email));
    }

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
