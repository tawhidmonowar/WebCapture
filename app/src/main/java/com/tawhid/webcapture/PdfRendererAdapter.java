package com.tawhid.webcapture;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.pdf.PdfRenderer;
import android.os.ParcelFileDescriptor;
import android.view.LayoutInflater;
import android.view.ScaleGestureDetector;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.IOException;

public class PdfRendererAdapter extends RecyclerView.Adapter<PdfRendererAdapter.PdfPageViewHolder> {

    private PdfRenderer pdfRenderer;

    public PdfRendererAdapter(File pdfFile) throws IOException {
        ParcelFileDescriptor fileDescriptor = ParcelFileDescriptor.open(pdfFile, ParcelFileDescriptor.MODE_READ_ONLY);
        pdfRenderer = new PdfRenderer(fileDescriptor);
    }

    @NonNull
    @Override
    public PdfPageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.pdf_page_item, parent, false);
        return new PdfPageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PdfPageViewHolder holder, int position) {
        holder.renderPage(position);
    }

    @Override
    public int getItemCount() {
        return pdfRenderer.getPageCount();
    }

    class PdfPageViewHolder extends RecyclerView.ViewHolder {
        ImageView pdfPageImageView;
        Matrix matrix = new Matrix();
        float scale = 1f;
        float currentScale = 1f;
        ScaleGestureDetector scaleGestureDetector;

        PdfPageViewHolder(View itemView) {
            super(itemView);
            pdfPageImageView = itemView.findViewById(R.id.pdfPageImageView);

            scaleGestureDetector = new ScaleGestureDetector(itemView.getContext(), new ScaleListener());
            itemView.setOnTouchListener((v, event) -> {
                scaleGestureDetector.onTouchEvent(event);
                pdfPageImageView.setImageMatrix(matrix);
                return true;
            });
        }

        void renderPage(int position) {
            renderPageAtScale(position, 1f);  // Render initially at 1x scale
        }

        void renderPageAtScale(int position, float scale) {
            PdfRenderer.Page page = pdfRenderer.openPage(position);

            // Calculate the width and height based on the scale
            int width = (int) (page.getWidth() * scale);
            int height = (int) (page.getHeight() * scale);
            Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);

            // Adjust rendering options
            page.render(bitmap, null, null, PdfRenderer.Page.RENDER_MODE_FOR_DISPLAY);
            pdfPageImageView.setImageBitmap(bitmap);
            page.close();
        }

        private class ScaleListener extends ScaleGestureDetector.SimpleOnScaleGestureListener {
            @Override
            public boolean onScale(ScaleGestureDetector detector) {
                scale *= detector.getScaleFactor();
                scale = Math.max(1f, Math.min(scale, 5f));

                // Only re-render if the scale has changed significantly
                if (Math.abs(scale - currentScale) > 0.1f) {
                    currentScale = scale;
                    renderPageAtScale(getAdapterPosition(), currentScale);
                }

                matrix.setScale(scale, scale, detector.getFocusX(), detector.getFocusY());
                pdfPageImageView.setImageMatrix(matrix);
                pdfPageImageView.invalidate();
                return true;
            }
        }
    }
}