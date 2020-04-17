package ru.stepanov.uocns.web.interfaces;

import ru.stepanov.uocns.common.exceptions.CommonException;
import ru.stepanov.uocns.web.models.*;

public interface ISimulatorService {

    String MESH = "/mesh";
    String CIRCULANT_OPTIMAL = "/circulant/optimal";
    String CIRCULANT = "/circulant";
    String TORUS = "/torus";

    String MESH_TOPOLOGY = "Mesh";
    String TORUS_TOPOLOGY = "Torus";
    String CIRCULANT_TOPOLOGY = "Circulant";
    String OPTIMAL_CIRCULANT_TOPOLOGY = "CirculantOpt";

    CirculantResponse circulant(CirculantRequest request) throws CommonException;

    CirculantResponse optimalCirculant(CirculantRequest request) throws CommonException;

    MeshResponse mesh(MeshRequest request) throws CommonException;

    TorusResponse torus(TorusRequest request) throws CommonException;
}
