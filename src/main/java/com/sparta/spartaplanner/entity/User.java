package com.sparta.spartaplanner.entity;

import com.sparta.spartaplanner.dto.UserRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    private Long id;
    private String username;
    private String password;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public User(UserRequestDto requestDto) {
        this.username = requestDto.getUsername();
        this.password = requestDto.getPassword();
        this.email = requestDto.getEmail();
    }
}
