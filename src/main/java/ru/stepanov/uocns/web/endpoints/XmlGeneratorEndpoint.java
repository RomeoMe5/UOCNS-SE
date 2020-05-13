package ru.stepanov.uocns.web.endpoints;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import ru.stepanov.uocns.common.controllers.AbstractController;
import ru.stepanov.uocns.common.exceptions.CommonException;
import ru.stepanov.uocns.web.interfaces.IXmlGeneratorService;
import ru.stepanov.uocns.web.models.xmlgen.*;
import ru.stepanov.uocns.web.services.XmlGeneratorService;

@RestController
public class XmlGeneratorEndpoint extends AbstractController implements IXmlGeneratorService {

    @Autowired
    XmlGeneratorService xmlGeneratorService;

    @Override
    @CrossOrigin
    @RequestMapping(value = XML_CIRCULANT, method = RequestMethod.POST)
    public CirculantXmlResponse circulant(@RequestBody CirculantXmlRequest request) throws CommonException {
        return xmlGeneratorService.circulant(request);
    }

    @Override
    @CrossOrigin
    @RequestMapping(value = XML_CIRCULANT_OPTIMAL, method = RequestMethod.POST)
    public CirculantXmlResponse optimalCirculant(@RequestBody CirculantXmlRequest request) throws CommonException {
        return xmlGeneratorService.optimalCirculant(request);
    }

    @Override
    @CrossOrigin
    @RequestMapping(value = XML_MESH, method = RequestMethod.POST)
    public MeshXmlResponse mesh(@RequestBody MeshXmlRequest request) throws CommonException {
        return xmlGeneratorService.mesh(request);
    }

    @Override
    @CrossOrigin
    @RequestMapping(value = XML_TORUS, method = RequestMethod.POST)
    public TorusXmlResponse torus(@RequestBody TorusXmlRequest request) throws CommonException {
        return xmlGeneratorService.torus(request);
    }
}
