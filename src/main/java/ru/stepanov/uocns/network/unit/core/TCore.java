package ru.stepanov.uocns.network.unit.core;

import ru.stepanov.uocns.network.TNetworkManager;
import ru.stepanov.uocns.network.common.IConstants;
import ru.stepanov.uocns.network.common.TTypeFlit;
import ru.stepanov.uocns.network.traffic.ICoreTraffic;
import ru.stepanov.uocns.network.traffic.TCoreTraffic;
import ru.stepanov.uocns.network.traffic.TFlit;
import ru.stepanov.uocns.network.traffic.TPacket;
import ru.stepanov.uocns.network.unit.core.buffer.TBufferPacketTx;
import ru.stepanov.uocns.network.unit.router.IRouter;
import ru.stepanov.uocns.network.unit.router.buffer.IBufferPLink;
import ru.stepanov.uocns.network.unit.router.buffer.TBufferPLink;
import ru.stepanov.uocns.network.unit.router.link.TPLinkTx;

import java.util.Vector;

public class TCore {
    private static int[] $SWITCH_TABLE$network$common$TTypeFlit;
    public IRouter fRouter;
    public ICoreTraffic fCoreTraffic;
    TBufferPacketTx fBufferPacketTx;
    private final int fId;
    private long fNextPacketId;
    private final Vector<TFlit>[] fArrPacketFlitsRx;
    private final int[] fArrCountPacketFlitsToRx;
    private final int fVLinkCount;
    private final int fSwitchCorePLinkId;
    private int fLastSender = 0;
    private int fVLinkIdLastServedTx = 0;
    private int fVLinkIdLastServedRx = 0;
    private boolean fIsPLinkTxUsed = false;
    private int fClockLastFlitRx = -1;
    private int fClockLastFlitTx = -1;
    private final IBufferPLink fBufferPLinkRx;
    private final TPLinkTx fPLinkTx;

    public TCore(int aCoreId, IRouter aRouter, int aSwitchCorePLinkId, int aVLinkCount) {
        this.fId = aCoreId;
        this.fRouter = aRouter;
        this.fNextPacketId = 0;
        this.fSwitchCorePLinkId = aSwitchCorePLinkId;
        this.fVLinkCount = aVLinkCount;
        this.fBufferPacketTx = new TBufferPacketTx(IConstants.fBufferPacketTxSize);
        this.fArrPacketFlitsRx = new Vector[aVLinkCount];
        this.fArrCountPacketFlitsToRx = new int[aVLinkCount];
        this.fBufferPLinkRx = new TBufferPLink(aVLinkCount, IConstants.fConfigNoC.fSizeVLinkBuffer);
        this.fPLinkTx = new TPLinkTx(aVLinkCount, IConstants.fConfigNoC.fSizeVLinkBuffer);
        this.fCoreTraffic = new TCoreTraffic(aCoreId);
    }

    static /* synthetic */ int[] $SWITCH_TABLE$network$common$TTypeFlit() {
        int[] arrn;
        int[] arrn2 = $SWITCH_TABLE$network$common$TTypeFlit;
        if (arrn2 != null) {
            return arrn2;
        }
        arrn = new int[TTypeFlit.values().length];
        try {
            arrn[TTypeFlit.Data.ordinal()] = 2;
        } catch (NoSuchFieldError ignored) {
        }
        try {
            arrn[TTypeFlit.Header.ordinal()] = 1;
        } catch (NoSuchFieldError ignored) {
        }
        try {
            arrn[TTypeFlit.Unknown.ordinal()] = 3;
        } catch (NoSuchFieldError ignored) {
        }
        $SWITCH_TABLE$network$common$TTypeFlit = arrn;
        return $SWITCH_TABLE$network$common$TTypeFlit;
    }

    public int getClockLastFlitRx() {
        return this.fClockLastFlitRx;
    }

    public void setClockLastFlitRx(int iClock) {
        this.fClockLastFlitRx = iClock;
    }

    private void doGeneratePacket(int iClock, TNetworkManager tNetworkManager) throws Exception {
        if (this.fCoreTraffic.getClockNextPacket() != iClock) {
            return;
        }
        int aSlotId = this.fBufferPacketTx.tryAllocFreeSlot();
        if (-1 == aSlotId) {
            this.fCoreTraffic.setNextMessageTime(iClock, true, tNetworkManager);
            tNetworkManager.getStatistic().incCountNewPacketErr(this.fId);
            return;
        }
        TPacket aPacket = new TPacket();
        aPacket.setFlits(this.fCoreTraffic.getNewPacket(this.fNextPacketId++, iClock, tNetworkManager));
        this.fBufferPacketTx.insertPacket(aPacket, aSlotId);
        tNetworkManager.getStatistic().incCountNewPacketWell(this.fId);
    }

