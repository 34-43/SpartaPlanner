package com.sparta.spartaplanner.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

// 단순 Dto 클래스로, Plan 을 조회할 때 필터링 쿼리를 전달함

@Getter
@Setter
@NoArgsConstructor
public class PlanFilterRequestDto {
    private Long userId;
    private LocalDate date;
    private String sort;
}
