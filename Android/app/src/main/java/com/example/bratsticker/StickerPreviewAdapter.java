package com.example.bratsticker;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class StickerPreviewAdapter
        extends RecyclerView.Adapter<StickerPreviewAdapter.ViewHolder> {

    private final List<Sticker> stickers = new ArrayList<>();

    public StickerPreviewAdapter(List<Sticker> stickers) {
        if (stickers != null) {
            this.stickers.addAll(stickers);
        }
    }

    public void updateStickers(List<Sticker> newStickers) {
        stickers.clear();
        if (newStickers != null) {
            stickers.addAll(newStickers);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sticker_preview, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        // 👉 SELALU AMBIL STICKER PERTAMA
        if (stickers.isEmpty()) return;

        Sticker sticker = stickers.get(0);

        File file = new File(
                holder.itemView.getContext().getFilesDir(),
                "stickers/" + DynamicPackHolder.currentPack.identifier + "/" +
                        sticker.imageFileName
        );

        if (file.exists()) {
            Bitmap bitmap = BitmapFactory.decodeFile(file.getAbsolutePath());
            if (bitmap != null) {
                holder.imageView.setImageBitmap(bitmap);
            } else {
                Log.e("STICKER", "Bitmap null: " + file.getAbsolutePath());
                holder.imageView.setImageResource(R.drawable.ic_sticker_placeholder);
            }
        } else {
            Log.e("STICKER", "File tidak ada: " + file.getAbsolutePath());
            holder.imageView.setImageResource(R.drawable.ic_sticker_placeholder);
        }
    }

    @Override
    public int getItemCount() {
        // 👉 TAMPILKAN 1 ITEM SAJA
        return stickers.isEmpty() ? 0 : 1;
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        ImageView imageView;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.sticker_preview_image);
        }
    }
}
