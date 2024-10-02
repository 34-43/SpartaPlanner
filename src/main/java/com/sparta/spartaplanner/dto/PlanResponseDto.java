package com.sparta.spartaplanner.dto;

import com.sparta.spartaplanner.entity.Plan;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.sql.ResultSet;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanResponseDto {
    private Long id;
    private String title;
    private String content;
    private String username;
    private LocalDateTime createdDatetime;
    private LocalDateTime lastDatetime;

    public PlanResponseDto(Plan plan) {
        this.id = plan.getId();
        this.title = plan.getTitle();
        this.content = plan.getContent();
        this.username = plan.getUsername();
        this.createdDatetime = plan.getCreatedDatetime();
        this.lastDatetime = plan.getLastDatetime();
    }
}
