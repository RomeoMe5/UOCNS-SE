package ru.stepanov.uocns.network.traffic;

import ru.stepanov.uocns.network.TNetworkManager;
import ru.stepanov.uocns.network.common.IConstants;
import ru.stepanov.uocns.network.common.TTypeFlit;
import ru.stepanov.uocns.network.common.TUtilities;

import java.util.Vector;

public class TCoreTraffic
        implements ICoreTraffic {
    private final int fCoreId;
    private int fClockNextPacket;

    public TCoreTraffic(int aCoreId) {
        this.fCoreId = aCoreId;
    }

    @Override
    public Vector<TFlit> getNewPacket(long aPacketId, int iClock) {
        this.setNextMessageTime(iClock, false);
        int aPacketSize = this.getPacketSize();
        int aPacketCoreTo = this.getPacketCoreTo();
        Vector<TFlit> aPacket = new Vector<TFlit>();
        TFlit iFlit = this.getNewHeadFlit(aPacketId, aPacketCoreTo, aPacketSize, iClock);
        iFlit.doAddExtra("[Head][Core" + this.fCoreId + "]");
        aPacket.add(iFlit);
        int iNewDataFlit = 1;
        while (iNewDataFlit < aPacketSize) {
            iFlit = this.getNewDataFlit(iClock, aPacketCoreTo);
            iFlit.doAddExtra("[Data][Core" + this.fCoreId + "]");
            aPacket.add(iFlit);
            ++iNewDataFlit;
        }
        return aPacket;
    }

    @Override
    public void setNextMessageTime(int iClock, boolean nextClock) {
        if (nextClock) {
            this.fClockNextPacket = iClock + 1;
            return;
        }
        this.fClockNextPacket = (int) ((double) (-IConstants.fConfigNoC.fPacketAvgGenTime) * Math.log(TNetworkManager.getUtilities().getRandNextDouble())) + iClock + 1;
    }

    @Override
    public int getClockNextPacket() {
        return this.fClockNextPacket;
    }

    @Override
    public int getPacketCoreTo() {
        int iCoreTo = (int) (Math.random() * (double) IConstants.fConfigNoC.fCountCores);
        while (iCoreTo == this.fCoreId) {
            iCoreTo = (int) (Math.random() * (double) IConstants.fConfigNoC.fCountCores);
        }
        return iCoreTo;
    }

    @Override
    public TFlit getNewHeadFlit(long aPacketId, int aCoreTo, int aPacketSize, int iClock) {
        TFlitData aFlitData = new TFlitData();
        aFlitData.fType = TTypeFlit.Header;
        aFlitData.fSizePacket = aPacketSize;
        aFlitData.fSizeCore = TUtilities.getMinBitsCount(IConstants.fConfigNoC.fCountCores);
        aFlitData.fCoreFrom = this.fCoreId;
        aFlitData.fCoreTo = aCoreTo;
        aFlitData.fVLinkId = -1;
        aFlitData.fClockBorn = iClock;
        int aMinSize = 1;
        aMinSize += TUtilities.getMinBitsCount(IConstants.fConfigNoC.fCountVLinkPerPLink);
        aMinSize += 2 * aFlitData.fSizeCore;
        aMinSize += TUtilities.getMinBitsCount(aFlitData.fSizeCore);
        if ((aMinSize += TUtilities.getMinBitsCount(aPacketSize)) <= IConstants.fConfigNoC.fFlitSize) {
            aMinSize = IConstants.fConfigNoC.fFlitSize;
        } else {
            System.err.print("Warning in getNewDataFlit()");
        }
        return new TFlit(aFlitData, aPacketId);
    }

    @Override
    public TFlit getNewDataFlit(int iClock, int aCoreTo) {
        TFlitData aFlitData = new TFlitData();
        aFlitData.fType = TTypeFlit.Data;
        aFlitData.fSizePacket = -1;
        aFlitData.fSizeCore = TUtilities.getMinBitsCount(IConstants.fConfigNoC.fCountCores);
        aFlitData.fCoreFrom = this.fCoreId;
        aFlitData.fCoreTo = aCoreTo;
        aFlitData.fVLinkId = -1;
        aFlitData.fClockBorn = iClock;
        aFlitData.fMinSize = -1;
        return new TFlit(aFlitData, -1);
    }

    @Override
    public int getPacketSize() {
        int aNewMessageSize;
        int aBitsPerByte = 8;
        if (IConstants.fConfigNoC.fPacketIsFixedLength) {
            aNewMessageSize = IConstants.fConfigNoC.fPacketAvgLenght;
        } else {
            System.err.printf("\u041e\u0448\u0438\u0431\u043a\u0430, \u0412\u043e \u0432\u0445\u043e\u0434\u043d\u043e\u043c \u0444\u0430\u0439\u043b\u0435 \u0440\u0430\u0437\u043c\u0435\u0440 \u043f\u0430\u043a\u0435\u0442\u0430 \u0434\u043e\u043b\u0436\u0435\u043d \u0437\u0430\u0434\u0430\u0432\u0430\u0442\u044c\u0441\u044f \u0432 \u0444\u043b\u0438\u0442\u0430\u0445.\u0412 \u0442\u0435\u043a\u0443\u0449\u0435\u0439 \u0440\u0435\u0430\u043b\u0438\u0437\u0430\u0446\u0438\u0438 \u0438\u043d\u0442\u0435\u0440\u043f\u0440\u0435\u0442\u0438\u0440\u0443\u0435\u0442\u0441\u044f \u043a\u0430\u043a \u0431\u0430\u0439\u0442\u044b.");
            aNewMessageSize = (int) ((double) (-8 * IConstants.fConfigNoC.fPacketAvgLenght) * Math.log(TNetworkManager.getUtilities().getRandNextDouble()) / (double) IConstants.fConfigNoC.fFlitSize);
            aNewMessageSize = aNewMessageSize > 1 ? aNewMessageSize : 2;
        }
        return aNewMessageSize;
    }
}

