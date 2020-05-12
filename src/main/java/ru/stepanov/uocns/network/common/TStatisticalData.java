package ru.stepanov.uocns.network.common;

import ru.stepanov.uocns.network.IControllerOCNS;

public class TStatisticalData {
    private final long[] fArrCountRouterFlitTx;
    private final long[] fArrCountCoreFlitTx;
    private final long[] fArrCountPacketTime;
    private final long[] fArrCountNewPacketWell;
    private final long[] fArrCountNewPacketErr;
    private final long[] fArrCountPacketRx;
    private final long[] fArrCountPacketTx;
    private final long[] fArrCountCoreFlitRx;
    private final long[] fArrCountUsedRouterPLinkTx;
    private final long[] fArrCountRouterPLink;
    private final long[] fArrCountUsedCorePLinkTx;
    private final long[] fArrCountUsedRouterSlotRx;
    private final long[] fArrCountUsedRouterSlotTx;
    private final long[] fArrCountUsedCoreSlotRx;
    private final long[] fArrCountUsedCoreSlotTx;
    private final long[] fArrCountPacketHop;
    private long fCountWarmUpPacketToRx;
    private final IControllerOCNS fControllerOCNS;

    public TStatisticalData(IControllerOCNS aControllerOCNS) {
        this.fControllerOCNS = aControllerOCNS;
        this.fCountWarmUpPacketToRx = (long) IConstants.fConfigNoC.fCountCores * IConstants.fConfigNoC.fCountPacketRxWarmUp;
        this.fArrCountNewPacketWell = new long[IConstants.fConfigNoC.fCountCores];
        this.fArrCountNewPacketErr = new long[IConstants.fConfigNoC.fCountCores];
        this.fArrCountPacketRx = new long[IConstants.fConfigNoC.fCountCores];
        this.fArrCountPacketTx = new long[IConstants.fConfigNoC.fCountCores];
        this.fArrCountCoreFlitRx = new long[IConstants.fConfigNoC.fCountCores];
        this.fArrCountCoreFlitTx = new long[IConstants.fConfigNoC.fCountCores];
        this.fArrCountPacketTime = new long[IConstants.fConfigNoC.fCountCores];
        this.fArrCountPacketHop = new long[IConstants.fConfigNoC.fCountCores];
        this.fArrCountUsedCorePLinkTx = new long[IConstants.fConfigNoC.fCountCores];
        this.fArrCountUsedCoreSlotRx = new long[IConstants.fConfigNoC.fCountCores];
        this.fArrCountUsedCoreSlotTx = new long[IConstants.fConfigNoC.fCountCores];
        int iCoreId = 0;
        while (iCoreId < IConstants.fConfigNoC.fCountCores) {
            this.fArrCountNewPacketWell[iCoreId] = 0;
            this.fArrCountNewPacketErr[iCoreId] = 0;
            this.fArrCountPacketRx[iCoreId] = 0;
            this.fArrCountPacketTx[iCoreId] = 0;
            this.fArrCountCoreFlitRx[iCoreId] = 0;
            this.fArrCountCoreFlitTx[iCoreId] = 0;
            this.fArrCountPacketTime[iCoreId] = 0;
            this.fArrCountPacketHop[iCoreId] = 0;
            this.fArrCountUsedCorePLinkTx[iCoreId] = 0;
            this.fArrCountUsedCoreSlotRx[iCoreId] = 0;
            this.fArrCountUsedCoreSlotTx[iCoreId] = 0;
            ++iCoreId;
        }
        this.fArrCountRouterPLink = new long[IConstants.fConfigNoC.fRoutingTable.length];
        this.fArrCountRouterFlitTx = new long[IConstants.fConfigNoC.fRoutingTable.length];
        this.fArrCountUsedRouterPLinkTx = new long[IConstants.fConfigNoC.fRoutingTable.length];
        this.fArrCountUsedRouterSlotRx = new long[IConstants.fConfigNoC.fRoutingTable.length];
        this.fArrCountUsedRouterSlotTx = new long[IConstants.fConfigNoC.fRoutingTable.length];
        int iRouterId = 0;
        while (iRouterId < IConstants.fConfigNoC.fRoutingTable.length) {
            this.fArrCountRouterPLink[iRouterId] = 0;
            this.fArrCountRouterFlitTx[iRouterId] = 0;
            this.fArrCountUsedRouterPLinkTx[iRouterId] = 0;
            this.fArrCountUsedRouterSlotRx[iRouterId] = 0;
            this.fArrCountUsedRouterSlotTx[iRouterId] = 0;
            ++iRouterId;
        }
    }

