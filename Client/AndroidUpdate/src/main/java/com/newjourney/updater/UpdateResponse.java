package com.newjourney.updater;

import java.io.Serializable;

/**
 * Created by shishengyi on 16/10/1.
 */
public class UpdateResponse implements Serializable {
    public static final int HasNewVersion = 1;
    public static final int ForceUpdate = 2;
    public static final int NoUpdateVer = 3;

    public static final int ERROR_CODE_NONE = 0;
    public static final int ERROR_CODE_UNKNOWN = -1;
    public static final int ERROR_CODE_SERVEREXCEPTION = 1;


    public int errcode = 0;
    public int updateType =  NoUpdateVer;
    public String version = null;
    public String updateUrl = null;
    public String updateLog = null;

    public long fileSize;
    public long fileHashMD5;


    /*public String path;
    public String origin;
    public String proto_ver;
    public String new_md5;
    public String size;
    public String target_size;
    public boolean delta = false;*/

}
