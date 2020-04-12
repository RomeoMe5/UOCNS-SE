package ru.stepanov.uocns.network.unit.router.buffer;

import ru.stepanov.uocns.network.traffic.TFlit;

public interface IBufferVLink {

    void pushBack(TFlit var1, int var2) throws Exception;

    TFlit getFront();

    TFlit popFront(int var1) throws Exception;

    boolean canPushHead(int var1);

    boolean canPushData(int var1);

    boolean canPop(int var1);

    int getCount();

    int GetPacketFlitsRemain();

    void doPrepareNextClock(int var1);
}

