package ru.stepanov.uocns.network.traffic;

import java.util.Vector;

public interface ICoreTraffic {

    void setNextMessageTime(int var1, boolean var2);

    int getClockNextPacket();

    int getPacketCoreTo();

    int getPacketSize();

    Vector<TFlit> getNewPacket(long var1, int var3);

    TFlit getNewHeadFlit(long var1, int var3, int var4, int var5);

    TFlit getNewDataFlit(int var1, int var2);
}

