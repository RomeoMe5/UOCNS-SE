package ru.stepanov.uocns.network.unit.router.link;

import ru.stepanov.uocns.network.traffic.TFlit;
import ru.stepanov.uocns.network.unit.router.buffer.IBufferVLink;

public final class TPLinkTx
        extends TPLink {
    private final int[] fArrClockAllocated;

    public TPLinkTx(int aVLinkCount, int iVLinkSlotCount) {
        super(aVLinkCount, iVLinkSlotCount);
        this.fArrClockAllocated = new int[aVLinkCount];
        int iVLink = 0;
        while (iVLink < aVLinkCount) {
            this.fArrClockAllocated[iVLink] = 0;
            ++iVLink;
        }
    }

    @Override
    public void pushBack(TFlit aFlit, int aVLinkId, int iClock) throws Exception {
        super.pushBack(aFlit, aVLinkId, iClock);
        aFlit.setVLinkId(aVLinkId);
    }

    public int tryAllocVLink(int iClock) {
        int iVLink = 0;
        while (iVLink < this.fArrClockAllocated.length) {
            IBufferVLink iBufferVLink = this.fBufferPLink.getBufferVLink(iVLink);
            if (this.fArrClockAllocated[iVLink] < iClock && iBufferVLink.GetPacketFlitsRemain() == 0 && iBufferVLink.getCount() == 0) {
                this.fArrClockAllocated[iVLink] = iClock;
                return iVLink;
            }
            ++iVLink;
        }
        return -1;
    }

    public int getCountVLinkAllocated() {
        int aCount = 0;
        int iVLink = 0;
        while (iVLink < this.fBufferPLink.getBufferVLinkCount()) {
            if (this.fBufferPLink.getBufferVLink(iVLink).GetPacketFlitsRemain() > 0) {
                ++aCount;
            }
            ++iVLink;
        }
        return aCount;
    }
}

