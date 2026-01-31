package com.example.bratsticker;

import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

public class StickerPackDetailsActivity extends AddStickerPackActivity {

    private RecyclerView rvStickers;
    private StickerPreviewAdapter previewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_pack_details);

        StickerPack pack = DynamicPackHolder.currentPack;
        if (pack == null) {
            finish();
            return;
        }

        TextView tvTitle = findViewById(R.id.pack_title);
        TextView tvPublisher = findViewById(R.id.publisher);
        ImageView ivTray = findViewById(R.id.tray_icon);
        Button btnAdd = findViewById(R.id.add_button);
        rvStickers = findViewById(R.id.rv_stickers);

        tvTitle.setText(pack.name);
        tvPublisher.setText(pack.publisher);
        ivTray.setImageResource(R.drawable.ic_tray_placeholder);

        btnAdd.setOnClickListener(v -> addStickerPackToWhatsApp(pack.identifier, pack.name));

        // Preview 3 stiker
        LinearLayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);
        rvStickers.setLayoutManager(layoutManager);

        previewAdapter = new StickerPreviewAdapter(pack.getStickers());
        rvStickers.setAdapter(previewAdapter);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle(pack.name);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }
}