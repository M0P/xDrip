package com.eveningoutpost.dexdrip.utilitymodels;

import androidx.annotation.NonNull;
import com.eveningoutpost.dexdrip.adapters.ObservableArrayMapNoNotify;

public class PrefsViewString extends ObservableArrayMapNoNotify<String, String> {

    public String getString(String name) {
        return Pref.getString(name, "");
    }

    public void setString(String name, String value) {
        Pref.setString(name, value);
        super.put(name, value);
    }

    @Override
    public String get(Object key) {
        if (!(key instanceof String)) return null;
        final String skey = (String) key;

        String value = super.get(skey);
        if (value == null) {
            value = getString(skey);
            super.putNoNotify(skey, value);
        }
        return value;
    }

    @Override
    public String put(@NonNull String key, @NonNull String value) {
        String current = super.get(key);
        if (current == null || !current.equals(value)) {
            setString(key, value);
        }
        return value;
    }

    @Override
    public String remove(Object key) {
        if (key instanceof String) {
            // falls du Pref.remove(...) hast, nimm das; sonst wie bisher:
            Pref.setString((String) key, "");
        }
        return super.remove(key);
    }

    @Override
    public boolean remove(Object key, Object value) {
        if (key instanceof String && value instanceof String) {
            Pref.setString((String) key, "");
        }
        return super.remove(key, value);
    }
}
