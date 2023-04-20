package com.prostudio.webtopdf;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.Manifest;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_STORAGE_PERMISSION = 1;
    private EditText urlEditText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        urlEditText = findViewById(R.id.url_edit_text);
        Button convertButton = findViewById(R.id.convert_button);

        convertButton.setOnClickListener(view -> {
            String url = urlEditText.getText().toString();
            if (url.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter a URL", Toast.LENGTH_SHORT).show();
            } else {
                if (ContextCompat.checkSelfPermission(
                        MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) ==
                        PackageManager.PERMISSION_GRANTED) {
                        convertToPDF(url);
                } else {
                    ActivityCompat.requestPermissions(MainActivity.this,
                            new String[] {Manifest.permission.WRITE_EXTERNAL_STORAGE},
                            REQUEST_CODE_STORAGE_PERMISSION);
                }
            }
        });
    }

    private void convertToPDF(String url) {
        String fileName = "webpage.pdf";
        File outputFile = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName);
        try {
            OutputStream outputStream = new FileOutputStream(outputFile);
            Document document = new Document();
            PdfWriter.getInstance(document, outputStream);
            document.open();
            Paragraph paragraph = new Paragraph();
            paragraph.add(url);
            document.add(paragraph);
            document.close();
            Toast.makeText(this, "PDF saved to " + outputFile.getAbsolutePath(), Toast.LENGTH_SHORT).show();
        } catch (IOException | DocumentException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error saving PDF", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE_STORAGE_PERMISSION && grantResults.length > 0 &&
                grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            String url = urlEditText.getText().toString();
            convertToPDF(url);
        } else {
            Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show();
        }
    }

}