    public void incCountPacketRx(int aCoreToId, long aPacketId) {
        if (aPacketId < IConstants.fConfigNoC.fCountPacketRxWarmUp) {
            --this.fCountWarmUpPacketToRx;
            return;
        }
        if (this.IsWarmUp()) {
            return;
        }
        long[] arrl = this.fArrCountPacketRx;
        arrl[aCoreToId] = arrl[aCoreToId] + 1;
        long aCountPacketRxTotal = 0;
        int iCoreToId = 0;
        while (iCoreToId < this.fArrCountPacketRx.length) {
            aCountPacketRxTotal += this.fArrCountPacketRx[iCoreToId];
            ++iCoreToId;
        }
        if (aCountPacketRxTotal == (long) IConstants.fConfigNoC.fCountCores * IConstants.fConfigNoC.fCountPacketRx) {
            this.fControllerOCNS.cbTerminate();
        }
    }

    private boolean IsWarmUp() {
        return this.fCountWarmUpPacketToRx > 0;
    }

    public void incCountPacketTime(int aCoreId, int aTime) {
        if (this.IsWarmUp()) {
            return;
        }
        long[] arrl = this.fArrCountPacketTime;
        arrl[aCoreId] = arrl[aCoreId] + (long) aTime;
    }

    public void incCountPacketHop(int aCoreToId, int aHopCount) {
        if (this.IsWarmUp()) {
            return;
        }
        long[] arrl = this.fArrCountPacketHop;
        arrl[aCoreToId] = arrl[aCoreToId] + (long) aHopCount;
    }

    public void incCountRouterFlitTx(int aRouterId) {
        if (this.IsWarmUp()) {
            return;
        }
        long[] arrl = this.fArrCountRouterFlitTx;
        arrl[aRouterId] = arrl[aRouterId] + 1;
    }

    public void incCountCoreFlitRx(int aCoreId) {
        if (this.IsWarmUp()) {
            return;
        }
        long[] arrl = this.fArrCountCoreFlitRx;
        arrl[aCoreId] = arrl[aCoreId] + 1;
    }

    public void incCountCoreFlitTx(int aCoreId) {
        if (this.IsWarmUp()) {
            return;
        }
        long[] arrl = this.fArrCountCoreFlitTx;
        arrl[aCoreId] = arrl[aCoreId] + 1;
    }

    public void incCountCorePacketTx(int aCoreId) {
        if (this.IsWarmUp()) {
            return;
        }
        long[] arrl = this.fArrCountPacketTx;
        arrl[aCoreId] = arrl[aCoreId] + 1;
    }

    public void incCountNewPacketWell(int aCoreId) {
        if (this.IsWarmUp()) {
            return;
        }
        long[] arrl = this.fArrCountNewPacketWell;
        arrl[aCoreId] = arrl[aCoreId] + 1;
    }

    public void incCountNewPacketErr(int aCoreId) {
        if (this.IsWarmUp()) {
            return;
        }
        long[] arrl = this.fArrCountNewPacketErr;
        arrl[aCoreId] = arrl[aCoreId] + 1;
    }

    public void incCountUsedRouterPLinkTx(int aRouterId, int aCount) {
        if (this.IsWarmUp()) {
            return;
        }
        long[] arrl = this.fArrCountUsedRouterPLinkTx;
        arrl[aRouterId] = arrl[aRouterId] + (long) aCount;
    }

    public void incCountUsedCorePLinkTx(int aCoreId) {
        if (this.IsWarmUp()) {
            return;
        }
        long[] arrl = this.fArrCountUsedCorePLinkTx;
        arrl[aCoreId] = arrl[aCoreId] + 1;
    }

    public void incCountUsedRouterSlotRx(int aRouterId, int aCountUsedSlotRx) {
        if (this.IsWarmUp()) {
            return;
        }
        long[] arrl = this.fArrCountUsedRouterSlotRx;
        arrl[aRouterId] = arrl[aRouterId] + (long) aCountUsedSlotRx;
    }

