package com.prostudio.urltopdfconverter;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class InternetCheck {

    public static boolean isConnected(PDFActivity context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null && activeNetwork.isConnectedOrConnecting();
    }

    public static void doSomethingWhenConnected(PDFActivity context) {
        // Perform your actions when there's internet connection
    }

    public static void doSomethingWhenDisconnected(PDFActivity context) {
        // Perform your actions when there's no internet connection
    }
}