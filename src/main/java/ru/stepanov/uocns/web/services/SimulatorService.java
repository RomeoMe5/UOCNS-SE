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
import ru.stepanov.uocns.database.entities.TopologyReport;
import ru.stepanov.uocns.database.entities.TopologyTable;
import ru.stepanov.uocns.database.entities.TopologyXml;
import ru.stepanov.uocns.database.services.DatabaseService;
import ru.stepanov.uocns.network.TControllerOCNS;
import ru.stepanov.uocns.network.common.generator.Circulant;
import ru.stepanov.uocns.network.common.generator.Mesh;
import ru.stepanov.uocns.network.common.generator.Torus;
import ru.stepanov.uocns.web.interfaces.ISimulatorService;
import ru.stepanov.uocns.web.models.simulator.*;
import ru.stepanov.uocns.web.services.util.XmlHelper;

import javax.annotation.PostConstruct;


@Service
public class SimulatorService implements ISimulatorService {

    @Autowired
    DatabaseService databaseService;

    @Autowired
    TControllerOCNS fControllerOCNS;

    @Autowired
    XmlHelper xmlHelper;

    ObjectContext objectContext;

    protected static final Logger log = LoggerFactory.getLogger(SimulatorService.class);

    @PostConstruct
    public void init() {
        objectContext = databaseService.getContext();
    }

