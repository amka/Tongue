package me.meamka.tongue;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

/**
 * Created by meamka on 24.04.17.
 */

public final class Utils {

    /**
     * Checks whether the network connection is active. Not to be confused with Internet connection
     *
     * @param context — activity context
     * @return active network connection state
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null;
    }

    /**
     * Checks whether the Internet connection is active or not
     *
     * @param context — activity context
     * @return active Internet connection state
     */
    public static boolean hasInternetConnection(Context context) {
        if (!isNetworkAvailable(context)) {
            Log.d("TONGUE", "No network available!");
        } else {
            try {
                HttpURLConnection cxn = (HttpURLConnection) (new URL("http://www.google.com").openConnection());
                cxn.setRequestProperty("User-Agent", "Test");
                cxn.setRequestProperty("Connection", "close");
                cxn.setConnectTimeout(1500);
                cxn.connect();
                return (cxn.getResponseCode() == 200);
            } catch (IOException e) {
                Log.e("TONGUE", "Error checking internet connection", e);
            }
        }
        return false;
    }
}
