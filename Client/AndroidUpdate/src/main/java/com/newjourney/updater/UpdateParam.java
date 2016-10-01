package com.newjourney.updater;

import android.os.Parcelable;

import java.io.Serializable;

/**
 * Created by shishengyi on 16/10/1.
 */
public class UpdateParam implements Serializable {
    public String checkUpdateUrl;

    // 下载地址，如果不设置，主是默认的cache目录下
    public String downloadFolder;
}
