package ru.stepanov.uocns.web.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import ru.stepanov.uocns.common.exceptions.CommonException;
import ru.stepanov.uocns.web.interfaces.ISimulatorService;
import ru.stepanov.uocns.web.models.*;
import ru.stepanov.uocns.web.services.SimulatorService;

@RestController
public class SimulatorEndpoint implements ISimulatorService {

    @Autowired
    SimulatorService simulatorService;

    @Override
    @CrossOrigin
    @RequestMapping(value = CIRCULANT, method = RequestMethod.POST)
    public CirculantResponse circulant(CirculantRequest request) throws CommonException {
        return simulatorService.circulant(request);
    }

    @Override
    @CrossOrigin
    @RequestMapping(value = MESH, method = RequestMethod.POST)
    public MeshResponse mesh(MeshRequest request) throws CommonException {
        return simulatorService.mesh(request);
    }

    @Override
    @CrossOrigin
    @RequestMapping(value = TORUS, method = RequestMethod.POST)
    public TorusResponse torus(TorusRequest request) throws CommonException {
        return simulatorService.torus(request);
    }
}
