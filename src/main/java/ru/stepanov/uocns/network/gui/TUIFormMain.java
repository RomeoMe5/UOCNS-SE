package ru.stepanov.uocns.network.gui;

import com.trolltech.qt.QUiForm;
import com.trolltech.qt.core.QCoreApplication;
import com.trolltech.qt.core.QRect;
import com.trolltech.qt.core.QSize;
import com.trolltech.qt.core.Qt;
import com.trolltech.qt.gui.*;

public class TUIFormMain
        implements QUiForm<QMainWindow> {
    public QWidget centralwidget;
    public QVBoxLayout verticalLayout;
    public QTextEdit TELog;
    public QHBoxLayout horizontalLayout;
    public QPushButton fBtnRun;
    public QPushButton fBtnPause;
    public QPushButton fBtnResume;
    public QPushButton fBtnStop;
    public QPushButton fBtnExport;
    public QProgressBar PBProgress;
    public QMenuBar menubar;
    public QStatusBar statusbar;

    @Override
    public void setupUi(QMainWindow FormMain) {
        FormMain.setObjectName("FormMain");
        FormMain.resize(new QSize(520, 516).expandedTo(FormMain.minimumSizeHint()));
        this.centralwidget = new QWidget(FormMain);
        this.centralwidget.setObjectName("centralwidget");
        this.verticalLayout = new QVBoxLayout(this.centralwidget);
        this.verticalLayout.setObjectName("verticalLayout");
        this.TELog = new QTextEdit(this.centralwidget);
        this.TELog.setObjectName("TELog");
        QSizePolicy sizePolicy = new QSizePolicy(QSizePolicy.Policy.Preferred, QSizePolicy.Policy.Preferred);
        sizePolicy.setHorizontalStretch((byte) 0);
        sizePolicy.setVerticalStretch((byte) 0);
        sizePolicy.setHeightForWidth(this.TELog.sizePolicy().hasHeightForWidth());
        this.TELog.setSizePolicy(sizePolicy);
        this.TELog.setFrameShape(QFrame.Shape.Box);
        this.TELog.setFrameShadow(QFrame.Shadow.Plain);
        this.TELog.setReadOnly(true);
        this.TELog.setTabStopWidth(24);
        this.TELog.setAcceptRichText(false);
        this.TELog.setTextInteractionFlags(Qt.TextInteractionFlag.createQFlags(Qt.TextInteractionFlag.TextSelectableByMouse));
        this.verticalLayout.addWidget(this.TELog);
        this.horizontalLayout = new QHBoxLayout();
        this.horizontalLayout.setObjectName("horizontalLayout");
        this.fBtnRun = new QPushButton(this.centralwidget);
        this.fBtnRun.setObjectName("fBtnRun");
        this.fBtnRun.setEnabled(true);
        this.fBtnRun.setCheckable(false);
        this.horizontalLayout.addWidget(this.fBtnRun);
        this.fBtnPause = new QPushButton(this.centralwidget);
        this.fBtnPause.setObjectName("fBtnPause");
        this.fBtnPause.setEnabled(false);
        this.horizontalLayout.addWidget(this.fBtnPause);
        this.fBtnResume = new QPushButton(this.centralwidget);
        this.fBtnResume.setObjectName("fBtnResume");
        this.fBtnResume.setEnabled(false);
        this.horizontalLayout.addWidget(this.fBtnResume);
        this.fBtnStop = new QPushButton(this.centralwidget);
        this.fBtnStop.setObjectName("fBtnStop");
        this.fBtnStop.setEnabled(false);
        this.horizontalLayout.addWidget(this.fBtnStop);
        this.fBtnExport = new QPushButton(this.centralwidget);
        this.fBtnExport.setObjectName("fBtnExport");
        this.fBtnExport.setEnabled(false);
        this.horizontalLayout.addWidget(this.fBtnExport);
        this.verticalLayout.addLayout(this.horizontalLayout);
        this.PBProgress = new QProgressBar(this.centralwidget);
        this.PBProgress.setObjectName("PBProgress");
        this.PBProgress.setValue(0);
        this.PBProgress.setAlignment(Qt.AlignmentFlag.createQFlags(Qt.AlignmentFlag.AlignCenter));
        this.verticalLayout.addWidget(this.PBProgress);
        FormMain.setCentralWidget(this.centralwidget);
        this.menubar = new QMenuBar(FormMain);
        this.menubar.setObjectName("menubar");
        this.menubar.setGeometry(new QRect(0, 0, 520, 22));
        FormMain.setMenuBar(this.menubar);
        this.statusbar = new QStatusBar(FormMain);
        this.statusbar.setObjectName("statusbar");
        FormMain.setStatusBar(this.statusbar);
        this.retranslateUi(FormMain);
        FormMain.connectSlotsByName();
    }

    void retranslateUi(QMainWindow FormMain) {
        FormMain.setWindowTitle(QCoreApplication.translate("FormMain", "OCNS", null));
        this.TELog.setDocumentTitle("");
        this.TELog.setHtml(QCoreApplication.translate("FormMain", "<!DOCTYPE HTML PUBLIC \"-//W3C//DTD HTML 4.0//EN\" \"http://www.w3.org/TR/REC-html40/strict.dtd\">\n<html><head><meta name=\"qrichtext\" content=\"1\" /><style type=\"text/css\">\np, li { white-space: pre-wrap; }\n</style></head><body style=\" font-family:'MS Shell Dlg 2'; font-size:8.25pt; font-weight:400; font-style:normal;\">\n<p style=\" margin-top:0px; margin-bottom:0px; margin-left:0px; margin-right:0px; -qt-block-indent:0; text-indent:0px;\">\t</p>\n<p style=\"-qt-paragraph-type:empty; margin-top:0px; margin-bottom:0px; margin-left:0px; margin-right:0px; -qt-block-indent:0; text-indent:0px; font-size:8pt;\"></p></body></html>", null));
        this.fBtnRun.setText(QCoreApplication.translate("FormMain", "Run...", null));
        this.fBtnPause.setText(QCoreApplication.translate("FormMain", "Pause", null));
        this.fBtnResume.setText(QCoreApplication.translate("FormMain", "Resume", null));
        this.fBtnStop.setText(QCoreApplication.translate("FormMain", "Stop", null));
        this.fBtnExport.setText(QCoreApplication.translate("FormMain", "Export...", null));
    }
}

