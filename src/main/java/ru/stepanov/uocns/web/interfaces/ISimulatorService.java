package ru.stepanov.uocns.web.interfaces;

import ru.stepanov.uocns.common.exceptions.CommonException;
import ru.stepanov.uocns.web.models.*;

public interface ISimulatorService {

    String MESH = "/mesh";
    String CIRCULANT = "/circulant";
    String TORUS = "/torus";

    CirculantResponse circulant(CirculantRequest request) throws CommonException;

    MeshResponse mesh(MeshRequest request) throws CommonException;

    TorusResponse torus(TorusRequest request) throws CommonException;
}
