package ru.stepanov.uocns.network;

import org.apache.cayenne.ObjectContext;
import org.apache.cayenne.query.SelectById;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.stepanov.uocns.database.entities.Topology;
import ru.stepanov.uocns.database.entities.TopologyReport;
import ru.stepanov.uocns.database.entities.TopologyTable;
import ru.stepanov.uocns.database.services.DatabaseService;
import ru.stepanov.uocns.network.common.IConstants;
import ru.stepanov.uocns.web.services.SimulatorService;

import javax.annotation.PostConstruct;

@Component
public class TControllerOCNS implements IControllerOCNS {

    private double fThroughputNetwork;
    private double fThroughputSwitch;
    private double fPacketDelay;
    private double fPacketCountGenError;
    private double fUtilizationNetworkPLink;
    private double fPacketRate;
    private double fFlitRatePerNode;
    private double fCoreInjectionRate;
    private double fUtilizationCoreBufferRx;
    private double fUtilizationCoreBufferTx;
    private double fUtilizationRouterBufferRx;
    private double fUtilizationRouterBufferTx;
    private double fUtilizationNetworkBuffer;
    private double fPacketCountHop;
    private double fPacketCountTotalTx;
    private double fPacketCountTotalRx;

    @Autowired
    DatabaseService databaseService;

    ObjectContext objectContext;

    protected static final Logger log = LoggerFactory.getLogger(SimulatorService.class);


    @PostConstruct
    public void init() {
        objectContext = databaseService.getContext();
    }

    void ResetPerformanceParameters() {
        this.fPacketCountTotalTx = 0.0;
        this.fPacketCountGenError = 0.0;
        this.fPacketDelay = 0.0;
        this.fThroughputSwitch = 0.0;
        this.fThroughputNetwork = 0.0;
        this.fUtilizationCoreBufferTx = 0.0;
        this.fUtilizationCoreBufferRx = 0.0;
        this.fPacketRate = 0.0;
        this.fUtilizationNetworkPLink = 0.0;
        this.fUtilizationNetworkBuffer = 0.0;
        this.fUtilizationRouterBufferTx = 0.0;
        this.fUtilizationRouterBufferRx = 0.0;
        this.fPacketCountTotalRx = 0.0;
        this.fPacketCountHop = 0.0;
        this.fCoreInjectionRate = 0.0;
        this.fFlitRatePerNode = 0.0;
    }

