package com.example.bratsticker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class StickerGenerator {

    private static final String TAG = "BRAT_GENERATOR";
    private static final int MAX_SIZE = 100_000;
    private static final int DIMENSION = 512;

    public static void generate(Context context, String text) throws Exception {
        Log.d(TAG, "MULAI GENERATE DARI TEKS: " + text);


        String identifier = "brat_" + Math.abs(text.toLowerCase().hashCode());
        File packDir = new File(context.getFilesDir(), "stickers/" + identifier);
        packDir.mkdirs();

        // 3 VARIASI TEKS DARI 1 INPUT USER
        String[] variants = {text, text, text};
        List<Sticker> stickers = new ArrayList<>();

        for (int i = 0; i < 3; i++) {
            String urlStr = "https://api.autoresbot.com/api/maker/brat?apikey=apikey_ujicoba&text=" +
                    URLEncoder.encode(variants[i], "UTF-8");

            Bitmap bitmap = downloadBitmap(urlStr);
            bitmap = Bitmap.createScaledBitmap(bitmap, DIMENSION, DIMENSION, true);

            byte[] webpBytes = compressToWebpUnder100KB(bitmap);

            String fileName = (i + 1) + ".webp";
            File stickerFile = new File(packDir, fileName);
            try (FileOutputStream fos = new FileOutputStream(stickerFile)) {
                fos.write(webpBytes);
            }

            stickers.add(new Sticker(fileName, Arrays.asList("brat")));
            Log.d(TAG, "Stiker " + (i+1) + " berhasil: " + variants[i]);
        }

        // Copy tray icon
        File trayFile = new File(packDir, "tray.webp");
        try (InputStream input = context.getAssets().open("tray_icon.webp");
             FileOutputStream output = new FileOutputStream(trayFile)) {
            byte[] buffer = new byte[1024];
            int read;
            while ((read = input.read(buffer)) != -1) {
                output.write(buffer, 0, read);
            }
        }

        StickerPack pack = new StickerPack(
                identifier,
                "Brat " + text,
                "Brat Generator",
                "tray.webp",
                "",
                "",
                "",
                "",
                "1",
                false
        );

        pack.setStickers(stickers);

        DynamicPackHolder.currentPack = pack;

        Log.d(TAG, "GENERATE SUKSES! Pack: " + pack.name + " | 3 stiker");
        Log.d(TAG, "Pack name: " + pack.name);
        Log.d(TAG, "Identifier: " + pack.identifier);
        Log.d(TAG, "Total stiker: " + pack.getStickers().size());
        Log.d(TAG, "Total size: " + pack.getTotalSize() + " bytes");

    }

    private static Bitmap downloadBitmap(String urlStr) throws Exception {
        URL url = new URL(urlStr);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(15000);
        conn.setReadTimeout(15000);
        conn.connect();

        if (conn.getResponseCode() != 200) {
            throw new Exception("Download gagal");
        }
        return BitmapFactory.decodeStream(conn.getInputStream());
    }

    private static byte[] compressToWebpUnder100KB(Bitmap bitmap) throws Exception {
        int quality = 95;
        byte[] bytes;

        do {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.WEBP, quality, baos);
            bytes = baos.toByteArray();
            quality -= 10;
        } while (bytes.length > MAX_SIZE && quality > 10);

        if (bytes.length > MAX_SIZE) {
            throw new Exception("Kompres gagal");
        }
        return bytes;
    }
}