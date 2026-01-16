package com.eveningoutpost.dexdrip.utils.framework;

// jamorham

import android.Manifest;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.ContactsContract;
import androidx.core.app.ActivityCompat;
import android.telephony.PhoneNumberUtils;
import android.telephony.TelephonyManager;

import com.eveningoutpost.dexdrip.models.JoH;
import com.eveningoutpost.dexdrip.models.UserError;
import com.eveningoutpost.dexdrip.utilitymodels.Constants;
import com.eveningoutpost.dexdrip.utilitymodels.Inevitable;
//import com.eveningoutpost.dexdrip.watch.lefun.LeFun;
//import com.eveningoutpost.dexdrip.watch.lefun.LeFunEntry;
//import com.eveningoutpost.dexdrip.watch.miband.MiBand;
//import com.eveningoutpost.dexdrip.watch.miband.MiBandEntry;
//import com.eveningoutpost.dexdrip.watch.thinjam.BlueJayEntry;
import com.eveningoutpost.dexdrip.xdrip;

//import static com.eveningoutpost.dexdrip.watch.miband.Const.MIBAND_NOTIFY_TYPE_CALL;
//import static com.eveningoutpost.dexdrip.watch.miband.Const.MIBAND_NOTIFY_TYPE_CANCEL;
//import static com.eveningoutpost.dexdrip.watch.thinjam.Const.THINJAM_NOTIFY_TYPE_CALL;

public class IncomingCallsReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {

    }
}
