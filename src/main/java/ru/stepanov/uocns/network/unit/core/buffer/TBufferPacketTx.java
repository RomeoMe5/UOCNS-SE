package ru.stepanov.uocns.network.unit.core.buffer;

import ru.stepanov.uocns.network.traffic.TFlit;
import ru.stepanov.uocns.network.traffic.TPacket;

public final class TBufferPacketTx {
    private final TPacket[] fArrPacket;
    private int fPacketCount = 0;
    private final int[] fArrVLinkIdTx;

    public TBufferPacketTx(int aSlotCount) {
        this.fArrPacket = new TPacket[aSlotCount];
        this.fArrVLinkIdTx = new int[aSlotCount];
        int iSlot = 0;
        while (iSlot < aSlotCount) {
            this.fArrPacket[iSlot] = null;
            this.fArrVLinkIdTx[iSlot] = -1;
            ++iSlot;
        }
    }

    public int getPacketCount() {
        return this.fPacketCount;
    }

    public int getSlotCount() {
        return this.fArrPacket.length;
    }

    public int tryAllocFreeSlot() {
        int iSlot = 0;
        while (iSlot < this.fArrPacket.length) {
            if (this.fArrPacket[iSlot] == null) {
                return iSlot;
            }
            ++iSlot;
        }
        return -1;
    }

    public void setVLinkTxId(int aSlotId, int aVLinkTxId) throws Exception {
        if (this.fArrPacket[aSlotId] == null || this.fArrPacket[aSlotId].getCount() == 0 || this.fArrVLinkIdTx[aSlotId] != -1) {
            throw new Exception("\u0421\u043b\u043e\u0442 \u043d\u0435 \u0441\u043e\u0434\u0435\u0440\u0436\u0438\u0442 \u043f\u0430\u043a\u0435\u0442\u0430");
        }
        this.fArrVLinkIdTx[aSlotId] = aVLinkTxId;
    }

    public int getVLinkTxId(int aSlotId) throws Exception {
        if (this.fArrPacket[aSlotId] == null || this.fArrPacket[aSlotId].getCount() == 0) {
            throw new Exception("\u0421\u043b\u043e\u0442 \u043d\u0435 \u0441\u043e\u0434\u0435\u0440\u0436\u0438\u0442 \u043f\u0430\u043a\u0435\u0442\u0430");
        }
        return this.fArrVLinkIdTx[aSlotId];
    }

    public void insertPacket(TPacket aPacket, int aSlotId) throws Exception {
        if (this.fArrPacket[aSlotId] != null || this.fArrVLinkIdTx[aSlotId] != -1) {
            throw new Exception("\u0421\u043b\u043e\u0442 \u0437\u0430\u043d\u044f\u0442 \u0434\u0440\u0443\u0433\u0438\u043c \u043f\u0430\u043a\u0435\u0442\u043e\u043c");
        }
        this.fArrPacket[aSlotId] = aPacket;
        ++this.fPacketCount;
    }

    public TFlit popFront(int aSlotId) throws Exception {
        if (this.fArrPacket[aSlotId] == null || this.fArrPacket[aSlotId].getCount() == 0 || this.fArrVLinkIdTx[aSlotId] == -1) {
            throw new Exception("\u0421\u043b\u043e\u0442 \u043d\u0435 \u0441\u043e\u0434\u0435\u0440\u0436\u0438\u0442 \u043f\u0430\u043a\u0435\u0442\u0430");
        }
        TFlit aFlit = this.fArrPacket[aSlotId].popFlit();
        if (this.fArrPacket[aSlotId].getCount() == 0) {
            this.fArrPacket[aSlotId] = null;
            this.fArrVLinkIdTx[aSlotId] = -1;
            --this.fPacketCount;
        }
        return aFlit;
    }

    public boolean isEmpty(int aSlotId) throws Exception {
        if (this.fArrPacket[aSlotId] != null && this.fArrPacket[aSlotId].getCount() == 0) {
            throw new Exception("\u041e\u0448\u0438\u0431\u043a\u0430 \u0446\u0435\u043b\u043e\u0441\u043d\u043e\u0441\u0442\u0438 \u0431\u0443\u0444\u0435\u0440\u0430");
        }
        return this.fArrPacket[aSlotId] == null;
    }
}

