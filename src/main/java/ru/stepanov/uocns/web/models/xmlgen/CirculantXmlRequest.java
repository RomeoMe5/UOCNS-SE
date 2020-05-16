package ru.stepanov.uocns.web.models.xmlgen;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CirculantXmlRequest {

    private String name;

    private String description;

    private Integer nodes;

    private Integer firstStep;

    private Integer secondStep;

    private Double destInjectionRate;
}
