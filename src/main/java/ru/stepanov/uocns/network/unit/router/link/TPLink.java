package ru.stepanov.uocns.network.unit.router.link;

import ru.stepanov.uocns.network.traffic.TFlit;
import ru.stepanov.uocns.network.unit.router.buffer.IBufferPLink;
import ru.stepanov.uocns.network.unit.router.buffer.TBufferPLink;

public class TPLink {
    protected IBufferPLink fBufferPLink;

    public TPLink(int aVLinkCount, int iVLinkSlotCount) {
        this.fBufferPLink = new TBufferPLink(aVLinkCount, iVLinkSlotCount);
    }

    public void pushBack(TFlit aFlit, int aVLinkId, int iClock) throws Exception {
        this.fBufferPLink.getBufferVLink(aVLinkId).pushBack(aFlit, iClock);
    }

    public final TFlit popFront(int aVLinkId, int iClock) throws Exception {
        return this.fBufferPLink.getBufferVLink(aVLinkId).popFront(iClock);
    }

    public final TFlit getFront(int aVLinkId) {
        return this.fBufferPLink.getBufferVLink(aVLinkId).getFront();
    }

    public final boolean canPushHead(int aVLinkId, int iClock) {
        return this.fBufferPLink.getBufferVLink(aVLinkId).canPushHead(iClock);
    }

    public final boolean canPushData(int aVLinkId, int iClock) {
        return this.fBufferPLink.getBufferVLink(aVLinkId).canPushData(iClock);
    }

    public final boolean canPop(int aVLinkId, int iClock) {
        return this.fBufferPLink.getBufferVLink(aVLinkId).canPop(iClock);
    }

    public final int getCountSlotUsed() {
        return this.fBufferPLink.getCountSlotUsed();
    }

    public void doPrepareNextClock(int iClock) {
        this.fBufferPLink.doPrepareNextClock(iClock);
    }
}

