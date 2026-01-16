package com.eveningoutpost.dexdrip.utilitymodels;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.eveningoutpost.dexdrip.adapters.ObservableArrayMapNoNotify;

public class PrefsViewImpl extends ObservableArrayMapNoNotify<String, Boolean> implements PrefsView {

    private Runnable runnable;

    public boolean getbool(String name) {
        if (name == null) return false;
        PrefHandle h = PrefHandle.parse(name);
        return h != null && h.getBoolean();
    }

    public void setbool(String name, boolean value) {
        final PrefHandle handle = PrefHandle.parse(name);
        if (handle == null) return;
        Pref.setBoolean(handle.key, value);
        super.put(handle.key, value);
        doRunnable();
    }

    public void togglebool(String name) {
        setbool(name, !getbool(name));
    }

    public PrefsViewImpl setRefresh(final Runnable runnable) {
        this.runnable = runnable;
        return this;
    }

    private void doRunnable() {
        if (runnable != null) runnable.run();
    }

    // WICHTIG für Data Binding: Map.get(Object)
    @Override
    @Nullable
    public Boolean get(Object key) {
        if (!(key instanceof String)) return null;

        final PrefHandle handle = PrefHandle.parse((String) key);
        if (handle == null) return null;

        Boolean value = super.get(handle.key);
        if (value == null) {
            value = handle.getBoolean();
            super.putNoNotify(handle.key, value);
        }
        return value;
    }

    @Override
    public Boolean put(@NonNull String key, @NonNull Boolean value) {
        final PrefHandle handle = PrefHandle.parse(key);
        if (handle == null) return value;

        final Boolean current = super.get(handle.key);
        if (current == null || !current.equals(value)) {
            Pref.setBoolean(handle.key, value);
            super.put(handle.key, value);
            doRunnable();
        }
        return value;
    }

    // Optional: falls irgendwo direkt mit Object-Key gearbeitet wird
    public void put(Object key, boolean value) {
        if (!(key instanceof String)) return;
        final PrefHandle handle = PrefHandle.parse((String) key);
        if (handle == null) return;

        final Boolean current = super.get(handle.key);
        if (current == null || current != value) {
            super.put(handle.key, value);
        }
    }

    public static class PrefHandle {
        public final String key;
        public final boolean defaultValue;

        public PrefHandle(final String key, final boolean defaultValue) {
            this.key = key;
            this.defaultValue = defaultValue;
        }

        boolean getBoolean() {
            return Pref.getBoolean(key, defaultValue);
        }

        @Nullable
        public static PrefHandle parse(final String identifier) {
            if (identifier == null) return null;
            final String[] parts = identifier.split(":", 2);
            if (parts.length == 2) {
                return new PrefHandle(parts[0], Boolean.parseBoolean(parts[1]));
            } else {
                return new PrefHandle(identifier, false);
            }
        }
    }
}
