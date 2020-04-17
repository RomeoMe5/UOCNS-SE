package ru.stepanov.uocns.network.gui;

import com.trolltech.qt.gui.QApplication;
import com.trolltech.qt.gui.QFileDialog;
import com.trolltech.qt.gui.QMainWindow;
import ru.stepanov.uocns.network.TCommandOCNS;
import ru.stepanov.uocns.network.TControllerOCNS;
import ru.stepanov.uocns.network.TNetworkManager;
import ru.stepanov.uocns.network.common.generator.Circulant;
import ru.stepanov.uocns.network.common.generator.Mesh;
import ru.stepanov.uocns.network.common.generator.Torus;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public class TFormMain extends QMainWindow {
    private static /* synthetic */ int[] $SWITCH_TABLE$gui$TFormMain$TStateOCNS;
    public static final String MESH_TOPOLOGY = "Mesh";
    public static final String TORUS_TOPOLOGY = "Torus";
    public static final String CIRCULANT_TOPOLOGY = "Circulant";
    public static final String OPTIMAL_CIRCULANT_TOPOLOGY = "CirculantOpt";
    public static final String RESULTS_PATH = "D:/UOCNS/"; //TODO: убрать хардкод
    public boolean isCmd = false;
    public String filepath;
    public String outputFilepath;
    public String outputTableFilepath;
    private final TUIFormMain fUIFormMain = new TUIFormMain();
    private final TControllerOCNS fControllerOCNS;


    public TFormMain() {
        this.fUIFormMain.setupUi(this);
        this.SetStateButtons(TStateOCNS.Stoped);
        this.fUIFormMain.TELog.clear();
        this.fControllerOCNS = new TControllerOCNS(this);
        this.fControllerOCNS.fSglProgressChanged.connect((Object) this, "onProgressChanged(TControllerOCNS, String)");
        this.fControllerOCNS.fSglNetworkSimulated.connect((Object) this, "onNetworkSimulated(TControllerOCNS, String)");
        this.fControllerOCNS.fSglNetworkSimulatedAll.connect((Object) this, "OnNetworkSimulatedAll(TControllerOCNS)");
        this.fUIFormMain.fBtnRun.clicked.connect(this, "onBtnRunClick()");
        this.fUIFormMain.fBtnPause.clicked.connect(this, "onBtnPauseClick()");
        this.fUIFormMain.fBtnResume.clicked.connect(this, "onBtnResumeClick()");
        this.fUIFormMain.fBtnStop.clicked.connect(this, "onBtnStopClick()");
        this.fUIFormMain.fBtnExport.clicked.connect(this, "onBtnExportClick()");
    }

    public static void main(String[] args) {
        QApplication.initialize(args);
        TFormMain fFormMain = new TFormMain();
        if (args.length > 0) {
            String descr = parseArgs(args);
            fFormMain.filepath = RESULTS_PATH + descr + "/config-" + descr + ".xml";
            fFormMain.outputFilepath = RESULTS_PATH + descr + "/result-" + descr
                    + LocalTime.now().format(DateTimeFormatter.ofPattern("HH-mm-ss")) + ".html";
            fFormMain.outputTableFilepath = RESULTS_PATH + descr + "/table-" + descr + ".txt";
            fFormMain.show();
            fFormMain.onBtnRunClick();
        } else {
            fFormMain.show();
        }

        QApplication.execStatic();
    }

    static int[] $SWITCH_TABLE$gui$TFormMain$TStateOCNS() {
        int[] arrn;
        int[] arrn2 = $SWITCH_TABLE$gui$TFormMain$TStateOCNS;
        if (arrn2 != null) {
            return arrn2;
        }
        arrn = new int[TStateOCNS.values().length];
        try {
            arrn[TStateOCNS.Finished.ordinal()] = 4;
        } catch (NoSuchFieldError ignored) {
        }
        try {
            arrn[TStateOCNS.Paused.ordinal()] = 2;
        } catch (NoSuchFieldError ignored) {
        }
        try {
            arrn[TStateOCNS.Running.ordinal()] = 1;
        } catch (NoSuchFieldError ignored) {
        }
        try {
            arrn[TStateOCNS.Stoped.ordinal()] = 3;
        } catch (NoSuchFieldError ignored) {
        }
        $SWITCH_TABLE$gui$TFormMain$TStateOCNS = arrn;
        return $SWITCH_TABLE$gui$TFormMain$TStateOCNS;
    }

    public void onBtnRunClick() {
        String aFilePath;
        if (filepath != null && !filepath.isEmpty()) {
            aFilePath = filepath;
        } else {
            aFilePath = QFileDialog.getOpenFileName(this, this.tr("Open NOC parameters file"), "", new QFileDialog.Filter(this.tr("NOC Parameters file (*.xml)")));
        }

        if (aFilePath.isEmpty()) {
            return;
        }
        TNetworkManager.doReadConfigFile(aFilePath);
        this.fControllerOCNS.AddCommand(TCommandOCNS.Start);
        this.SetStateButtons(TStateOCNS.Running);
        this.fUIFormMain.TELog.clear();
    }

    public void onBtnPauseClick() {
        this.fControllerOCNS.AddCommand(TCommandOCNS.Pause);
        this.SetStateButtons(TStateOCNS.Paused);
    }

    public void onBtnResumeClick() {
        this.fControllerOCNS.AddCommand(TCommandOCNS.Continue);
        this.SetStateButtons(TStateOCNS.Running);
    }

    public void onBtnStopClick() {
        if (outputFilepath != null && !outputFilepath.isEmpty()) {
            onBtnExportClick();
            close();
        }

        this.fControllerOCNS.AddCommand(TCommandOCNS.Stop);
        this.SetStateButtons(TStateOCNS.Stoped);
        this.fUIFormMain.PBProgress.setValue(0);
    }

    public void onBtnExportClick() {
        String FilePath;

        if (outputFilepath != null && !outputFilepath.isEmpty()) {
            Path finalPath = Paths.get(outputFilepath);

            try {
                Path directory = finalPath.getParent();

                if (!Files.exists(directory)) {
                    Files.createDirectories(finalPath.getParent());
                }
            } catch (Exception ignored) {
            }
            try {
                Files.createFile(finalPath);
                Files.write(finalPath, StandardCharsets.UTF_8.encode(fUIFormMain.TELog.toHtml()).array());
            } catch (Exception ignored) {
            }
        } else {
            FilePath = QFileDialog.getSaveFileName(this, this.tr("Choose file to save simulation report"), "", new QFileDialog.Filter(this.tr("NOC Performance file (*.txt)")));
            if (FilePath.isEmpty()) {
                return;
            }

            this.fUIFormMain.TELog.append("Save As dialog OK");
        }


        this.fUIFormMain.TELog.append("Save As dialog OK");
    }

    public void onProgressChanged(TControllerOCNS Sender, String value) {
        this.fUIFormMain.PBProgress.setValue(Integer.parseInt(value));
    }

    public void onNetworkSimulated(TControllerOCNS Sender, String aReport) {
        this.fUIFormMain.TELog.append(aReport);
    }

    public void OnNetworkSimulatedAll(TControllerOCNS Sender) {
        this.SetStateButtons(TStateOCNS.Finished);
    }

    private void SetStateButtons(TStateOCNS aStateOCNS) {
        switch (TFormMain.$SWITCH_TABLE$gui$TFormMain$TStateOCNS()[aStateOCNS.ordinal()]) {
            case 3: {
                this.fUIFormMain.fBtnRun.setEnabled(true);
                this.fUIFormMain.fBtnPause.setEnabled(false);
                this.fUIFormMain.fBtnResume.setEnabled(false);
                this.fUIFormMain.fBtnStop.setEnabled(false);
                this.fUIFormMain.fBtnExport.setEnabled(false);
                break;
            }
            case 1: {
                this.fUIFormMain.fBtnRun.setEnabled(false);
                this.fUIFormMain.fBtnPause.setEnabled(true);
                this.fUIFormMain.fBtnResume.setEnabled(false);
                this.fUIFormMain.fBtnStop.setEnabled(true);
                this.fUIFormMain.fBtnExport.setEnabled(false);
                break;
            }
            case 2: {
                this.fUIFormMain.fBtnRun.setEnabled(false);
                this.fUIFormMain.fBtnPause.setEnabled(false);
                this.fUIFormMain.fBtnResume.setEnabled(true);
                this.fUIFormMain.fBtnStop.setEnabled(true);
                this.fUIFormMain.fBtnExport.setEnabled(false);
                break;
            }
            case 4: {
                this.fUIFormMain.fBtnRun.setEnabled(true);
                this.fUIFormMain.fBtnPause.setEnabled(false);
                this.fUIFormMain.fBtnResume.setEnabled(false);
                this.fUIFormMain.fBtnStop.setEnabled(false);
                this.fUIFormMain.fBtnExport.setEnabled(true);

                if (outputFilepath != null && !outputFilepath.isEmpty()) {
                    onBtnExportClick();
                    this.close();
                }

                break;
            }
            default: {
            }
        }
    }

    public enum TStateOCNS {
        Running,
        Paused,
        Stoped,
        Finished;

        //private TStateOCNS(String string2, int n2) {
        //}
    }

    public static String parseArgs(String[] args) {
        String descr;
        if (MESH_TOPOLOGY.equals(args[0])) {
            int n, m;
            n = Integer.parseInt(args[1]);
            m = Integer.parseInt(args[2]);
            Mesh meshNetwork;
            if (n == m) {
                meshNetwork = new Mesh(n, n);
            } else {
                meshNetwork = new Mesh(n, m);
            }
            meshNetwork.createNetlist();
            meshNetwork.createRouting();
            descr = MESH_TOPOLOGY + "-(" + n + ", " + m + ")";
            createXml(meshNetwork.getNetlist(), meshNetwork.getRouting(), descr);

        } else if (TORUS_TOPOLOGY.equals(args[0])) {
            int n, m;
            n = Integer.parseInt(args[1]);
            m = Integer.parseInt(args[2]);
            Torus torusNetwork;
            if (n == m) {
                torusNetwork = new Torus(n, n);
            } else {
                torusNetwork = new Torus(n, m);
            }
            torusNetwork.createNetlist();
            torusNetwork.createRouting();
            descr = TORUS_TOPOLOGY + "-(" + n + ", " + m + ")";
            createXml(torusNetwork.getNetlist(), torusNetwork.getRouting(), descr);
        } else if (OPTIMAL_CIRCULANT_TOPOLOGY.equals(args[0])) {
            int k = Integer.parseInt(args[1]);

            if (k < 5) {
                System.out.println("Unexpected topology type!\nPlease check input args");
                return null;
            } else {
                Circulant circulantNetwork = new Circulant(k);
                circulantNetwork.createNetlist();
                circulantNetwork.createRouting(circulantNetwork.adjacencyMatrix(circulantNetwork.getNetlist(), k),
                        circulantNetwork.getNetlist());
                descr = OPTIMAL_CIRCULANT_TOPOLOGY + "-(" + k + ", " + circulantNetwork.s1 + ", " + circulantNetwork.s2 + ")";
                createXml(circulantNetwork.getNetlist(), circulantNetwork.getRouting(), descr);
            }

        } else if (CIRCULANT_TOPOLOGY.equals(args[0])) {
            int k = Integer.parseInt(args[1]);
            int s1 = Integer.parseInt(args[2]);
            int s2 = Integer.parseInt(args[3]);
            if (k < 5) {
                System.out.println("Unexpected topology type!\nPlease check input args");
                return null;
            } else {
                Circulant circulantNetwork = new Circulant(k, s1, s2);
                circulantNetwork.createNetlist();
                circulantNetwork.createRouting(circulantNetwork.adjacencyMatrix(circulantNetwork.getNetlist(), k),
                        circulantNetwork.getNetlist());
                descr = CIRCULANT_TOPOLOGY + "-(" + k + ", " + s1 + ", " + s2 + ")";
                createXml(circulantNetwork.getNetlist(), circulantNetwork.getRouting(), descr);
            }
        } else {
            System.out.println("Unexpected topology type!");
            return null;
        }
        return descr;
    }

    public static void createXml(int[][] netlist, int[][] routing, String descr) {
        StringBuilder netlistData = new StringBuilder("\n");
        for (int i = 0; i < routing.length; i++) {
            for (int j = 0; j < 4; j++) {
                netlistData.append(netlist[i][j]).append(" ");
            }
            netlistData.append("\n");
        }

        StringBuilder routingData = new StringBuilder("\n");
        for (int[] ints : routing) {
            for (int j = 0; j < routing.length; j++) {
                routingData.append(ints[j]).append(" ");
            }
            routingData.append("\n");
        }
        //TODO:
//        XmlWriter xmlDoc = new XmlWriter(netlistData.toString(), routingData.toString(), descr);
    }


}

