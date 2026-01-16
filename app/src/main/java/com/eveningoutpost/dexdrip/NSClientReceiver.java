package com.eveningoutpost.dexdrip;

import static com.eveningoutpost.dexdrip.Home.startWatchUpdaterService;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.eveningoutpost.dexdrip.models.JoH;
import com.eveningoutpost.dexdrip.models.UserError.Log;
import com.eveningoutpost.dexdrip.models.Treatments;
import com.eveningoutpost.dexdrip.utilitymodels.Constants;
import com.eveningoutpost.dexdrip.utilitymodels.IncompatibleApps;
import com.eveningoutpost.dexdrip.utilitymodels.Notifications;
import com.eveningoutpost.dexdrip.utilitymodels.Pref;
import com.eveningoutpost.dexdrip.wearintegration.WatchUpdaterService;
import com.google.gson.Gson;


public class NSClientReceiver extends BroadcastReceiver {

    private static final String TAG = "jamorham NSClientRecv";

    @Override
    public void onReceive(Context context, Intent intent) {

        final String action = intent.getAction();
        if (action == null) return;
        final Bundle bundle = intent.getExtras();
        if (bundle == null) return;

        switch (action) {
//            case "info.nightscout.client.TREATMENT_BROADCAST":
//                if (!Pref.getBooleanDefaultFalse("accept_nsclient_treatments")) return;
//                try {
//                    final String json = bundle.getString("treatment");
//                    if (json != null) {
//                        Treatments t = new Gson().fromJson(json, Treatments.class);
//                        if (t != null) {
//                            Log.d(TAG, "Received treatment from nsclient: " + json);
//                            Treatments.treatmentInsertFromJson(json);
//                        }
//                    }
//                } catch (Exception e) {
//                    Log.e(TAG, "Exception processing nsclient treatment: " + e);
//                }
//                break;

            case "info.nightscout.client.SNOOZE_BROADCAST":
                Log.d(TAG, "Received snooze broadcast from NSClient");
//                Notifications.snoozeAlert(context);
                break;

            case "info.nightscout.client.REPLY_XFER":
                try {
                    final String reply_type = bundle.getString("reply_type");
                    if (reply_type != null) {
                        switch (reply_type) {
//                            case "preferences":
//                                final String prefs_json = bundle.getString("preferences_json");
//                                if (prefs_json != null) {
//                                    Pref.injectRemotePreferences(prefs_json);
//                                }
//                                break;
//                            case "sync_treatment":
//                                final String treatment_json = bundle.getString("treatment_json");
//                                if (treatment_json != null) {
//                                    Treatments.treatmentInsertFromJson(treatment_json);
//                                }
//                                break;
//                            case "idempotent_migrations":
//                                final String migrations_json = bundle.getString("migrations_json");
//                                if (migrations_json != null) {
//                                    com.eveningoutpost.dexdrip.utilitymodels.IdempotentMigrations.injectRemoteMigrations(migrations_json);
//                                }
//                                break;
                            case "status_items":
                                final String status_items_json = bundle.getString("status_items_json");
                                if (status_items_json != null) {
                                    com.eveningoutpost.dexdrip.utilitymodels.NanoStatus.setRemote(status_items_json);
                                }
                                break;
                        }
                    }
                } catch (Exception e) {
                    Log.e(TAG, "Exception processing nsclient reply: " + e);
                }
                break;
        }

    }
}
