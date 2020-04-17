package ru.stepanov.uocns.web.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TorusRequest {

    private String name;

    private String description;

    private String columns;

    private String nodes;

    private String rows;
}
