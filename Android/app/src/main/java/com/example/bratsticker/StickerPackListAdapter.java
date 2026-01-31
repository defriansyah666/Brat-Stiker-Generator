package com.example.bratsticker;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class StickerPackListAdapter extends RecyclerView.Adapter<StickerPackListAdapter.ViewHolder> {

    private final List<StickerPack> packs;
    private final OnAddClickListener listener;

    public StickerPackListAdapter(List<StickerPack> packs, OnAddClickListener listener) {
        this.packs = packs;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_sticker_pack_list, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        StickerPack pack = packs.get(position);
        holder.name.setText(pack.name);
        holder.publisher.setText(pack.publisher);
        holder.trayImage.setImageResource(R.drawable.ic_tray_placeholder);
        holder.addButton.setOnClickListener(v -> listener.onAddClick(pack));
    }

    @Override
    public int getItemCount() {
        return packs.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, publisher;
        ImageView trayImage;
        Button addButton;

        ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.pack_name);
            publisher = itemView.findViewById(R.id.publisher_name);
            trayImage = itemView.findViewById(R.id.tray_image);
            addButton = itemView.findViewById(R.id.add_button);
        }
    }

    interface OnAddClickListener {
        void onAddClick(StickerPack pack);
    }
}