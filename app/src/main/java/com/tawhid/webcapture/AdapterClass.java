package com.tawhid.webcapture;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.List;

public class AdapterClass extends RecyclerView.Adapter<AdapterClass.AdapterViewholder> {

    Context context;
    List<String> pdffiles;

    public AdapterClass (Context context, List<String> pdffiles){
        this.context = context;
        this.pdffiles = pdffiles;
    }

    @NonNull
    @Override
    public AdapterViewholder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.pdf_files_design,parent,false);
        return new AdapterViewholder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull AdapterViewholder holder, @SuppressLint("RecyclerView") int position) {
        String path = pdffiles.get(position);
        File pdfFile = new File(path);
        String filename = pdfFile.getName();

        holder.filename.setText(filename);

        holder.filename.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(context,PDF_ViewActivity.class);
                intent.putExtra("filepath",path);
                context.startActivity(intent);
            }
        });

        holder.share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(pdffiles.get(position));
                Uri uri;

                uri = FileProvider.getUriForFile(context,"com.example.pdffiles.fileprovider",file);

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                intent.setType("application/pdf");
                intent.putExtra(Intent.EXTRA_STREAM,uri);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                context.startActivity(Intent.createChooser(intent,"Share File: "));

            }
        });

    }

    @Override
    public int getItemCount() {
        return pdffiles.size();
    }

    static class AdapterViewholder extends RecyclerView.ViewHolder{

        TextView filename;
        ImageView share;
        public AdapterViewholder(@NonNull View itemView) {
            super(itemView);

            filename = itemView.findViewById(R.id.fileName);
            share = itemView.findViewById(R.id.shareFile);

        }
    }
}