    @Override
    public CirculantResponse circulant(CirculantRequest request) throws CommonException {
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

            String configPath = xmlHelper.createXml(circulantNetwork.getNetlist(), circulantNetwork.getRouting(), description, (Long) topology.getObjectId().getIdSnapshot().get("id"));

            fControllerOCNS.simulate(request.getDestInjectionRate(), (Long) topology.getObjectId().getIdSnapshot().get("id"), configPath);

            TopologyReport topologyReport = ObjectSelect.query(TopologyReport.class)
                    .where(TopologyReport.REPORT_TO_TOPOLOGY.eq(topology)).selectFirst(objectContext);
            TopologyTable topologyTable = ObjectSelect.query(TopologyTable.class)
                    .where(TopologyTable.TABLE_TO_TOPOLOGY.eq(topology)).selectFirst(objectContext);
            TopologyXml topologyXml = ObjectSelect.query(TopologyXml.class)
                    .where(TopologyXml.XML_TO_TOPOLOGY.eq(topology)).selectFirst(objectContext);

            return CirculantResponse.builder()
                    .id((Long) topology.getObjectId().getIdSnapshot().get("id"))
                    .reportId((Long) topologyReport.getObjectId().getIdSnapshot().get("id"))
                    .tableId((Long) topologyTable.getObjectId().getIdSnapshot().get("id"))
                    .xmlId((Long) topologyXml.getObjectId().getIdSnapshot().get("id"))
                    .name(topology.getName())
                    .description(topology.getDescription())
                    .nodes(topology.getNodes())
                    .firstStep(request.getFirstStep())
                    .secondStep(request.getSecondStep())
                    .content(topologyReport.getContent())
                    .build();
        }
    }

    @Override
    public CirculantResponse optimalCirculant(CirculantRequest request) throws CommonException {
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

            String configPath = xmlHelper.createXml(circulantNetwork.getNetlist(), circulantNetwork.getRouting(), description, (Long) topology.getObjectId().getIdSnapshot().get("id"));

            fControllerOCNS.simulate(request.getDestInjectionRate(), (Long) topology.getObjectId().getIdSnapshot().get("id"), configPath);

            TopologyReport topologyReport = ObjectSelect.query(TopologyReport.class)
                    .where(TopologyReport.REPORT_TO_TOPOLOGY.eq(topology)).selectFirst(objectContext);
            TopologyTable topologyTable = ObjectSelect.query(TopologyTable.class)
                    .where(TopologyTable.TABLE_TO_TOPOLOGY.eq(topology)).selectFirst(objectContext);
            TopologyXml topologyXml = ObjectSelect.query(TopologyXml.class)
                    .where(TopologyXml.XML_TO_TOPOLOGY.eq(topology)).selectFirst(objectContext);

            return CirculantResponse.builder()
                    .id((Long) topology.getObjectId().getIdSnapshot().get("id"))
                    .reportId((Long) topologyReport.getObjectId().getIdSnapshot().get("id"))
                    .tableId((Long) topologyTable.getObjectId().getIdSnapshot().get("id"))
                    .xmlId((Long) topologyXml.getObjectId().getIdSnapshot().get("id"))
                    .name(topology.getName())
                    .description(topology.getDescription())
                    .nodes(topology.getNodes())
                    .content(topologyReport.getContent())
                    .build();
        }
    }

    @Override
    public MeshResponse mesh(MeshRequest request) throws CommonException {
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

        String configPath = xmlHelper.createXml(meshNetwork.getNetlist(), meshNetwork.getRouting(), description, (Long) topology.getObjectId().getIdSnapshot().get("id"));

        fControllerOCNS.simulate(request.getDestInjectionRate(), (Long) topology.getObjectId().getIdSnapshot().get("id"), configPath);

        TopologyReport topologyReport = ObjectSelect.query(TopologyReport.class)
                .where(TopologyReport.REPORT_TO_TOPOLOGY.eq(topology)).selectFirst(objectContext);
        TopologyTable topologyTable = ObjectSelect.query(TopologyTable.class)
                .where(TopologyTable.TABLE_TO_TOPOLOGY.eq(topology)).selectFirst(objectContext);
        TopologyXml topologyXml = ObjectSelect.query(TopologyXml.class)
                .where(TopologyXml.XML_TO_TOPOLOGY.eq(topology)).selectFirst(objectContext);

        return MeshResponse.builder()
                .id((Long) topology.getObjectId().getIdSnapshot().get("id"))
                .reportId((Long) topologyReport.getObjectId().getIdSnapshot().get("id"))
                .tableId((Long) topologyTable.getObjectId().getIdSnapshot().get("id"))
                .xmlId((Long) topologyXml.getObjectId().getIdSnapshot().get("id"))
                .name(topology.getName())
                .description(topology.getDescription())
                .columns(topology.getColumns())
                .rows(topology.getRows())
                .content(topologyReport.getContent())
                .build();
    }

    @Override
    public TorusResponse torus(TorusRequest request) throws CommonException {
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

        String configPath = xmlHelper.createXml(torusNetwork.getNetlist(), torusNetwork.getRouting(), description, (Long) topology.getObjectId().getIdSnapshot().get("id"));


        fControllerOCNS.simulate(request.getDestInjectionRate(), (Long) topology.getObjectId().getIdSnapshot().get("id"), configPath);

        TopologyReport topologyReport = ObjectSelect.query(TopologyReport.class)
                .where(TopologyReport.REPORT_TO_TOPOLOGY.eq(topology)).selectFirst(objectContext);
        TopologyTable topologyTable = ObjectSelect.query(TopologyTable.class)
                .where(TopologyTable.TABLE_TO_TOPOLOGY.eq(topology)).selectFirst(objectContext);
        TopologyXml topologyXml = ObjectSelect.query(TopologyXml.class)
                .where(TopologyXml.XML_TO_TOPOLOGY.eq(topology)).selectFirst(objectContext);

        return TorusResponse.builder()
                .id((Long) topology.getObjectId().getIdSnapshot().get("id"))
                .reportId((Long) topologyReport.getObjectId().getIdSnapshot().get("id"))
                .tableId((Long) topologyTable.getObjectId().getIdSnapshot().get("id"))
                .xmlId((Long) topologyXml.getObjectId().getIdSnapshot().get("id"))
                .name(topology.getName())
                .description(topology.getDescription())
                .columns(topology.getColumns())
                .rows(topology.getRows())
                .content(topologyReport.getContent())
                .build();
    }

}
