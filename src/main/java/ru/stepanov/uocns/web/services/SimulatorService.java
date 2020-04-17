package ru.stepanov.uocns.web.services;

import org.springframework.stereotype.Service;
import ru.stepanov.uocns.common.exceptions.CommonException;
import ru.stepanov.uocns.web.interfaces.ISimulatorService;
import ru.stepanov.uocns.web.models.*;

@Service
public class SimulatorService implements ISimulatorService {

    @Override
    public CirculantResponse circulant(CirculantRequest request) throws CommonException {
        return null;
    }

    @Override
    public MeshResponse mesh(MeshRequest request) throws CommonException {
        return null;
    }

    @Override
    public TorusResponse torus(TorusRequest request) throws CommonException {
        return null;
    }
}
