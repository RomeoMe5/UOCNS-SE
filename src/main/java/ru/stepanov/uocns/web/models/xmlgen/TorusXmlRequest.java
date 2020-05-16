package ru.stepanov.uocns.web.models.xmlgen;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TorusXmlRequest {

    private String name;

    private String description;

    private Integer columns;

    private Integer rows;

    private Double destInjectionRate;
}