    public void moveTraficExternal(int iClock, TNetworkManager tNetworkManager) throws Exception {
        this.doGeneratePacket(iClock, tNetworkManager);
        this.allocFreeVLink(iClock);
        this.fillEmptyBuffer(iClock, tNetworkManager);
        this.sendFlitToRouter(iClock, tNetworkManager);
    }

    private void allocFreeVLink(int iClock) throws Exception {
        if (this.fBufferPacketTx.getPacketCount() == 0) {
            return;
        }
        if (this.fPLinkTx.getCountVLinkAllocated() == this.fVLinkCount) {
            return;
        }
        int aSlotCount = this.fBufferPacketTx.getSlotCount();
        int iSlot = 0;
        while (iSlot < aSlotCount) {
            if (!this.fBufferPacketTx.isEmpty(this.fLastSender) && this.fBufferPacketTx.getVLinkTxId(this.fLastSender) == -1) {
                int iVLinkTxId = this.fPLinkTx.tryAllocVLink(iClock);
                this.fBufferPacketTx.setVLinkTxId(this.fLastSender, iVLinkTxId);
                return;
            }
            ++this.fLastSender;
            this.fLastSender %= aSlotCount;
            ++iSlot;
        }
    }

    /*
     * Unable to fully structure code
     * Enabled aggressive block sorting
     * Lifted jumps to return sites
     */
    private void fillEmptyBuffer(int iClock, TNetworkManager tNetworkManager) throws Exception {
        int iSlot = 0;
        while (iSlot < this.fBufferPacketTx.getSlotCount()) {
            {
                int iVLinkTxId;

                if (!this.fBufferPacketTx.isEmpty(iSlot) && -1 != (iVLinkTxId = this.fBufferPacketTx.getVLinkTxId(iSlot))) {
                    this.fBufferPacketTx.isEmpty(iSlot);

                    while (this.fPLinkTx.canPushHead(iVLinkTxId, iClock) || this.fPLinkTx.canPushData(iVLinkTxId, iClock)) {
                        TFlit aFlit = this.fBufferPacketTx.popFront(iSlot);
                        this.fPLinkTx.pushBack(aFlit, iVLinkTxId, iClock);
                        if (TTypeFlit.Header == aFlit.getType()) {
                            tNetworkManager.getStatistic().incCountCorePacketTx(this.fId);
                        }

                        this.fBufferPacketTx.isEmpty(iSlot);
                    }
                }

            }
            ++iSlot;
        }
    }

    /*
     * Enabled aggressive block sorting
     */
    private void sendFlitToRouter(int iClock, TNetworkManager tNetworkManager) throws Exception {
        int iVLink = 0;
        while (iVLink < this.fVLinkCount) {
            block6:
            {
                int aClockLastServed;
                ++this.fVLinkIdLastServedTx;
                this.fVLinkIdLastServedTx %= this.fVLinkCount;
                if (!this.fPLinkTx.canPop(this.fVLinkIdLastServedTx, iClock) || (aClockLastServed = this.fPLinkTx.getFront(this.fVLinkIdLastServedTx).getClockLastServed()) >= iClock || this.fClockLastFlitTx >= iClock)
                    break block6;
                TTypeFlit aFlitType = this.fPLinkTx.getFront(this.fVLinkIdLastServedTx).getType();
                switch (TCore.$SWITCH_TABLE$network$common$TTypeFlit()[aFlitType.ordinal()]) {
                    case 1: {
                        boolean IsCanSendFlit = this.fRouter.canPushHead(this.fSwitchCorePLinkId, this.fVLinkIdLastServedTx, iClock);
                        if (IsCanSendFlit) {
                            break;
                        }
                        break block6;
                    }
                    case 2: {
                        boolean IsCanSendFlit = this.fRouter.canPushData(this.fSwitchCorePLinkId, this.fVLinkIdLastServedTx, iClock);
                        if (!IsCanSendFlit) break block6;
                    }
                }
                TFlit aFlit = this.fPLinkTx.popFront(this.fVLinkIdLastServedTx, iClock);
                this.fRouter.pushBackRx(this.fSwitchCorePLinkId, aFlit, iClock);
                this.fClockLastFlitTx = iClock;
                this.fIsPLinkTxUsed = true;
                tNetworkManager.getStatistic().incCountCoreFlitTx(this.fId);
                return;
            }
            ++iVLink;
        }
    }

    public boolean canPushHead(int aVLinkId, int iClock) {
        return this.fBufferPLinkRx.getBufferVLink(aVLinkId).canPushHead(iClock);
    }

    public boolean canPush(int aVLinkId, int iClock) {
        return this.fBufferPLinkRx.getBufferVLink(aVLinkId).canPushData(iClock);
    }

