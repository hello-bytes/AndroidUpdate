package com.newjourney.updater.download.core;


import com.newjourney.updater.download.DownloadConfiguration;
import com.newjourney.updater.download.DownloadException;
import com.newjourney.updater.download.DownloadInfo;
import com.newjourney.updater.download.DownloadRequest;
import com.newjourney.updater.download.architecture.ConnectTask;
import com.newjourney.updater.download.architecture.DownloadResponse;
import com.newjourney.updater.download.architecture.DownloadStatus;
import com.newjourney.updater.download.architecture.DownloadTask;
import com.newjourney.updater.download.architecture.Downloader;

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Executor;

/**
 * Created by Aspsine on 2015/10/28.
 */
public class DownloaderImpl implements Downloader, ConnectTask.OnConnectListener, DownloadTask.OnDownloadListener {

    private DownloadRequest mRequest;
    private DownloadResponse mResponse;
    private Executor mExecutor;

    private String mTag;

    private DownloadConfiguration mConfig;
    private OnDownloaderDestroyedListener mListener;

    private int mStatus;
    private DownloadInfo mDownloadInfo;

    private ConnectTask mConnectTask;

    private List<DownloadTask> mDownloadTasks;

    public DownloaderImpl(DownloadRequest request, DownloadResponse response, Executor executor, String key, DownloadConfiguration config, OnDownloaderDestroyedListener listener) {
        mRequest = request;
        mResponse = response;
        mExecutor = executor;
        mTag = key;
        mConfig = config;
        mListener = listener;

        init();
    }

    private void init() {
        mDownloadInfo = new DownloadInfo(mRequest.getTitle().toString(), mRequest.getUri(), mRequest.getFolder());
        mDownloadTasks = new LinkedList<>();
    }

    public DownloadInfo getDownloadInfo(){
        return mDownloadInfo;
    }

    @Override
    public boolean isRunning() {
        return mStatus == DownloadStatus.STATUS_STARTED
                || mStatus == DownloadStatus.STATUS_CONNECTING
                || mStatus == DownloadStatus.STATUS_CONNECTED
                || mStatus == DownloadStatus.STATUS_PROGRESS;
    }

    @Override
    public void start() {
        mStatus = DownloadStatus.STATUS_STARTED;
        mResponse.onStarted();
        connect();
    }

    @Override
    public void pause() {
        if (mConnectTask != null) {
            mConnectTask.cancel();
        }
        for (DownloadTask task : mDownloadTasks) {
            task.pause();
        }
    }

    @Override
    public void cancel() {
        if (mConnectTask != null) {
            mConnectTask.cancel();
        }
        for (DownloadTask task : mDownloadTasks) {
            task.cancel();
        }
    }

    @Override
    public void onDestroy() {
        // trigger the onDestroy callback tell download manager
        mListener.onDestroyed(mTag, this);
    }

    @Override
    public void onConnecting() {
        mStatus = DownloadStatus.STATUS_CONNECTING;
        mResponse.onConnecting();
    }

    @Override
    public void onConnected(long time, long length, boolean isAcceptRanges) {
        mStatus = DownloadStatus.STATUS_CONNECTED;
        mResponse.onConnected(time, length, isAcceptRanges);

        mDownloadInfo.setAcceptRanges(isAcceptRanges);
        mDownloadInfo.setLength(length);
        download(length, isAcceptRanges);
    }

    @Override
    public void onConnectFailed(DownloadException de) {
        mStatus = DownloadStatus.STATUS_FAILED;
        mResponse.onConnectFailed(de);
        onDestroy();
    }

    @Override
    public void onConnectCanceled() {
        mStatus = DownloadStatus.STATUS_CANCELED;
        mResponse.onConnectCanceled();
        onDestroy();
    }

    @Override
    public void onDownloadConnecting() {
    }

    @Override
    public void onDownloadProgress(long finished, long length) {
        mStatus = DownloadStatus.STATUS_PROGRESS;
        // calculate percent
        final int percent = (int) (finished * 100 / length);
        mResponse.onDownloadProgress(finished, length, percent);
    }

    @Override
    public void onDownloadCompleted() {
        if (isAllComplete()) {
            mStatus = DownloadStatus.STATUS_COMPLETED;
            mResponse.onDownloadCompleted();
            onDestroy();
        }
    }

    @Override
    public void onDownloadPaused() {
        if (isAllPaused()) {
            mStatus = DownloadStatus.STATUS_PAUSED;
            mResponse.onDownloadPaused();
            onDestroy();
        }
    }

    @Override
    public void onDownloadCanceled() {
        if (isAllCanceled()) {
            mStatus = DownloadStatus.STATUS_CANCELED;
            mResponse.onDownloadCanceled();
            onDestroy();
        }
    }

    @Override
    public void onDownloadFailed(DownloadException de) {
        if (isAllFailed()) {
            mStatus = DownloadStatus.STATUS_FAILED;
            mResponse.onDownloadFailed(de);
            onDestroy();
        }
    }

    private void connect() {
        try{
            mConnectTask = new ConnectTaskImpl(mRequest.getUri(), this);
            mExecutor.execute(mConnectTask);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    private void download(long length, boolean acceptRanges) {
        initDownloadTasks(length, acceptRanges);
        // start tasks
        for (DownloadTask downloadTask : mDownloadTasks) {
            mExecutor.execute(downloadTask);
        }
    }

    //TODO
    private void initDownloadTasks(long length, boolean acceptRanges) {
        mDownloadTasks.clear();
        if(acceptRanges){

        }else{
            SingleDownloadTask sdt = new SingleDownloadTask(mDownloadInfo,this);
            mDownloadTasks.add(sdt);
        }
    }

    private boolean isAllComplete() {
        boolean allFinished = true;
        for (DownloadTask task : mDownloadTasks) {
            if (!task.isComplete()) {
                allFinished = false;
                break;
            }
        }
        return allFinished;
    }

    private boolean isAllFailed() {
        boolean allFailed = true;
        for (DownloadTask task : mDownloadTasks) {
            if (task.isDownloading()) {
                allFailed = false;
                break;
            }
        }
        return allFailed;
    }

    private boolean isAllPaused() {
        boolean allPaused = true;
        for (DownloadTask task : mDownloadTasks) {
            if (task.isDownloading()) {
                allPaused = false;
                break;
            }
        }
        return allPaused;
    }

    private boolean isAllCanceled() {
        boolean allCanceled = true;
        for (DownloadTask task : mDownloadTasks) {
            if (task.isDownloading()) {
                allCanceled = false;
                break;
            }
        }
        return allCanceled;
    }
}
