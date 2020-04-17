package ru.stepanov.uocns.network.gui;

import com.trolltech.qt.gui.QFileDialog;
import com.trolltech.qt.gui.QMainWindow;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import ru.stepanov.uocns.network.TCommandOCNS;
import ru.stepanov.uocns.network.TControllerOCNS;
import ru.stepanov.uocns.network.TNetworkManager;
import ru.stepanov.uocns.network.common.generator.utils.XmlWriter;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class TFormMain extends QMainWindow {

    @Autowired
    XmlWriter xmlWriter;

    private static int[] $SWITCH_TABLE$gui$TFormMain$TStateOCNS;
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

//    public void main(String[] args) {
//        QApplication.initialize(args);
//        TFormMain fFormMain = new TFormMain();
//        if (args.length > 0) {
//            fFormMain.filepath = RESULTS_PATH + descr + "/config-" + descr + ".xml";
//            fFormMain.outputFilepath = RESULTS_PATH + descr + "/result-" + descr
//                    + LocalTime.now().format(DateTimeFormatter.ofPattern("HH-mm-ss")) + ".html";
//            fFormMain.outputTableFilepath = RESULTS_PATH + descr + "/table-" + descr + ".txt";
//            fFormMain.show();
//            fFormMain.onBtnRunClick();
//        } else {
//            fFormMain.show();
//        }
//
//        QApplication.execStatic();
//    }

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
    }
}

