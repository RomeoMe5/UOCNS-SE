package ru.stepanov.uocns.network;

import ru.stepanov.uocns.network.common.IConstants;
import ru.stepanov.uocns.network.unit.core.TCore;
import ru.stepanov.uocns.network.unit.router.IRouter;
import ru.stepanov.uocns.network.unit.router.TRouter;

import java.util.Vector;

public class TNetwork {
    private Vector<TCore> fVtrCores = new Vector();
    private Vector<IRouter> fVtrRouters = new Vector();

    public TNetwork() {
        this.doCreateNetworkCustom();
    }

    private void doCreateNetworkCustom() {
        int iCoreId = 0;
        int iRouterId = 0;
        while (iRouterId < IConstants.fConfigNoC.fNetlist.length) {
            TRouter iRouter = new TRouter(iRouterId, iRouterId, IConstants.fCountPLinkCores, IConstants.fConfigNoC.fCountPLinkRouters, IConstants.fConfigNoC.fCountVLinkPerPLink);
            int iPLinkIdCore = 0;
            while (iPLinkIdCore < IConstants.fCountPLinkCores) {
                TCore aCore = new TCore(iCoreId++, iRouter, iPLinkIdCore, IConstants.fConfigNoC.fCountVLinkPerPLink);
                iRouter.doConnectCore(iPLinkIdCore, aCore);
                this.fVtrCores.add(aCore);
                ++iPLinkIdCore;
            }
            this.fVtrRouters.add(iRouter);
            ++iRouterId;
        }
        iRouterId = 0;
        while (iRouterId < this.fVtrRouters.size()) {
            int iPLinkId = 0;
            while (iPLinkId < IConstants.fConfigNoC.fCountPLinkRouters) {
                if (-1 != IConstants.fConfigNoC.fNetlist[iRouterId][iPLinkId]) {
                    this.fVtrRouters.get(iRouterId).doConnectRouter(iPLinkId, this.fVtrRouters.get(IConstants.fConfigNoC.fNetlist[iRouterId][iPLinkId]));
                }
                ++iPLinkId;
            }
            ++iRouterId;
        }
        Vector<String> aNetlistConnected = new Vector<String>();
        int iRouterId2 = 0;
        while (iRouterId2 < this.fVtrRouters.size()) {
            TRouter iRouterCustom = (TRouter) this.fVtrRouters.get(iRouterId2);
            aNetlistConnected.add(iRouterCustom.getConnnectedRouters());
            ++iRouterId2;
        }
        int foo = 5;
    }

    public void setInitalEvents() {
        int iCoreId = 0;
        while (iCoreId < this.fVtrCores.size()) {
            this.fVtrCores.get((int) iCoreId).fCoreTraffic.setNextMessageTime(0, false);
            ++iCoreId;
        }
        int iRouterId = 0;
        while (iRouterId < this.fVtrRouters.size()) {
            this.fVtrRouters.get(iRouterId).doResetCrossbarLinks();
            TNetworkManager.getStatistic().setCountRouterPLink(iRouterId, this.fVtrRouters.get(iRouterId).getCountPLink());
            ++iRouterId;
        }
    }

    public void setCrossbarLinks(int iClock) throws Exception {
        int iRouterId = 0;
        while (iRouterId < this.fVtrRouters.size()) {
            this.fVtrRouters.get(iRouterId).setCrossbarLinks(iClock);
            ++iRouterId;
        }
    }

    public void moveTraficTxCore(int iClock) throws Exception {
        int iCoreId = 0;
        while (iCoreId < this.fVtrCores.size()) {
            this.fVtrCores.get(iCoreId).moveTraficExternal(iClock);
            ++iCoreId;
        }
    }

    public void moveTraficRxRouter(int iClock) throws Exception {
        int iRouterId = 0;
        while (iRouterId < this.fVtrRouters.size()) {
            this.fVtrRouters.get(iRouterId).moveTraficInternal(iClock);
            ++iRouterId;
        }
    }

    public void moveTraficTxRouter(int iClock) throws Exception {
        int iRouterId = 0;
        while (iRouterId < this.fVtrRouters.size()) {
            this.fVtrRouters.get(iRouterId).moveTraficExternal(iClock);
            ++iRouterId;
        }
    }

    public void doRestorePackets(int iClock) throws Exception {
        int iCoreId = 0;
        while (iCoreId < this.fVtrCores.size()) {
            this.fVtrCores.get(iCoreId).doRestorePackets(iClock);
            ++iCoreId;
        }
    }

    public void doUpdateStatistic(int iClock) {
        int iCoreId = 0;
        while (iCoreId < this.fVtrCores.size()) {
            this.fVtrCores.get(iCoreId).doUpdateStatistic(iClock);
            ++iCoreId;
        }
        int iRouterId = 0;
        while (iRouterId < this.fVtrRouters.size()) {
            this.fVtrRouters.get(iRouterId).doUpdateStatistic(iClock);
            ++iRouterId;
        }
    }

    public void doPrepareNextClock(int iClock) {
        int iCoreId = 0;
        while (iCoreId < this.fVtrCores.size()) {
            this.fVtrCores.get(iCoreId).doPrepareNextClock(iClock);
            ++iCoreId;
        }
        int iSwitchId = 0;
        while (iSwitchId < this.fVtrRouters.size()) {
            this.fVtrRouters.get(iSwitchId).doPrepareNextClock(iClock);
            ++iSwitchId;
        }
    }
}

