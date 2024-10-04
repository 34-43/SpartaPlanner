package com.sparta.spartaplanner.entity;

import com.sparta.spartaplanner.dto.PlanFormRequestDto;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Plan {
    private Long id;
    private Long userId;
    private String title;
    private String content;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Plan(PlanFormRequestDto planRequestDto) {
        this.title = planRequestDto.getTitle();
        this.content = planRequestDto.getContent();
        this.userId = planRequestDto.getUserId();
    }
}
