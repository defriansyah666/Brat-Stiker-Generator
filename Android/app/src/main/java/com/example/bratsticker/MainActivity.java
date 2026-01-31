package com.example.bratsticker;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private EditText editText;
    private Button btnGenerate;
    private RecyclerView rvPreview;
    private StickerPreviewAdapter previewAdapter;
    private ProgressBar progressBar;
    private Button btnAddToWhatsApp;

    private static final int ADD_PACK = 200;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.edit_text);
        btnGenerate = findViewById(R.id.btn_generate);
        rvPreview = findViewById(R.id.rv_preview);
        progressBar = findViewById(R.id.progress_bar);
        btnAddToWhatsApp = findViewById(R.id.btn_add_to_whatsapp);

        // Setup RecyclerView preview
        rvPreview.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        previewAdapter = new StickerPreviewAdapter(new ArrayList<>());
        rvPreview.setAdapter(previewAdapter);

        // Awalnya sembunyikan preview & tombol add
        rvPreview.setVisibility(View.GONE);
        btnAddToWhatsApp.setVisibility(View.GONE);

        btnGenerate.setOnClickListener(v -> {
            String text = editText.getText().toString().trim();
            if (text.isEmpty()) {
                Toast.makeText(this, "Masukkan teks dulu ya!", Toast.LENGTH_SHORT).show();
                return;
            }

            // Tampilkan loading
            progressBar.setVisibility(View.VISIBLE);
            btnGenerate.setEnabled(false);
            rvPreview.setVisibility(View.GONE);
            btnAddToWhatsApp.setVisibility(View.GONE);

            new GenerateStickerPackTask(text).execute();
        });

        btnAddToWhatsApp.setOnClickListener(v -> {
            StickerPack pack = DynamicPackHolder.currentPack;
            if (pack != null) {
                Intent intent = new Intent();
                intent.setAction("com.whatsapp.intent.action.ENABLE_STICKER_PACK");
                intent.putExtra("sticker_pack_id", pack.identifier);
                intent.putExtra("sticker_pack_authority", "com.example.bratsticker.stickercontentprovider");
                intent.putExtra("sticker_pack_name", pack.name);

                try {
                    startActivityForResult(intent, ADD_PACK);
                } catch (Exception e) {
                    Toast.makeText(this, "WhatsApp tidak ditemukan atau versi lama", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_PACK) {
            progressBar.setVisibility(View.GONE);
            btnGenerate.setEnabled(true);

            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Pack stiker berhasil ditambahkan ke WhatsApp! 🎉", Toast.LENGTH_LONG).show();
                // Optional: reset setelah berhasil add
                editText.setText("");
                rvPreview.setVisibility(View.GONE);
                btnAddToWhatsApp.setVisibility(View.GONE);
            } else {
                Toast.makeText(this, "Penambahan pack dibatalkan", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void onGenerateSuccess() {
        progressBar.setVisibility(View.GONE);
        btnGenerate.setEnabled(true);

        StickerPack pack = DynamicPackHolder.currentPack;
        if (pack != null && pack.getStickers() != null) {
            previewAdapter.updateStickers(pack.getStickers());
            rvPreview.setVisibility(View.VISIBLE);
            btnAddToWhatsApp.setVisibility(View.VISIBLE);
            Toast.makeText(this, "3 stiker siap! Klik tombol di bawah untuk tambah ke WhatsApp", Toast.LENGTH_LONG).show();
        }
    }

    private class GenerateStickerPackTask extends android.os.AsyncTask<Void, Void, Boolean> {
        private final String text;

        GenerateStickerPackTask(String text) {
            this.text = text;
        }

        @Override
        protected Boolean doInBackground(Void... voids) {
            try {
                StickerGenerator.generate(MainActivity.this, text);
                return true;
            } catch (Exception e) {
                e.printStackTrace();
                return false;
            }
        }

        @Override
        protected void onPostExecute(Boolean success) {
            if (success) {
                onGenerateSuccess();
            } else {
                progressBar.setVisibility(View.GONE);
                btnGenerate.setEnabled(true);
                Toast.makeText(MainActivity.this, "Gagal generate stiker. Coba lagi.", Toast.LENGTH_LONG).show();
            }
        }
    }
}