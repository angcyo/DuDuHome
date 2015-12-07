package com.dudu.obd;


import com.dudu.android.launcher.utils.ByteTools;
import com.dudu.network.utils.DuduLog;

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
    private byte[] mResponseData = new byte[1024];
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
            processObdData(event.getData());
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

        if (data.length >= 4
                && data[0] == STX[0]
                && data[1] == STX[1]
                && data[2] == STX[2]
                && data[3] == STX[3]) {
            log.debug("processing data ");
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

       try {
           if(mResponseData!=null)
                 System.arraycopy(data, 0, mResponseData, mResponseDataOffset, data.length);
       }catch (NullPointerException e){

       }
        mResponseDataOffset += data.length;
        processHead();
    }

    /**缓存dpu发来的数据大小  */
    private final int DPU_DATA_BUFFER_LENGTH = 5*1024;

    /**用于缓存dpu发来的数据  */
    byte[] dpuDataBuffer;
    int dpuDataBufferPos = 0;

    public void proObdData(byte[] dataArrayFromData){
        DuduLog.d("收到数据："+ ByteTools.bytesToHexString(dataArrayFromData));

        //数据放入缓存，如果缓存不能放下新收到的数据，则覆盖之前的缓存
        if (dpuDataBufferPos + dataArrayFromData.length > DPU_DATA_BUFFER_LENGTH ) {
            System.arraycopy(dataArrayFromData, 0, dpuDataBuffer, 0, dataArrayFromData.length);
            dpuDataBufferPos = dataArrayFromData.length;
        } else {
            System.arraycopy(dataArrayFromData, 0, dpuDataBuffer, dpuDataBufferPos, dataArrayFromData.length);
            dpuDataBufferPos += dataArrayFromData.length;
        }


        for (int i = 0; i < dpuDataBufferPos; i++) {
            if (dpuDataBufferPos - i  < 10) {
                //GoloLog.e("缓存数据已处理完，返回");
                return;
            }
            if ((dpuDataBuffer[i] & 0xff) == 0x55 && (dpuDataBuffer[i + 1] & 0xff) == 0xaa)
            {
                int packageLength = ByteTools.parseShortFromArrayAsBig(dpuDataBuffer, i + 4);
                if ((packageLength <= dpuDataBufferPos -i - 7) && packageLength < 0x1003) {
                    byte[] dpuPackageArray = new byte[packageLength + 7];
                    byte checkData = (byte)0xff;
                    for (int j = 0; j < dpuPackageArray.length; j++) {
                        dpuPackageArray[j] = dpuDataBuffer[i + j];
                        checkData ^= dpuPackageArray[j];
                    }
                    if (checkData == 0) {
                        DuduLog.d("收到一包数据："+ ByteTools.bytesToHexString(dpuPackageArray));
                        for (int j = i + dpuPackageArray.length; j < dpuDataBufferPos; j++) {
                            dpuDataBuffer[j - (i + dpuPackageArray.length)] = dpuDataBuffer[j];
                        }
                        dpuDataBufferPos -= (i + dpuPackageArray.length);
                        i = -1;
                    }
                }
            }
        }




    }


    StringBuffer stringBuffer = new StringBuffer();
    private void processObdData(byte[] dataArray){
        try {
            String dataString = new String(dataArray, "UTF-8");
            stringBuffer.append(dataString);
            if(stringBuffer.toString().endsWith("\n")){
                praseData(stringBuffer.toString());
                stringBuffer.delete(0, stringBuffer.length() - 1);
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void praseData(String obdDatas){
        String[] obdDataArray = obdDatas.split("\n");
        if (obdDataArray.length <= 0)
            return;
        for (int i= 0; i < obdDataArray.length; i++){
            EventBus.getDefault().post(new EventRead.L1ReadDone(obdDataArray[i].getBytes()));
        }

    }


}
