package com.newjourney.updater.download;


import java.net.HttpURLConnection;

/**
 * CallBack of download status
 */
public interface CallBack {

    void onStarted();
    void onConnecting();
    void onConnected(long total, boolean isRangeSupport);

    /**
     * <p> progress callback.
     *
     * @param finished the downloaded length of the file
     * @param total    the total length of the file same value with method {@link }
     * @param progress the percent of progress (finished/total)*100
     */
    void onProgress(long finished, long total, int progress);

    /**
     * <p> download complete
     */
    void onCompleted();

    /**
     * <p> if you invoke {@link DownloadManager#pause(String)} or {@link DownloadManager#pauseAll()}
     * this method will be invoke if the downloading task is successfully paused.
     */
    void onDownloadPaused();

    /**
     * <p> if you invoke {@link DownloadManager#cancel(String)} or {@link DownloadManager#cancelAll()}
     * this method will be invoke if the downloading task is successfully canceled.
     */
    void onDownloadCanceled();

    /**
     * <p> download fail or exception callback
     *
     * @param e download exception
     */
    void onFailed(DownloadException e);
}
