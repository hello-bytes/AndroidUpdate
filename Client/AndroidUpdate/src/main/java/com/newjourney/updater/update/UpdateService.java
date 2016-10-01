package com.newjourney.updater.update;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.widget.Toast;

import com.newjourney.updater.R;
import com.newjourney.updater.UpdateConst;
import com.newjourney.updater.UpdateParam;
import com.newjourney.updater.UpdateResponse;
import com.newjourney.updater.download.CallBack;
import com.newjourney.updater.download.DownloadException;
import com.newjourney.updater.download.DownloadManager;
import com.newjourney.updater.download.DownloadRequest;
import com.newjourney.updater.util.IOCloseUtils;
import com.newjourney.updater.util.JSONObjectUtil;
import com.newjourney.updater.util.ShellUtil;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;



/**
 * Created by shishengyi on 16/10/1.
 */
public class UpdateService extends Service {

    private UpdateParam updateParam = null;

    private Notification mNotification;
    private NotificationManager mNotificationManager;


    @Override
    public void onCreate() {
        super.onCreate();

        DownloadManager.getInstance().init(getApplicationContext());
        mNotificationManager = (NotificationManager) getSystemService(Service.NOTIFICATION_SERVICE);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if(intent == null){
            return START_NOT_STICKY;
        }

        int commandId = intent.getIntExtra("command",0);
        if(commandId == UpdateConst.COMMAND_ID_CHECKUPDATE){
            onCheckUpdateCommand();
        }else if(commandId == UpdateConst.COMMAND_ID_DOWNLOAD){
            String url = intent.getStringExtra("url");
            startDownload(url);
        }else if(commandId == UpdateConst.COMMAND_ID_SETPARAM){
            Bundle bundle = intent.getBundleExtra(UpdateConst.INTENT_BUNDLE);
            UpdateParam param = (UpdateParam)bundle.getSerializable(UpdateConst.UPDATE_PARAM);
            setUpdateParam(param);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private Thread mCheckUpdateThread = null;
    private int onCheckUpdateCommand(){
        if(!checkEnv()){
            showToastErrorEnv();
            return START_NOT_STICKY;
        }

        if(mCheckUpdateThread != null){
            //上一次检查正在进行中，本次取消
            showToastErrorEnv();
            return START_NOT_STICKY;
        }

        try{
            mCheckUpdateThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try{
                        UpdateService.this.checkUpdate();
                    }catch(Exception ex){
                    }finally {
                        UpdateService.this.mCheckUpdateThread = null;
                    }
                }
            });
            mCheckUpdateThread.start();
        }catch (Exception ex){
            mCheckUpdateThread = null;
        }
        return START_NOT_STICKY;
    }

    private void showToastErrorEnv(){
        Toast.makeText(getApplicationContext(),"请先设置更新参数",Toast.LENGTH_SHORT).show();
    }

    private void startDownload(String url){
        DownloadRequest.Builder builder = new DownloadRequest.Builder().setUri(url);
        builder.setTitle("").setDescription("").setFolder(new File(updateParam.downloadFolder + "/" + getPackageName() + ".apk"));
        DownloadManager.getInstance().download(builder.build(), new CallBack() {
            private int lastProgress = 0;
            @Override
            public void onStarted() {
                lastProgress = 0;
                notifyMsg(getTitle(), getResources().getString(R.string.update_download_downloading), 0);
            }

            @Override
            public void onConnecting() {
            }

            @Override
            public void onConnected(long total, boolean isRangeSupport) {
            }

            @Override
            public void onProgress(long finished, long total, int progress) {
                if (progress - lastProgress > 4) {
                    notifyMsg(getTitle(), getResources().getString(R.string.update_download_downloading), (int)(progress));
                    lastProgress = progress;
                }
            }

            @Override
            public void onCompleted() {
                notifyMsg(getTitle(), getResources().getString(R.string.update_download_success), 100);

                String fileApk = updateParam.downloadFolder + "/" + getPackageName() + ".apk";

                File fileApkObj = new File(fileApk);
                if(!fileApkObj.exists()){
                    Toast.makeText(getApplicationContext(),"Error",Toast.LENGTH_SHORT).show();
                    return;
                }

                ShellUtil.execCommand("chmod 777 \"" + fileApkObj.getParent() + "\"", false);
                ShellUtil.execCommand("chmod 777 \"" + fileApk + "\"", false);

                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.setDataAndType(Uri.parse("file://" + fileApk), "application/vnd.android.package-archive");
                startActivity(intent);
            }

            @Override
            public void onDownloadPaused() {
            }

            @Override
            public void onDownloadCanceled() {
            }

            @Override
            public void onFailed(DownloadException e) {
                notifyMsg(getTitle(), getResources().getString(R.string.update_download_error), 100);
            }
        });
    }

    private void setUpdateParam(UpdateParam param){
        this.updateParam = param;
        if(this.updateParam != null){
            if(TextUtils.isEmpty(this.updateParam.downloadFolder)){
                File folderPath = getApplicationContext().getCacheDir();
                File updateFolder = new File(folderPath.getAbsolutePath(),"update");
                if(!updateFolder.exists()){
                    updateFolder.mkdir();
                }
                this.updateParam.downloadFolder = updateFolder.getAbsolutePath();
            }
        }
    }

    private boolean checkEnv(){
        if(this.updateParam == null){
            return false;
        }

        if(TextUtils.isEmpty(this.updateParam.checkUpdateUrl)){
            return false;
        }

        return true;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    /**
     * 安装apk文件
     *
     * @return
     */
    private PendingIntent getInstallIntent() {
        String fileApk = updateParam.downloadFolder + "/" + getPackageName() + ".apk";
        //File file = new File(fileApk);
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.parse("file://" + fileApk), "application/vnd.android.package-archive");
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        return pendingIntent;
    }

    private void checkUpdate(){
        if(!this.checkEnv()){
            return;
        }

        BufferedReader bufferedReader = null;
        HttpURLConnection urlConnection = null;
        try{
            String checkUrl = this.updateParam.checkUpdateUrl;
            String ver = getPackageManager().getPackageInfo(getPackageName(),0).versionCode + "";
            if(checkUrl.indexOf("?") > 0){
                checkUrl += "&ver=";
                checkUrl += ver;
            }else{
                checkUrl += "?ver=";
                checkUrl += ver;
            }

            URL url = new URL(checkUrl);
            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setUseCaches(false);
            urlConnection.setDoInput(true);
            urlConnection.setDoOutput(true);

            urlConnection.connect();

            StringBuilder sber = new StringBuilder();
            bufferedReader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream()));
            String readLine = null;
            while((readLine = bufferedReader.readLine()) != null){
                sber.append(readLine);
            }

            JSONObject jsonObject = new JSONObject(sber.toString());
            parseUpdateInfo(jsonObject);
        }catch (Exception ex){
        }finally {
            IOCloseUtils.close(bufferedReader);
            IOCloseUtils.disconnect(urlConnection);
        }
    }

    private void parseUpdateInfo( JSONObject jsonObject){
        UpdateResponse response = null;
        try
        {
            response = new UpdateResponse();
            response.errcode = JSONObjectUtil.getInt(jsonObject,"errorCode",-1);
            if(response.errcode == 0){
                JSONObject dataobj = jsonObject.getJSONObject("data");
                response.updateUrl = JSONObjectUtil.getString(dataobj,"url","");
                response.updateType = JSONObjectUtil.getInt(dataobj,"updateType",0);
                response.updateLog = JSONObjectUtil.getString(dataobj,"updateLog","");
                response.version = JSONObjectUtil.getString(dataobj,"version","");
                response.fileSize = JSONObjectUtil.getInt(dataobj,"fileSize",0);
            }
        }catch (Exception ex){
            response.errcode = -1;
        }finally {
            Intent intent = new Intent(UpdateConst.LOCAL_UPDATE_ACTION);
            intent.putExtra(UpdateConst.UPDATE_RESPONSE_PARAM,response);
            LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(getApplicationContext());
            lbm.sendBroadcast(intent);
        }
    }

    private void notifyMsg(String title, String content, int progress) {
        int resId = getLogo();//R.drawable.ic_launcher;
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
        builder.setSmallIcon(resId).setLargeIcon(BitmapFactory.decodeResource(getResources(),resId)).setContentTitle(title);
        //builder.setContentTitle(title);
        //builder.setS
        if (progress > 0 && progress < 100) {
            //下载进行中
            builder.setProgress(100, progress, false);
        } else {
            builder.setProgress(0, 0, false);
        }
        builder.setAutoCancel(true);
        builder.setWhen(System.currentTimeMillis());
        builder.setContentText(content);
        if (progress >= 100) {
            //下载完成
            builder.setContentIntent(getInstallIntent());
        }
        mNotification = builder.build();
        mNotificationManager.notify(0, mNotification);
    }

    private String mAppTitle;
    private String getTitle(){
        if(TextUtils.isEmpty(mAppTitle)){
            try{
                mAppTitle = getPackageManager().getPackageInfo(getPackageName(),0).applicationInfo.loadLabel(getPackageManager()).toString();
            }catch (Exception ex){
            }
        }
        return mAppTitle;
    }

    private int appLogoId = 0;
    private int getLogo(){
        if(appLogoId == 0){
            try{
                appLogoId = getPackageManager().getPackageInfo(getPackageName(),0).applicationInfo.icon;
            }catch (Exception ex){
            }
        }
        return appLogoId;
    }
}
