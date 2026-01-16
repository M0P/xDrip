package com.eveningoutpost.dexdrip;

import static com.eveningoutpost.dexdrip.xdrip.gs;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.Button;

import com.eveningoutpost.dexdrip.models.JoH;
import com.eveningoutpost.dexdrip.models.UserError.Log;
import com.eveningoutpost.dexdrip.utilitymodels.Pref;
import com.eveningoutpost.dexdrip.utils.ActivityWithMenu;

import java.util.Date;


public class Reminders extends ActivityWithMenu {

    private final static String TAG = "jamorham Reminders";

    @Override
    public String getMenuName() {
        return gs(R.string.reminders);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);

//        Button button_cal = (Button) findViewById(R.id.button_remind_cal);

//        button_cal.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // TODO
//            }
//        });

    }

    public static void HandleGoodBg() {
        Log.d(TAG, "Reminders: HandleGoodBg");
        if (!Pref.getBooleanDefaultFalse("reminders_enabled")) return;
        // TODO
    }

    public static void HandleGoodCal() {
        Log.d(TAG, "Reminders: HandleGoodCal");
        if (!Pref.getBooleanDefaultFalse("reminders_enabled")) return;
        // TODO
    }

}
