package ru.stepanov.uocns.web.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CirculantResponse {

    private Long id;

    private Long reportId;

    private Long tableId;

    private Long xmlId;

    private String name;

    private String description;

    private String columns;

    private String nodes;

    private String rows;
}
