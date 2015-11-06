package com.dudu.obd;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PersistentStorage {
    private SharedPreferences mPreferences;
    private String JSONSTR = "JSONSTR";
    private List<String> alldatas;
    private static PersistentStorage mPersistentStorage;
    private Logger log;

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
        getAll();
    }

    public int getCount() {
        getAll();
        if (!alldatas.isEmpty())
            return alldatas.size();
        return 0;
    }

    public String getHeader() {
        getAll();
        if (alldatas != null && alldatas.size() > 0) {
            return alldatas.get(0);
        } else
            return "";
    }

    public boolean addTail(String jsonStrData) {
        if (alldatas != null)
            alldatas.add(jsonStrData);
        String alldataString = "";
        JSONArray jsonArray = new JSONArray(alldatas);

        if (jsonArray != null) {
            alldataString = jsonArray.toString();
            if (!alldataString.equals(new JSONArray().toString())) {
                mPreferences.edit().putString(JSONSTR, alldataString).commit();
            } else {
                mPreferences.edit().putString(JSONSTR, "").commit();
            }
        }
        return true;
    }

    public boolean deleteHeader() {
        getAll();
        if (alldatas != null && alldatas.size() > 0) {
            alldatas.remove(0);
            JSONArray jsonArray2 = new JSONArray(alldatas);
            if (!jsonArray2.toString().equals(new JSONArray().toString())) {
                mPreferences.edit().putString(JSONSTR, jsonArray2.toString())
                        .commit();
            } else {
                mPreferences.edit().putString(JSONSTR, "").commit();
            }
        } else {
            return false;
        }

        return true;
    }

    // 移除第一条数据
    public boolean modifyHeader(String jsonStrData) {
        String alldatasing = "";
        getAll();
        if (alldatas != null && alldatas.size() > 0) {
            alldatas.set(0, jsonStrData);
            JSONArray jsonArray2 = new JSONArray(alldatas);
            if (jsonArray2 != null) {
                alldatasing = jsonArray2.toString();
                jsonArray2 = null;
                if (!alldatasing.equals(new JSONArray().toString())) {
                    mPreferences.edit().putString(JSONSTR, alldatasing)
                            .commit();
                } else {
                    mPreferences.edit().putString(JSONSTR, "").commit();
                }
            }
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
                    && !str.equals(new JSONArray().toString())) {
                JSONArray jsonArray = new JSONArray(str);

                if (jsonArray != null) {
                    for (int i = 0; i < jsonArray.length(); i++) {
                        alldatas.add(jsonArray.get(i).toString());
                    }
                }
            } else {
                alldatas = Collections
                        .synchronizedList(new ArrayList<String>());

            }
        } catch (JSONException e) {

            log.error("PersistentStorage", e);

        }
        return alldatas;
    }

    public boolean clear(Context context) {
        mPreferences.edit().clear().commit();
        return true;
    }

}
