package com.newjourney.updater.util;

import org.json.JSONObject;

/**
 * Created by shishengyi on 16/10/1.
 */
public class JSONObjectUtil {

    public static int getInt(JSONObject jsonObject, String key, int def){
        try{
            return jsonObject.getInt(key);
        }catch (Exception ex){
        }
        return def;
    }

    public static String getString(JSONObject jsonObject, String key, String def){
        try{
            return jsonObject.getString(key);
        }catch (Exception ex){
        }
        return def;
    }
}
