package com.newjourney.updater;

import android.app.Activity;
import android.app.AlertDialog;

import com.newjourney.updater.download.CallBack;
import com.newjourney.updater.download.DownloadException;
import com.newjourney.updater.download.DownloadManager;
import com.newjourney.updater.download.DownloadRequest;
import com.newjourney.updater.update.UpdateService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.content.LocalBroadcastManager;

import java.io.File;

/**
 * Created by shishengyi on 16/10/1.
 */
public class UpdateAgent {
    private static UpdateListener updateListener;

    public static void init(Context context, String url){
        setUpdateParam(context,url);

        IntentFilter intentFilter = new IntentFilter(UpdateConst.LOCAL_UPDATE_ACTION);
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(context.getApplicationContext());
        lbm.registerReceiver(mLocalReceive, intentFilter);
    }

    public static void uninit(Context context) {
        LocalBroadcastManager lbm = LocalBroadcastManager.getInstance(context.getApplicationContext());
        lbm.unregisterReceiver(mLocalReceive);
    }

    private static BroadcastReceiver mLocalReceive = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if(intent.getAction().compareToIgnoreCase(UpdateConst.LOCAL_UPDATE_ACTION) == 0){
                if(UpdateAgent.updateListener != null){
                    UpdateResponse response = (UpdateResponse)intent.getSerializableExtra(UpdateConst.UPDATE_RESPONSE_PARAM);
                    UpdateAgent.updateListener.onUpdateReturned(response);
                }
            }
        }
    };

    private static void setUpdateParam(Context context, String url){
        UpdateParam param = new UpdateParam();
        param.checkUpdateUrl = url;

        Intent updateIntent = new Intent(context.getApplicationContext(), UpdateService.class);
        updateIntent.putExtra(UpdateConst.UPDATE_SERVICE_COMMAND,UpdateConst.COMMAND_ID_SETPARAM);

        Bundle bundle = new Bundle();
        bundle.putSerializable(UpdateConst.UPDATE_PARAM, param);
        updateIntent.putExtra(UpdateConst.INTENT_BUNDLE,bundle);
        //updateIntent.getBundleExtra(UpdateConst.INTENT_BUNDLE).putSerializable(UpdateConst.UPDATE_PARAM, param);
        context.getApplicationContext().startService(updateIntent);
    }

    public static void checkUpdate(Context context){
        Intent updateIntent = new Intent(context.getApplicationContext(), UpdateService.class);
        updateIntent.putExtra(UpdateConst.UPDATE_SERVICE_COMMAND,UpdateConst.COMMAND_ID_CHECKUPDATE);
        context.getApplicationContext().startService(updateIntent);
    }

    public static void download(Context context){
        Intent updateIntent = new Intent(context.getApplicationContext(), UpdateService.class);
        updateIntent.putExtra(UpdateConst.UPDATE_SERVICE_COMMAND,UpdateConst.COMMAND_ID_DOWNLOAD);
        context.getApplicationContext().startService(updateIntent);
    }

    public static boolean showUpdateDialog(final Context context, final UpdateResponse response){
        if(!(context instanceof Activity)){
            return false;
        }

        try{
            AlertDialog.Builder builder = new AlertDialog.Builder(context).setTitle(context.getResources().getString(R.string.update_dialog_title));
            builder.setMessage(response.updateLog);
            builder.setPositiveButton(context.getResources().getString(R.string.update_button_text), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    downloadFile(context, response);
                }
            });
            builder.setNegativeButton(context.getResources().getString(R.string.delayupdate_button_text), new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    dialogInterface.dismiss();
                }
            });
            builder.show();
        }catch (Exception ex){
        }
        return true;
    }

    private static String getDownloadPath(){
        return "";
    }

    private static void downloadFile(Context context, UpdateResponse response){
        Intent updateIntent = new Intent(context.getApplicationContext(), UpdateService.class);
        updateIntent.putExtra(UpdateConst.UPDATE_SERVICE_COMMAND,UpdateConst.COMMAND_ID_DOWNLOAD);
        updateIntent.putExtra("url",response.updateUrl);
        context.getApplicationContext().startService(updateIntent);
    }

    public static void setUpdateListener(UpdateListener updateListener){
        UpdateAgent.updateListener = updateListener;
    }
}
