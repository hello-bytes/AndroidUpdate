package com.newjourney.updater.download;

import java.io.File;

/**
 * Created by aspsine on 15-4-19.
 */
public class DownloadInfo {
    private String name;
    private String uri;
    private File downloadedFile;
    private int progress;
    private long length;
    private long finished;
    private boolean acceptRanges;

    private int status;

    public DownloadInfo() {
    }

    public DownloadInfo(String name, String uri, File downloadedFile) {
        this.name = name;
        this.uri = uri;
        this.downloadedFile = downloadedFile;
    }

    /*public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }*/

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public File getFile() {
        return downloadedFile;
    }

    public void setFile(File dir) {
        this.downloadedFile = dir;
    }

    public int getProgress() {
        return progress;
    }

    public void setProgress(int progress) {
        this.progress = progress;
    }

    public long getLength() {
        return length;
    }

    public void setLength(long length) {
        this.length = length;
    }

    public long getFinished() {
        return finished;
    }

    public void setFinished(long finished) {
        this.finished = finished;
    }

    public boolean isAcceptRanges() {
        return acceptRanges;
    }

    public void setAcceptRanges(boolean acceptRanges) {
        this.acceptRanges = acceptRanges;
    }
}
