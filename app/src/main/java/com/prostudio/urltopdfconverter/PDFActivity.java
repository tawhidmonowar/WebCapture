package com.prostudio.urltopdfconverter;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.android.material.snackbar.Snackbar;

public class PDFActivity extends AppCompatActivity {

    WebView printWeb, webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pdf);
        webView = findViewById(R.id.webViewMain);
        Button savePdfBtn = findViewById(R.id.savePdfBtn);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        // Checking Internet Connection
        if(InternetCheck.isConnected(this)) {
            RelativeLayout relativeLayout = findViewById(R.id.webViewLayout);
            relativeLayout.setVisibility(View.VISIBLE);

        } else {
            ImageView imageView = findViewById(R.id.no_internet_img);
            imageView.setVisibility(View.VISIBLE);
        }


        // Set WebView client
        webView.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                // Show the ProgressBar when the web page starts loading
                progressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                // Initialize printWeb object
                printWeb = webView;
                // Hide the ProgressBar when the web page finishes loading
                progressBar.setVisibility(View.GONE);
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);

        Intent intent = getIntent();
        String input_url = intent.getStringExtra("input-url");
        performSearch(input_url);

        // Save PDF button click listener
        savePdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (printWeb != null) {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        // Call createWebPrintJob() to start printing
                        PrintTheWebPage(printWeb);
                    } else {
                        showSnackbar("Not available for devices below Android Lollipop");
                    }
                } else {
                    showSnackbar("Web page not loaded");
                }
            }
        });

    }

    PrintJob printJob;
    boolean printBtnPressed = false;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void PrintTheWebPage(WebView webView) {
        // Set printBtnPressed to true
        printBtnPressed = true;

        // Create PrintManager instance
        PrintManager printManager = (PrintManager) this.getSystemService(Context.PRINT_SERVICE);

        // Set the name of the print job
        String tempName = "(url to pdf)" + webView.getUrl();
        String jobName = tempName.replace("https://.", " ");

        // Create PrintDocumentAdapter instance
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(jobName);

        // Create a print job with name and adapter instance
        assert printManager != null;
        printJob = printManager.print(jobName, printAdapter, new PrintAttributes.Builder().build());
    }

    @Override
    protected void onResume() {
        super.onResume();
        View parentLayout = findViewById(android.R.id.content);
        if (printJob != null && printBtnPressed) {
            if (printJob.isCompleted()) {
                // Show "Completed" message
                showSnackbar("Completed");
            } else if (printJob.isStarted()) {
                // Show "Started" message
                showSnackbar("Started");
            } else if (printJob.isBlocked()) {
                // Show "Blocked" message
                showSnackbar("Blocked");
            } else if (printJob.isCancelled()) {
                // Show "Cancelled" message
                showSnackbar("Cancelled");
            } else if (printJob.isFailed()) {
                // Show "Failed" message
                showSnackbar("Failed");
            } else if (printJob.isQueued()) {
                // Show "Queued" message
                showSnackbar("Queued");
            }
            // Set printBtnPressed to false
            printBtnPressed = false;
        }
    }

    // Perform search function
    private void performSearch(String input_url) {
        webView.loadUrl(input_url);
    }

    private void showSnackbar(String message) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            super.onBackPressed();
        }
    }
}