package com.dudu.obd.drive;

import com.dudu.android.launcher.utils.FileUtils;
import com.dudu.android.launcher.utils.LogUtils;
import com.dudu.android.launcher.utils.MyDate;
import com.dudu.obd.gsensor.MotionData;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by dengjun on 2015/11/18.
 * Description :
 */
public class SaveGsensorDataEntity implements ISaveGsensorData {

    @Override
    public void saveToFile(List<MotionData> motionDataList, String filePath) {
        File file = new File(filePath);
        if (!file.exists())
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }

        FileWriter fileWriter = null;
        try {
            fileWriter = new FileWriter(filePath, true);
            for (int i = 0; i <motionDataList.size() ; i++) {
//                LogUtils.d("SaveGsensorDataEntity", "保存数据--"+motionDataList.get(i).toString());
                fileWriter.write(motionDataList.get(i).toString());
                fileWriter.write("\r\n");
                fileWriter.flush();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if (fileWriter != null)
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        }
    }

    @Override
    public List<MotionData> getFromFile(String filePath) {
        File file = new File(filePath);
        if (!file.exists())
            return null;

        List<MotionData> motionDataList = new ArrayList<MotionData>();

        FileReader fileReader = null;
        try {
            fileReader = new FileReader(filePath);
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String motionDataString = null;
            while ((motionDataString = bufferedReader.readLine()) != null){
//                LogUtils.d("SaveGsensorDataEntity", "读取数据"+ motionDataString);
//                LogUtils.d("SaveGsensorDataEntity", "读取数据--"+ createFromString(motionDataString).toString());
                motionDataList.add(createFromString(motionDataString));


//                if (motionDataList.size() == 100)
//                    break;
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e){
            e.printStackTrace();
        }


        return  motionDataList;
    }


    private MotionData createFromString(String motionDataString){
        MotionData motionData = new MotionData();
        motionData.mCurrentTime = motionDataString.split("V")[0];
        String[] xyzArray = motionDataString.split("V")[1].split(",");
        motionData.mX = Float.valueOf(xyzArray[0]);
        motionData.mY = Float.valueOf(xyzArray[1]);
        motionData.mZ = Float.valueOf(xyzArray[2]);

        return motionData;
    }
}
