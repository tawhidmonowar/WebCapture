package com.tawhid.webcapture;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class InternetCheck {

    public static boolean isConnected(WebViewActivity context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static boolean internetDisconnected(WebViewActivity context) {
        // Perform your actions when there's no internet connection
        return false;
    }
}