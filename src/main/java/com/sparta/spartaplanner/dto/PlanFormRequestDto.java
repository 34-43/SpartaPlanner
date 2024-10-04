package com.sparta.spartaplanner.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlanFormRequestDto {
    private Long userId;
    private String title;
    private String content;
    private String password;
}
