package com.eveningoutpost.dexdrip.plugin;

import static com.eveningoutpost.dexdrip.models.JoH.decompressBytesToBytes;
import static com.eveningoutpost.dexdrip.utils.DexCollectionType.Disabled;
import static com.eveningoutpost.dexdrip.utils.DexCollectionType.setDexCollectionType;
import static com.eveningoutpost.dexdrip.utils.FileUtils.writeToFile;

import com.eveningoutpost.dexdrip.Home;
import com.eveningoutpost.dexdrip.models.JoH;
import com.eveningoutpost.dexdrip.models.UserError;
import com.eveningoutpost.dexdrip.xdrip;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

/**
 * JamOrHam
 *
 * Plugin downloader
 */

public class Download {

    private static final String TAG = "PluginDownload";
    private static final String SCHEME = "https://";

    private static byte[] getData(final PluginDef pluginDef) {
        if (pluginDef == null) return null;
        final String url = getUrl(pluginDef);
        final OkHttpClient client = new OkHttpClient();
        final Request.Builder builder = new Request.Builder().url(url);
        try {
            final Response response = client.newCall(builder.build()).execute();
            if (response.code() == 410) {
                UserError.Log.wtf(TAG, "Shutdown requested");
                setDexCollectionType(Disabled);
            }
            if (response.isSuccessful()) {
                return response.body().bytes();
            } else {
                throw new RuntimeException("Got failure response code: " + response.code() + "\n" + (response.body() != null ? response.body().string() : ""));
            }
        } catch (IOException e) {
            UserError.Log.e(TAG, "Exception getting plugin: " + e);
            JoH.static_toast_long("Problem downloading plugin!");
            e.printStackTrace();
        }
        return null;
    }

    private static byte[] getSizedBlock(final ByteBuffer bb) {
        final int b1size = bb.getInt();
        if (b1size < 0 || b1size > 10000000) return null;
        final byte[] b1 = new byte[b1size];
        bb.get(b1);
        return b1;
    }

    public static boolean get(final PluginDef pluginDef) {
        try {
            final byte[] bytes = getData(pluginDef);
            if (bytes == null) return false;
            final ByteBuffer bb = ByteBuffer.wrap(bytes);
            final byte[] b1 = getSizedBlock(bb);
            final byte[] b2 = getSizedBlock(bb);
            final byte[] b3 = getSizedBlock(bb);
            final boolean ok = Verify.verify(b1, b2);
            if (ok && b3 != null) {
                final String storagePath = xdrip.getAppContext().getFilesDir().getPath();
                final String fileStruct = storagePath + "/" + pluginDef.name;
                writeToFile(TAG, fileStruct + ".dex", decompressBytesToBytes(b1));
                writeToFile(TAG, fileStruct + ".sig", b3);
                writeToFile(TAG, fileStruct + ".ver", pluginDef.version.getBytes(StandardCharsets.UTF_8));
                UserError.Log.ueh(TAG, pluginDef.canonical() + " plugin successfully downloaded");
                JoH.static_toast_long("Plugin successfully downloaded");
                Home.staticRefreshBGCharts();
                return true;
            } else {
                UserError.Log.e(TAG, "Invalid verification for " + pluginDef.name);
            }
        } catch (Exception e) {
            UserError.Log.e(TAG, "Exception in get() " + e);
        } finally {
            pluginDef.reset(); // move out of loading state
        }

        return false;
    }

    private static String getUrl(final PluginDef pluginDef) {
        return SCHEME + pluginDef.repository + "/" + pluginDef.canonical() + ".bin";
    }

}