    public void simulate(Double destInjectionRate, Long topologyId, String configPath) {

        TNetworkManager tNetworkManager = new TNetworkManager(configPath);

        Topology topology = SelectById.query(Topology.class, topologyId).selectFirst(objectContext);

        TopologyReport topologyReport = objectContext.newObject(TopologyReport.class);
        topologyReport.setReportToTopology(topology);
        topologyReport.setName(IConstants.fConfigNoC.fDescription + ".html");
        StringBuilder topologyReportContent = new StringBuilder();

        TopologyTable topologyTable = objectContext.newObject(TopologyTable.class);
        topologyTable.setTableToTopology(topology);
        topologyTable.setName(IConstants.fConfigNoC.fDescription + ".txt");
        StringBuilder topologyTableContent = new StringBuilder();


        long aCountPercent;
        tNetworkManager.doNetworkSetupNext(false);
        tNetworkManager.getUtilities().setRandSeedRandom();
        IConstants.fConfigNoC.fPacketAvgGenTime = (int) ((double) IConstants.fConfigNoC.fPacketAvgLenght / destInjectionRate);
        this.ResetPerformanceParameters();
        long iCountTotal = IConstants.fConfigNoC.fCountPacketRx * (long) IConstants.fConfigNoC.fCountCores;
        long aCountNextPersent = aCountPercent = iCountTotal / 100 * (long) IConstants.fConfigNoC.fCountRun;
        int simulationProgress = 0;
        int iSimulatorRun = 0;
        while (iSimulatorRun < IConstants.fConfigNoC.fCountRun) {
            tNetworkManager.doNetworkReset(this);
            TNetwork iNetwork = tNetworkManager.getNetworkInstance();
            tNetworkManager.getUtilities().setRandSeedRandom();
            iNetwork.setInitalEvents(tNetworkManager);
            int iClock = 0;
            do {
                IConstants.fConfigNoC.fCountClocksTotal = iClock;
                try {
                    iNetwork.moveTraficTxCore(iClock, tNetworkManager);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    iNetwork.setCrossbarLinks(iClock);
                } catch (Exception x) {
                    x.printStackTrace();
                }
                try {
                    iNetwork.moveTraficRxRouter(iClock);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    iNetwork.moveTraficTxRouter(iClock, tNetworkManager);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                try {
                    iNetwork.doRestorePackets(iClock, tNetworkManager);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                iNetwork.doUpdateStatistic(iClock, tNetworkManager);
                iNetwork.doPrepareNextClock(iClock);
                int aCountRx = (int) tNetworkManager.getStatistic().getCountPacketRxTotal();
                if (aCountRx > 0 && (long) aCountRx / aCountNextPersent > 0) {
                    ++simulationProgress;
                    aCountNextPersent += aCountPercent;
                }
                ++iClock;
            } while (simulationProgress < 100);
            this.fPacketCountTotalTx += (double) tNetworkManager.getStatistic().getCountPacketTxTotal();
            this.fPacketCountTotalRx += tNetworkManager.getStatistic().getCountPacketRxTotal();
            this.fPacketCountGenError += tNetworkManager.getStatistic().getCountNewPacketErrAvg();
            this.fPacketRate += tNetworkManager.getStatistic().getPacketRate();
            this.fFlitRatePerNode += tNetworkManager.getStatistic().getFlitRatePerNode();
            this.fCoreInjectionRate += tNetworkManager.getStatistic().getCoreInjectionRate();
            this.fPacketDelay += tNetworkManager.getStatistic().getCountPacketTimeAvg();
            this.fPacketCountHop += tNetworkManager.getStatistic().getCountPacketHopAvg();
            this.fThroughputNetwork += tNetworkManager.getStatistic().getThroughputNetwork();
            this.fThroughputSwitch += tNetworkManager.getStatistic().getThroughputSwitch();
            this.fUtilizationCoreBufferRx += tNetworkManager.getStatistic().getUtilizationCoreBufferRxAvg();
            this.fUtilizationCoreBufferTx += tNetworkManager.getStatistic().getUtilizationCoreBufferTxAvg();
            this.fUtilizationRouterBufferRx += tNetworkManager.getStatistic().getUtilizationRouterBufferRxAvg();
            this.fUtilizationRouterBufferTx += tNetworkManager.getStatistic().getUtilizationRouterBufferTxAvg();
            this.fUtilizationNetworkBuffer += tNetworkManager.getStatistic().getUtilizationNetworkBufferAvg();
            this.fUtilizationNetworkPLink += tNetworkManager.getStatistic().getUtilizationNetworkPLinkAvg();
            ++iSimulatorRun;
        }
        this.fPacketCountTotalRx /= iSimulatorRun;
        this.fPacketCountTotalTx /= iSimulatorRun;
        this.fPacketCountGenError /= iSimulatorRun;
        this.fThroughputNetwork /= iSimulatorRun;
        this.fThroughputSwitch /= iSimulatorRun;
        this.fPacketRate /= iSimulatorRun;
        this.fFlitRatePerNode /= iSimulatorRun;
        this.fCoreInjectionRate /= iSimulatorRun;
        this.fPacketDelay /= iSimulatorRun;
        this.fPacketCountHop /= iSimulatorRun;
        this.fUtilizationCoreBufferRx /= iSimulatorRun;
        this.fUtilizationCoreBufferTx /= iSimulatorRun;
        this.fUtilizationRouterBufferRx /= iSimulatorRun;
        this.fUtilizationRouterBufferTx /= iSimulatorRun;
        this.fUtilizationNetworkPLink /= iSimulatorRun;
        this.fUtilizationNetworkBuffer /= iSimulatorRun;

        String aPlotPoint = String.format("%10d\t\t%10.3f\t\t%4.2f\t\t%10.3f\n", IConstants.fConfigNoC.fPacketAvgGenTime, this.fPacketDelay, destInjectionRate, this.fCoreInjectionRate);

        topologyReportContent.append(GetPerformanceReport(IConstants.fConfigNoC.fCountClocksTotal));
        topologyTableContent.append(aPlotPoint);

        topologyReport.setContent(topologyReportContent.toString());
        topologyTable.setContent(topologyTableContent.toString());

        objectContext.commitChanges();
    }

    private String getReportParameter(String aParameterName, String aParameterValue) {
        return String.format("%s   %s", aParameterName, aParameterValue);
    }

    private String GetPerformanceReport(int aClocksTotal) {
        return String.format("Конфигурация сети на кристалле\n\n%s\n%s\n\n%s\n%s\n%s\n%s\n\n%s\n%s\n\n%s\n%s\n%s\n\n\nУсредненные результаты моделирования\n\n%s\n\n%s\n%s\n%s\n\n%s\n%s\n%s\n%s\n%s\n\n%s\n%s\n\n%s\n%s\n\n%s\n%s\n\n%s\n%s",
                this.getReportParameter("Описание сети:", IConstants.fConfigNoC.fDescription),
                this.getReportParameter("Количество IP-ядер:", "" + IConstants.fConfigNoC.fCountCores),
                this.getReportParameter("Размер флита, бит:", "" + IConstants.fConfigNoC.fFlitSize),
                this.getReportParameter("Средняя длина пакета, флитов:", "" + IConstants.fConfigNoC.fPacketAvgLenght),
                this.getReportParameter("Фиксированный размер пакета, флитов:", "" + IConstants.fConfigNoC.fPacketIsFixedLength),
                this.getReportParameter("Средний период генерации пакетов, тактов:", "" + IConstants.fConfigNoC.fPacketAvgGenTime),
                this.getReportParameter("Количество виртуальных каналов:", "" + IConstants.fConfigNoC.fCountVLinkPerPLink),
                this.getReportParameter("Размер буфера виртуального канала, флитов:", "" + IConstants.fConfigNoC.fSizeVLinkBuffer),
                this.getReportParameter("Время моделирования, принятых пакетов:", "" + IConstants.fConfigNoC.fCountPacketRx),
                this.getReportParameter("Время насыщения модели сети, принятых пакетов:", "" + IConstants.fConfigNoC.fCountPacketRxWarmUp),
                this.getReportParameter("Количество прогонов симулятора:", "" + IConstants.fConfigNoC.fCountRun),
                this.getReportParameter("Время моделирования, тактов:", "" + aClocksTotal),
                this.getReportParameter("Количество отправленных пакетов:", String.format("%.0f", this.fPacketCountTotalTx)),
                this.getReportParameter("Количество принятых пакетов:", String.format("%.0f", this.fPacketCountTotalRx)),
                this.getReportParameter("Ошибки генерации пакетов:", String.format("%.0f", this.fPacketCountGenError)),
                this.getReportParameter("Скорость генерации пакетов, пакетов/такт:", String.format("%.3f", this.fPacketRate)),
                this.getReportParameter("Скорость генерации флитов, флитов/такт/ядро:", String.format("%.3f", this.fFlitRatePerNode)),
                this.getReportParameter("Скорость отправки флитов, флитов/такт/ядро:", String.format("%.3f", this.fCoreInjectionRate)),
                this.getReportParameter("Время доставки пакета, тактов:", String.format("%.3f", this.fPacketDelay)),
                this.getReportParameter("Количество хопов пакета:", String.format("%.3f", this.fPacketCountHop - 2.0)),
                this.getReportParameter("Пропускная способность сети, флитов/такт:", String.format("%.3f", this.fThroughputNetwork)),
                this.getReportParameter("Пропускная способность роутера, флитов/такт:", String.format("%.3f", this.fThroughputSwitch)),
                this.getReportParameter("Загруженность принимающих буферов IP-ядра, %:", String.format("%.3f", this.fUtilizationCoreBufferRx)),
                this.getReportParameter("Загруженность передающих буферов IP-ядра %:", String.format("%.3f", this.fUtilizationCoreBufferTx)),
                this.getReportParameter("Загруженность принимающих буферов роутеров, %:", String.format("%.3f", this.fUtilizationRouterBufferRx)),
                this.getReportParameter("Загруженность передающих буферов роутеров, %:", String.format("%.3f", this.fUtilizationRouterBufferTx)),
                this.getReportParameter("Загруженность буферов сети, %/такт:", String.format("%.3f", this.fUtilizationNetworkBuffer)),
                this.getReportParameter("Загруженность физических каналов сети, %:", String.format("%.3f", this.fUtilizationNetworkPLink)));
    }

    @Override
    public void cbTerminate() {
    }

}

