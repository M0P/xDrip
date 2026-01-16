package com.eveningoutpost.dexdrip.utilitymodels;

import com.eveningoutpost.dexdrip.g5model.G6CalibrationParameters;

import java.util.ArrayList;
import java.util.List;

// jamorham

// persistent store strings, will be set to "" if undefined

public class WearSyncPersistentStrings {

    private static final List<String> persistentStrings = new ArrayList<>();

    static {
        persistentStrings.add(G6CalibrationParameters.PREF_CURRENT_CODE);
    }

    public static List<String> getPersistentStrings() {
        return persistentStrings;
    }
}
