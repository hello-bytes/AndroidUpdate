package com.newjourney.testupdater;

import android.app.Application;

import com.newjourney.updater.UpdateAgent;

/**
 * Created by shishengyi on 16/10/1.
 */
public class App extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        UpdateAgent.init(this,"http://www.tuboshu.mobi/update?product=test");
    }
}
