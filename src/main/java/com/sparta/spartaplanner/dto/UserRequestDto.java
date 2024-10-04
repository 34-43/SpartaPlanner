package com.sparta.spartaplanner.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

// User 를 생성, 수정할 때 필요한 정보를 전달하는 요청 Dto

@Getter
@NoArgsConstructor
public class UserRequestDto {
    private String username;
    private String password;
    private String email;
}