    public void incCountUsedRouterSlotTx(int aRouterId, int aCountUsedSlotTx) {
        if (this.IsWarmUp()) {
            return;
        }
        long[] arrl = this.fArrCountUsedRouterSlotTx;
        arrl[aRouterId] = arrl[aRouterId] + (long) aCountUsedSlotTx;
    }

    public void incCountUsedCoreSlotRx(int aCoreId, int aCountUsedSlotsRx) {
        if (this.IsWarmUp()) {
            return;
        }
        long[] arrl = this.fArrCountUsedCoreSlotRx;
        arrl[aCoreId] = arrl[aCoreId] + (long) aCountUsedSlotsRx;
    }

    public void incCountUsedCoreSlotTx(int aCoreId, int aCountUsedSlotsTx) {
        if (this.IsWarmUp()) {
            return;
        }
        long[] arrl = this.fArrCountUsedCoreSlotTx;
        arrl[aCoreId] = arrl[aCoreId] + (long) aCountUsedSlotsTx;
    }

    public void setCountRouterPLink(int aRouterId, int aCount) {
        this.fArrCountRouterPLink[aRouterId] = aCount;
    }

    public double getCountPacketTimeAvg() {
        double aTotalCountPacketRx = 0.0;
        double aTotalPacketTime = 0.0;
        int iCoreId = 0;
        while (iCoreId < IConstants.fConfigNoC.fCountCores) {
            aTotalPacketTime += (double) this.fArrCountPacketTime[iCoreId];
            aTotalCountPacketRx += (double) this.fArrCountPacketRx[iCoreId];
            ++iCoreId;
        }
        return aTotalPacketTime / aTotalCountPacketRx;
    }

    public double getCountPacketHopAvg() {
        long aCountTotalPacketHop = 0;
        long aCountTotalPacketRx = 0;
        int iCoreId = 0;
        while (iCoreId < IConstants.fConfigNoC.fCountCores) {
            aCountTotalPacketHop += this.fArrCountPacketHop[iCoreId];
            aCountTotalPacketRx += this.fArrCountPacketRx[iCoreId];
            ++iCoreId;
        }
        return (double) aCountTotalPacketHop / (double) aCountTotalPacketRx;
    }

    public long getCountNewPacketWellTotal() {
        long aCountPacketProduced = 0;
        int iCoreId = 0;
        while (iCoreId < this.fArrCountNewPacketWell.length) {
            aCountPacketProduced += this.fArrCountNewPacketWell[iCoreId];
            ++iCoreId;
        }
        return aCountPacketProduced;
    }

    public long getCountPacketTxTotal() {
        long aCountPacketTxTotal = 0;
        int iCoreId = 0;
        while (iCoreId < IConstants.fConfigNoC.fCountCores) {
            aCountPacketTxTotal += this.fArrCountPacketTx[iCoreId];
            ++iCoreId;
        }
        return aCountPacketTxTotal;
    }

    public double getCountPacketRxTotal() {
        long packetsReceived = 0;
        int iCoreId = 0;
        while (iCoreId < IConstants.fConfigNoC.fCountCores) {
            packetsReceived += this.fArrCountPacketRx[iCoreId];
            ++iCoreId;
        }
        return packetsReceived;
    }

    public double getPacketRate() {
        return (double) this.getCountNewPacketWellTotal() / (double) IConstants.fConfigNoC.fCountClocksTotal;
    }

    public double getFlitRatePerNode() {
        if (IConstants.fConfigNoC.fPacketIsFixedLength) {
            return (double) this.getCountNewPacketWellTotal() * (double) IConstants.fConfigNoC.fPacketAvgLenght / (double) IConstants.fConfigNoC.fCountClocksTotal / (double) IConstants.fConfigNoC.fCountCores;
        }
        System.err.println("\u041d\u0435 \u043e\u0431\u0440\u0430\u0431\u043e\u0442\u0430\u043d \u0441\u043b\u0443\u0447\u0430\u0439, \u043a\u043e\u0433\u0434\u0430 \u043f\u0430\u043a\u0435\u0442 \u0438\u043c\u0435\u0435\u0442 \u043f\u0435\u0440\u0435\u043c\u0435\u043d\u043d\u0443\u044e \u0434\u043b\u0438\u043d\u0443.");
        return -1.0;
    }

