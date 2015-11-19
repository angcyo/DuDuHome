package com.dudu.obd.service;


import org.scf4a.EventRead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.UnsupportedEncodingException;
import java.util.LinkedList;

import de.greenrobot.event.EventBus;

public class PrefixReadL1 {
    public static final byte[] STX = {'$', 'O', 'B', 'D'};
    private final Logger log;
    private int mResponseDataOffset;
    private byte[] mResponseData;
    private LinkedList<EventRead.L0ReadDone> fifo;
    private volatile boolean processing;

    public PrefixReadL1() {
        mResponseDataOffset = 0;
        processing = false;
        fifo = new LinkedList<>();
        log = LoggerFactory.getLogger("aio.read.l1");
    }

    public void onEventBackgroundThread(EventRead.L0ReadDone event) {
        fifo.add(event);
        if (!processing) {
            processHead();
        }
    }

    private void processHead() {
        EventRead.L0ReadDone event = fifo.poll();
        if (event == null) {
            processing = false;
            return;
        }
        processing = true;

        final byte[] data = event.getData();
        if (data == null) return;
        if (data.length == 0) return;

        if (data.length >= 9
                && data[0] == STX[0]
                && data[1] == STX[1]
                && data[2] == STX[2]
                && data[3] == STX[3]) {

            if (mResponseDataOffset > 0) {
                byte[] tmp = new byte[mResponseDataOffset];
                System.arraycopy(mResponseData, 0, tmp, 0, mResponseDataOffset);
                EventBus.getDefault().post(new EventRead.L1ReadDone(tmp));

                try {
                    log.trace("Received Data = \n{}.", new String(tmp, "UTF-8"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            mResponseDataOffset = 0;
            mResponseData = new byte[1024];
        }

        System.arraycopy(data, 0, mResponseData, mResponseDataOffset, data.length);
        mResponseDataOffset += data.length;

        processHead();
    }
}
