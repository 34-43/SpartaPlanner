package com.sparta.spartaplanner.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
public class PlanFilterDto {
    private Long userId;
    private LocalDate date;
    private String sort;
}
