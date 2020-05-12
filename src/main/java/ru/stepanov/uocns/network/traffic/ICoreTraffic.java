package ru.stepanov.uocns.network.traffic;

import ru.stepanov.uocns.network.TNetworkManager;

import java.util.Vector;

public interface ICoreTraffic {

    void setNextMessageTime(int var1, boolean var2, TNetworkManager tNetworkManager);

    int getClockNextPacket();

    int getPacketCoreTo();

    int getPacketSize(TNetworkManager tNetworkManager);

    Vector<TFlit> getNewPacket(long var1, int var3, TNetworkManager tNetworkManager);

    TFlit getNewHeadFlit(long var1, int var3, int var4, int var5);

    TFlit getNewDataFlit(int var1, int var2);
}

