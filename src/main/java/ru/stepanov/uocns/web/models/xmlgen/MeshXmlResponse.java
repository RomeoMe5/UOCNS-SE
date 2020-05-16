package ru.stepanov.uocns.web.models.xmlgen;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MeshXmlResponse {

    private Long id;

    private String name;

    private String content;
}
