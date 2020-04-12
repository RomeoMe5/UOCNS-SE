package ru.stepanov.uocns.network.unit.router.buffer;

public interface IBufferPLink {

    int getBufferVLinkCount();

    IBufferVLink getBufferVLink(int var1);

    int getCountSlotUsed();

    void doPrepareNextClock(int var1);
}

