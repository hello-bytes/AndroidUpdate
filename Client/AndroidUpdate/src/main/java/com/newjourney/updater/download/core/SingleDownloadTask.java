package com.newjourney.updater.download.core;

import com.newjourney.updater.download.DownloadInfo;
import com.newjourney.updater.download.architecture.DownloadTask;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.util.Map;

/**
 * Created by Aspsine on 2015/7/22.
 */
public class SingleDownloadTask extends DownloadTaskImpl {

    public SingleDownloadTask(DownloadInfo mDownloadInfo, DownloadTask.OnDownloadListener mOnDownloadListener) {
        super(mDownloadInfo, mOnDownloadListener);
    }

    @Override
    protected int getResponseCode() {
        return HttpURLConnection.HTTP_OK;
    }

    @Override
    protected RandomAccessFile getFile(File fileObj,  long offset) throws IOException {
        RandomAccessFile raf = new RandomAccessFile(fileObj, "rwd");
        raf.seek(0);
        return raf;
    }

    @Override
    protected String getTag() {
        return this.getClass().getSimpleName();
    }
}

