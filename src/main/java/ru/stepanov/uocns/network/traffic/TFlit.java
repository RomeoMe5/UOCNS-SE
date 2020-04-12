package ru.stepanov.uocns.network.traffic;

import ru.stepanov.uocns.network.common.TTypeFlit;

public class TFlit {
    TFlitData FFlitData;
    private long fId;
    private int fTimeLastServed;
    private int fCountHops;
    private String fExtra;

    public TFlit(TFlitData aFlitData, long aFlitId) {
        this.FFlitData = aFlitData;
        this.fTimeLastServed = this.FFlitData.fClockBorn;
        this.fCountHops = 0;
        this.fExtra = "";
        this.fId = aFlitId;
    }

    public TTypeFlit getType() {
        return this.FFlitData.fType;
    }

    public int getVLinkId() {
        return this.FFlitData.fVLinkId;
    }

    public void setVLinkId(int aVLinkId) {
        this.FFlitData.fVLinkId = aVLinkId;
    }

    public int getAddressSize() {
        return this.FFlitData.fSizeCore;
    }

    public int gePacketSize() {
        return this.FFlitData.fSizePacket;
    }

    public void incHopCount() {
        ++this.fCountHops;
    }

    public int getHopCount() {
        return this.fCountHops;
    }

    public int getClockLastServed() {
        return this.fTimeLastServed;
    }

    public void setClockLastServed(int iClock) {
        this.fTimeLastServed = iClock;
    }

    public int getTimeGenerated() {
        return this.FFlitData.fClockBorn;
    }

    public int getCoreFrom() {
        return this.FFlitData.fCoreFrom;
    }

    public int getCoreTo() {
        return this.FFlitData.fCoreTo;
    }

    public String doTagGet() {
        return this.fExtra;
    }

    public void doAddExtra(String aTail) {
        this.fExtra = String.valueOf(this.fExtra) + aTail;
    }

    public long GetId() {
        return this.fId;
    }

    public void SetId(long anId) {
        this.fId = anId;
    }
}

