package ru.stepanov.uocns.network;

import com.trolltech.qt.QSignalEmitter;
import com.trolltech.qt.QThread;
import ru.stepanov.uocns.network.gui.TFormMain;
import ru.stepanov.uocns.network.common.IConstants;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

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

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
//    @Override
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
        return String.format("<b>\u041a\u043e\u043d\u0444\u0438\u0433\u0443\u0440\u0430\u0446\u0438\u044f \u0441\u0435\u0442\u0438 \u043d\u0430 \u043a\u0440\u0438\u0441\u0442\u0430\u043b\u043b\u0435:</b><p><table width=100%%>%s%s</table><p><table width=100%%>%s%s%s%s</table><p><table width=100%%>%s%s</table><p><table width=100%%>%s%s%s</table><p><b>\u0423\u0441\u0440\u0435\u0434\u043d\u0435\u043d\u043d\u044b\u0435 \u0440\u0435\u0437\u0443\u043b\u044c\u0442\u0430\u0442\u044b \u043c\u043e\u0434\u0435\u043b\u0438\u0440\u043e\u0432\u0430\u043d\u0438\u044f:</b><p><table width=100%%>%s</table><p><table width=100%%>%s%s%s</table><p><table width=100%%>%s%s%s%s%s</table><p><table width=100%%>%s%s</table><p><table width=100%%>%s%s</table><p><table width=100%%>%s%s</table><p><table width=100%%>%s%s</table><p></p>", this.getHtmlReportParameter("\u041e\u043f\u0438\u0441\u0430\u043d\u0438\u0435 \u0441\u0435\u0442\u0438:", IConstants.fConfigNoC.fDescription), this.getHtmlReportParameter("\u041a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e IP-\u044f\u0434\u0435\u0440:", "" + IConstants.fConfigNoC.fCountCores), this.getHtmlReportParameter("\u0420\u0430\u0437\u043c\u0435\u0440 \u0444\u043b\u0438\u0442\u0430, \u0431\u0438\u0442:", "" + IConstants.fConfigNoC.fFlitSize), this.getHtmlReportParameter("\u0421\u0440\u0435\u0434\u043d\u044f\u044f \u0434\u043b\u0438\u043d\u0430 \u043f\u0430\u043a\u0435\u0442\u0430, \u0444\u043b\u0438\u0442\u043e\u0432:", "" + IConstants.fConfigNoC.fPacketAvgLenght), this.getHtmlReportParameter("\u0424\u0438\u043a\u0441\u0438\u0440\u043e\u0432\u0430\u043d\u043d\u044b\u0439 \u0440\u0430\u0437\u043c\u0435\u0440 \u043f\u0430\u043a\u0435\u0442\u0430, \u0444\u043b\u0438\u0442\u043e\u0432:", "" + IConstants.fConfigNoC.fPacketIsFixedLength), this.getHtmlReportParameter("\u0421\u0440\u0435\u0434\u043d\u0438\u0439 \u043f\u0435\u0440\u0438\u043e\u0434 \u0433\u0435\u043d\u0435\u0440\u0430\u0446\u0438\u0438 \u043f\u0430\u043a\u0435\u0442\u043e\u0432, \u0442\u0430\u043a\u0442\u043e\u0432:", "" + IConstants.fConfigNoC.fPacketAvgGenTime), this.getHtmlReportParameter("\u041a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e \u0432\u0438\u0440\u0442\u0443\u0430\u043b\u044c\u043d\u044b\u0445 \u043a\u0430\u043d\u0430\u043b\u043e\u0432:", "" + IConstants.fConfigNoC.fCountVLinkPerPLink), this.getHtmlReportParameter("\u0420\u0430\u0437\u043c\u0435\u0440 \u0431\u0443\u0444\u0435\u0440\u0430 \u0432\u0438\u0440\u0442\u0443\u0430\u043b\u044c\u043d\u043e\u0433\u043e \u043a\u0430\u043d\u0430\u043b\u0430, \u0444\u043b\u0438\u0442\u043e\u0432:", "" + IConstants.fConfigNoC.fSizeVLinkBuffer), this.getHtmlReportParameter("\u0412\u0440\u0435\u043c\u044f \u043c\u043e\u0434\u0435\u043b\u0438\u0440\u043e\u0432\u0430\u043d\u0438\u044f, \u043f\u0440\u0438\u043d\u044f\u0442\u044b\u0445 \u043f\u0430\u043a\u0435\u0442\u043e\u0432:", "" + IConstants.fConfigNoC.fCountPacketRx), this.getHtmlReportParameter("\u0412\u0440\u0435\u043c\u044f \u043d\u0430\u0441\u044b\u0449\u0435\u043d\u0438\u044f \u043c\u043e\u0434\u0435\u043b\u0438 \u0441\u0435\u0442\u0438, \u043f\u0440\u0438\u043d\u044f\u0442\u044b\u0445 \u043f\u0430\u043a\u0435\u0442\u043e\u0432:", "" + IConstants.fConfigNoC.fCountPacketRxWarmUp), this.getHtmlReportParameter("\u041a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e \u043f\u0440\u043e\u0433\u043e\u043d\u043e\u0432 \u0441\u0438\u043c\u0443\u043b\u044f\u0442\u043e\u0440\u0430:", "" + IConstants.fConfigNoC.fCountRun), this.getHtmlReportParameter("\u0412\u0440\u0435\u043c\u044f \u043c\u043e\u0434\u0435\u043b\u0438\u0440\u043e\u0432\u0430\u043d\u0438\u044f, \u0442\u0430\u043a\u0442\u043e\u0432:", "" + aClocksTotal), this.getHtmlReportParameter("\u041a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e \u043e\u0442\u043f\u0440\u0430\u0432\u043b\u0435\u043d\u043d\u044b\u0445 \u043f\u0430\u043a\u0435\u0442\u043e\u0432:", String.format("%.0f", this.fPacketCountTotalTx)), this.getHtmlReportParameter("\u041a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e \u043f\u0440\u0438\u043d\u044f\u0442\u044b\u0445 \u043f\u0430\u043a\u0435\u0442\u043e\u0432:", String.format("%.0f", this.fPacketCountTotalRx)), this.getHtmlReportParameter("\u041e\u0448\u0438\u0431\u043a\u0438 \u0433\u0435\u043d\u0435\u0440\u0430\u0446\u0438\u0438 \u043f\u0430\u043a\u0435\u0442\u043e\u0432:", String.format("%.0f", this.fPacketCountGenError)), this.getHtmlReportParameter("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0433\u0435\u043d\u0435\u0440\u0430\u0446\u0438\u0438 \u043f\u0430\u043a\u0435\u0442\u043e\u0432, \u043f\u0430\u043a\u0435\u0442\u043e\u0432/\u0442\u0430\u043a\u0442:", String.format("%.3f", this.fPacketRate)), this.getHtmlReportParameter("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u0433\u0435\u043d\u0435\u0440\u0430\u0446\u0438\u0438 \u0444\u043b\u0438\u0442\u043e\u0432, \u0444\u043b\u0438\u0442\u043e\u0432/\u0442\u0430\u043a\u0442/\u044f\u0434\u0440\u043e:", String.format("%.3f", this.fFlitRatePerNode)), this.getHtmlReportParameter("\u0421\u043a\u043e\u0440\u043e\u0441\u0442\u044c \u043e\u0442\u043f\u0440\u0430\u0432\u043a\u0438 \u0444\u043b\u0438\u0442\u043e\u0432, \u0444\u043b\u0438\u0442\u043e\u0432/\u0442\u0430\u043a\u0442/\u044f\u0434\u0440\u043e:", String.format("%.3f", this.fCoreInjectionRate)), this.getHtmlReportParameter("\u0412\u0440\u0435\u043c\u044f \u0434\u043e\u0441\u0442\u0430\u0432\u043a\u0438 \u043f\u0430\u043a\u0435\u0442\u0430, \u0442\u0430\u043a\u0442\u043e\u0432:", String.format("%.3f", this.fPacketDelay)), this.getHtmlReportParameter("\u041a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e \u0445\u043e\u043f\u043e\u0432 \u043f\u0430\u043a\u0435\u0442\u0430:", String.format("%.3f", this.fPacketCountHop - 2.0)), this.getHtmlReportParameter("\u041f\u0440\u043e\u043f\u0443\u0441\u043a\u043d\u0430\u044f \u0441\u043f\u043e\u0441\u043e\u0431\u043d\u043e\u0441\u0442\u044c \u0441\u0435\u0442\u0438, \u0444\u043b\u0438\u0442\u043e\u0432/\u0442\u0430\u043a\u0442:", String.format("%.3f", this.fThroughputNetwork)), this.getHtmlReportParameter("\u041f\u0440\u043e\u043f\u0443\u0441\u043a\u043d\u0430\u044f \u0441\u043f\u043e\u0441\u043e\u0431\u043d\u043e\u0441\u0442\u044c \u0440\u043e\u0443\u0442\u0435\u0440\u0430, \u0444\u043b\u0438\u0442\u043e\u0432/\u0442\u0430\u043a\u0442:", String.format("%.3f", this.fThroughputSwitch)), this.getHtmlReportParameter("\u0417\u0430\u0433\u0440\u0443\u0436\u0435\u043d\u043d\u043e\u0441\u0442\u044c \u043f\u0440\u0438\u043d\u0438\u043c\u0430\u044e\u0449\u0438\u0445 \u0431\u0443\u0444\u0435\u0440\u043e\u0432 IP-\u044f\u0434\u0440\u0430, %:", String.format("%.3f", this.fUtilizationCoreBufferRx)), this.getHtmlReportParameter("\u0417\u0430\u0433\u0440\u0443\u0436\u0435\u043d\u043d\u043e\u0441\u0442\u044c \u043f\u0435\u0440\u0435\u0434\u0430\u044e\u0449\u0438\u0445 \u0431\u0443\u0444\u0435\u0440\u043e\u0432 IP-\u044f\u0434\u0440\u0430 %:", String.format("%.3f", this.fUtilizationCoreBufferTx)), this.getHtmlReportParameter("\u0417\u0430\u0433\u0440\u0443\u0436\u0435\u043d\u043d\u043e\u0441\u0442\u044c \u043f\u0440\u0438\u043d\u0438\u043c\u0430\u044e\u0449\u0438\u0445 \u0431\u0443\u0444\u0435\u0440\u043e\u0432 \u0440\u043e\u0443\u0442\u0435\u0440\u043e\u0432, %:", String.format("%.3f", this.fUtilizationRouterBufferRx)), this.getHtmlReportParameter("\u0417\u0430\u0433\u0440\u0443\u0436\u0435\u043d\u043d\u043e\u0441\u0442\u044c \u043f\u0435\u0440\u0435\u0434\u0430\u044e\u0449\u0438\u0445 \u0431\u0443\u0444\u0435\u0440\u043e\u0432 \u0440\u043e\u0443\u0442\u0435\u0440\u043e\u0432, %:", String.format("%.3f", this.fUtilizationRouterBufferTx)), this.getHtmlReportParameter("\u0417\u0430\u0433\u0440\u0443\u0436\u0435\u043d\u043d\u043e\u0441\u0442\u044c \u0431\u0443\u0444\u0435\u0440\u043e\u0432 \u0441\u0435\u0442\u0438, %/\u0442\u0430\u043a\u0442:", String.format("%.3f", this.fUtilizationNetworkBuffer)), this.getHtmlReportParameter("\u0417\u0430\u0433\u0440\u0443\u0436\u0435\u043d\u043d\u043e\u0441\u0442\u044c \u0444\u0438\u0437\u0438\u0447\u0435\u0441\u043a\u0438\u0445 \u043a\u0430\u043d\u0430\u043b\u043e\u0432 \u0441\u0435\u0442\u0438, %:", String.format("%.3f", this.fUtilizationNetworkPLink)));
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

