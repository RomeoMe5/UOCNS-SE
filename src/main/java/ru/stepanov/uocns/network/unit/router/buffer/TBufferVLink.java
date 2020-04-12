package ru.stepanov.uocns.network.unit.router.buffer;

import ru.stepanov.uocns.network.common.TTypeFlit;
import ru.stepanov.uocns.network.traffic.TFlit;

import java.util.concurrent.ArrayBlockingQueue;

public final class TBufferVLink
        implements IBufferVLink {
    private static int[] $SWITCH_TABLE$network$common$TTypeFlit;
    private final ArrayBlockingQueue<TFlit> FBufferFifo;
    private int fPacketFlitsRemain;
    private int fClockPush;
    private int fClockPop;

    public TBufferVLink(int aSlotCount) {
        this.FBufferFifo = new ArrayBlockingQueue(aSlotCount);
        this.fPacketFlitsRemain = 0;
        this.fClockPush = 0;
        this.fClockPop = 0;
    }

    static int[] $SWITCH_TABLE$network$common$TTypeFlit() {
        int[] arrn;
        int[] arrn2 = $SWITCH_TABLE$network$common$TTypeFlit;
        if (arrn2 != null) {
            return arrn2;
        }
        arrn = new int[TTypeFlit.values().length];
        try {
            arrn[TTypeFlit.Data.ordinal()] = 2;
        } catch (NoSuchFieldError ignored) {
        }
        try {
            arrn[TTypeFlit.Header.ordinal()] = 1;
        } catch (NoSuchFieldError ignored) {
        }
        try {
            arrn[TTypeFlit.Unknown.ordinal()] = 3;
        } catch (NoSuchFieldError ignored) {
        }
        $SWITCH_TABLE$network$common$TTypeFlit = arrn;
        return $SWITCH_TABLE$network$common$TTypeFlit;
    }

    @Override
    public void pushBack(TFlit aFlit, int iClock) throws Exception {
        if (this.fClockPush < iClock) {
            switch (TBufferVLink.$SWITCH_TABLE$network$common$TTypeFlit()[aFlit.getType().ordinal()]) {
                case 1: {
                    if (this.fPacketFlitsRemain != 0 || !this.FBufferFifo.isEmpty()) {
                        throw new Exception("\u041d\u0435 \u043f\u043e\u043b\u043d\u043e\u0441\u0442\u044c\u044e \u043f\u0440\u0438\u043d\u044f\u0442 \u043f\u0440\u0435\u0434\u044b\u0434\u0443\u0449\u0438\u0439 \u043f\u0430\u043a\u0435\u0442");
                    }
                    this.fPacketFlitsRemain = aFlit.gePacketSize() - 1;
                    break;
                }
                case 2: {
                    if (this.fPacketFlitsRemain <= 0) {
                        throw new Exception("\u041d\u0435\u0432\u0435\u0440\u043d\u044b\u0439 \u043f\u043e\u0434\u0441\u0447\u0435\u0442 \u0444\u043b\u0438\u0442\u043e\u0432 \u043f\u0430\u043a\u0435\u0442\u0430");
                    }
                    --this.fPacketFlitsRemain;
                    break;
                }
                default: {
                    throw new Exception("\u041d\u0435\u043e\u043f\u0440\u0435\u0434\u0435\u043b\u0435\u043d\u043d\u044b\u0439 \u0442\u0438\u043f \u0444\u043b\u0438\u0442\u0430");
                }
            }
        } else {
            throw new Exception("\u0417\u0430\u043f\u0438\u0441\u044c \u044d\u043b\u0435\u043c\u0435\u043d\u0442\u0430 \u0443\u0436\u0435 \u0432\u044b\u043f\u043e\u043b\u043d\u044f\u043b\u0430\u0441\u044c \u043d\u0430 \u0442\u0435\u043a\u0443\u0449\u0435\u043c \u0448\u0430\u0433\u0435 \u043c\u043e\u0434\u0435\u043b\u0438\u0440\u043e\u0432\u0430\u043d\u0438\u044f");
        }
        this.FBufferFifo.add(aFlit);
        aFlit.setClockLastServed(iClock);
        this.fClockPush = iClock;
    }

    @Override
    public TFlit getFront() {
        return this.FBufferFifo.peek();
    }

    @Override
    public TFlit popFront(int iClock) throws Exception {
        if (this.fClockPop < iClock) {
            TFlit aFlit = this.FBufferFifo.remove();
            aFlit.setClockLastServed(iClock);
            this.fClockPop = iClock;
            return aFlit;
        }
        throw new Exception("\u0427\u0442\u0435\u043d\u0438\u0435 \u044d\u043b\u0435\u043c\u0435\u043d\u0442\u0430 \u0443\u0436\u0435 \u0432\u044b\u043f\u043e\u043b\u043d\u044f\u043b\u043e\u0441\u044c \u043d\u0430 \u0442\u0435\u043a\u0443\u0449\u0435\u043c \u0448\u0430\u0433\u0435 \u043c\u043e\u0434\u0435\u043b\u0438\u0440\u043e\u0432\u0430\u043d\u0438\u044f");
    }

    @Override
    public boolean canPushHead(int iClock) {
        return this.fClockPush < iClock && this.FBufferFifo.isEmpty() && this.fPacketFlitsRemain == 0;
    }

    @Override
    public boolean canPushData(int iClock) {
        return this.fClockPush < iClock && this.FBufferFifo.remainingCapacity() > 0 && this.fPacketFlitsRemain > 0;
    }

    @Override
    public boolean canPop(int iClock) {
        return this.fClockPop < iClock && !this.FBufferFifo.isEmpty();
    }

    @Override
    public int getCount() {
        return this.FBufferFifo.size();
    }

    @Override
    public int GetPacketFlitsRemain() {
        return this.fPacketFlitsRemain;
    }

    @Override
    public void doPrepareNextClock(int iClock) {
    }
}

