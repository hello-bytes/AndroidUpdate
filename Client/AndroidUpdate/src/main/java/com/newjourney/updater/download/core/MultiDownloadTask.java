package com.newjourney.updater.download.core;

/**
 * Created by Aspsine on 2015/7/20.
 */

import com.newjourney.updater.download.DownloadInfo;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Map;

/**
 * download thread
 */
public class MultiDownloadTask extends DownloadTaskImpl {

    public MultiDownloadTask(DownloadInfo downloadInfo, OnDownloadListener listener) {

        super(downloadInfo, listener);
    }

    @Override
    protected int getResponseCode() {
        return HttpURLConnection.HTTP_PARTIAL;
    }

    @Override
    protected RandomAccessFile getFile(File fileObj, long offset) throws IOException {
        //File file = new File(dir, name);
        RandomAccessFile raf = new RandomAccessFile(fileObj, "rwd");
        raf.seek(offset);
        return raf;
    }


    @Override
    protected String getTag() {
        return this.getClass().getSimpleName();
    }
}