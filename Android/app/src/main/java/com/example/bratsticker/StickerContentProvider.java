package com.example.bratsticker;

import android.content.UriMatcher;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import android.text.TextUtils;

public class StickerContentProvider extends android.content.ContentProvider {

    // ================== KONSTANTA RESMI WHATSAPP (WAJIB ADA) ==================
    public static final String STICKER_PACK_IDENTIFIER_IN_QUERY = "sticker_pack_identifier";
    public static final String STICKER_PACK_NAME_IN_QUERY = "sticker_pack_name";
    public static final String STICKER_PACK_PUBLISHER_IN_QUERY = "sticker_pack_publisher";
    public static final String STICKER_PACK_ICON_IN_QUERY = "sticker_pack_icon";
    public static final String ANDROID_APP_DOWNLOAD_LINK_IN_QUERY = "android_play_store_link";
    public static final String IOS_APP_DOWNLOAD_LINK_IN_QUERY = "ios_app_download_link";
    public static final String PUBLISHER_EMAIL = "sticker_pack_publisher_email";
    public static final String PUBLISHER_WEBSITE = "sticker_pack_publisher_website";
    public static final String PRIVACY_POLICY_WEBSITE = "sticker_pack_privacy_policy_website";
    public static final String LICENSE_AGREENMENT_WEBSITE = "sticker_pack_license_agreement_website";
    public static final String IMAGE_DATA_VERSION = "image_data_version";
    public static final String AVOID_CACHE = "whatsapp_will_not_cache_stickers";

    public static final String STICKER_FILE_NAME_IN_QUERY = "sticker_file_name";
    public static final String STICKER_FILE_EMOJI_IN_QUERY = "sticker_emoji";

    // =========================================================================

    private static final String AUTHORITY = "com.example.bratsticker.stickercontentprovider";
    private static final UriMatcher MATCHER = new UriMatcher(UriMatcher.NO_MATCH);

    private static final int METADATA_CODE = 1;
    private static final int METADATA_SINGLE_CODE = 2;
    private static final int STICKERS_CODE = 3;
    private static final int ASSET_CODE = 4;

    static {
        MATCHER.addURI(AUTHORITY, "metadata", METADATA_CODE);
        MATCHER.addURI(AUTHORITY, "metadata/*", METADATA_SINGLE_CODE);
        MATCHER.addURI(AUTHORITY, "stickers/*", STICKERS_CODE);
        MATCHER.addURI(AUTHORITY, "stickers_asset/*/*", ASSET_CODE);
    }

    @Override
    public boolean onCreate() {
        return true;
    }

    private List<StickerPack> getPacks() {
        List<StickerPack> list = new ArrayList<>();
        if (DynamicPackHolder.currentPack != null) {
            list.add(DynamicPackHolder.currentPack);
        }
        return list;
    }

    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection,
                        @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        int code = MATCHER.match(uri);
        if (code == METADATA_CODE) {
            return getAllPacksCursor();
        } else if (code == METADATA_SINGLE_CODE) {
            return getSinglePackCursor(uri.getLastPathSegment());
        } else if (code == STICKERS_CODE) {
            return getStickersCursor(uri.getLastPathSegment());
        }
        return null;
    }

    private Cursor getAllPacksCursor() {
        return getPackCursor(getPacks());
    }

    private Cursor getSinglePackCursor(String identifier) {
        for (StickerPack pack : getPacks()) {
            if (pack.identifier.equals(identifier)) {
                return getPackCursor(Collections.singletonList(pack));
            }
        }
        return getPackCursor(new ArrayList<>());
    }

    private Cursor getPackCursor(List<StickerPack> packs) {
        MatrixCursor cursor = new MatrixCursor(new String[]{
                STICKER_PACK_IDENTIFIER_IN_QUERY,
                STICKER_PACK_NAME_IN_QUERY,
                STICKER_PACK_PUBLISHER_IN_QUERY,
                STICKER_PACK_ICON_IN_QUERY,
                ANDROID_APP_DOWNLOAD_LINK_IN_QUERY,
                IOS_APP_DOWNLOAD_LINK_IN_QUERY,
                PUBLISHER_EMAIL,
                PUBLISHER_WEBSITE,
                PRIVACY_POLICY_WEBSITE,
                LICENSE_AGREENMENT_WEBSITE,
                IMAGE_DATA_VERSION,
                AVOID_CACHE
        });

        for (StickerPack pack : packs) {
            cursor.newRow()
                    .add(pack.identifier)
                    .add(pack.name)
                    .add(pack.publisher)
                    .add(pack.trayImageFile)
                    .add(pack.androidPlayStoreLink)
                    .add(pack.iosAppStoreLink)
                    .add("")  // publisher_email
                    .add("")  // publisher_website
                    .add("")  // privacy_policy_website
                    .add("")  // license_agreement_website
                    .add("1") // image_data_version
                    .add(0);  // avoid_cache
        }
        return cursor;
    }

    private Cursor getStickersCursor(String identifier) {
        MatrixCursor cursor = new MatrixCursor(new String[]{
                STICKER_FILE_NAME_IN_QUERY,
                STICKER_FILE_EMOJI_IN_QUERY
        });

        for (StickerPack pack : getPacks()) {
            if (pack.identifier.equals(identifier)) {
                for (Sticker sticker : pack.getStickers()) {
                    cursor.addRow(new Object[]{
                            sticker.imageFileName,
                            TextUtils.join(",", sticker.emojis)
                    });
                }
            }
        }
        return cursor;
    }

    @Override
    public ParcelFileDescriptor openFile(@NonNull Uri uri, @NonNull String mode) throws FileNotFoundException {
        if (MATCHER.match(uri) != ASSET_CODE) return null;

        StickerPack pack = DynamicPackHolder.currentPack;
        if (pack == null) return null;

        File packDir = new File(getContext().getFilesDir(), "stickers/" + pack.identifier);
        String fileName = uri.getLastPathSegment();
        File file = new File(packDir, fileName);

        if (!file.exists()) {
            throw new FileNotFoundException("File tidak ditemukan: " + fileName);
        }

        return ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY);
    }

    @Override
    public String getType(@NonNull Uri uri) {
        return "image/webp";
    }

    // Operasi tidak didukung
    @Override public int delete(@NonNull Uri uri, String selection, String[] selectionArgs) { throw new UnsupportedOperationException(); }
    @Override public android.net.Uri insert(@NonNull Uri uri, android.content.ContentValues values) { throw new UnsupportedOperationException(); }
    @Override public int update(@NonNull Uri uri, android.content.ContentValues values, String selection, String[] selectionArgs) { throw new UnsupportedOperationException(); }
}