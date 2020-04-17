package ru.stepanov.uocns.web.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
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
    public CirculantResponse circulant(@RequestBody CirculantRequest request) throws CommonException {
        return simulatorService.circulant(request);
    }

    @Override
    @CrossOrigin
    @RequestMapping(value = CIRCULANT_OPTIMAL, method = RequestMethod.POST)
    public CirculantResponse optimalCirculant(@RequestBody CirculantRequest request) throws CommonException {
        return simulatorService.optimalCirculant(request);
    }

    @Override
    @CrossOrigin
    @RequestMapping(value = MESH, method = RequestMethod.POST)
    public MeshResponse mesh(@RequestBody MeshRequest request) throws CommonException {
        return simulatorService.mesh(request);
    }

    @Override
    @CrossOrigin
    @RequestMapping(value = TORUS, method = RequestMethod.POST)
    public TorusResponse torus(@RequestBody TorusRequest request) throws CommonException {
        return simulatorService.torus(request);
    }
}
