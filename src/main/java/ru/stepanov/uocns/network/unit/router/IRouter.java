package ru.stepanov.uocns.network.unit.router;

import ru.stepanov.uocns.network.traffic.TFlit;
import ru.stepanov.uocns.network.unit.core.TCore;

public interface IRouter {

    int getId();

    int getCountPLink();

    void doConnectCore(int var1, TCore var2);

    void doConnectRouter(int var1, IRouter var2);

    void doResetCrossbarLinks();

    void pushBackRx(int var1, TFlit var2, int var3) throws Exception;

    boolean canPushData(int var1, int var2, int var3);

    void setCrossbarLinks(int var1) throws Exception;

    void moveTraficInternal(int var1) throws Exception;

    void moveTraficExternal(int var1) throws Exception;

    boolean canPushHead(int var1, int var2, int var3);

    void doUpdateStatistic(int var1);

    void doPrepareNextClock(int var1);

    int getPLinIdConnectedRouter(IRouter var1);
}

