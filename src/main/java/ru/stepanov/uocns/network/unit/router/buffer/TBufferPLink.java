package ru.stepanov.uocns.network.unit.router.buffer;

public final class TBufferPLink
        implements IBufferPLink {
    protected IBufferVLink[] fArrBufferVLink;

    public TBufferPLink(int aVLinkCount, int iVLinkSlotCount) {
        this.fArrBufferVLink = new IBufferVLink[aVLinkCount];
        int iVLinkId = 0;
        while (iVLinkId < aVLinkCount) {
            this.fArrBufferVLink[iVLinkId] = new TBufferVLink(iVLinkSlotCount);
            ++iVLinkId;
        }
    }

    @Override
    public int getBufferVLinkCount() {
        return this.fArrBufferVLink.length;
    }

    @Override
    public IBufferVLink getBufferVLink(int aVLinkId) {
        return this.fArrBufferVLink[aVLinkId];
    }

    @Override
    public int getCountSlotUsed() {
        int aCount = 0;
        int iVLink = 0;
        while (iVLink < this.fArrBufferVLink.length) {
            aCount += this.fArrBufferVLink[iVLink].getCount();
            ++iVLink;
        }
        return aCount;
    }

    @Override
    public void doPrepareNextClock(int iClock) {
        int iVLink = 0;
        while (iVLink < this.fArrBufferVLink.length) {
            this.fArrBufferVLink[iVLink].doPrepareNextClock(iClock);
            ++iVLink;
        }
    }
}

