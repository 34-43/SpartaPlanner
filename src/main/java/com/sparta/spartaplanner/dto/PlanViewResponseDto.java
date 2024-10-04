package com.sparta.spartaplanner.dto;

import com.sparta.spartaplanner.entity.Plan;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import java.time.LocalDateTime;

// Plan 을 조회하였을 때 Plan 과 연관된 User 의 필요한 정보만을 전달하는 응답 Dto

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class PlanViewResponseDto {
    private Long id;
    private String title;
    private String content;
    private String username;
    private String email;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public PlanViewResponseDto(Plan plan) {
        this.id = plan.getId();
        this.title = plan.getTitle();
        this.content = plan.getContent();
        this.createdAt = plan.getCreatedAt();
        this.updatedAt = plan.getUpdatedAt();
    }
}
