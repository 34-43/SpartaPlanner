package com.sparta.spartaplanner.entity;

import com.sparta.spartaplanner.dto.PlanRequestDto;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class Plan {
    private Long id;
    private String title;
    private String content;
    private String username;
    private LocalDateTime createdDatetime;
    private LocalDateTime lastDatetime;
    private String password;

    public Plan(PlanRequestDto planRequestDto) {
        this.title = planRequestDto.getTitle();
        this.content = planRequestDto.getContent();
        this.username = planRequestDto.getUsername();
        this.password = planRequestDto.getPassword();
        this.createdDatetime = LocalDateTime.now();
        this.lastDatetime = this.createdDatetime;
    }
}
