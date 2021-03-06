package ru.stepanov.uocns.web.services;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.ObjectSelect;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.stepanov.uocns.common.exceptions.BadRequestException;
import ru.stepanov.uocns.common.exceptions.CommonException;
import ru.stepanov.uocns.database.entities.Topology;
import ru.stepanov.uocns.database.entities.TopologyXml;
import ru.stepanov.uocns.database.services.DatabaseService;
import ru.stepanov.uocns.network.common.generator.Circulant;
import ru.stepanov.uocns.network.common.generator.Mesh;
import ru.stepanov.uocns.network.common.generator.Torus;
import ru.stepanov.uocns.web.interfaces.IXmlGeneratorService;
import ru.stepanov.uocns.web.models.xmlgen.*;
import ru.stepanov.uocns.web.services.util.XmlHelper;

import javax.annotation.PostConstruct;

@Service
public class XmlGeneratorService implements IXmlGeneratorService {

    @Autowired
    DatabaseService databaseService;

    @Autowired
    XmlHelper xmlHelper;

    ObjectContext objectContext;

    protected static final Logger log = LoggerFactory.getLogger(XmlGeneratorService.class);

    @PostConstruct
    public void init() {
        objectContext = databaseService.getContext();
    }

    @Override
    public CirculantXmlResponse circulant(CirculantXmlRequest request) throws CommonException {
        if (request.getNodes() < 5) {
            log.error("Invalid number of nodes for optimal circulant");
            throw new BadRequestException("Invalid number of nodes for optimal circulant");
        } else {
            Topology topology = objectContext.newObject(Topology.class);
            topology.setName(request.getName());
            topology.setNodes(request.getNodes());

            Circulant circulantNetwork = new Circulant(request.getNodes(), request.getFirstStep(), request.getSecondStep());
            circulantNetwork.createNetlist();
            circulantNetwork.createRouting(circulantNetwork.adjacencyMatrix(circulantNetwork.getNetlist(), request.getNodes()),
                    circulantNetwork.getNetlist());

            String description = CIRCULANT_TOPOLOGY + "-(" + request.getNodes() + ", " + request.getFirstStep() + ", " + request.getSecondStep() + ")";
            topology.setDescription(description);
            objectContext.commitChanges();

            xmlHelper.createXml(circulantNetwork.getNetlist(), circulantNetwork.getRouting(), description, (Long) topology.getObjectId().getIdSnapshot().get("id"));

            TopologyXml topologyXml = ObjectSelect.query(TopologyXml.class)
                    .where(TopologyXml.XML_TO_TOPOLOGY.eq(topology)).selectFirst(objectContext);

            return CirculantXmlResponse.builder()
                    .id((Long) topologyXml.getObjectId().getIdSnapshot().get("id"))
                    .name(topologyXml.getName())
                    .content(topologyXml.getContent())
                    .build();
        }
    }

    @Override
    public CirculantXmlResponse optimalCirculant(CirculantXmlRequest request) throws CommonException {
        if (request.getNodes() < 5) {
            log.error("Invalid number of nodes for optimal circulant");
            throw new BadRequestException("Invalid number of nodes for optimal circulant");
        } else {
            Topology topology = objectContext.newObject(Topology.class);
            topology.setName(request.getName());
            topology.setNodes(request.getNodes());

            Circulant circulantNetwork = new Circulant(request.getNodes());
            circulantNetwork.createNetlist();
            circulantNetwork.createRouting(circulantNetwork.adjacencyMatrix(circulantNetwork.getNetlist(), request.getNodes()),
                    circulantNetwork.getNetlist());

            String description = OPTIMAL_CIRCULANT_TOPOLOGY + "-(" + request.getNodes() + ", " + circulantNetwork.s1 + ", " + circulantNetwork.s2 + ")";
            topology.setDescription(description);
            objectContext.commitChanges();

            xmlHelper.createXml(circulantNetwork.getNetlist(), circulantNetwork.getRouting(), description, (Long) topology.getObjectId().getIdSnapshot().get("id"));

            TopologyXml topologyXml = ObjectSelect.query(TopologyXml.class)
                    .where(TopologyXml.XML_TO_TOPOLOGY.eq(topology)).selectFirst(objectContext);

            return CirculantXmlResponse.builder()
                    .id((Long) topologyXml.getObjectId().getIdSnapshot().get("id"))
                    .name(topologyXml.getName())
                    .content(topologyXml.getContent())
                    .build();
        }
    }

    @Override
    public MeshXmlResponse mesh(MeshXmlRequest request) throws CommonException {
        Topology topology = objectContext.newObject(Topology.class);
        topology.setName(request.getName());
        topology.setColumns(request.getColumns());
        topology.setRows(request.getRows());

        Mesh meshNetwork = new Torus(request.getColumns(), request.getRows());
        meshNetwork.createNetlist();
        meshNetwork.createRouting();

        String description = MESH_TOPOLOGY + "-(" + request.getColumns() + ", " + request.getRows() + ")";
        topology.setDescription(description);
        objectContext.commitChanges();

        xmlHelper.createXml(meshNetwork.getNetlist(), meshNetwork.getRouting(), description, (Long) topology.getObjectId().getIdSnapshot().get("id"));

        TopologyXml topologyXml = ObjectSelect.query(TopologyXml.class)
                .where(TopologyXml.XML_TO_TOPOLOGY.eq(topology)).selectFirst(objectContext);

        return MeshXmlResponse.builder()
                .id((Long) topologyXml.getObjectId().getIdSnapshot().get("id"))
                .name(topologyXml.getName())
                .content(topologyXml.getContent())
                .build();
    }

    @Override
    public TorusXmlResponse torus(TorusXmlRequest request) throws CommonException {
        Topology topology = objectContext.newObject(Topology.class);
        topology.setName(request.getName());
        topology.setColumns(request.getColumns());
        topology.setRows(request.getRows());

        Torus torusNetwork = new Torus(request.getColumns(), request.getRows());
        torusNetwork.createNetlist();
        torusNetwork.createRouting();

        String description = TORUS_TOPOLOGY + "-(" + request.getColumns() + ", " + request.getRows() + ")";
        topology.setDescription(description);
        objectContext.commitChanges();

        xmlHelper.createXml(torusNetwork.getNetlist(), torusNetwork.getRouting(), description, (Long) topology.getObjectId().getIdSnapshot().get("id"));

        TopologyXml topologyXml = ObjectSelect.query(TopologyXml.class)
                .where(TopologyXml.XML_TO_TOPOLOGY.eq(topology)).selectFirst(objectContext);

        return TorusXmlResponse.builder()
                .id((Long) topologyXml.getObjectId().getIdSnapshot().get("id"))
                .name(topologyXml.getName())
                .content(topologyXml.getContent())
                .build();
    }
}
