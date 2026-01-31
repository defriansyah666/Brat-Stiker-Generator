package com.example.bratsticker;

import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.DialogFragment;

public abstract class AddStickerPackActivity extends BaseActivity {

    public static final String EXTRA_STICKER_PACK_ID = "sticker_pack_id";
    public static final String EXTRA_STICKER_PACK_AUTHORITY = "sticker_pack_authority";
    public static final String EXTRA_STICKER_PACK_NAME = "sticker_pack_name";

    private static final int ADD_PACK = 200;
    private static final String TAG = "WA_STICKER_DEBUG";

    protected void addStickerPackToWhatsApp(String identifier, String stickerPackName) {

        Log.d(TAG, "========== MULAI TAMBAH STICKER ==========");
        Log.d(TAG, "StickerPackId   : " + identifier);
        Log.d(TAG, "StickerPackName : " + stickerPackName);
        Log.d(TAG, "Authority       : " + BuildConfig.CONTENT_PROVIDER_AUTHORITY);

        try {
            boolean consumerInstalled =
                    WhitelistCheck.isWhatsAppConsumerAppInstalled(getPackageManager());
            boolean businessInstalled =
                    WhitelistCheck.isWhatsAppSmbAppInstalled(getPackageManager());

            Log.d(TAG, "WA Consumer terinstall : " + consumerInstalled);
            Log.d(TAG, "WA Business terinstall : " + businessInstalled);

            if (!consumerInstalled && !businessInstalled) {
                Log.e(TAG, "WhatsApp tidak terinstall!");
                Toast.makeText(this,
                        R.string.add_pack_fail_prompt_update_whatsapp,
                        Toast.LENGTH_LONG).show();
                return;
            }

            boolean whitelistedConsumer =
                    WhitelistCheck.isStickerPackWhitelistedInWhatsAppConsumer(this, identifier);
            boolean whitelistedBusiness =
                    WhitelistCheck.isStickerPackWhitelistedInWhatsAppSmb(this, identifier);

            Log.d(TAG, "Whitelisted Consumer : " + whitelistedConsumer);
            Log.d(TAG, "Whitelisted Business : " + whitelistedBusiness);

            if (!whitelistedConsumer && !whitelistedBusiness) {
                Log.d(TAG, "Belum ada di WA → tampilkan chooser");
                launchIntentToAddPackToChooser(identifier, stickerPackName);

            } else if (!whitelistedConsumer) {
                Log.d(TAG, "Tambah ke WA Consumer");
                launchIntentToAddPackToSpecificPackage(
                        identifier,
                        stickerPackName,
                        WhitelistCheck.CONSUMER_WHATSAPP_PACKAGE_NAME
                );

            } else if (!whitelistedBusiness) {
                Log.d(TAG, "Tambah ke WA Business");
                launchIntentToAddPackToSpecificPackage(
                        identifier,
                        stickerPackName,
                        WhitelistCheck.SMB_WHATSAPP_PACKAGE_NAME
                );

            } else {
                Log.w(TAG, "Sticker sudah ada di Consumer & Business");
                Toast.makeText(this,
                        "Sticker sudah terpasang di WhatsApp",
                        Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            Log.e(TAG, "EXCEPTION SAAT TAMBAH STICKER", e);
            Toast.makeText(this,
                    R.string.add_pack_fail_prompt_update_whatsapp,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void launchIntentToAddPackToSpecificPackage(
            String identifier,
            String stickerPackName,
            String whatsappPackageName) {

        Intent intent = createIntentToAddStickerPack(identifier, stickerPackName);
        intent.setPackage(whatsappPackageName);

        Log.d(TAG, "Launch ke package: " + whatsappPackageName);

        try {
            startActivityForResult(intent, ADD_PACK);
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "Activity WhatsApp tidak ditemukan", e);
            Toast.makeText(this,
                    R.string.add_pack_fail_prompt_update_whatsapp,
                    Toast.LENGTH_LONG).show();
        }
    }

    private void launchIntentToAddPackToChooser(
            String identifier,
            String stickerPackName) {

        Intent intent = createIntentToAddStickerPack(identifier, stickerPackName);

        Log.d(TAG, "Launch chooser WhatsApp");

        try {
            startActivityForResult(
                    Intent.createChooser(intent, getString(R.string.add_to_whatsapp)),
                    ADD_PACK
            );
        } catch (ActivityNotFoundException e) {
            Log.e(TAG, "Chooser gagal dibuka", e);
            Toast.makeText(this,
                    R.string.add_pack_fail_prompt_update_whatsapp,
                    Toast.LENGTH_LONG).show();
        }
    }

    @NonNull
    private Intent createIntentToAddStickerPack(
            String identifier,
            String stickerPackName) {

        Intent intent = new Intent("com.whatsapp.intent.action.ENABLE_STICKER_PACK");

        intent.putExtra(EXTRA_STICKER_PACK_ID, identifier);
        intent.putExtra(EXTRA_STICKER_PACK_AUTHORITY,
                BuildConfig.CONTENT_PROVIDER_AUTHORITY);
        intent.putExtra(EXTRA_STICKER_PACK_NAME, stickerPackName);

        Log.d(TAG, "Intent dibuat dengan extra:");
        Log.d(TAG, "- ID       : " + identifier);
        Log.d(TAG, "- NAME     : " + stickerPackName);
        Log.d(TAG, "- AUTHORITY: " + BuildConfig.CONTENT_PROVIDER_AUTHORITY);

        return intent;
    }

    @Override
    protected void onActivityResult(
            int requestCode,
            int resultCode,
            Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "========== HASIL DARI WHATSAPP ==========");
        Log.d(TAG, "requestCode: " + requestCode);
        Log.d(TAG, "resultCode : " + resultCode);

        if (requestCode == ADD_PACK) {

            if (resultCode == Activity.RESULT_OK) {
                Log.d(TAG, "SUCCESS: Sticker pack berhasil ditambahkan");
                Toast.makeText(this,
                        "Sticker berhasil ditambahkan ke WhatsApp",
                        Toast.LENGTH_LONG).show();

            } else if (resultCode == Activity.RESULT_CANCELED) {
                Log.e(TAG, "FAILED: WhatsApp menolak sticker pack");

                if (data != null && data.getExtras() != null) {
                    Log.e(TAG, "Extras dari WhatsApp:");
                    for (String key : data.getExtras().keySet()) {
                        Log.e(TAG, key + " = " + data.getExtras().get(key));
                    }

                    String validationError =
                            data.getStringExtra("validation_error");

                    if (validationError != null) {
                        Log.e(TAG, "VALIDATION ERROR: " + validationError);
                        Toast.makeText(this,
                                "WhatsApp error: " + validationError,
                                Toast.LENGTH_LONG).show();
                    }

                } else {
                    Log.e(TAG, "Tidak ada data tambahan dari WhatsApp");
                    Toast.makeText(this,
                            "Sticker ditolak tanpa detail",
                            Toast.LENGTH_LONG).show();
                }
            }
        }
    }
}
