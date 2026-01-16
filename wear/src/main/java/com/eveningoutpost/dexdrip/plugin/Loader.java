package com.eveningoutpost.dexdrip.plugin;

import static com.eveningoutpost.dexdrip.utils.FileUtils.readFromFile;

import com.eveningoutpost.dexdrip.models.UserError.Log;

import java.io.File;
import java.util.HashMap;

import dalvik.system.DexClassLoader;


/**
 * JamOrHam
 * <p>
 * Load plugins from file system and instantiate classes
 */

public class Loader {

    private static final String TAG = "PluginLoader";
    private static final HashMap<String, Loaded> loaded = new HashMap<>();

    public static class Loaded {
        private final Object instance;
        private final Class<?> clazz;

        public Loaded(Object instance, Class<?> clazz) {
            this.instance = instance;
            this.clazz = clazz;
        }

        public Object getInstance() {
            return instance;
        }

        public Class<?> getClazz() {
            return clazz;
        }
    }

    public static synchronized Loaded load(final PluginDef def) {
        if (loaded.containsKey(def.getName())) {
            return loaded.get(def.getName());
        }
        if (def.isLoaded()) {
            final String path = Cache.getPath(def);
            if (path == null) {
                Log.e(TAG, "Cannot load plugin missing path: " + def.getName());
                return null;
            }
            try {
                final File tmpDir = new File(path).getParentFile();
                //final File tmpDir =  xdrip.getAppContext().getDir("dex", 0);

                final DexClassLoader classLoader = new DexClassLoader(path, tmpDir.getAbsolutePath(), null, Loader.class.getClassLoader());
                final Class<?> clazz = classLoader.loadClass(def.pname() + "Plugin");
                //Method m = clazz.getMethod("test", String.class);
                final Object instance = clazz.newInstance();
                final Loaded l = new Loaded(instance, clazz);
                loaded.put(def.getName(), l);
                Log.d(TAG, "Loaded plugin: " + def.getName());
                return l;

            } catch (Exception e) {
                Log.e(TAG, "Exception loading plugin: " + e);
            }
        } else {
            Log.d(TAG, "Plugin not ready: " + def.getName());
            Cache.refresh(def);
        }
        return null;
    }

    public static synchronized void clear() {
        loaded.clear();
    }

}
