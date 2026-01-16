package com.eveningoutpost.dexdrip.plugin;

import static com.eveningoutpost.dexdrip.utils.FileUtils.readFromFile;

import com.eveningoutpost.dexdrip.models.UserError.Log;
import com.eveningoutpost.dexdrip.utilitymodels.Inevitable;
import com.eveningoutpost.dexdrip.xdrip;

import java.io.File;


/**
 * JamOrHam
 *
 * Manage local file system cache for plugins
 */

public class Cache {

    private static final String TAG = "PluginCache";

    public static synchronized void refresh(final PluginDef def) {
        if (def.isFresh()) {
            if (getPath(def) != null) {
                Log.d(TAG, "Marking " + def.name + " as loaded");
                def.setLoaded();
                return;
            }
            def.setLoading();
            Inevitable.task("plugin-dload", 200, () -> Download.get(def));
            Log.d(TAG, "Trying to load " + def.name + "");
        }
        if (def.loadingFailed()) {
            def.reset();
            Log.e(TAG, "Download failed for: " + def.name);
        }
    }

    private static String checkPath(final String path, final PluginDef def, final boolean erase) {
        try {
            File f = new File(path, def.name + ".dex");
            if (f.exists()) {
                if (erase) {
                    f.delete();
                    return null;
                }
                File fs = new File(path, def.name + ".sig");
                if (fs.exists()) {
                    File fv = new File(path, def.name + ".ver");
                    if (fv.exists()) {
                        String v1 = new String(readFromFile(TAG, fv));
                        if (def.version.equals(v1)) {
                            byte[] b1 = readFromFile(TAG, f);
                            byte[] b2 = readFromFile(TAG, fs);
                            boolean ok = Verify.verify(b1, b2);
                            if (!ok) {
                                Log.e(TAG, "Failed local verification " + path + " " + def.name);
                                return null;
                            }
                            return f.getAbsolutePath();
                        } else {
                            Log.d(TAG, "Version doesn't match " + v1 + " vs " + def.version);
                        }
                    } else {
                        Log.d(TAG, "Missing version for " + def.name);
                    }
                } else {
                    Log.d(TAG, "Missing signature for " + def.name);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "checkPath exception: " + e);
        }
        return null;
    }

    private static String[] getPaths() {
        String appDir = xdrip.getAppContext().getFilesDir().getPath();
        return new String[]{appDir};
    }

    public static String getPath(final PluginDef def) {
        for (String c : getPaths()) {
            String r = checkPath(c, def, false);
            if (r != null) return r;
        }
        return null;
    }

    public static synchronized void erase(final PluginDef def) {
        for (String c : getPaths()) {
            String r = checkPath(c, def, true);
        }
        Loader.clear();
    }

}
