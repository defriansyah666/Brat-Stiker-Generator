package com.example.bratsticker;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import java.util.ArrayList;
import java.util.List;

public class StickerPackLoader {

    public static List<StickerPack> fetchStickerPacks(Context context) {
        List<StickerPack> stickerPackList = new ArrayList<>();

        // Gunakan BuildConfig untuk authority dinamis
        Uri metadataUri = Uri.parse("content://" + BuildConfig.CONTENT_PROVIDER_AUTHORITY + "/metadata");

        try (Cursor cursor = context.getContentResolver().query(metadataUri, null, null, null, null)) {
            if (cursor != null && cursor.moveToFirst()) {
                do {
                    String identifier = cursor.getString(cursor.getColumnIndex(StickerContentProvider.STICKER_PACK_IDENTIFIER_IN_QUERY));
                    String name = cursor.getString(cursor.getColumnIndex(StickerContentProvider.STICKER_PACK_NAME_IN_QUERY));
                    String publisher = cursor.getString(cursor.getColumnIndex(StickerContentProvider.STICKER_PACK_PUBLISHER_IN_QUERY));
                    String trayImageFile = cursor.getString(cursor.getColumnIndex(StickerContentProvider.STICKER_PACK_ICON_IN_QUERY));

                    // Kita tidak load full pack di sini karena sudah ada di DynamicPackHolder
                    // Cukup tambahkan jika perlu
                } while (cursor.moveToNext());
            }
        }

        // Karena kita pakai dynamic, cukup ambil dari holder
        if (DynamicPackHolder.currentPack != null) {
            stickerPackList.add(DynamicPackHolder.currentPack);
        }

        return stickerPackList;
    }
}