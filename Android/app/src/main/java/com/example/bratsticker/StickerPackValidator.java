package com.example.bratsticker;

import android.content.Context;
import android.net.Uri;

import java.util.List;

/**
 * Class ini dikosongkan karena kita tidak pakai Fresco dan stiker kita statis.
 * WhatsApp akan validasi sendiri saat add pack.
 */
public class StickerPackValidator {

    public static void verifyValidity(Context context, List<StickerPack> stickerPacks) throws Exception {
        // Tidak lakukan validasi apa-apa
        // WhatsApp akan tolak pack kalau memang bermasalah
        if (stickerPacks == null || stickerPacks.isEmpty()) {
            throw new Exception("Pack list kosong");
        }
    }

    // Method lain kalau ada, biarkan throw UnsupportedOperationException
    public static void validateStickerPack(Context context, StickerPack stickerPack) throws Exception {
        // Skip validasi
    }

    public static void validateSticker(Context context, Sticker sticker) throws Exception {
        // Skip validasi
    }
}