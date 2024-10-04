package com.sparta.spartaplanner.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

// Plan 을 생성, 수정할 때 필요한 정보와 password 를 포함한 Dto

@Getter
@NoArgsConstructor
public class PlanFormRequestDto {
    private Long userId;
    private String title;
    private String content;
    private String password;
}
