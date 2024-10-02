package com.sparta.spartaplanner.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class PlanRequestDto {
    private String title;
    private String content;
    private String username;
    private String password;
}
