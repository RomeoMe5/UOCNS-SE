package ru.stepanov.uocns.network.unit.router.link;

import ru.stepanov.uocns.network.common.TTypeFlit;
import ru.stepanov.uocns.network.traffic.TFlit;
import ru.stepanov.uocns.network.unit.router.ICrossbar;
import ru.stepanov.uocns.network.unit.router.IRoute;
import ru.stepanov.uocns.network.unit.router.buffer.IBufferVLink;

public final class TPLinkRx
        extends TPLink {
    private final int fPLinkId;
    private final ICrossbar fCrossbar;
    private final IRoute fRoute;
    private final int[] fArrConnectionId;

    public TPLinkRx(ICrossbar aCrossbar, IRoute aRoute, int aPLinkId, int aVLinkCount, int iVLinkSlotCount) {
        super(aVLinkCount, iVLinkSlotCount);
        this.fRoute = aRoute;
        this.fCrossbar = aCrossbar;
        this.fPLinkId = aPLinkId;
        this.fArrConnectionId = new int[aVLinkCount];
        int iVLink = 0;
        while (iVLink < aVLinkCount) {
            this.fArrConnectionId[iVLink] = -1;
            ++iVLink;
        }
    }

    @Override
    public void pushBack(TFlit aFlit, int aVLinkId, int iClock) throws Exception {
        super.pushBack(aFlit, aVLinkId, iClock);
        aFlit.doAddExtra("[Router" + this.fCrossbar.getId() + "]");
        aFlit.incHopCount();
    }

    @Override
    public void doPrepareNextClock(int iClock) {
        super.doPrepareNextClock(iClock);
        int iVLinkId = 0;
        while (iVLinkId < this.fArrConnectionId.length) {
            IBufferVLink iBufferVlink = this.fBufferPLink.getBufferVLink(iVLinkId);
            if (iBufferVlink.getCount() == 0 && iBufferVlink.GetPacketFlitsRemain() == 0) {
                this.fArrConnectionId[iVLinkId] = -1;
            }
            ++iVLinkId;
        }
    }

    public void setCrossbarLinks(int iClock) throws Exception {
        int iVLink = 0;
        while (iVLink < this.fArrConnectionId.length) {
            if (!this.fBufferPLink.getBufferVLink(iVLink).canPop(iClock)) {
                this.fCrossbar.resetConnection(this.fPLinkId, iVLink);
            } else {
                TFlit iFlit = this.fBufferPLink.getBufferVLink(iVLink).getFront();
                if (iFlit.getType() == TTypeFlit.Header) {
                    int aPLinkTxId = this.fRoute.getPLinkTxId(iFlit.getCoreTo());
                    int aVLinkTxId = this.fCrossbar.tryAllocVLink(aPLinkTxId, iClock);
                    if (-1 != aVLinkTxId) {
                        this.fArrConnectionId[iVLink] = this.fCrossbar.setConnectionHead(this.fPLinkId, iVLink, aPLinkTxId, aVLinkTxId);
                    }
                } else if (iFlit.getType() == TTypeFlit.Data) {
                    if (-1 == this.fArrConnectionId[iVLink]) {
                        throw new Exception("\u041d\u0435\u0432\u0435\u0440\u043d\u044b\u0439 \u0438\u0434\u0435\u043d\u0442\u0438\u0444\u0438\u043a\u0430\u0442\u043e\u0440 \u0441\u043e\u0435\u0434\u0438\u043d\u0435\u043d\u0438\u044f");
                    }
                    if (this.fCrossbar.canPushData(this.fArrConnectionId[iVLink], iClock)) {
                        this.fCrossbar.setConnectionData(this.fPLinkId, iVLink, this.fArrConnectionId[iVLink]);
                    }
                } else {
                    throw new Exception("\u041d\u0435\u0438\u0437\u0432\u0435\u0441\u0442\u043d\u044b\u0439 \u0442\u0438\u043f \u0444\u043b\u0438\u0442\u0430");
                }
            }
            ++iVLink;
        }
    }
}