    public double getCountNewPacketErrAvg() {
        long aCountNewPacketErrTotal = 0;
        int iCoreId = 0;
        while (iCoreId < IConstants.fConfigNoC.fCountCores) {
            aCountNewPacketErrTotal += this.fArrCountNewPacketErr[iCoreId];
            if (this.fArrCountNewPacketErr[iCoreId] > 0) {
                System.out.printf("[WARNING][TStatistic] \u041e\u0448\u0438\u0431\u043a\u0438 \u0433\u0435\u043d\u0435\u0440\u0430\u0446\u0438\u0438 \u043f\u0430\u043a\u0435\u0442\u043e\u0432 {\n\tiCoreId:    %d\n\t\u041a\u043e\u043b\u0438\u0447\u0435\u0441\u0442\u0432\u043e: %d\n}\n", iCoreId, this.fArrCountNewPacketErr[iCoreId]);
            }
            ++iCoreId;
        }
        return (double) aCountNewPacketErrTotal / (double) IConstants.fConfigNoC.fCountCores;
    }

    public double getUtilizationCoreBufferRxAvg() {
        long aCountUsedCoreSlotRxTotal = 0;
        int iCoreId = 0;
        while (iCoreId < IConstants.fConfigNoC.fCountCores) {
            aCountUsedCoreSlotRxTotal += this.fArrCountUsedCoreSlotRx[iCoreId];
            ++iCoreId;
        }
        long aCountCoreSlotRxTotal = IConstants.fConfigNoC.fCountVLinkPerPLink * IConstants.fConfigNoC.fSizeVLinkBuffer * IConstants.fConfigNoC.fCountCores;
        return (double) aCountUsedCoreSlotRxTotal / (double) aCountCoreSlotRxTotal * 100.0 / (double) IConstants.fConfigNoC.fCountClocksTotal;
    }

    public double getUtilizationCoreBufferTxAvg() {
        long aCountUsedCoreSlotTxTotal = 0;
        int iCore = 0;
        while (iCore < IConstants.fConfigNoC.fCountCores) {
            aCountUsedCoreSlotTxTotal += this.fArrCountUsedCoreSlotTx[iCore];
            ++iCore;
        }
        long aCountCoreSlotTxTotal = IConstants.fConfigNoC.fCountVLinkPerPLink * IConstants.fConfigNoC.fSizeVLinkBuffer * IConstants.fConfigNoC.fCountCores;
        return (double) aCountUsedCoreSlotTxTotal / (double) aCountCoreSlotTxTotal * 100.0 / (double) IConstants.fConfigNoC.fCountClocksTotal;
    }

    public double getUtilizationRouterBufferRxAvg() {
        long aCountRouterPLinkTotal = 0;
        long aCountUsedRouterSlotRxTotal = 0;
        int i = 0;
        while (i < IConstants.fConfigNoC.fRoutingTable.length) {
            aCountRouterPLinkTotal += this.fArrCountRouterPLink[i];
            aCountUsedRouterSlotRxTotal += this.fArrCountUsedRouterSlotRx[i];
            ++i;
        }
        long aCountRouterSlotRxTotal = (long) (IConstants.fConfigNoC.fCountVLinkPerPLink * IConstants.fConfigNoC.fSizeVLinkBuffer) * aCountRouterPLinkTotal;
        return (double) aCountUsedRouterSlotRxTotal / (double) aCountRouterSlotRxTotal * 100.0 / (double) IConstants.fConfigNoC.fCountClocksTotal;
    }

    public double getUtilizationRouterBufferTxAvg() {
        long aCountRouterPLinkTotal = 0;
        long aCountUsedRouterSlotTxTotal = 0;
        int i = 0;
        while (i < IConstants.fConfigNoC.fRoutingTable.length) {
            aCountRouterPLinkTotal += this.fArrCountRouterPLink[i];
            aCountUsedRouterSlotTxTotal += this.fArrCountUsedRouterSlotTx[i];
            ++i;
        }
        long aCountRouterSlotTxTotal = (long) (IConstants.fConfigNoC.fCountVLinkPerPLink * IConstants.fConfigNoC.fSizeVLinkBuffer) * aCountRouterPLinkTotal;
        return (double) aCountUsedRouterSlotTxTotal / (double) aCountRouterSlotTxTotal * 100.0 / (double) IConstants.fConfigNoC.fCountClocksTotal;
    }