    public void flitPushBackRx(TFlit aFlit, int iClock, TNetworkManager tNetworkManager) throws Exception {
        if (this.fId != aFlit.getCoreTo()) {
            System.err.printf("[ERROR][TCore] Не совпадение сетевого адреса IP-ядра и значения поля CoreTo флита. {\n\tFlitType: %d\n\tCoreFrom: %d\n\tCoreTo:   %d\n\tCoreNow:  %d\n}\n", aFlit.getType(), aFlit.getCoreFrom(), aFlit.getCoreTo(), this.fId);
            return;
        }
        aFlit.doAddExtra("[Core" + this.fId + "]");
        this.fBufferPLinkRx.getBufferVLink(aFlit.getVLinkId()).pushBack(aFlit, iClock);
        aFlit.incHopCount();
        tNetworkManager.getStatistic().incCountCoreFlitRx(this.fId);
    }

    public void doRestorePackets(int iClock, TNetworkManager tNetworkManager) throws Exception {
        int iVLink = 0;
        while (iVLink < this.fVLinkCount) {
            int aClockLastServed;
            ++this.fVLinkIdLastServedRx;
            this.fVLinkIdLastServedRx %= this.fVLinkCount;
            if (this.fBufferPLinkRx.getBufferVLink(this.fVLinkIdLastServedRx).canPop(iClock) && (aClockLastServed = this.fBufferPLinkRx.getBufferVLink(this.fVLinkIdLastServedRx).getFront().getClockLastServed()) != iClock) {
                TFlit iFlit = this.fBufferPLinkRx.getBufferVLink(this.fVLinkIdLastServedRx).popFront(iClock);
                switch (TCore.$SWITCH_TABLE$network$common$TTypeFlit()[iFlit.getType().ordinal()]) {
                    case 1: {
                        this.fArrPacketFlitsRx[iFlit.getVLinkId()] = new Vector<>();
                        this.fArrPacketFlitsRx[iFlit.getVLinkId()].add(iFlit);
                        this.fArrCountPacketFlitsToRx[iFlit.getVLinkId()] = iFlit.gePacketSize() - 1;
                        return;
                    }
                    case 2: {
                        if (this.fArrPacketFlitsRx[iFlit.getVLinkId()] == null) {
                            System.err.println("[ERROR][TCore] Не найден Header-флит для принятого Data-флита.");
                            return;
                        }
                        this.fArrPacketFlitsRx[iFlit.getVLinkId()].add(iFlit);
                        int[] arrn = this.fArrCountPacketFlitsToRx;
                        int n = iFlit.getVLinkId();
                        arrn[n] = arrn[n] - 1;
                        if (this.fArrCountPacketFlitsToRx[iFlit.getVLinkId()] == 0) break;
                        return;
                    }
                }
                long aPacketId = this.fArrPacketFlitsRx[iFlit.getVLinkId()].firstElement().GetId();
                if (!this.isPacketValid(this.fArrPacketFlitsRx[iFlit.getVLinkId()])) {
                    System.err.print("[ERROR][TCore] @todo: ");
                    this.fArrPacketFlitsRx[iFlit.getVLinkId()] = null;
                    return;
                }
                tNetworkManager.getStatistic().incCountPacketRx(this.fId, aPacketId);
                tNetworkManager.getStatistic().incCountPacketTime(this.fId, iClock - iFlit.getTimeGenerated());
                tNetworkManager.getStatistic().incCountPacketHop(this.fId, iFlit.getHopCount());
                this.fArrPacketFlitsRx[iFlit.getVLinkId()] = null;
                return;
            }
            ++iVLink;
        }
    }

    private boolean isPacketValid(Vector<TFlit> aPacket) {
        if (aPacket.isEmpty()) {
            return false;
        }
        TFlit aFlitHeader = aPacket.firstElement();
        int iFlitDataId = 0;
        while (!aPacket.isEmpty()) {
            TFlit iFlitData = aPacket.remove(0);
            ++iFlitDataId;
            if (aFlitHeader.getHopCount() == iFlitData.getHopCount()) continue;
            System.err.printf("[ERROR][TCore] Несовпадение количества хопов у флитов Rx-пакета {\n\taHFlitHopCount: %d\n\tiDFlitHopCount: %d\n\tiDFlitId:       %d\n}\n", aFlitHeader.getHopCount(), iFlitData.getHopCount(), iFlitDataId);
            return false;
        }
        return true;
    }

    public void doUpdateStatistic(int iClock, TNetworkManager tNetworkManager) {
        tNetworkManager.getStatistic().incCountUsedCoreSlotRx(this.fId, this.fBufferPLinkRx.getCountSlotUsed());
        tNetworkManager.getStatistic().incCountUsedCoreSlotTx(this.fId, this.fPLinkTx.getCountSlotUsed());
        if (this.fIsPLinkTxUsed) {
            tNetworkManager.getStatistic().incCountUsedCorePLinkTx(this.fId);
        }
        this.fIsPLinkTxUsed = false;
    }

    public void doPrepareNextClock(int iClock) {
        this.fBufferPLinkRx.doPrepareNextClock(iClock);
        this.fPLinkTx.doPrepareNextClock(iClock);
    }
}

