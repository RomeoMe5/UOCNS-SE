package ru.stepanov.uocns.network.common;

public class IConstants {
    public static TConfigNoC fConfigNoC = new TConfigNoC();
    public static int fCountPLinkCores = 1;
    public static int fBufferPacketTxSize = 4;

    public static class TConfigNoC {
        public boolean fIsModeGALS;
        public int fCountCores;
        public int fFlitSize;
        public int fPacketAvgLenght;
        public boolean fPacketIsFixedLength;
        public int fPacketAvgGenTime;
        public int fCountPLinkRouters = 4;
        public int fCountVLinkPerPLink;
        public int fSizeVLinkBuffer;
        public int fCountClocksTotal;
        public int fCountRun;
        public long fCountPacketRx;
        public long fCountPacketRxWarmUp;
        public int[][] fNetlist;
        public int[][] fRoutingTable;
        public String fDescription;
    }

}

