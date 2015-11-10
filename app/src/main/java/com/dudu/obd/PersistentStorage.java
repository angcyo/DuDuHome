package com.dudu.obd;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;

import org.json.JSONArray;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PersistentStorage {
    private SharedPreferences mPreferences;
    private String JSONSTR = "JSONSTR";
    private List<String> alldatas;
    private static PersistentStorage mPersistentStorage;
    private Logger log;
    private Gson gson;
    private String[] alldataArr;
    public static PersistentStorage getInstance(Context context) {
        if (mPersistentStorage == null)
            mPersistentStorage = new PersistentStorage(context);
        return mPersistentStorage;
    }

    public PersistentStorage(Context context) {
        String fileName = "important_upload_data";
        mPreferences = context.getSharedPreferences(fileName,
                Context.MODE_PRIVATE);
        alldatas = new ArrayList<>();
        log = LoggerFactory.getLogger("cache.persist");
        gson = new Gson();
        getAll();
    }

    public int getCount() {
        getAll();
        if (!alldatas.isEmpty())
            return alldatas.size();
        return 0;
    }



    public boolean addTail(String jsonStrData) {
        if (alldatas != null)
            alldatas.add(jsonStrData);

            if (!gson.toJson(alldatas).equals("[]")) {
                mPreferences.edit().putString(JSONSTR, gson.toJson(alldatas)).commit();
            } else {
                mPreferences.edit().putString(JSONSTR, "").commit();
            }

        return true;
    }

    public boolean deleteHeader() {
        getAll();
        String str = "";
        if (alldatas != null && alldatas.size() > 0) {
            alldatas.remove(0);
            if(alldatas.size()>0){
                str = gson.toJson(alldatas);
            }
            mPreferences.edit().putString(JSONSTR,str).commit();
        } else {
            return false;
        }

        return true;
    }


    public List<String> getAll() {
        String str = mPreferences.getString(JSONSTR, "");
        if (alldatas != null && alldatas.size() > 0)
            alldatas.clear();
        try {
            if (str != null && !str.equals("")
                    && !str.equals("[]")) {
                alldataArr = gson.fromJson(str,String[].class);
                if (alldataArr != null) {
                    for (int i = 0; i < alldataArr.length; i++) {
                        alldatas.add(alldataArr[i].toString());
                    }
                }

            } else {
                alldatas = Collections
                        .synchronizedList(new ArrayList<String>());

            }
        } catch (Exception e) {

            log.error("PersistentStorage", e);

        }
        return alldatas;
    }

    public boolean clear(Context context) {
        mPreferences.edit().clear().commit();
        return true;
    }

}
