package ru.stepanov.uocns.network;

import com.trolltech.qt.QSignalEmitter;
import com.trolltech.qt.QThread;
import org.springframework.stereotype.Component;
import ru.stepanov.uocns.network.common.IConstants;
import ru.stepanov.uocns.network.gui.TFormMain;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Component
public class TControllerOCNS
        extends QSignalEmitter
        implements Runnable,
        IControllerOCNS {
    private static int[] $SWITCH_TABLE$network$TCommandOCNS;
    public QSignalEmitter.Signal2<TControllerOCNS, String> fSglProgressChanged;
    public QSignalEmitter.Signal2<TControllerOCNS, String> fSglNetworkSimulated;
    public QSignalEmitter.Signal1<TControllerOCNS> fSglNetworkSimulatedAll;
    private volatile QThread fThreadOCNS;
    private volatile boolean fIsNeedThreadPause;
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
    private TFormMain form;

    public TControllerOCNS() {
        this.fSglNetworkSimulatedAll = new QSignalEmitter.Signal1();
        this.fSglProgressChanged = new QSignalEmitter.Signal2();
        this.fSglNetworkSimulated = new QSignalEmitter.Signal2();
        this.fIsNeedThreadPause = false;
    }

    public TControllerOCNS(TFormMain form) {
        this.fSglNetworkSimulatedAll = new QSignalEmitter.Signal1();
        this.fSglProgressChanged = new QSignalEmitter.Signal2();
        this.fSglNetworkSimulated = new QSignalEmitter.Signal2();
        this.fIsNeedThreadPause = false;
        this.form = form;
    }

    static int[] $SWITCH_TABLE$network$TCommandOCNS() {
        int[] arrn;
        int[] arrn2 = $SWITCH_TABLE$network$TCommandOCNS;
        if (arrn2 != null) {
            return arrn2;
        }
        arrn = new int[TCommandOCNS.values().length];
        try {
            arrn[TCommandOCNS.Continue.ordinal()] = 3;
        } catch (NoSuchFieldError ignored) {
        }
        try {
            arrn[TCommandOCNS.Pause.ordinal()] = 2;
        } catch (NoSuchFieldError ignored) {
        }
        try {
            arrn[TCommandOCNS.Start.ordinal()] = 1;
        } catch (NoSuchFieldError ignored) {
        }
        try {
            arrn[TCommandOCNS.Stop.ordinal()] = 4;
        } catch (NoSuchFieldError ignored) {
        }
        $SWITCH_TABLE$network$TCommandOCNS = arrn;
        return $SWITCH_TABLE$network$TCommandOCNS;
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

    public void run() {
        boolean aFileAppend = false;
        double aDestInjectionRate = 0.05;
        while (aDestInjectionRate <= 1.0) {
            long aCountPercent;
            TNetworkManager.getInstance().doNetworkSetupNext(false);
            TNetworkManager.getUtilities().setRandSeedRandom();
            IConstants.fConfigNoC.fPacketAvgGenTime = (int) ((double) IConstants.fConfigNoC.fPacketAvgLenght / aDestInjectionRate);
            this.ResetPerformanceParameters();
            long iCountTotal = IConstants.fConfigNoC.fCountPacketRx * (long) IConstants.fConfigNoC.fCountCores;
            long aCountNextPersent = aCountPercent = iCountTotal / 100 * (long) IConstants.fConfigNoC.fCountRun;
            int simulationProgress = 0;
            int iSimulatorRun = 0;
            while (iSimulatorRun < IConstants.fConfigNoC.fCountRun) {
                TNetworkManager.getInstance().doNetworkReset(this);
                TNetwork iNetwork = TNetworkManager.getNetworkInstance();
                TNetworkManager.getUtilities().setRandSeedRandom();
                iNetwork.setInitalEvents();
                int iClock = 0;
                do {
                    TControllerOCNS tControllerOCNS = this;
                    synchronized (tControllerOCNS) {
                        try {
                            while (this.fIsNeedThreadPause && this.fThreadOCNS == Thread.currentThread()) {
                                this.wait();
                            }
                        } catch (InterruptedException interruptedException) {
                            // empty catch block
                        }
                        if (this.fThreadOCNS != Thread.currentThread()) {
                            if (1 == IConstants.fConfigNoC.fCountRun) {
                                IConstants.fConfigNoC.fCountClocksTotal = iClock;
                                this.fThreadOCNS = (QThread) Thread.currentThread();
                                break;
                            }
                            return;
                        }
                    }
                    try {
                        iNetwork.moveTraficTxCore(iClock);
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
                        iNetwork.moveTraficTxRouter(iClock);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    try {
                        iNetwork.doRestorePackets(iClock);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    iNetwork.doUpdateStatistic(iClock);
                    iNetwork.doPrepareNextClock(iClock);
                    int aCountRx = (int) TNetworkManager.getStatistic().getCountPacketRxTotal();
                    if (aCountRx > 0 && (long) aCountRx / aCountNextPersent > 0) {
                        this.fSglProgressChanged.emit(this, "" + ++simulationProgress);
                        aCountNextPersent += aCountPercent;
                    }
                    ++iClock;
                } while (true);
                this.fPacketCountTotalTx += (double) TNetworkManager.getStatistic().getCountPacketTxTotal();
                this.fPacketCountTotalRx += TNetworkManager.getStatistic().getCountPacketRxTotal();
                this.fPacketCountGenError += TNetworkManager.getStatistic().getCountNewPacketErrAvg();
                this.fPacketRate += TNetworkManager.getStatistic().getPacketRate();
                this.fFlitRatePerNode += TNetworkManager.getStatistic().getFlitRatePerNode();
                this.fCoreInjectionRate += TNetworkManager.getStatistic().getCoreInjectionRate();
                this.fPacketDelay += TNetworkManager.getStatistic().getCountPacketTimeAvg();
                this.fPacketCountHop += TNetworkManager.getStatistic().getCountPacketHopAvg();
                this.fThroughputNetwork += TNetworkManager.getStatistic().getThroughputNetwork();
                this.fThroughputSwitch += TNetworkManager.getStatistic().getThroughputSwitch();
                this.fUtilizationCoreBufferRx += TNetworkManager.getStatistic().getUtilizationCoreBufferRxAvg();
                this.fUtilizationCoreBufferTx += TNetworkManager.getStatistic().getUtilizationCoreBufferTxAvg();
                this.fUtilizationRouterBufferRx += TNetworkManager.getStatistic().getUtilizationRouterBufferRxAvg();
                this.fUtilizationRouterBufferTx += TNetworkManager.getStatistic().getUtilizationRouterBufferTxAvg();
                this.fUtilizationNetworkBuffer += TNetworkManager.getStatistic().getUtilizationNetworkBufferAvg();
                this.fUtilizationNetworkPLink += TNetworkManager.getStatistic().getUtilizationNetworkPLinkAvg();
                ++iSimulatorRun;
            }
            this.fPacketCountTotalRx /= (double) iSimulatorRun;
            this.fPacketCountTotalTx /= (double) iSimulatorRun;
            this.fPacketCountGenError /= (double) iSimulatorRun;
            this.fThroughputNetwork /= (double) iSimulatorRun;
            this.fThroughputSwitch /= (double) iSimulatorRun;
            this.fPacketRate /= (double) iSimulatorRun;
            this.fFlitRatePerNode /= (double) iSimulatorRun;
            this.fCoreInjectionRate /= (double) iSimulatorRun;
            this.fPacketDelay /= (double) iSimulatorRun;
            this.fPacketCountHop /= (double) iSimulatorRun;
            this.fUtilizationCoreBufferRx /= (double) iSimulatorRun;
            this.fUtilizationCoreBufferTx /= (double) iSimulatorRun;
            this.fUtilizationRouterBufferRx /= (double) iSimulatorRun;
            this.fUtilizationRouterBufferTx /= (double) iSimulatorRun;
            this.fUtilizationNetworkPLink /= (double) iSimulatorRun;
            this.fUtilizationNetworkBuffer /= (double) iSimulatorRun;
            this.fSglNetworkSimulated.emit(this, this.GetPerformanceReport(IConstants.fConfigNoC.fCountClocksTotal));
            String aPlotPoint = String.format("%10d\t\t%10.3f\t\t%4.2f\t\t%10.3f\n", IConstants.fConfigNoC.fPacketAvgGenTime, this.fPacketDelay, aDestInjectionRate, this.fCoreInjectionRate);
            try {
                String path;

                if (form != null && form.outputTableFilepath != null && !form.outputTableFilepath.isEmpty()) {
                    path = form.outputTableFilepath;
                    Path finalPath = Paths.get(path);
                    try {
                        Files.createDirectories(finalPath.getParent());
                    } catch (Exception e) {
                    }
                } else {
                    path = IConstants.fConfigNoC.fDescription + ".txt";
                }

                File aFile = new File(path);
                if (!aFile.exists()) {
                    aFile.createNewFile();
                }

                FileWriter aWriter = new FileWriter(aFile, aFileAppend);
                BufferedWriter aBufferedWriter = new BufferedWriter(aWriter);
                aBufferedWriter.write(aPlotPoint);
                aBufferedWriter.close();
                aFileAppend = true;
            } catch (Exception x) {
                return;
            }
            aDestInjectionRate += 0.05;
        }
        this.fSglProgressChanged.emit(this, "100");
        this.fSglNetworkSimulatedAll.emit(this);
    }

    private String getHtmlReportParameter(String aParameterName, String aParameterValue) {
        return String.format("<tr><td width=25></td><td >%s</td><td width=75>%s</td></tr>", aParameterName, aParameterValue);
    }

    private String GetPerformanceReport(int aClocksTotal) {
        return String.format("<b>Конфигурация сети на кристалле:</b><p><table width=100%%>%s%s</table><p><table width=100%%>%s%s%s%s</table><p><table width=100%%>%s%s</table><p><table width=100%%>%s%s%s</table><p><b>Усредненные результаты моделирования:</b><p><table width=100%%>%s</table><p><table width=100%%>%s%s%s</table><p><table width=100%%>%s%s%s%s%s</table><p><table width=100%%>%s%s</table><p><table width=100%%>%s%s</table><p><table width=100%%>%s%s</table><p><table width=100%%>%s%s</table><p></p>",
                this.getHtmlReportParameter("Описание сети:", IConstants.fConfigNoC.fDescription),
                this.getHtmlReportParameter("Количество IP-ядер:", "" + IConstants.fConfigNoC.fCountCores),
                this.getHtmlReportParameter("Размер флита, бит:", "" + IConstants.fConfigNoC.fFlitSize),
                this.getHtmlReportParameter("Средняя длина пакета, флитов:", "" + IConstants.fConfigNoC.fPacketAvgLenght),
                this.getHtmlReportParameter("Фиксированный размер пакета, флитов:", "" + IConstants.fConfigNoC.fPacketIsFixedLength),
                this.getHtmlReportParameter("Средний период генерации пакетов, тактов:", "" + IConstants.fConfigNoC.fPacketAvgGenTime),
                this.getHtmlReportParameter("Количество виртуальных каналов:", "" + IConstants.fConfigNoC.fCountVLinkPerPLink),
                this.getHtmlReportParameter("Размер буфера виртуального канала, флитов:", "" + IConstants.fConfigNoC.fSizeVLinkBuffer),
                this.getHtmlReportParameter("Время моделирования, принятых пакетов:", "" + IConstants.fConfigNoC.fCountPacketRx),
                this.getHtmlReportParameter("Время насыщения модели сети, принятых пакетов:", "" + IConstants.fConfigNoC.fCountPacketRxWarmUp),
                this.getHtmlReportParameter("Количество прогонов симулятора:", "" + IConstants.fConfigNoC.fCountRun),
                this.getHtmlReportParameter("Время моделирования, тактов:", "" + aClocksTotal),
                this.getHtmlReportParameter("Количество отправленных пакетов:", String.format("%.0f", this.fPacketCountTotalTx)),
                this.getHtmlReportParameter("Количество принятых пакетов:", String.format("%.0f", this.fPacketCountTotalRx)),
                this.getHtmlReportParameter("Ошибки генерации пакетов:", String.format("%.0f", this.fPacketCountGenError)),
                this.getHtmlReportParameter("Скорость генерации пакетов, пакетов/такт:", String.format("%.3f", this.fPacketRate)),
                this.getHtmlReportParameter("Скорость генерации флитов, флитов/такт/ядро:", String.format("%.3f", this.fFlitRatePerNode)),
                this.getHtmlReportParameter("Скорость отправки флитов, флитов/такт/ядро:", String.format("%.3f", this.fCoreInjectionRate)),
                this.getHtmlReportParameter("Время доставки пакета, тактов:", String.format("%.3f", this.fPacketDelay)),
                this.getHtmlReportParameter("Количество хопов пакета:", String.format("%.3f", this.fPacketCountHop - 2.0)),
                this.getHtmlReportParameter("Пропускная способность сети, флитов/такт:", String.format("%.3f", this.fThroughputNetwork)),
                this.getHtmlReportParameter("Пропускная способность роутера, флитов/такт:", String.format("%.3f", this.fThroughputSwitch)),
                this.getHtmlReportParameter("Загруженность принимающих буферов IP-ядра, %:", String.format("%.3f", this.fUtilizationCoreBufferRx)),
                this.getHtmlReportParameter("Загруженность передающих буферов IP-ядра %:", String.format("%.3f", this.fUtilizationCoreBufferTx)),
                this.getHtmlReportParameter("Загруженность принимающих буферов роутеров, %:", String.format("%.3f", this.fUtilizationRouterBufferRx)),
                this.getHtmlReportParameter("Загруженность передающих буферов роутеров, %:", String.format("%.3f", this.fUtilizationRouterBufferTx)),
                this.getHtmlReportParameter("Загруженность буферов сети, %/такт:", String.format("%.3f", this.fUtilizationNetworkBuffer)),
                this.getHtmlReportParameter("Загруженность физических каналов сети, %:", String.format("%.3f", this.fUtilizationNetworkPLink)));
    }

    public void AddCommand(TCommandOCNS aCommandOCNS) {
        switch (TControllerOCNS.$SWITCH_TABLE$network$TCommandOCNS()[aCommandOCNS.ordinal()]) {
            case 1: {
                this.ExecCommandStart();
                break;
            }
            case 4: {
                this.ExecCommandStop();
                break;
            }
            case 2: {
                this.ExecCommandPause();
                break;
            }
            case 3: {
                this.ExecCommandContinue();
                break;
            }
            default: {
            }
        }
    }

    private void ExecCommandStart() {
        this.fThreadOCNS = new QThread(this, "ThreadOCNS");
        this.fThreadOCNS.setDaemon(true);
        this.fThreadOCNS.start();
    }

    private synchronized void ExecCommandStop() {
        this.fThreadOCNS = null;
        this.notify();
        this.fIsNeedThreadPause = false;
    }

    private synchronized void ExecCommandPause() {
        this.fIsNeedThreadPause = true;
    }

    private synchronized void ExecCommandContinue() {
        this.fIsNeedThreadPause = false;
        this.notify();
    }

    @Override
    public void cbTerminate() {
        this.ExecCommandStop();
    }
}

