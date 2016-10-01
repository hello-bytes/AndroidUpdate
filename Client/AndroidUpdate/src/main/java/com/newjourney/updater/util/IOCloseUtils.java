package com.newjourney.updater.util;

import java.io.Closeable;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URLConnection;

/**
 * Created by Aspsine on 2015/7/10.
 */
public class IOCloseUtils {

    public static final void close(Closeable closeable){
        try{
            if (closeable != null) {
                closeable.close();
            }
        }catch (Exception ex){
        }
    }

    public static final void disconnect(HttpURLConnection connect){
        try{
            if(connect != null){
                connect.disconnect();
            }
        }catch (Exception ex){

        }
    }
}
