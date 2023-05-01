package com.prostudio.urltopdfconverter;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.print.PrintAttributes;
import android.print.PrintDocumentAdapter;
import android.print.PrintJob;
import android.print.PrintManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import com.google.android.material.snackbar.Snackbar;

public class MainActivity extends AppCompatActivity {

    //creating object of WebView
    WebView printWeb;
    WebView webView;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.AppTheme);
        setContentView(R.layout.activity_main);

        //Initializing the WebView
        webView=(WebView)findViewById(R.id.webViewMain);
        //Initializing the Button
        Button savePdfBtn=(Button)findViewById(R.id.savePdfBtn);

        //Setting we View Client
        webView.setWebViewClient(new WebViewClient()
        {
            @Override
            public void onPageFinished(WebView view, String url) {
                super.onPageFinished(view, url);
                //initializing the printWeb Object
                printWeb=webView;
            }
        });

        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setLoadWithOverviewMode(true);
        webView.getSettings().setUseWideViewPort(true);
        webView.getSettings().setDomStorageEnabled(true);
        webView.getSettings().setLoadsImagesAutomatically(true);
        //loading the URL
        webView.loadUrl("https://www.google.com");

        //setting clickListener for Save Pdf Button
        savePdfBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(printWeb!=null)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        //Calling createWebPrintJob()
                        PrintTheWebPage(printWeb);
                    }else
                    {
                        //Showing Toast message to user
                        Toast.makeText(MainActivity.this, "Not available for device below Android LOLLIPOP", Toast.LENGTH_SHORT).show();
                    }
                }
                else
                {
                    //Showing Toast message to user
                    Toast.makeText(MainActivity.this, "WebPage not fully loaded", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
    //object of print job
    PrintJob printJob;

    //a boolean to check the status of printing
    boolean printBtnPressed=false;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void PrintTheWebPage(WebView webView) {

        //set printBtnPressed true
        printBtnPressed=true;

        // Creating  PrintManager instance
        PrintManager printManager = (PrintManager) this
                .getSystemService(Context.PRINT_SERVICE);

        //setting the name of job
        String jobName = getString(R.string.app_name) +webView.getUrl();

        // Creating  PrintDocumentAdapter instance
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
        if(printJob!=null &&printBtnPressed) {
            if (printJob.isCompleted()) {
                //Showing Toast Message
                Snackbar.make(parentLayout, "Completed", Snackbar.LENGTH_SHORT).show();
            } else if (printJob.isStarted()) {
                //Showing Toast Message
                Snackbar.make(parentLayout, "Started", Snackbar.LENGTH_SHORT).show();
            } else if (printJob.isBlocked()) {
                //Showing Toast Message
                Snackbar.make(parentLayout, "Blocked", Snackbar.LENGTH_SHORT).show();
            } else if (printJob.isCancelled()) {
                //Showing Toast Message
                Snackbar.make(parentLayout, "Cancelled", Snackbar.LENGTH_SHORT).show();
            } else if (printJob.isFailed()) {
                //Showing Toast Message
                Snackbar.make(parentLayout, "Failed", Snackbar.LENGTH_SHORT).show();
            } else if (printJob.isQueued()) {
                //Showing Toast Message
                Snackbar.make(parentLayout, "Queued", Snackbar.LENGTH_SHORT).show();
            }
            //set printBtnPressed false
            printBtnPressed=false;
        }
    }


    @Override
    public void onBackPressed() {
        if(webView.canGoBack()){
            webView.goBack();
        }
        else
        {
            showDialog();
        }
    }

    public void showDialog(){
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu,menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Enter URL");
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                webView.loadUrl(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                return false;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

}