package com.prostudio.urltopdfconverter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    LinearLayout duckduckgo, buttons, google, bing;
    WebView printWeb;
    WebView webView;

    @SuppressLint({"SetJavaScriptEnabled", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);

        // Initialize views
        webView = (WebView) findViewById(R.id.webViewMain);
        Button savePdfBtn = (Button) findViewById(R.id.savePdfBtn);
        ImageView back = findViewById(R.id.back);
        ImageView search = findViewById(R.id.search);
        EditText userInput = findViewById(R.id.webURL);
        ProgressBar progressBar = findViewById(R.id.progressBar);

        buttons = findViewById(R.id.buttons);
        duckduckgo = findViewById(R.id.duckduckgo);
        google = findViewById(R.id.google);
        bing = findViewById(R.id.bing);

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

        // Search button click listener
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String inputUrl = userInput.getText().toString().trim();
                // Check if the input URL is empty or does not start with "http://" or "https://"
                if (inputUrl.isEmpty() || (!inputUrl.startsWith("http://") && !inputUrl.startsWith("https://"))) {
                    // Add "https://" and "www." to the input URL
                    inputUrl = "https://www." + inputUrl;
                } else if (inputUrl.startsWith("http://")) {
                    // Replace "http://" with "https://" in the input URL
                    inputUrl = "https://" + inputUrl.substring(7);
                } else if (!inputUrl.startsWith("www.")) {
                    // Add "www." to the input URL
                    inputUrl = "https://www." + inputUrl;
                }
                // Add a trailing "/" to the input URL if it doesn't have one
                if (!inputUrl.endsWith("/")) {
                    inputUrl += "/";
                }

                loadWebPage(inputUrl);
                buttons.setVisibility(View.GONE);
                search.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                back.setVisibility(View.VISIBLE);
            }
        });

        // Back button click listener
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.loadData("", "text/html", null);
                buttons.setVisibility(View.VISIBLE);
                search.setVisibility(View.VISIBLE);
                webView.setVisibility(View.GONE);
                back.setVisibility(View.GONE);
            }
        });

        // Load predefined URLs
        duckduckgo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadWebPage("https://duckduckgo.com/");
                buttons.setVisibility(View.GONE);
                search.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                back.setVisibility(View.VISIBLE);
            }
        });

        google.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadWebPage("https://www.google.com/");
                buttons.setVisibility(View.GONE);
                search.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                back.setVisibility(View.VISIBLE);
            }
        });

        bing.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadWebPage("https://www.bing.com/");
                buttons.setVisibility(View.GONE);
                search.setVisibility(View.GONE);
                webView.setVisibility(View.VISIBLE);
                back.setVisibility(View.VISIBLE);
            }
        });

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
        String jobName = tempName.replace("https://www.", " ");

        // Create PrintDocumentAdapter instance
        PrintDocumentAdapter printAdapter = webView.createPrintDocumentAdapter(jobName);

        // Create a print job with name and adapter instance
        assert printManager != null;
        printJob = printManager.print(jobName, printAdapter,
                new PrintAttributes.Builder().build());
    }

    @Override
    protected void onResume() {
        super.onResume();
        View parentLayout = findViewById(android.R.id.content);
        if (printJob != null && printBtnPressed) {
            if (printJob.isCompleted()) {
                // Show "Completed" message
                Snackbar.make(parentLayout, "Completed", Snackbar.LENGTH_SHORT).show();
            } else if (printJob.isStarted()) {
                // Show "Started" message
                Snackbar.make(parentLayout, "Started", Snackbar.LENGTH_SHORT).show();
            } else if (printJob.isBlocked()) {
                // Show "Blocked" message
                Snackbar.make(parentLayout, "Blocked", Snackbar.LENGTH_SHORT).show();
            } else if (printJob.isCancelled()) {
                // Show "Cancelled" message
                Snackbar.make(parentLayout, "Cancelled", Snackbar.LENGTH_SHORT).show();
            } else if (printJob.isFailed()) {
                // Show "Failed" message
                Snackbar.make(parentLayout, "Failed", Snackbar.LENGTH_SHORT).show();
            } else if (printJob.isQueued()) {
                // Show "Queued" message
                Snackbar.make(parentLayout, "Queued", Snackbar.LENGTH_SHORT).show();
            }
            // Set printBtnPressed to false
            printBtnPressed = false;
        }
    }

    @Override
    public void onBackPressed() {
        if (webView.canGoBack()) {
            webView.goBack();
        } else {
            showDialog();
        }
    }

    public void showDialog() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.dialog_design);
        Button noButton = dialog.findViewById(R.id.no);
        noButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        Button yesButton = dialog.findViewById(R.id.yes);
        yesButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        dialog.show();
    }

    private void showSnackbar(String message) {
        View parentLayout = findViewById(android.R.id.content);
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT).show();
    }

    private void loadWebPage(String url) {
        webView.loadUrl(url);
    }
}