    public double getUtilizationNetworkBufferAvg() {
        long aCountUsedSlotTotal = 0;
        int iCoreId = 0;
        while (iCoreId < IConstants.fConfigNoC.fCountCores) {
            aCountUsedSlotTotal += this.fArrCountUsedCoreSlotRx[iCoreId];
            aCountUsedSlotTotal += this.fArrCountUsedCoreSlotTx[iCoreId];
            ++iCoreId;
        }
        long aCountRouterPLinkTotal = 0;
        int iRouterId = 0;
        while (iRouterId < IConstants.fConfigNoC.fRoutingTable.length) {
            aCountRouterPLinkTotal += this.fArrCountRouterPLink[iRouterId];
            aCountUsedSlotTotal += this.fArrCountUsedRouterSlotRx[iRouterId];
            aCountUsedSlotTotal += this.fArrCountUsedRouterSlotTx[iRouterId];
            ++iRouterId;
        }
        long aCountCoreSlotTotal = 2 * IConstants.fConfigNoC.fCountVLinkPerPLink * IConstants.fConfigNoC.fSizeVLinkBuffer * IConstants.fConfigNoC.fCountCores;
        long aCountRouterSlotTotal = (long) (2 * IConstants.fConfigNoC.fCountVLinkPerPLink * IConstants.fConfigNoC.fSizeVLinkBuffer) * aCountRouterPLinkTotal;
        return (double) aCountUsedSlotTotal / (double) (aCountCoreSlotTotal + aCountRouterSlotTotal) * 100.0 / (double) IConstants.fConfigNoC.fCountClocksTotal;
    }

    public double getUtilizationNetworkPLinkAvg() {
        long aCountPLinkTotal = 0;
        long aCountUsedPLinkTxTotal = 0;
        int iRouterId = 0;
        while (iRouterId < IConstants.fConfigNoC.fRoutingTable.length) {
            aCountUsedPLinkTxTotal += this.fArrCountUsedRouterPLinkTx[iRouterId];
            aCountPLinkTotal += this.fArrCountRouterPLink[iRouterId];
            ++iRouterId;
        }
        aCountPLinkTotal += IConstants.fConfigNoC.fCountCores;
        int iCoreId = 0;
        while (iCoreId < IConstants.fConfigNoC.fCountCores) {
            aCountUsedPLinkTxTotal += this.fArrCountUsedCorePLinkTx[iCoreId];
            ++iCoreId;
        }
        return (double) aCountUsedPLinkTxTotal / (double) aCountPLinkTotal * 100.0 / (double) IConstants.fConfigNoC.fCountClocksTotal;
    }

    public double getThroughputNetwork() {
        long aCountCoreFlitsRxTotal = 0;
        int iCoreId = 0;
        while (iCoreId < IConstants.fConfigNoC.fCountCores) {
            aCountCoreFlitsRxTotal += this.fArrCountCoreFlitRx[iCoreId];
            ++iCoreId;
        }
        return (double) aCountCoreFlitsRxTotal / (double) IConstants.fConfigNoC.fCountCores / (double) IConstants.fConfigNoC.fCountClocksTotal;
    }

    public double getThroughputSwitch() {
        long aTotalCountSwitchFlitLeft = 0;
        int i = 0;
        while (i < IConstants.fConfigNoC.fRoutingTable.length) {
            aTotalCountSwitchFlitLeft += this.fArrCountRouterFlitTx[i];
            ++i;
        }
        return (double) aTotalCountSwitchFlitLeft / (double) IConstants.fConfigNoC.fCountClocksTotal / (double) IConstants.fConfigNoC.fRoutingTable.length;
    }

    public double getCoreInjectionRate() {
        double aCountCoreFlitTxTotal = 0.0;
        int iCoreId = 0;
        while (iCoreId < this.fArrCountCoreFlitTx.length) {
            aCountCoreFlitTxTotal += (double) this.fArrCountCoreFlitTx[iCoreId];
            ++iCoreId;
        }
        return aCountCoreFlitTxTotal / (double) IConstants.fConfigNoC.fCountClocksTotal / (double) IConstants.fConfigNoC.fCountCores;
    }
}

