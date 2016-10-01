package com.newjourney.updater.download;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

import com.newjourney.updater.download.architecture.DownloadResponse;
import com.newjourney.updater.download.architecture.DownloadStatusDelivery;
import com.newjourney.updater.download.architecture.Downloader;
import com.newjourney.updater.download.core.DownloadResponseImpl;
import com.newjourney.updater.download.core.DownloadStatusDeliveryImpl;
import com.newjourney.updater.download.core.DownloaderImpl;

import java.util.LinkedHashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Created by Aspsine on 2015/7/14.
 */
public class DownloadManager implements Downloader.OnDownloaderDestroyedListener {
    public static final String TAG = DownloadManager.class.getSimpleName();

    private static DownloadManager sDownloadManager;

    private DownloadConfiguration mConfig;
    private DownloadStatusDelivery mDelivery;
    private ExecutorService mExecutorService;
    private Map<String, Downloader> mDownloaderMap;

    public static DownloadManager getInstance() {
        if (sDownloadManager == null) {
            synchronized (DownloadManager.class) {
                sDownloadManager = new DownloadManager();
            }
        }
        return sDownloadManager;
    }

    /**
     * private construction
     */
    private DownloadManager() {
        mDownloaderMap = new LinkedHashMap<String, Downloader>();
    }

    public void init(Context context) {
        init(context, new DownloadConfiguration());
    }

    public void init(Context context, DownloadConfiguration config) {
        if (config.getThreadNum() > config.getMaxThreadNum()) {
            throw new IllegalArgumentException("thread num must < max thread num");
        }
        mConfig = config;
        mExecutorService = Executors.newFixedThreadPool(mConfig.getMaxThreadNum());
        mDelivery = new DownloadStatusDeliveryImpl(new Handler(Looper.getMainLooper()));
    }

    @Override
    public void onDestroyed(String key, Downloader downloader) {
        if (mDownloaderMap.containsKey(key)) {
            mDownloaderMap.remove(key);
        }
    }

    public void download(DownloadRequest request, CallBack callBack) {
        if (check(request.getTag())) {
            DownloadResponse response = new DownloadResponseImpl(mDelivery, callBack);
            Downloader downloader = new DownloaderImpl(request, response, mExecutorService, request.getTag(), mConfig, this);
            mDownloaderMap.put(request.getTag(), downloader);
            downloader.start();
        }
    }

    public void pause(String tag) {
        if (mDownloaderMap.containsKey(tag)) {
            Downloader downloader = mDownloaderMap.get(tag);
            if (downloader != null) {
                if (downloader.isRunning()) {
                    downloader.pause();
                }
            }
            mDownloaderMap.remove(tag);
        }
    }

    public void cancel(String tag) {
        if (mDownloaderMap.containsKey(tag)) {
            Downloader downloader = mDownloaderMap.get(tag);
            if (downloader != null) {
                downloader.cancel();
            }
            mDownloaderMap.remove(tag);
        }
    }

    public void pauseAll() {
        for (Downloader downloader : mDownloaderMap.values()) {
            if (downloader != null) {
                if (downloader.isRunning()) {
                    downloader.pause();
                }
            }
        }
    }

    public void cancelAll() {
        for (Downloader downloader : mDownloaderMap.values()) {
            if (downloader != null) {
                if (downloader.isRunning()) {
                    downloader.cancel();
                }
            }
        }
    }

    public DownloadInfo getDownloadProgress(String tag) {
        Downloader downloader = mDownloaderMap.get(tag);
        if(downloader != null){
            return downloader.getDownloadInfo();
        }
        return null;
    }

    private boolean check(String key) {
        if (mDownloaderMap.containsKey(key)) {
            //Downloader downloader = mDownloaderMap.get(key);
            //if (downloader != null) {
                return false;
            //}
        }
        return true;
    }

}
