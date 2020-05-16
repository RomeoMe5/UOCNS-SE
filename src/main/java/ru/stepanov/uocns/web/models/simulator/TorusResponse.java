package ru.stepanov.uocns.web.models.simulator;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TorusResponse {

    private Long id;

    private Long reportId;

    private Long tableId;

    private Long xmlId;

    private String name;

    private String description;

    private Integer columns;

    private Integer rows;

    private Double destInjectionRate;

    private String content;
}
