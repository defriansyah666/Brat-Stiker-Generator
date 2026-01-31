package com.example.bratsticker;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class StickerPackListActivity extends AddStickerPackActivity {

    private RecyclerView recyclerView;
    private StickerPackListAdapter adapter;
    private final ArrayList<StickerPack> packList = new ArrayList<>();

    private static final String TAG = "BRAT_LIST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sticker_pack_list);

        recyclerView = findViewById(R.id.sticker_pack_list);

        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));

        adapter = new StickerPackListAdapter(packList, pack -> addStickerPackToWhatsApp(pack.identifier, pack.name));
        recyclerView.setAdapter(adapter);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("Sticker Pack Tersedia");
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "onResume dipanggil - mulai load pack");

        packList.clear();

        if (DynamicPackHolder.currentPack != null) {
            packList.add(DynamicPackHolder.currentPack);
            Log.d(TAG, "Pack berhasil ditambahkan ke list: " + DynamicPackHolder.currentPack.name);
            Toast.makeText(this, "Pack \"" + DynamicPackHolder.currentPack.name + "\" tersedia!", Toast.LENGTH_LONG).show();
        } else {
            Log.d(TAG, "DynamicPackHolder.currentPack NULL!");
            Toast.makeText(this, "Tidak ada pack yang dibuat. Generate ulang.", Toast.LENGTH_LONG).show();
        }

        adapter.notifyDataSetChanged();
        Log.d(TAG, "Adapter di-notify. Total pack di list: " + packList.size());
    }
}