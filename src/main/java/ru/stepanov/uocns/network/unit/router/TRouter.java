package ru.stepanov.uocns.network.unit.router;

import ru.stepanov.uocns.network.TNetworkManager;
import ru.stepanov.uocns.network.common.IConstants;
import ru.stepanov.uocns.network.common.TTypeFlit;
import ru.stepanov.uocns.network.traffic.TFlit;
import ru.stepanov.uocns.network.unit.core.TCore;
import ru.stepanov.uocns.network.unit.router.link.TPLinkRx;
import ru.stepanov.uocns.network.unit.router.link.TPLinkTx;

public class TRouter
        implements IRouter,
        IRoute,
        ICrossbar {
    private static /* synthetic */ int[] $SWITCH_TABLE$network$common$TTypeFlit;
    private final int fId;
    private final int fCountPLink;
    private final int fCountVLinkPerPLink;
    private final TCore[] fArrConnectedCore;
    private final IRouter[] fArrConnectedRouter;
    private final int[] fArrVLinkLastServed;
    private final TPLinkRx[] fArrPLinkRx;
    private final TPLinkTx[] fArrPLinkTx;
    private final boolean[] fArrIsUsedPLinkTx;
    private final int[] fArrCrossbar;

    public TRouter(int aRouterId, int aNetworkAddress, int aCountPLinkCore, int aCountPLinkSwitch, int aVLinkCount) {
        this.fId = aRouterId;
        this.fCountVLinkPerPLink = aVLinkCount;
        this.fCountPLink = aCountPLinkCore + aCountPLinkSwitch;
        this.fArrPLinkRx = new TPLinkRx[this.fCountPLink];
        this.fArrPLinkTx = new TPLinkTx[this.fCountPLink];
        this.fArrIsUsedPLinkTx = new boolean[this.fCountPLink];
        int iPLinkId = 0;
        while (iPLinkId < this.fCountPLink) {
            this.fArrPLinkRx[iPLinkId] = new TPLinkRx(this, this, iPLinkId, aVLinkCount, IConstants.fConfigNoC.fSizeVLinkBuffer);
            this.fArrPLinkTx[iPLinkId] = new TPLinkTx(aVLinkCount, IConstants.fConfigNoC.fSizeVLinkBuffer);
            this.fArrIsUsedPLinkTx[iPLinkId] = false;
            ++iPLinkId;
        }
        this.fArrCrossbar = new int[this.fCountPLink * this.fCountVLinkPerPLink];
        this.doResetCrossbarLinks();
        this.fArrConnectedCore = new TCore[aCountPLinkCore];
        this.fArrConnectedRouter = new IRouter[aCountPLinkSwitch];
        this.fArrVLinkLastServed = new int[this.fCountPLink];
        int iPort = 0;
        while (iPort < this.fArrVLinkLastServed.length) {
            this.fArrVLinkLastServed[iPort] = 0;
            ++iPort;
        }
    }

    static int[] $SWITCH_TABLE$network$common$TTypeFlit() {
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

    @Override
    public int getId() {
        return this.fId;
    }

    @Override
    public void doConnectCore(int aPLinkIdCore, TCore aCore) {
        this.fArrConnectedCore[aPLinkIdCore] = aCore;
    }

    @Override
    public void doConnectRouter(int aPLinkId, IRouter aRouter) {
        this.fArrConnectedRouter[aPLinkId] = aRouter;
    }

    public String getConnnectedRouters() {
        String aConnectedRoutersPlinkId = "";
        int iPLinkId = 0;
        while (iPLinkId < this.fArrConnectedRouter.length) {
            aConnectedRoutersPlinkId = this.fArrConnectedRouter[iPLinkId] != null ? String.valueOf(aConnectedRoutersPlinkId) + this.fArrConnectedRouter[iPLinkId].getId() + " " : String.valueOf(aConnectedRoutersPlinkId) + "-1 ";
            ++iPLinkId;
        }
        return aConnectedRoutersPlinkId;
    }

    @Override
    public void pushBackRx(int aPLinkId, TFlit aFlit, int iClock) throws Exception {
        this.fArrPLinkRx[aPLinkId].pushBack(aFlit, aFlit.getVLinkId(), iClock);
    }

    @Override
    public boolean canPushData(int aPLinkId, int aVLinkId, int iClock) {
        return this.fArrPLinkRx[aPLinkId].canPushData(aVLinkId, iClock);
    }

    @Override
    public boolean canPushHead(int aPLinkId, int aVLinkId, int iClock) {
        if (this.fArrPLinkRx[aPLinkId] != null) {
            return this.fArrPLinkRx[aPLinkId].canPushHead(aVLinkId, iClock);
        }
        return false;
    }

    @Override
    public int getCountPLink() {
        int aCountPLinkUsed = 0;
        int iPLink = 0;
        while (iPLink < this.fArrPLinkTx.length) {
            if (this.fArrPLinkTx[iPLink] != null) {
                ++aCountPLinkUsed;
            }
            ++iPLink;
        }
        return aCountPLinkUsed;
    }

    @Override
    public void doResetCrossbarLinks() {
        int iItem = 0;
        while (iItem < this.fArrCrossbar.length) {
            this.fArrCrossbar[iItem] = -1;
            ++iItem;
        }
    }

    @Override
    public void setCrossbarLinks(int iClock) throws Exception {
        int iPLink = 0;
        while (iPLink < this.fArrPLinkRx.length) {
            if (this.fArrPLinkRx[iPLink] != null) {
                this.fArrPLinkRx[iPLink].setCrossbarLinks(iClock);
            }
            ++iPLink;
        }
    }

    @Override
    public void moveTraficInternal(int iClock) throws Exception {
        int iItem = 0;
        while (iItem < this.fArrCrossbar.length) {
            if (-1 != this.fArrCrossbar[iItem]) {
                int aPLinkIdRx = iItem / this.fCountVLinkPerPLink;
                int aVLinkIdRx = iItem % this.fCountVLinkPerPLink;
                int aPLinkIdTx = this.fArrCrossbar[iItem] / this.fCountVLinkPerPLink;
                int aVLinkIdTx = this.fArrCrossbar[iItem] % this.fCountVLinkPerPLink;
                TFlit aFlit = this.fArrPLinkRx[aPLinkIdRx].popFront(aVLinkIdRx, iClock);
                if (this.fArrPLinkTx[aPLinkIdTx].canPushHead(aVLinkIdTx, iClock) || this.fArrPLinkTx[aPLinkIdTx].canPushData(aVLinkIdTx, iClock)) {
                    this.fArrPLinkTx[aPLinkIdTx].pushBack(aFlit, aVLinkIdTx, iClock);
                } else {
                    this.fArrPLinkTx[aPLinkIdTx].canPushHead(aVLinkIdTx, iClock);
                    this.fArrPLinkTx[aPLinkIdTx].canPushData(aVLinkIdTx, iClock);
                    throw new Exception("\u041e\u0448\u0438\u0431\u043a\u0430 \u0443\u0441\u0442\u0430\u043d\u043e\u0432\u043a\u0438 Crossbar-\u0441\u043e\u0435\u0434\u0438\u043d\u0435\u043d\u0438\u044f");
                }
            }
            ++iItem;
        }
    }

    private void sendFlitToRouter(IRouter aRouterTo, int aPLinkId, int iClock) throws Exception {
        TPLinkTx aPLinkControllerTx = this.fArrPLinkTx[aPLinkId];
        int iVLink = 0;
        while (iVLink < this.fCountVLinkPerPLink) {
            block7:
            {
                int[] arrn = this.fArrVLinkLastServed;
                int n2 = arrn[aPLinkId] + 1;
                arrn[aPLinkId] = n2;
                this.fArrVLinkLastServed[aPLinkId] = n2 % this.fCountVLinkPerPLink;
                if (aPLinkControllerTx.canPop(this.fArrVLinkLastServed[aPLinkId], iClock)) {
                    int aRouterToPLinkId = aRouterTo.getPLinIdConnectedRouter(this) + IConstants.fCountPLinkCores;
                    int aClockLastServed = aPLinkControllerTx.getFront(this.fArrVLinkLastServed[aPLinkId]).getClockLastServed();
                    if (aClockLastServed < iClock) {
                        TTypeFlit aFlitType = aPLinkControllerTx.getFront(this.fArrVLinkLastServed[aPLinkId]).getType();
                        switch (TRouter.$SWITCH_TABLE$network$common$TTypeFlit()[aFlitType.ordinal()]) {
                            case 1: {
                                boolean IsCanSendFlit = aRouterTo.canPushHead(aRouterToPLinkId, this.fArrVLinkLastServed[aPLinkId], iClock);
                                if (IsCanSendFlit) break;
                                break block7;
                            }
                            case 2: {
                                boolean IsCanSendFlit = aRouterTo.canPushData(aRouterToPLinkId, this.fArrVLinkLastServed[aPLinkId], iClock);
                                if (IsCanSendFlit) break;
                                break block7;
                            }
                            default: {
                                break block7;
                            }
                        }
                        TFlit aFlit = aPLinkControllerTx.popFront(this.fArrVLinkLastServed[aPLinkId], iClock);
                        aRouterTo.pushBackRx(aRouterToPLinkId, aFlit, iClock);
                        this.fArrIsUsedPLinkTx[aPLinkId] = true;
                        TNetworkManager.getStatistic().incCountRouterFlitTx(this.fId);
                        return;
                    }
                }
            }
            ++iVLink;
        }
    }

    private void sendFlitToCore(TCore aCoreTo, int aPLinkId, int iClock) throws Exception {
        TPLinkTx aPLinkControllerTx = this.fArrPLinkTx[aPLinkId];
        int iVLink = 0;
        while (iVLink < this.fCountVLinkPerPLink) {
            int[] arrn = this.fArrVLinkLastServed;
            int n2 = arrn[aPLinkId] + 1;
            arrn[aPLinkId] = n2;
            this.fArrVLinkLastServed[aPLinkId] = n2 % this.fCountVLinkPerPLink;
            if (aPLinkControllerTx.canPop(this.fArrVLinkLastServed[aPLinkId], iClock)) {
                TTypeFlit aFlitType;
                int aTimeCoreWillFlitRx = iClock + 1;
                int aTimeLastServed = aPLinkControllerTx.getFront(this.fArrVLinkLastServed[aPLinkId]).getClockLastServed();
                if (aTimeLastServed < iClock && aCoreTo.getClockLastFlitRx() < aTimeCoreWillFlitRx && ((aFlitType = aPLinkControllerTx.getFront(this.fArrVLinkLastServed[aPLinkId]).getType()) != TTypeFlit.Header || aCoreTo.canPushHead(this.fArrVLinkLastServed[aPLinkId], iClock)) && (aFlitType != TTypeFlit.Data || aCoreTo.canPush(this.fArrVLinkLastServed[aPLinkId], iClock))) {
                    aCoreTo.setClockLastFlitRx(iClock + 1);
                    TFlit aFlit = aPLinkControllerTx.popFront(this.fArrVLinkLastServed[aPLinkId], iClock);
                    aCoreTo.flitPushBackRx(aFlit, iClock);
                    this.fArrIsUsedPLinkTx[aPLinkId] = true;
                    TNetworkManager.getStatistic().incCountRouterFlitTx(this.fId);
                    return;
                }
            }
            ++iVLink;
        }
    }

    @Override
    public void moveTraficExternal(int iClock) throws Exception {
        int iPLink = 0;
        while (iPLink < this.fCountPLink) {
            int iRouterId = iPLink;
            if (iPLink < this.fArrConnectedRouter.length) {
                if (this.fArrConnectedRouter[iPLink] != null) {
                    this.sendFlitToRouter(this.fArrConnectedRouter[iRouterId], iPLink, iClock);
                }
            } else {
                int iCoreId = iPLink - this.fArrConnectedRouter.length;
                if (this.fArrConnectedCore[iCoreId] != null) {
                    this.sendFlitToCore(this.fArrConnectedCore[iCoreId], iPLink, iClock);
                }
            }
            ++iPLink;
        }
    }

    @Override
    public int getPLinIdConnectedRouter(IRouter aConnectedRouter) {
        int aPLinkId = -1;
        int iRouterId = 0;
        while (iRouterId < this.fArrConnectedRouter.length) {
            if (this.fArrConnectedRouter[iRouterId] == aConnectedRouter) {
                aPLinkId = iRouterId;
                break;
            }
            ++iRouterId;
        }
        return aPLinkId;
    }

    @Override
    public void doUpdateStatistic(int iClock) {
        int iPLink = 0;
        while (iPLink < this.fArrPLinkRx.length) {
            if (this.fArrPLinkRx[iPLink] != null) {
                TNetworkManager.getStatistic().incCountUsedRouterSlotRx(this.fId, this.fArrPLinkRx[iPLink].getCountSlotUsed());
                TNetworkManager.getStatistic().incCountUsedRouterSlotTx(this.fId, this.fArrPLinkTx[iPLink].getCountSlotUsed());
                TNetworkManager.getStatistic().incCountUsedRouterPLinkTx(this.fId, this.fArrIsUsedPLinkTx[iPLink] ? 1 : 0);
                this.fArrIsUsedPLinkTx[iPLink] = false;
            }
            ++iPLink;
        }
    }

    @Override
    public void doPrepareNextClock(int iClock) {
        this.doResetCrossbarLinks();
        int iPLink = 0;
        while (iPLink < this.fArrPLinkRx.length) {
            if (this.fArrPLinkRx[iPLink] != null) {
                this.fArrPLinkRx[iPLink].doPrepareNextClock(iClock);
            }
            if (this.fArrPLinkTx[iPLink] != null) {
                this.fArrPLinkTx[iPLink].doPrepareNextClock(iClock);
            }
            ++iPLink;
        }
    }

    @Override
    public int setConnectionHead(int aPLinkIdRx, int aVLinkIdRx, int aPLinkIdTx, int aVLinkIdTx) throws Exception {
        int aCrossbarValue;
        int aCrossbarItem = aPLinkIdRx * this.fCountVLinkPerPLink + aVLinkIdRx;
        this.fArrCrossbar[aCrossbarItem] = aCrossbarValue = aPLinkIdTx * this.fCountVLinkPerPLink + aVLinkIdTx;
        return aCrossbarValue;
    }

    @Override
    public void setConnectionData(int aPLinkIdRx, int aVLinkIdRx, int aConnectionId) throws Exception {
        int aCrossbarItem = aPLinkIdRx * this.fCountVLinkPerPLink + aVLinkIdRx;
        if (this.fArrCrossbar[aCrossbarItem] != -1) {
            throw new Exception("VLinkTx-\u0431\u0443\u0444\u0435\u0440 \u0437\u0430\u043d\u044f\u0442");
        }
        this.fArrCrossbar[aCrossbarItem] = aConnectionId;
    }

    @Override
    public void resetConnection(int aPLinkIdRx, int aVLinkIdRx) throws Exception {
        int aCrossbarItem = aPLinkIdRx * this.fCountVLinkPerPLink + aVLinkIdRx;
        this.fArrCrossbar[aCrossbarItem] = -1;
    }

    @Override
    public boolean canPushData(int aConnectionId, int iClock) throws Exception {
        int aPLinkIdTx = aConnectionId / this.fCountVLinkPerPLink;
        int aVLinkIdTx = aConnectionId % this.fCountVLinkPerPLink;
        return this.fArrPLinkTx[aPLinkIdTx].canPushData(aVLinkIdTx, iClock);
    }

    @Override
    public int tryAllocVLink(int aPLinkIdTx, int iClock) throws Exception {
        return this.fArrPLinkTx[aPLinkIdTx].tryAllocVLink(iClock);
    }

    @Override
    public int getPLinkTxId(int aCoreIdTo) {
        return IConstants.fConfigNoC.fRoutingTable[this.fId][aCoreIdTo];
    }
}

