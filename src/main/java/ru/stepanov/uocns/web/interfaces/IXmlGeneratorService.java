package ru.stepanov.uocns.web.interfaces;

import ru.stepanov.uocns.common.exceptions.CommonException;
import ru.stepanov.uocns.web.models.xmlgen.*;

public interface IXmlGeneratorService {

    String XML_MESH = "/xml/mesh";
    String XML_CIRCULANT_OPTIMAL = "/xml/circulant/optimal";
    String XML_CIRCULANT = "/xml/circulant";
    String XML_TORUS = "/xml/torus";

    String MESH_TOPOLOGY = "Mesh";
    String TORUS_TOPOLOGY = "Torus";
    String CIRCULANT_TOPOLOGY = "Circulant";
    String OPTIMAL_CIRCULANT_TOPOLOGY = "CirculantOpt";

    CirculantXmlResponse circulant(CirculantXmlRequest request) throws CommonException;

    CirculantXmlResponse optimalCirculant(CirculantXmlRequest request) throws CommonException;

    MeshXmlResponse mesh(MeshXmlRequest request) throws CommonException;

    TorusXmlResponse torus(TorusXmlRequest request) throws CommonException;
}
