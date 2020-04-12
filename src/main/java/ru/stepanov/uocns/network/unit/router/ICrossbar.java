package ru.stepanov.uocns.network.unit.router;

public interface ICrossbar {

    int setConnectionHead(int var1, int var2, int var3, int var4) throws Exception;

    void setConnectionData(int var1, int var2, int var3) throws Exception;

    void resetConnection(int var1, int var2) throws Exception;

    boolean canPushData(int var1, int var2) throws Exception;

    int tryAllocVLink(int var1, int var2) throws Exception;

    int getId();
}

