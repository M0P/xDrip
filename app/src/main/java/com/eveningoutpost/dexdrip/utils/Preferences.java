package com.eveningoutpost.dexdrip.utils;


import static com.eveningoutpost.dexdrip.EditAlertActivity.unitsConvert2Disp;
import static com.eveningoutpost.dexdrip.models.JoH.showNotification;
import static com.eveningoutpost.dexdrip.models.JoH.tolerantParseDouble;
import static com.eveningoutpost.dexdrip.services.Ob1G5CollectionService.clearDataWhenTransmitterIdEntered;
import static com.eveningoutpost.dexdrip.utilitymodels.Constants.OUT_OF_RANGE_GLUCOSE_ENTRY_ID;
import static com.eveningoutpost.dexdrip.utils.DexCollectionType.getBestCollectorHardwareName;
import static com.eveningoutpost.dexdrip.xdrip.gs;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.appwidget.AppWidgetManager;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.EditTextPreference;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceCategory;
import android.preference.PreferenceFragment;
import android.preference.PreferenceGroup;
import android.preference.PreferenceManager;
import android.preference.PreferenceScreen;
import android.preference.RingtonePreference;
import android.preference.SwitchPreference;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.BaseAdapter;
import android.widget.Toast;

import com.bytehamster.lib.preferencesearch.SearchConfiguration;
import com.bytehamster.lib.preferencesearch.SearchPreferenceResult;
import com.bytehamster.lib.preferencesearch.SearchPreferenceResultListener;
import com.eveningoutpost.dexdrip.BasePreferenceActivity;
//import com.eveningoutpost.dexdrip.GcmActivity;
import com.eveningoutpost.dexdrip.Home;
import com.eveningoutpost.dexdrip.NFCReaderX;
import com.eveningoutpost.dexdrip.ParakeetHelper;
import com.eveningoutpost.dexdrip.R;
import com.eveningoutpost.dexdrip.WidgetUpdateService;
import com.eveningoutpost.dexdrip.alert.Registry;
import com.eveningoutpost.dexdrip.calibrations.PluggableCalibration;
//import com.eveningoutpost.dexdrip.cgm.carelinkfollow.CareLinkFollowService;
//import com.eveningoutpost.dexdrip.cgm.carelinkfollow.auth.CareLinkAuthType;
//import com.eveningoutpost.dexdrip.cgm.nsfollow.NightscoutFollow;
//import com.eveningoutpost.dexdrip.cgm.sharefollow.ShareFollowService;
//import com.eveningoutpost.dexdrip.cgm.webfollow.Cpref;
//import com.eveningoutpost.dexdrip.cgm.carelinkfollow.auth.CareLinkAuthenticator;
//import com.eveningoutpost.dexdrip.cgm.carelinkfollow.auth.CareLinkCredentialStore;
//import com.eveningoutpost.dexdrip.cloud.jamcm.Pusher;
import com.eveningoutpost.dexdrip.healthconnect.HealthConnectEntry;
import com.eveningoutpost.dexdrip.healthconnect.HealthGamut;
//import com.eveningoutpost.dexdrip.insulin.inpen.InPenEntry;
import com.eveningoutpost.dexdrip.models.DesertSync;
import com.eveningoutpost.dexdrip.models.JoH;
import com.eveningoutpost.dexdrip.models.Profile;
import com.eveningoutpost.dexdrip.models.UserError;
import com.eveningoutpost.dexdrip.models.UserError.ExtraLogTags;
import com.eveningoutpost.dexdrip.models.UserError.Log;
import com.eveningoutpost.dexdrip.models.UserNotification;
import com.eveningoutpost.dexdrip.plugin.Dialog;
import com.eveningoutpost.dexdrip.profileeditor.ProfileEditor;
import com.eveningoutpost.dexdrip.receiver.InfoContentProvider;
//import com.eveningoutpost.dexdrip.services.BluetoothGlucoseMeter;
import com.eveningoutpost.dexdrip.services.DexCollectionService;
import com.eveningoutpost.dexdrip.services.G5BaseService;
//import com.eveningoutpost.dexdrip.services.PlusSyncService;
import com.eveningoutpost.dexdrip.services.UiBasedCollector;
import com.eveningoutpost.dexdrip.services.broadcastservice.BroadcastService;
//import com.eveningoutpost.dexdrip.tidepool.AuthFlowOut;
//import com.eveningoutpost.dexdrip.tidepool.TidepoolUploader;
//import com.eveningoutpost.dexdrip.tidepool.UploadChunk;
import com.eveningoutpost.dexdrip.ui.LockScreenWallPaper;
import com.eveningoutpost.dexdrip.ui.dialog.GenericConfirmDialog;
import com.eveningoutpost.dexdrip.utilitymodels.BgGraphBuilder;
import com.eveningoutpost.dexdrip.utilitymodels.CollectionServiceStarter;
import com.eveningoutpost.dexdrip.utilitymodels.Constants;
import com.eveningoutpost.dexdrip.utilitymodels.Experience;
import com.eveningoutpost.dexdrip.utilitymodels.Inevitable;
import com.eveningoutpost.dexdrip.utilitymodels.Intents;
import com.eveningoutpost.dexdrip.utilitymodels.Pref;
import com.eveningoutpost.dexdrip.utilitymodels.ShotStateStore;
import com.eveningoutpost.dexdrip.utilitymodels.SpeechUtil;
//import com.eveningoutpost.dexdrip.utilitymodels.UpdateActivity;
import com.eveningoutpost.dexdrip.utilitymodels.WholeHouse;
import com.eveningoutpost.dexdrip.utils.framework.IncomingCallsReceiver;
import com.eveningoutpost.dexdrip.wearintegration.Amazfitservice;
import com.eveningoutpost.dexdrip.wearintegration.WatchUpdaterService;
import com.eveningoutpost.dexdrip.webservices.XdripWebService;
import com.eveningoutpost.dexdrip.xDripWidget;
import com.eveningoutpost.dexdrip.xdrip;
import com.google.zxing.BinaryBitmap;
import com.google.zxing.MultiFormatReader;
import com.google.zxing.NotFoundException;
import com.google.zxing.RGBLuminanceSource;
import com.google.zxing.Result;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
//import com.nightscout.core.barcode.NSBarcodeConfig;

import java.io.FileNotFoundException;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * A {@link PreferenceActivity} that presents a set of application settings. On
 * handset devices, settings are presented as a single list. On tablets,
 * settings are split by category, with category headers shown to the left of
 * the list of settings.
 * <p/>
 * See <a href="http://developer.android.com/design/patterns/settings.html">
 * Android Design: Settings</a> for design guidelines and the <a
 * href="http://developer.android.com/guide/topics/ui/settings.html">Settings
 * API Guide</a> for more information on developing a Settings UI.
 */
public class Preferences extends BasePreferenceActivity implements SearchPreferenceResultListener {
    private static final String TAG = "jamorham PREFS";
    private static byte[] staticKey;
    private volatile AllPrefsFragment preferenceFragment;

    private static Preference units_pref;
    private static String static_units;
    private static Preference profile_insulin_sensitivity_default;
    private static Preference profile_carb_ratio_default;

    private static ListPreference locale_choice;
    private static Preference force_english;
    private static Preference nfc_expiry_days;

    private static AllPrefsFragment pFragment;

    // NOTE: miband receiver is currently disabled (code commented out below). Keep the field null
    // and guard all registrations to avoid NPEs on some ROMs / builds.
    private BroadcastReceiver mibandStatusReceiver;

    // The following three variables enable us to create a common state from the input,
    // whether we scan from camera or a file, and continue with the same following
    // set of commands to avoid code duplication.
    private volatile String scanFormat = null; // The format of the scan
    private volatile String scanContents = null; // Text content of the scan coming either from camera or file
    private volatile byte[] scanRawBytes = null; // Raw bytes of the scan

    private void refreshFragments() {
        refreshFragments(null);
    }

    public static final double MIN_GLUCOSE_INPUT = 40; // The smallest acceptable input glucose value in mg/dL
    public static final double MAX_GLUCOSE_INPUT = 400; // The largest acceptable input glucose value in mg/dL

    private void refreshFragments(final String jumpTo) {
        this.preferenceFragment = new AllPrefsFragment(jumpTo);
        this.preferenceFragment.setParent(this);
        pFragment = this.preferenceFragment;
        getFragmentManager().beginTransaction().replace(android.R.id.content,
                this.preferenceFragment).commit();
    }

    public static List<String> getAllPreferenceKeys(final PreferenceGroup parent) {
        final List<Preference> source = getAllPreferences(parent);
        final List<String> results = new ArrayList<>(source.size());
        for (final Preference preference : source) {
            results.add(preference.getKey());
        }
        return results;
    }

    public static List<Preference> getAllPreferences(final PreferenceGroup parent) {
        final int preferenceCount = parent.getPreferenceCount();
        final List<Preference> results = new ArrayList<>(preferenceCount);
        for (int i = 0; i < preferenceCount; i++) {
            final Preference preference = parent.getPreference(i);
            results.add(preference);
            if (preference instanceof PreferenceGroup) {
                // recurse
                results.addAll(getAllPreferences((PreferenceGroup) preference));
            }
        }
        return results;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onSearchResultClicked(@NonNull SearchPreferenceResult searchPreferenceResult) {
        try {
            searchPreferenceResult.closeSearchPage(this);
            searchPreferenceResult.highlight(this.preferenceFragment, Color.YELLOW);
        } catch (RuntimeException e) {
            Log.wtf(TAG, "Got error trying to highlight search results: " + e);
            JoH.static_toast_long("" + e);
        }
    }


    public interface OnServiceTaskCompleted {
        void onTaskCompleted(byte[] result);
    }

    public class ServiceCallback implements OnServiceTaskCompleted {
        @Override
        public void onTaskCompleted(byte[] result) {
            if (result.length > 0) {
                if ((staticKey == null) || (staticKey.length != 16)) {
                    toast("Error processing security key");
                } else {
                    byte[] plainbytes = JoH.decompressBytesToBytes(CipherUtils.decryptBytes(result, staticKey));
                    staticKey = null;
                    Log.d(TAG, "Plain bytes size: " + plainbytes.length);
                    if (plainbytes.length > 0) {
                        SdcardImportExport.storePreferencesFromBytes(plainbytes, getApplicationContext());
                    } else {
                        toast("Error processing data - empty");
                    }
                }
            } else {
                toast("Error processing settings - no data - try again?");
            }
        }
    }


    private void toast(final String msg) {
        try {
            runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_SHORT).show();
                }
            });
            android.util.Log.d(TAG, "Toast msg: " + msg);
        } catch (Exception e) {
            android.util.Log.e(TAG, "Couldn't display toast: " + msg);
        }
    }

    private void installxDripPlusPreferencesFromQRCode(SharedPreferences prefs, String data) {
        Log.d(TAG, "installing preferences from QRcode");
        try {
            Map<String, String> prefsmap = QRcodeUtils.decodeString(data);
            if (prefsmap != null) {
                if (prefsmap.containsKey(getString(R.string.all_settings_wizard))) {
                    if (prefsmap.containsKey(getString(R.string.wizard_key))
                            && prefsmap.containsKey(getString(R.string.wizard_uuid))) {
                        staticKey = CipherUtils.hexToBytes(prefsmap.get(getString(R.string.wizard_key)));

                        new WebAppHelper(new ServiceCallback()).executeOnExecutor(xdrip.executor, getString(R.string.wserviceurl) + "/joh-getsw/" + prefsmap.get(getString(R.string.wizard_uuid)));
                    } else {
                        Log.d(TAG, "Incorrectly formatted wizard pref");
                    }
                    return;
                }

                final StringBuilder sb = getMapKeysString(prefsmap);
                final String msg = getString(R.string.import_qr_code_warning) + sb;

                GenericConfirmDialog.show(this, gs(R.string.are_you_sure), msg, () -> {
                    final SharedPreferences.Editor editor = prefs.edit();
                    int changes = 0;
                    for (Map.Entry<String, String> entry : prefsmap.entrySet()) {
                        String key = entry.getKey();
                        String value = entry.getValue();
                        //            Log.d(TAG, "Saving preferences: " + key + " = " + value);
                        if (value.equals("true") || (value.equals("false"))) {
                            editor.putBoolean(key, Boolean.parseBoolean(value));
                            changes++;
                        } else if (!value.equals("null")) {
                            editor.putString(key, value);
                            changes++;
                        }
                    }
                    editor.apply();
                    refreshFragments();
                    ExtraLogTags.readPreference(Pref.getStringDefaultBlank("extra_tags_for_logging"));
                    Toast.makeText(getApplicationContext(), "Loaded " + Integer.toString(changes) + " preferences from QR code", Toast.LENGTH_LONG).show();
//                    PlusSyncService.clearandRestartSyncService(getApplicationContext());
                    DesertSync.settingsChanged(); // refresh
                    InfoContentProvider.ping("pref");
//                    if (prefs.getString("dex_collection_method", "").equals("Follower")) {
//                        PlusSyncService.clearandRestartSyncService(getApplicationContext());
//                       // GcmActivity.last_sync_request = 0;
//                       // GcmActivity.requestBGsync();
//                    }
                });

            } else {
                android.util.Log.e(TAG, "Got null prefsmap during decode");
            }
        } catch (Exception e) {
            Log.e(TAG, "Got exception installing preferences");
        }

    }

    public static StringBuilder getMapKeysString(final Map<String, ?> prefsmap) {
        final StringBuilder sb = new StringBuilder();
        final Set<String> keysSet = prefsmap.keySet();
        final List<String> keyList = new ArrayList<>(keysSet);
        Collections.sort(keyList);
        for (final String entry : keyList) {
            sb.append(entry);
            sb.append("\n");
        }
        return sb;
    }


    public static Boolean getBooleanPreferenceViaContextWithoutException(Context context, String key, Boolean defaultValue) {
        try {
            return PreferenceManager.getDefaultSharedPreferences(context).getBoolean(key, defaultValue);

        } catch (ClassCastException ex) {
            return defaultValue;
        }
    }


    @Override
    protected synchronized void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Let's reset variables just to be sure
        scanFormat = null;
        scanContents = null;
        scanRawBytes = null;
        if (requestCode == Constants.HEALTH_CONNECT_RESPONSE_ID) {
            if (HealthConnectEntry.enabled()) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    if (JoH.ratelimit("health-connect-bump", 2)) {
                        HealthGamut.init(this);
                    }
                }
            }
        }

        if (requestCode == Constants.ZXING_FILE_REQ_CODE) { // If we are scanning an image file, not using the camera
            // The core of the following section, selecting the file, converting it into a bitmap, and then to a bitstream, is from:
            // https://stackoverflow.com/questions/55427308/scaning-qrcode-from-image-not-from-camera-using-zxing
            if (data == null || data.getData() == null) {
                Log.e("TAG", "No file was selected");
                return;
            }
            Uri uri = data.getData();
            try {
                InputStream inputStream = getContentResolver().openInputStream(uri);
                Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
                if (bitmap == null) {
                    Log.e("TAG", "uri is not a bitmap," + uri.toString());
                    return;
                }
                int width = bitmap.getWidth(), height = bitmap.getHeight();
                int[] pixels = new int[width * height];
                bitmap.getPixels(pixels, 0, width, 0, 0, width, height);
                bitmap.recycle();
                bitmap = null;
                RGBLuminanceSource source = new RGBLuminanceSource(width, height, pixels);
                BinaryBitmap bBitmap = new BinaryBitmap(new HybridBinarizer(source));
                MultiFormatReader reader = new MultiFormatReader();
                try {
                    Result result = reader.decode(bBitmap);
                    scanFormat = result.getBarcodeFormat().toString();
                    scanContents = result.getText(); // The text content  of the scanned file
                    scanRawBytes = result.getRawBytes();
                } catch (NotFoundException e) {
                    Log.e("TAG", "decode exception", e);
                }
            } catch (FileNotFoundException e) {
                Log.e("TAG", "can not open file" + uri.toString(), e);
            }
        } else if (requestCode == Constants.ZXING_CAM_REQ_CODE) { // If we are scanning from camera
            IntentResult scanResult = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
            scanFormat = scanResult.getFormatName();
            scanContents = scanResult.getContents(); // The text content of the scan from camera
            scanRawBytes = scanResult.getRawBytes();
        }
        // We now have scan format, scan text content, and scan raw bytes in the corresponding variables.
        // Everything after this is applied whether we scanned with camera or from a file.

        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        if (scanContents == null) { // If we have no scan content
            UserError.Log.d(TAG, "No scan results ");
            return;
        }

        if (scanFormat.equals("QR_CODE")) { // The scan is a QR code

//            if (QRcodeUtils.hasDecoderMarker(scanContents)) {
//                installxDripPlusPreferencesFromQRCode(prefs, scanContents);
//                return;
//            }
//
//            try {
//                if (BlueJay.processQRCode(scanRawBytes)) {
//                    refreshFragments();
//                    return;
//                }
//            } catch (Exception e) {
//                // meh
//            }


//            final NSBarcodeConfig barcode = new NSBarcodeConfig(scanContents);
//            if (barcode.hasMongoConfig()) {
//                if (barcode.getMongoUri().isPresent()) {
//                    SharedPreferences.Editor editor = prefs.edit();
//                    editor.putString("cloud_storage_mongodb_uri", barcode.getMongoUri().get());
//                    editor.putString("cloud_storage_mongodb_collection", barcode.getMongoCollection().or("entries"));
//                    editor.putString("cloud_storage_mongodb_device_status_collection", barcode.getMongoDeviceStatusCollection().or("devicestatus"));
//                    editor.putBoolean("cloud_storage_mongodb_enable", true);
//                    editor.apply();
//                }
//                if (barcode.hasApiConfig()) {
//                    SharedPreferences.Editor editor = prefs.edit();
//                    editor.putBoolean("cloud_storage_api_enable", true);
//                    editor.putString("cloud_storage_api_base", Joiner.on(' ').join(barcode.getApiUris()));
//                    editor.apply();
//                } else {
//                    prefs.edit().putBoolean("cloud_storage_api_enable", false).apply();
//                }
//            }
//            if (barcode.hasApiConfig()) {
//                SharedPreferences.Editor editor = prefs.edit();
//                editor.putBoolean("cloud_storage_api_enable", true);
//                editor.putString("cloud_storage_api_base", Joiner.on(' ').join(barcode.getApiUris()));
//                editor.apply();
//            } else {
//                prefs.edit().putBoolean("cloud_storage_api_enable", false).apply();
//            }

//            if (barcode.hasMqttConfig()) {
//                if (barcode.getMqttUri().isPresent()) {
//                    URI uri = URI.create(barcode.getMqttUri().or(""));
//                    if (uri.getUserInfo() != null) {
//                        String[] userInfo = uri.getUserInfo().split(":");
//                        if (userInfo.length == 2) {
//                            String endpoint = uri.getScheme() + "://" + uri.getHost() + ":" + uri.getPort();
//                            if (userInfo[0].length() > 0 && userInfo[1].length() > 0) {
//                                SharedPreferences.Editor editor = prefs.edit();
//                                editor.putString("cloud_storage_mqtt_endpoint", endpoint);
//                                editor.putString("cloud_storage_mqtt_user", userInfo[0]);
//                                editor.putString("cloud_storage_mqtt_password", userInfo[1]);
//                                editor.putBoolean("cloud_storage_mqtt_enable", true);
//                                editor.apply();
//                            }
//                        }
//                    }
//                }
//            } else {
//                SharedPreferences.Editor editor = prefs.edit();
//                editor.putBoolean("cloud_storage_mqtt_enable", false);
//                editor.apply();
//            }
        } else if (scanFormat.equals("CODE_128")) {
            Log.d(TAG, "Setting serial number to: " + scanContents);
            prefs.edit().putString("share_key", scanContents).apply();
        }
        refreshFragments();
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        try {
            setTheme(R.style.OldAppTheme);
        } catch (Exception e) {
            Log.e(TAG, "Failed to set theme");
        }
        super.onCreate(savedInstanceState);

        refreshFragments(getIntent() != null ? getIntent().getAction() : null);
        processExtraData();

        // cannot be in onResume as we display dialog to set
        try {
            PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(preferenceFragment.lockListener.prefListener);
        } catch (Exception e) {
            Log.e(TAG, "Got exception registering lockListener: " + e + " " + (preferenceFragment.lockListener == null));
        }

//        mibandStatusReceiver = new BroadcastReceiver() {
//            @Override
//            public void onReceive(Context context, Intent intent) {
//            final MiBandService.MIBAND_INTEND_STATES state = MiBandService.MIBAND_INTEND_STATES.valueOf(intent.getStringExtra("state"));
//            switch (state) {
//                case UPDATE_PREF_SCREEN:
//                    preferenceFragment.updateMiBandScreen();
//                    break;
//                case UPDATE_PREF_DATA:
//                    preferenceFragment.updateMibandPreferencesData();
//                    break;
//                }
//            }
//        };

        UiBasedCollector.onEnableCheckPermission(this);
    }

    @Override
    public void onStop() { // Everything here runs when xDrip is minimized or stopped.
        super.onStop();
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(this);
        try {
            if (!prefs.getBoolean("engineering_mode", false)) { // If engineering mode has been disabled
                try {
                } catch (Exception e) {
                    //
                }
            }
        } catch (Exception e) {
            //
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            getMenuInflater().inflate(R.menu.menu_preferences, menu);
        }
        return super.onCreateOptionsMenu(menu);
    }

    public void showSearch(MenuItem item) {
        if (JoH.ratelimit("preference-search-button", 1)) {
            this.preferenceFragment.showSearchFragment();
        }
    }


    private final SharedPreferences.OnSharedPreferenceChangeListener uiPrefListener = UiBasedCollector.getListener(this);

//    private final SharedPreferences.OnSharedPreferenceChangeListener xDripCloudListener = (sharedPreferences, key) -> {
//        if (key!= null && key.equals("use_xdrip_cloud_sync")) {
//            Pusher.requestReconnect();
//            CollectionServiceStarter.restartCollectionServiceBackground();
//        }
//    };

    @Override
    protected void onResume() {
        super.onResume();
//        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(ActivityRecognizedService.prefListener);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && DexCollectionType.hasBluetooth() && !WholeHouse.isRpi()) {
            LocationHelper.requestLocationForBluetooth(this); // double check!
        }
//        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(LeFunEntry.prefListener);
//        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(Cpref.prefListener);
//        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(MiBandEntry.prefListener);
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(BroadcastService.prefListener);
//        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(BlueJayEntry.prefListener);
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(uiPrefListener);
        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(Registry.prefListener);
//        PreferenceManager.getDefaultSharedPreferences(this).registerOnSharedPreferenceChangeListener(xDripCloudListener);

        // NOTE: mibandStatusReceiver may be null when the feature is disabled.
        if (mibandStatusReceiver != null) {
            LocalBroadcastManager.getInstance(this).registerReceiver(mibandStatusReceiver,
                    new IntentFilter(Intents.PREFERENCE_INTENT));
        }
    }

    @Override
    protected void onPause() {
//        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(ActivityRecognizedService.prefListener);
//        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(LeFunEntry.prefListener);
//        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(Cpref.prefListener);
//        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(MiBandEntry.prefListener);
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(BroadcastService.prefListener);
//        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(BlueJayEntry.prefListener);
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(uiPrefListener);
        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(Registry.prefListener);
//        PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(xDripCloudListener);

        // NOTE: mibandStatusReceiver may be null when the feature is disabled.
        if (mibandStatusReceiver != null) {
            LocalBroadcastManager.getInstance(this).unregisterReceiver(mibandStatusReceiver);
        }

        pFragment = null;
        super.onPause();
    }

    @Override
    protected void onDestroy() {
        try {
            PreferenceManager.getDefaultSharedPreferences(this).unregisterOnSharedPreferenceChangeListener(preferenceFragment.lockListener.prefListener);
        } catch (Exception e) {
            //
        }
        super.onDestroy();
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
//        addPreferencesFromResource(R.xml.pref_general);

    }

    @Override
    protected boolean isValidFragment(String fragmentName) {
        if (AllPrefsFragment.class.getName().equals(fragmentName)) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onIsMultiPane() {
        return isXLargeTablet(this);
    }

    private static boolean isXLargeTablet(Context context) {
        return (context.getResources().getConfiguration().screenLayout
                & Configuration.SCREENLAYOUT_SIZE_MASK) >= Configuration.SCREENLAYOUT_SIZE_XLARGE;
    }

    @Override
    public void onBuildHeaders(List<Header> target) {
        loadHeadersFromResource(R.xml.pref_headers, target);
    }

    @Override
    public void onNewIntent(Intent intent) {
        if (intent.getAction() != null) {
            try {
                refreshFragments(getIntent() != null ? getIntent().getAction() : null);
            } catch (Exception e) {
                //
            }
        }
        setIntent(intent);
        if (!processExtraData()) {
            super.onNewIntent(intent);
        }
    }

    private boolean processExtraData() {
        final Intent intent = getIntent();
        if (intent != null) {
            final Bundle bundle = intent.getExtras();
            if (bundle != null) {
                final String str = bundle.getString("refresh");
                if (str != null) {
                    refreshProfileRatios();
                    return true;
                }
            }
        }
        return false;
    }

    private static void refreshProfileRatios() {
        // NOTE: Some builds / preference XML variants may not include these keys.
        // Guard so the Preferences activity can still open.
        if (profile_carb_ratio_default != null) {
            profile_carb_ratio_default.setTitle(format_carb_ratio(profile_carb_ratio_default.getTitle().toString(), ProfileEditor.minMaxCarbs(ProfileEditor.loadData(false))));
        } else {
            Log.wtf(TAG, "Missing preference profile_carb_ratio_default");
        }
        if (profile_insulin_sensitivity_default != null) {
            profile_insulin_sensitivity_default.setTitle(format_insulin_sensitivity(profile_insulin_sensitivity_default.getTitle().toString(), ProfileEditor.minMaxSens(ProfileEditor.loadData(false))));
        } else {
            Log.wtf(TAG, "Missing preference profile_insulin_sensitivity_default");
        }
    }

//    private static void restartPebble() {
//        xdrip.getAppContext().startService(new Intent(xdrip.getAppContext(), PebbleWatchSync.class));
//    }

    private static Preference.OnPreferenceChangeListener sBindPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            if (preference instanceof ListPreference) {
                ListPreference listPreference = (ListPreference) preference;
                int index = listPreference.findIndexOfValue(stringValue);
                preference.setSummary(
                        index >= 0
                                ? listPreference.getEntries()[index]
                                : null);

            } else if (preference instanceof RingtonePreference) {
                // For ringtone preferences, look up the correct display value
                // using RingtoneManager.
                if (TextUtils.isEmpty(stringValue)) {
                    // Empty values correspond to 'silent' (no ringtone).
                    preference.setSummary(R.string.pref_ringtone_silent);

                } else {
                    Ringtone ringtone = RingtoneManager.getRingtone(
                            preference.getContext(), Uri.parse(stringValue));

                    if (ringtone == null) {
                        // Clear the summary if there was a lookup error.
                        preference.setSummary(null);
                    } else {
                        // Set the summary to reflect the new ringtone display
                        // name.
                        String name = ringtone.getTitle(preference.getContext());
                        preference.setSummary(name);
                    }
                }

            } else {
                // For all other preferences, set the summary to the value's
                // simple string representation.
                preference.setSummary(stringValue);
            }
            return true;
        }
    };

    private static Preference.OnPreferenceChangeListener sBindNumericPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            if (isNumeric(stringValue)) {
                preference.setSummary(stringValue);
                return true;
            }
            return false;
        }
    };

    private static Preference.OnPreferenceChangeListener sBindNumericUnitizedPreferenceSummaryToValueListener = new Preference.OnPreferenceChangeListener() { // This listener adds glucose unit in addition to the value to the summary and rejects out-of-range inputs
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {
            String stringValue = value.toString();
            if (isNumeric(stringValue)) {
                final boolean domgdl = Pref.getString("units", "mgdl").equals("mgdl"); // Identify which unit is chosen
                double submissionMgdl = domgdl ? tolerantParseDouble(stringValue) : tolerantParseDouble(stringValue) * Constants.MMOLL_TO_MGDL;
                if (submissionMgdl > MAX_GLUCOSE_INPUT || submissionMgdl < MIN_GLUCOSE_INPUT) {
                    JoH.static_toast_long(xdrip.gs(R.string.the_value_must_be_between_min_and_max, unitsConvert2Disp(domgdl, MIN_GLUCOSE_INPUT), unitsConvert2Disp(domgdl, MAX_GLUCOSE_INPUT)));
                    return false; // Reject input if out of range
                }
                preference.setSummary(stringValue + "  " + (domgdl ? "mg/dl" : "mmol/l")); // Set the summary to show the value followed by the chosen unit
                return true; // Accept input as it is numeric and in range
            }
            return false; // Reject input if not numeric
        }
    };

    private static Preference.OnPreferenceChangeListener sBindPreferenceTitleAppendToValueListenerUpdateChannel = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {

            boolean do_update = false;
            // detect not first run
            if (preference.getTitle().toString().contains("(")) {
                do_update = true;
            }

            preference.setTitle(preference.getTitle().toString().replaceAll("  \\([a-z0-9A-Z]+\\)$", "") + "  (" + value.toString() + ")");
            if (do_update) {
                preference.getEditor().putString(preference.getKey(), value.toString()).apply(); // update prefs now
                //UpdateActivity.last_check_time = -2;
                //UpdateActivity.checkForAnUpdate(preference.getContext());
            }
            return true;
        }
    };

    private static Preference.OnPreferenceChangeListener sBindPreferenceTitleAppendToIntegerValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {

            boolean do_update = false;
            // detect not first run
            if (preference.getTitle().toString().contains("(")) {
                do_update = true;
            }

            preference.setTitle(preference.getTitle().toString().replaceAll("  \\([a-z0-9A-Z]+\\)$", "") + "  (" + value.toString() + ")");
            if (do_update) {
                preference.getEditor().putInt(preference.getKey(), (int) value).apply(); // update prefs now
            }
            return true;
        }
    };

    private static Preference.OnPreferenceChangeListener sBindPreferenceTitleAppendToStringValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {

            boolean do_update = false;
            // detect not first run
            if (preference.getTitle().toString().contains("(")) {
                do_update = true;
            }

            preference.setTitle(preference.getTitle().toString().replaceAll("  \\([a-z0-9A-Z.]+\\)$", "") + "  (" + value.toString() + ")");
            if (do_update) {
                preference.getEditor().putString(preference.getKey(), (String) value).apply(); // update prefs now
            }
            return true;
        }
    };

    private static Preference.OnPreferenceChangeListener sBindPreferenceTitleAppendToMacValueListener = new Preference.OnPreferenceChangeListener() {
        @Override
        public boolean onPreferenceChange(Preference preference, Object value) {

            boolean do_update = false;
            // detect not first run
            if (preference.getTitle().toString().contains("(")) {
                do_update = true;
            }
            String title = preference.getTitle().toString().replaceAll("  \\([a-z0-9A-Z.:]+\\)$", "");
            if (!value.toString().isEmpty())
                title = title + "  (" + value.toString() + ")";
            preference.setTitle(title);
            if (do_update) {
                preference.getEditor().putString(preference.getKey(), (String) value).apply(); // update prefs now
            }
            return true;
        }
    };


    private static String format_carb_ratio(String oldValue, String newValue) {
        return oldValue.replaceAll(" \\(.*\\)$", "") + "  (" + newValue + "g per Unit)";
    }

    private static String format_carb_absorption_rate(String oldValue, String newValue) {
        return oldValue.replaceAll(" \\(.*\\)$", "") + "  (" + newValue + "g per hour)";
    }

    private static String format_insulin_sensitivity(String oldValue, String newValue) {
        try {
            return oldValue.replaceAll("  \\((.*)\\)$", "") + "  (" + newValue + " " + static_units + " per U)";
        } catch (Exception e) {
            return "ERROR - Invalid number";
        }
    }

    private static void bindPreferenceSummaryToValue(final Preference preference) {
        // NOTE: Some XML variants remove individual keys. Always guard against null.
        if (preference == null) {
            Log.wtf(TAG, "Cannot bind preference summary (null preference)");
            return;
        }
        try {
            preference.setOnPreferenceChangeListener(sBindPreferenceSummaryToValueListener);
            sBindPreferenceSummaryToValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        } catch (Exception e) {
            Log.e(TAG, "Got exception binding preference summary: " + e);
        }
    }

    private static void bindPreferenceTitleAppendToValueUpdateChannel(Preference preference) {
        try {
            preference.setOnPreferenceChangeListener(sBindPreferenceTitleAppendToValueListenerUpdateChannel);
            sBindPreferenceTitleAppendToValueListenerUpdateChannel.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        } catch (Exception e) {
            Log.e(TAG, "Got exception binding preference title: " + e.toString());
        }
    }

    private static void bindPreferenceTitleAppendToStringValue(Preference preference) {
        try {
            preference.setOnPreferenceChangeListener(sBindPreferenceTitleAppendToStringValueListener);
            sBindPreferenceTitleAppendToStringValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        } catch (Exception e) {
            Log.e(TAG, "Got exception binding preference title: " + e.toString());
        }
    }

    private static void bindPreferenceTitleAppendToMacValue(Preference preference) {
        try {
            preference.setOnPreferenceChangeListener(sBindPreferenceTitleAppendToMacValueListener);
            sBindPreferenceTitleAppendToMacValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getString(preference.getKey(), ""));
        } catch (Exception e) {
            Log.e(TAG, "Got exception binding preference title: " + e.toString());
        }
    }


    private static void bindPreferenceTitleAppendToIntegerValue(Preference preference) {
        try {
            preference.setOnPreferenceChangeListener(sBindPreferenceTitleAppendToIntegerValueListener);
            sBindPreferenceTitleAppendToIntegerValueListener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getInt(preference.getKey(), 0));
        } catch (Exception e) {
            Log.e(TAG, "Got exception binding preference title: " + e.toString());
        }
    }

    private static void bindPreferenceTitleAppendToIntegerValueFromLogSlider(Preference preference, NamedSliderProcessor ref, String name, boolean unitize) {

        final Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object value) {

                boolean do_update = false;
                // detect not first run
                if (preference.getTitle().toString().contains("(")) {
                    do_update = true;
                }
                final int result = ref.interpolate(name, (int) value);

                preference.setTitle(preference.getTitle().toString().replaceAll("  \\([a-z0-9A-Z \\.]+\\)$", "") + "  (" + (unitize ? BgGraphBuilder.unitized_string_static_no_interpretation_short(result) : result) + ")");
                if (do_update) {
                    preference.getEditor().putInt(preference.getKey(), (int) value).apply(); // update prefs now
                }
                return true;
            }
        };

        try {
            preference.setOnPreferenceChangeListener(listener);
            listener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getInt(preference.getKey(), 0));
        } catch (Exception e) {
            Log.e(TAG, "Got exception binding preference title: " + e.toString());
        }
    }

    private static void bindPreferenceSummaryAppendToIntegerValueFromLogSlider(Preference preference, NamedSliderProcessor ref, String name, boolean unitize) {

        final Preference.OnPreferenceChangeListener listener = new Preference.OnPreferenceChangeListener() {
            @Override
            public boolean onPreferenceChange(Preference preference, Object value) {

                boolean do_update = false;
                // detect not first run
                if (preference.getSummary().toString().contains("(")) {
                    do_update = true;
                }
                final int result = ref.interpolate(name, (int) value);

                preference.setSummary(preference.getSummary().toString().replaceAll("  \\([a-z0-9A-Z \\.]+\\)$", "") + "  (" + (unitize ? BgGraphBuilder.unitized_string_static_no_interpretation_short(result) : result) + ")");
                if (do_update) {
                    preference.getEditor().putInt(preference.getKey(), (int) value).apply(); // update prefs now
                }
                return true;
            }
        };

        try {
            preference.setOnPreferenceChangeListener(listener);
            listener.onPreferenceChange(preference,
                    PreferenceManager
                            .getDefaultSharedPreferences(preference.getContext())
                            .getInt(preference.getKey(), 0));
        } catch (Exception e) {
            Log.e(TAG, "Got exception binding preference summary: " + e.toString());
        }
    }


    private static void bindPreferenceSummaryToValueAndEnsureNumeric(final Preference preference) {
        // NOTE: Some XML variants remove individual keys. Always guard against null.
        if (preference == null) {
            Log.wtf(TAG, "Cannot bind numeric preference summary (null preference)");
            return;
        }
        preference.setOnPreferenceChangeListener(sBindNumericPreferenceSummaryToValueListener);
        sBindNumericPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    private static void bindPreferenceSummaryToUnitizedValueAndEnsureNumeric(final Preference preference) { // Use this to show the value as well as the corresponding glucose unit as the summary, and reject out-of-range inputs
        // NOTE: Some XML variants remove individual keys. Always guard against null.
        if (preference == null) {
            Log.wtf(TAG, "Cannot bind unitized numeric preference summary (null preference)");
            return;
        }
        preference.setOnPreferenceChangeListener(sBindNumericUnitizedPreferenceSummaryToValueListener);
        sBindNumericUnitizedPreferenceSummaryToValueListener.onPreferenceChange(preference,
                PreferenceManager
                        .getDefaultSharedPreferences(preference.getContext())
                        .getString(preference.getKey(), ""));
    }

    public static void applyPrefSettingRange(String pref_key, String def, Double min, Double max) { // Correct a preference glucose setting if the value is out of range
        final int notificationId = OUT_OF_RANGE_GLUCOSE_ENTRY_ID;
        String mySettingString = Pref.getString(pref_key, def);
        final boolean doMgdl = (Pref.getString("units", "mgdl").equals("mgdl"));
        double mySettingMgdl = doMgdl ? tolerantParseDouble(mySettingString) : tolerantParseDouble(mySettingString) * Constants.MMOLL_TO_MGDL; // The preference value in mg/dL
        if (mySettingMgdl > max) { // If the preference value is greater than max
            if (!doMgdl && mySettingString.equals(def)) { // If the setting value in mmol/L is the same as the default, which is in mg/dL, we correct the value next.
                // This will only happen if user has chosen mmol/L and updates to a version that has a new preference setting with default in mg/dL
                UserError.Log.d(TAG, "Setting  " + pref_key + "  to default converted to mmol/L");
                Pref.setString(pref_key, JoH.qs(tolerantParseDouble(def) * Constants.MGDL_TO_MMOLL, 1)); // Set the preference to the default value converted to mmol/L
            } else { // The preference has been set to a value greater than the max allowed.  Let's fix it and notify.
                // This will only happen if user has entered a preference setting value out of range before the listener range limit update has been merged.
                mySettingString = doMgdl ? max + "" : JoH.qs(max * Constants.MGDL_TO_MMOLL, 1) + "";
                Pref.setString(pref_key, mySettingString); // Set the preference to max
                UserError.Log.uel(TAG, xdrip.gs(R.string.pref_was_greater_than_max, pref_key)); // Inform the user that xDrip is changing the setting value
                showNotification(pref_key, xdrip.gs(R.string.setting_pref_to_max), null, notificationId, null, false, false, null, null, null, true);
            }
        } else if (mySettingMgdl < min) { // If the preference value is less than min, correct it and notify.
            // This will only happen if user has entered a preference setting value out of range before the listener range limit update has been merged.
            mySettingString = doMgdl ? min + "" : JoH.qs(min * Constants.MGDL_TO_MMOLL, 1) + "";
            Pref.setString(pref_key, mySettingString); // Set the preference to min
            UserError.Log.uel(TAG, xdrip.gs(R.string.pref_was_less_than_min, pref_key)); // Inform the user that xDrip is changing the setting value
            showNotification(pref_key, xdrip.gs(R.string.setting_pref_to_min), null, notificationId, null, false, false, null, null, null, true);

        }
    }

    public static class AllPrefsFragment extends PreferenceFragment {

        final String jumpTo;

        Preferences parent;
        SharedPreferences prefs;
        SearchConfiguration searchConfiguration;

        public LockScreenWallPaper.PrefListener lockListener = new LockScreenWallPaper.PrefListener();
        private Preference miband2_screen;
        private Preference miband3_4_screen;
        private Preference miband_send_readings_as_notification;
        private Preference miband_authkey;
        private Preference miband_nightmode_category;
        private Preference miband_nightmode_interval;
        private Preference miband_graph_category;

        // default constructor is required in addition on some platforms
        public AllPrefsFragment() {
            this(null);
        }

        @SuppressLint("ValidFragment")
        public AllPrefsFragment(String jumpTo) {
            this.jumpTo = jumpTo;
        }

        public void setParent(Preferences parent) {
            this.parent = parent;
        }

        private void setSummary(String pref_name) {
            setSummary_static(this, pref_name);
        }

        private static void setSummary_static(AllPrefsFragment allPrefsFragment, String pref_name) {
            try {
                // is there a cleaner way to bind these values when setting programatically?
                final String pref_val = allPrefsFragment.prefs.getString(pref_name, "");
                allPrefsFragment.findPreference(pref_name).setSummary(pref_val);
                EditTextPreference thispref = (EditTextPreference) allPrefsFragment.findPreference(pref_name);
                thispref.setText(pref_val);
            } catch (Exception e) {
                Log.e(TAG, "Exception during setSummary: " + e.toString());
            }
        }

        private Preference safeFindPreference(final String key) {
            // NOTE: Some builds / translations ship reduced preference XMLs.
            // Always guard lookups to prevent Preferences activity crashes.
            final Preference pref = findPreference(key);
            if (pref == null) {
                Log.wtf(TAG, "Missing preference " + key);
            }
            return pref;
        }

        private void safeSetOnPreferenceChangeListener(final String key, final Preference.OnPreferenceChangeListener listener) {
            final Preference pref = safeFindPreference(key);
            if (pref == null) return;
            pref.setOnPreferenceChangeListener(listener);
        }

        private void safeSetOnPreferenceClickListener(final String key, final Preference.OnPreferenceClickListener listener) {
            final Preference pref = safeFindPreference(key);
            if (pref == null) return;
            pref.setOnPreferenceClickListener(listener);
        }

        private static boolean safeRemovePreference(final PreferenceGroup group, final Preference preference, final String debugKey) {
            // NOTE: Some builds have reduced preference XMLs. PreferenceGroup#removePreference(null)
            // throws a NullPointerException in framework code, so always guard before calling it.
            if (group == null) return false;
            if (preference == null) {
                Log.wtf(TAG, "Cannot remove missing preference " + debugKey);
                return false;
            }
            try {
                return group.removePreference(preference);
            } catch (Exception e) {
                Log.wtf(TAG, "Failed to remove preference " + debugKey + ": " + e);
                return false;
            }
        }

        private static boolean safeAddPreference(final PreferenceGroup group, final Preference preference, final String debugKey) {
            // NOTE: PreferenceGroup#addPreference(null) can crash on some Android versions.
            if (group == null) return false;
            if (preference == null) {
                Log.wtf(TAG, "Cannot add missing preference " + debugKey);
                return false;
            }
            try {
                return group.addPreference(preference);
            } catch (Exception e) {
                Log.wtf(TAG, "Failed to add preference " + debugKey + ": " + e);
                return false;
            }
        }


        @SuppressLint("ApplySharedPref")
        @Override
        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            this.prefs = PreferenceManager.getDefaultSharedPreferences(getActivity());
            final DexCollectionType collectionType = DexCollectionType.getType(this.prefs.getString("dex_collection_method", "BluetoothWixel"));

            static_units = this.prefs.getString("units", "mgdl");
            addPreferencesFromResource(R.xml.pref_license);
            addPreferencesFromResource(R.xml.pref_general);
            bindPreferenceSummaryToValueAndEnsureNumeric(findPreference("highValue"));
            bindPreferenceSummaryToValueAndEnsureNumeric(findPreference("lowValue"));
            units_pref = safeFindPreference("units");
            if (units_pref != null) {
                bindPreferenceSummaryToValue(units_pref);
            }

            addPreferencesFromResource(R.xml.pref_notifications);
            bindPreferenceSummaryToValue(findPreference("bg_alert_profile"));
            bindPreferenceSummaryToValue(findPreference("calibration_notification_sound"));
            bindPreferenceSummaryToValueAndEnsureNumeric(findPreference("calibration_snooze"));
            bindPreferenceSummaryToValueAndEnsureNumeric(findPreference("bg_unclear_readings_minutes"));
            bindPreferenceSummaryToValueAndEnsureNumeric(findPreference("disable_alerts_stale_data_minutes"));
            bindPreferenceSummaryToValue(findPreference("falling_bg_val"));
            bindPreferenceSummaryToValue(findPreference("rising_bg_val"));
            bindPreferenceSummaryToValue(findPreference("other_alerts_sound"));
            bindPreferenceSummaryToUnitizedValueAndEnsureNumeric(findPreference("persistent_high_threshold"));
            bindPreferenceSummaryToUnitizedValueAndEnsureNumeric(findPreference("forecast_low_threshold"));

            addPreferencesFromResource(R.xml.pref_data_source);

            addPreferencesFromResource(R.xml.pref_data_sync);
            setupBarcodeConfigScanner();
            setupBarcodeShareScanner();
            setupQrFromFile();
            bindPreferenceSummaryToValue(findPreference("cloud_storage_mongodb_uri"));
            bindPreferenceSummaryToValue(findPreference("cloud_storage_mongodb_collection"));
            bindPreferenceSummaryToValue(findPreference("cloud_storage_mongodb_device_status_collection"));

            addPreferencesFromResource(R.xml.pref_advanced_settings);
            addPreferencesFromResource(R.xml.xdrip_plus_prefs);

            bindPreferenceSummaryToValue(findPreference("persistent_high_threshold_mins"));
            bindPreferenceSummaryToValue(findPreference("persistent_high_repeat_mins"));

            bindPreferenceTitleAppendToValueUpdateChannel(findPreference("update_channel"));


            profile_insulin_sensitivity_default = safeFindPreference("profile_insulin_sensitivity_default");
            profile_carb_ratio_default = safeFindPreference("profile_carb_ratio_default");
            refreshProfileRatios();

            nfc_expiry_days = safeFindPreference("nfc_expiry_days");

            final Preference forcedLanguagePref = safeFindPreference("forced_language");
            if (forcedLanguagePref instanceof ListPreference) {
                locale_choice = (ListPreference) forcedLanguagePref;
            } else {
                // NOTE: Keep locale_choice null if missing or wrong type.
                locale_choice = null;
                if (forcedLanguagePref != null) {
                    Log.wtf(TAG, "Preference forced_language is not a ListPreference");
                }
            }

            force_english = safeFindPreference("force_english");

            update_force_english_title("");

            if (force_english != null) {
                force_english.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                                                                @Override
                                                                public boolean onPreferenceChange(Preference preference, Object newValue) {
                                                                    prefs.edit().putBoolean("force_english", (boolean) newValue).commit();
                                                                    SdcardImportExport.hardReset();
                                                                    return true;
                                                                }
                                                            }
                );
            }

            if (locale_choice != null) {
                locale_choice.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                                                                @Override
                                                                public boolean onPreferenceChange(Preference preference, Object newValue) {
                                                                    prefs.edit().putString("forced_language", (String) newValue).commit();
                                                                    update_force_english_title((String) newValue);
                                                                    if (prefs.getBoolean("force_english", false)) {
                                                                        SdcardImportExport.hardReset();
                                                                    }
                                                                    return true;
                                                                }
                                                            }
                );
            }

            safeSetOnPreferenceChangeListener("disable_all_sync", new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    prefs.edit().putBoolean("disable_all_sync", (boolean) newValue).commit();
                    SdcardImportExport.hardReset();
                    return true;
                }

            });

            // this gets cached in a static final field at the moment so needs hard reset
            safeSetOnPreferenceChangeListener("g5-battery-warning-level", (preference, newValue) -> {
                prefs.edit().putString("g5-battery-warning-level", (String) newValue).commit();
                G5BaseService.resetTransmitterBatteryStatus();
                SdcardImportExport.hardReset();
                return true;
            });

            final Preference profile_carb_absorption_default = safeFindPreference("profile_carb_absorption_default");
            if (profile_carb_absorption_default != null) {
                profile_carb_absorption_default.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (!isNumeric(newValue.toString())) {
                            return false;
                        }
                        preference.setTitle(format_carb_absorption_rate(preference.getTitle().toString(), newValue.toString()));
                        Profile.reloadPreferences(AllPrefsFragment.this.prefs);
                        Home.staticRefreshBGCharts();
                        return true;
                    }
                });

                profile_carb_absorption_default.setTitle(format_carb_absorption_rate(profile_carb_absorption_default.getTitle().toString(), this.prefs.getString("profile_carb_absorption_default", "")));
            }


            refresh_extra_items();
            safeSetOnPreferenceChangeListener("plus_extra_features", new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    Home.invalidateMenu = true; // force redraw
                    refresh_extra_items();

                    return true;
                }
            });

            final Preference crash_reports = safeFindPreference("enable_crashlytics");
            if (crash_reports != null) {
                crash_reports.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        Toast.makeText(preference.getContext(),
                                "Crash Setting takes effect on next restart", Toast.LENGTH_LONG).show();
                        return true;
                    }
                });
            }

            bindTTSListener();
            final Preference collectionMethod = safeFindPreference("dex_collection_method");
            final Preference runInForeground = safeFindPreference("run_service_in_foreground");
            final Preference g5nonraw = safeFindPreference("g5_non_raw_method");
            final Preference wifiRecievers = safeFindPreference("wifi_recievers_addresses");
            final Preference predictiveBG = safeFindPreference("predictive_bg");
            final Preference interpretRaw = safeFindPreference("interpret_raw");
            final Preference bfappid = safeFindPreference("bugfender_appid");
            final Preference nfcSettings = safeFindPreference("xdrip_plus_nfc_settings");
            final Preference bluereadersettings = safeFindPreference("xdrip_blueReader_advanced_settings");
            final Preference libre2settings = safeFindPreference("xdrip_libre2_advanced_settings");

            final Preference currentCalibrationPluginPref = safeFindPreference("current_calibration_plugin");
            final ListPreference currentCalibrationPlugin = currentCalibrationPluginPref instanceof ListPreference ? (ListPreference) currentCalibrationPluginPref : null;
            final PreferenceCategory collectionCategory = (PreferenceCategory) safeFindPreference("collection_category");

            // NOTE: Must be declared at this scope (not inside a try-block), because it is used later in this method.
            // Some preference XML variants may omit this key, so it is allowed to be null.
            final EditTextPreference transmitterId = (EditTextPreference) findPreference("dex_txid");

            final Preference shareKey = safeFindPreference("share_key");
            if (shareKey != null) {
                shareKey.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        AllPrefsFragment.this.prefs.edit().remove("dexcom_share_session_id").apply();
                        return true;
                    }
                });
            }

            Preference.OnPreferenceChangeListener shareTokenResettingListener = new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    AllPrefsFragment.this.prefs.edit().remove("dexcom_share_session_id").apply();
                    return true;
                }
            };

            final Preference sharePassword = safeFindPreference("dexcom_account_password");
            if (sharePassword != null) {
                sharePassword.setOnPreferenceChangeListener(shareTokenResettingListener);
            }
            final Preference shareAccountName = safeFindPreference("dexcom_account_name");
            if (shareAccountName != null) {
                shareAccountName.setOnPreferenceChangeListener(shareTokenResettingListener);
            }

            final Preference nsFollowDownload = safeFindPreference("nsfollow_download_treatments_screen");
            final Preference nsFollowUrl = safeFindPreference("nsfollow_url");
            final Preference nsFollowLag = safeFindPreference("nsfollow_lag");
            bindPreferenceSummaryToValue(findPreference("nsfollow_lag"));

            final Preference xSyncFollowChime = safeFindPreference("follower_chime");

            if (collectionType != DexCollectionType.WebFollow) {
                final Preference webfollow = safeFindPreference("xdrip_plus_web_follow_settings");
                safeRemovePreference(collectionCategory, webfollow, "xdrip_plus_web_follow_settings");
            }

            final Preference carelinkFollowCountry = safeFindPreference("clfollow_country");
            final Preference carelinkFollowPatient = safeFindPreference("clfollow_patient");
            final Preference carelinkFollowLogin = safeFindPreference("clfollow_login");
            final Preference carelinkFollowGracePeriod = safeFindPreference("clfollow_grace_period");
            final Preference carelinkFollowMissedPollInterval = safeFindPreference("clfollow_missed_poll_interval");
            final Preference carelinkFollowDownloadFingerBGs = safeFindPreference("clfollow_download_finger_bgs");
            final Preference carelinkFollowDownloadBoluses = safeFindPreference("clfollow_download_boluses");
            final Preference carelinkFollowDownloadMeals = safeFindPreference("clfollow_download_meals");
            final Preference carelinkFollowDownloadNotifications = safeFindPreference("clfollow_download_notifications");
            if (collectionType == DexCollectionType.CLFollow) {
                safeAddPreference(collectionCategory, carelinkFollowCountry, "clfollow_country");
                safeAddPreference(collectionCategory, carelinkFollowPatient, "clfollow_patient");
                safeAddPreference(collectionCategory, carelinkFollowLogin, "clfollow_login");
                safeAddPreference(collectionCategory, carelinkFollowGracePeriod, "clfollow_grace_period");
                safeAddPreference(collectionCategory, carelinkFollowMissedPollInterval, "clfollow_missed_poll_interval");
                safeAddPreference(collectionCategory, carelinkFollowDownloadFingerBGs, "clfollow_download_finger_bgs");
                safeAddPreference(collectionCategory, carelinkFollowDownloadBoluses, "clfollow_download_boluses");
                safeAddPreference(collectionCategory, carelinkFollowDownloadMeals, "clfollow_download_meals");
                safeAddPreference(collectionCategory, carelinkFollowDownloadNotifications, "clfollow_download_notifications");
            } else {
                safeRemovePreference(collectionCategory, carelinkFollowCountry, "clfollow_country");
                safeRemovePreference(collectionCategory, carelinkFollowPatient, "clfollow_patient");
                safeRemovePreference(collectionCategory, carelinkFollowLogin, "clfollow_login");
                safeRemovePreference(collectionCategory, carelinkFollowGracePeriod, "clfollow_grace_period");
                safeRemovePreference(collectionCategory, carelinkFollowMissedPollInterval, "clfollow_missed_poll_interval");
                safeRemovePreference(collectionCategory, carelinkFollowDownloadFingerBGs, "clfollow_download_finger_bgs");
                safeRemovePreference(collectionCategory, carelinkFollowDownloadBoluses, "clfollow_download_boluses");
                safeRemovePreference(collectionCategory, carelinkFollowDownloadMeals, "clfollow_download_meals");
                safeRemovePreference(collectionCategory, carelinkFollowDownloadNotifications, "clfollow_download_notifications");
            }

            final PreferenceCategory flairCategory = (PreferenceCategory) safeFindPreference("xdrip_plus_display_colorset9_android5plus");
            final PreferenceScreen loggingScreen = (PreferenceScreen) safeFindPreference("xdrip_logging_adv_settings");
            final PreferenceScreen motionScreen = (PreferenceScreen) safeFindPreference("xdrip_plus_motion_settings");
            final PreferenceScreen nfcScreen = (PreferenceScreen) safeFindPreference("xdrip_plus_nfc_settings");
            final PreferenceCategory otherCategory = (PreferenceCategory) safeFindPreference("other_category");
            final PreferenceScreen calibrationAlertsScreen = (PreferenceScreen) safeFindPreference("calibration_alerts_screen");
            final PreferenceCategory alertsCategory = (PreferenceCategory) safeFindPreference("alerts_category");
            final Preference disableAlertsStaleDataMinutes = safeFindPreference("disable_alerts_stale_data_minutes");
            final PreferenceScreen calibrationSettingsScreen = (PreferenceScreen) safeFindPreference("xdrip_plus_calibration_settings");
            final PreferenceScreen colorScreen = (PreferenceScreen) safeFindPreference("xdrip_plus_color_settings");
            final Preference old_school_calibration_mode = safeFindPreference("old_school_calibration_mode");
            final Preference extraTagsForLogs = safeFindPreference("extra_tags_for_logging");
            final Preference enableBF = safeFindPreference("enable_bugfender");
            final PreferenceCategory displayCategory = (PreferenceCategory) safeFindPreference("xdrip_plus_display_category");


            lockListener.setSummaryPreference(findPreference("pick_numberwall_start"));

            final Preference enableAmazfit = safeFindPreference("pref_amazfit_enable_key");


            if (enableAmazfit != null) {
                enableAmazfit.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        final Context context = preference.getContext();
                        Boolean enabled = (boolean) newValue;
                        if (enabled == true) {
                            context.startService(new Intent(context, Amazfitservice.class));

                        } else {
                            context.stopService(new Intent(context, Amazfitservice.class));
                        }

                        return true;
                    }
                });
            }

            safeSetOnPreferenceChangeListener("wear_sync", (preference, newValue) -> {
                        WatchUpdaterService.startSelf();
                        return true;
                    }
            );

            safeSetOnPreferenceChangeListener("use_wear_heartrate", (preference, newValue) -> {
                        WatchUpdaterService.startSelf();
                        return true;
                    }
            );

            final Preference scanAndPair = safeFindPreference("scan_and_pair_meter");
            if (scanAndPair != null) {
                scanAndPair.setSummary(prefs.getString("selected_bluetooth_meter_info", ""));
            }

            safeSetOnPreferenceChangeListener("xdrip_webservice", (preference, newValue) -> {
                preference.getEditor().putBoolean(preference.getKey(), (boolean) newValue).apply();
                XdripWebService.immortality();
                return true;
            });

            safeSetOnPreferenceChangeListener("xdrip_webservice_open", (preference, newValue) -> {
                preference.getEditor().putBoolean(preference.getKey(), (boolean) newValue).apply();
                XdripWebService.settingsChanged();
                return true;
            });

            safeSetOnPreferenceChangeListener("desert_sync_enabled", (preference, newValue) -> {
                preference.getEditor().putBoolean(preference.getKey(), (boolean) newValue).apply();
                DesertSync.settingsChanged();
                return true;
            });


            if (enableBF != null)
                enableBF.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                                                      @Override
                                                      public boolean onPreferenceChange(Preference preference, Object newValue) {
                                                          preference.getEditor().putBoolean(preference.getKey(), (boolean) newValue).apply();
                                                          xdrip.initBF();
                                                          return true;
                                                      }
                                                  }

                );

            if (disableAlertsStaleDataMinutes != null) {
                disableAlertsStaleDataMinutes.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if (!isNumeric(newValue.toString())) {
                            return false;
                        }
                        if ((Integer.parseInt(newValue.toString())) < 10) {
                            Toast.makeText(preference.getContext(),
                                    "Value must be at least 10 minutes", Toast.LENGTH_LONG).show();
                            return false;
                        }
                        preference.setSummary(newValue.toString());
                        return true;
                    }
                });
            }

            final Preference showShowcase = safeFindPreference("show_showcase");
            if (showShowcase != null) {
                showShowcase.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        if ((boolean) newValue) {
                            ShotStateStore.resetAllShots();
                            JoH.static_toast(preference.getContext(), getString(R.string.interface_tips_from_start), Toast.LENGTH_LONG);
                        }
                        return true;
                    }
                });
            }

            // NOTE: units_pref can be missing in some XML variants; guard to avoid crashes.
            if (units_pref != null) {
                units_pref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {

                        handleUnitsChange(preference, newValue, pFragment);
                        return true;
                    }
                });
            }

            if (this.prefs.getString("custom_sync_key", "").equals("")) {
                this.prefs.edit().putString("custom_sync_key", CipherUtils.getRandomHexKey()).apply();
            }

            bindPreferenceSummaryToValue(findPreference("xplus_insulin_dia"));
            bindPreferenceSummaryToValue(findPreference("xplus_liver_sensitivity"));
            bindPreferenceSummaryToValue(findPreference("xplus_liver_maximpact"));

            bindPreferenceSummaryToValue(findPreference("low_predict_alarm_level"));
            Profile.validateTargetRange();
            bindPreferenceSummaryToValue(findPreference("plus_target_range"));


            Log.d(TAG, collectionType.name());
            if (collectionType != DexCollectionType.DexcomShare) {
                safeRemovePreference(collectionCategory, shareKey, "share_key");
                safeRemovePreference(collectionCategory, findPreference("scan_share2_barcode"), "scan_share2_barcode");
                safeRemovePreference(otherCategory, interpretRaw, "interpret_raw");
                safeAddPreference(alertsCategory, calibrationAlertsScreen, "calibration_alerts_screen");
            } else {
                safeRemovePreference(otherCategory, predictiveBG, "predictive_bg");
                safeRemovePreference(alertsCategory, calibrationAlertsScreen, "calibration_alerts_screen");
                this.prefs.edit().putBoolean("calibration_notifications", false).apply();
            }

            if (collectionType != DexCollectionType.Medtrum) {
                safeRemovePreference(collectionCategory, findPreference("medtrum_use_native"), "medtrum_use_native");
                safeRemovePreference(collectionCategory, findPreference("medtrum_a_hex"), "medtrum_a_hex");
            }

            if (collectionType != DexCollectionType.NSFollow) {
                safeRemovePreference(collectionCategory, nsFollowUrl, "nsfollow_url");
                safeRemovePreference(collectionCategory, nsFollowDownload, "nsfollow_download_treatments_screen");
                safeRemovePreference(collectionCategory, nsFollowLag, "nsfollow_lag");
            }

            if (collectionType != DexCollectionType.Follower) {
                safeRemovePreference(collectionCategory, xSyncFollowChime, "follower_chime");
            }

            if (getBestCollectorHardwareName().equals("G7")) {
                try {
                    PreferenceScreen screen = (PreferenceScreen) findPreference("xdrip_plus_g5_extra_settings");
                    Preference pref = getPreferenceManager().findPreference("dex_battery_category");
                    if (screen != null && pref != null) {
                        screen.removePreference(pref);
                    }
                } catch (Exception e) {
                    UserError.Log.wtf(TAG, "Failed to remove G7 battery options");
                }
            }

            if (collectionType != DexCollectionType.CLFollow) {
                safeRemovePreference(collectionCategory, carelinkFollowCountry, "clfollow_country");
                safeRemovePreference(collectionCategory, carelinkFollowPatient, "clfollow_patient");
                safeRemovePreference(collectionCategory, carelinkFollowGracePeriod, "clfollow_grace_period");
                safeRemovePreference(collectionCategory, carelinkFollowMissedPollInterval, "clfollow_missed_poll_interval");
                safeRemovePreference(collectionCategory, carelinkFollowDownloadFingerBGs, "clfollow_download_finger_bgs");
                safeRemovePreference(collectionCategory, carelinkFollowDownloadMeals, "clfollow_download_meals");
                safeRemovePreference(collectionCategory, carelinkFollowDownloadNotifications, "clfollow_download_notifications");
            }

            try {
                findPreference("nfc_scan_homescreen").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        NFCReaderX.handleHomeScreenScanPreference(xdrip.getAppContext(), (boolean) newValue && (NFCReaderX.useNFC()));
                        return true;
                    }
                });
            } catch (NullPointerException e) {
                Log.d(TAG, "Nullpointer looking for nfc_scan_homescreen");
            }
            try {
                findPreference("use_nfc_scan").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(final Preference preference, final Object newValue) {
                        final AlertDialog.Builder builder = new AlertDialog.Builder(preference.getContext());
                        if ((boolean) newValue) {

                            final boolean paranoidAboutNFC = false;
                            if (paranoidAboutNFC) {
                                builder.setTitle("Stop! Are you sure?");
                                builder.setMessage("This can sometimes crash / break a sensor!\nWith some phones there can be problems, try on expiring sensor first for safety. You have been warned.");

                                builder.setPositiveButton("I AM SURE", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        ((SwitchPreference) preference).setChecked(true);
                                        preference.getEditor().putBoolean("use_nfc_scan", true).apply();
                                        NFCReaderX.handleHomeScreenScanPreference(xdrip.getAppContext(), (boolean) newValue && prefs.getBoolean("nfc_scan_homescreen", false));
                                    }
                                });
                                builder.setNegativeButton("NOPE", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                final AlertDialog alert = builder.create();
                                alert.show();

                            } else {
                                ((SwitchPreference) preference).setChecked(true);
                                preference.getEditor().putBoolean("use_nfc_scan", true).apply();
                                NFCReaderX.handleHomeScreenScanPreference(xdrip.getAppContext(), (boolean) newValue && prefs.getBoolean("nfc_scan_homescreen", false));
                                return true;
                            }

                            return false;
                        } else {
                            NFCReaderX.handleHomeScreenScanPreference(xdrip.getAppContext(), (boolean) newValue && prefs.getBoolean("nfc_scan_homescreen", false));
                        }
                        return true;
                    }
                });
            } catch (NullPointerException e) {
                Log.d(TAG, "Nullpointer looking for nfc_scan");
            }

            try {
                findPreference("external_blukon_algorithm").setOnPreferenceChangeListener((preference, newValue) -> {
                    boolean isEnabled = ((Boolean) newValue).booleanValue();
                    findPreference("retrieve_blukon_history").setEnabled(!isEnabled);
                    return true;
                });
            } catch (NullPointerException e) {
                //
            }

            final boolean engineering_mode = this.prefs.getBoolean("engineering_mode", false);

            if (!engineering_mode) {
                try {
                    if (displayCategory != null) {
                        displayCategory.removePreference(findPreference("bg_compensate_noise_ultrasensitive"));
                    }
                } catch (Exception e) {
                    //
                }
            }

            try {
                ((PreferenceScreen) findPreference("dexcom_server_upload_screen")).removePreference(findPreference("share_test_key"));
                ((PreferenceScreen) findPreference("dexcom_server_upload_screen")).removePreference(findPreference("share_key"));
            } catch (Exception e) {
                //
            }

            if (currentCalibrationPlugin != null) {
                PluggableCalibration.setListPreferenceData(currentCalibrationPlugin);

                currentCalibrationPlugin.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        PluggableCalibration.invalidateCache();
                        PluggableCalibration.invalidateCache(newValue.toString());
                        PluggableCalibration.invalidatePluginCache();
                        return true;
                    }
                });
            }

            if (!DexCollectionType.hasLibre(collectionType)) {
                safeRemovePreference(collectionCategory, nfcSettings, "xdrip_plus_nfc_settings");
            } else {
                if (!engineering_mode)
                    try {
                        if (nfcScreen != null) {
                            nfcScreen.removePreference(findPreference("nfc_test_diagnostic"));
                        }
                    } catch (NullPointerException e) {
                        //
                    }
                set_nfc_expiry_change_listeners();
                update_nfc_expiry_preferences(null);
            }

            if (!DexCollectionService.getBestLimitterHardwareName().equals("BlueReader")) {
                safeRemovePreference(collectionCategory, bluereadersettings, "xdrip_blueReader_advanced_settings");
            } else {
                findPreference("blueReader_turn_off_value").setTitle(getString(R.string.blueReader_turnoffvalue) + " (" + prefs.getInt("blueReader_turn_off_value", 5) + ")");

                findPreference("blueReader_turn_off_value").setOnPreferenceChangeListener((preference, newValue) ->
                        {
                            prefs.edit().putInt("blueReader_turn_off_value", (Integer) newValue).commit();
                            preference.setTitle(getString(R.string.blueReader_turnoffvalue) + " (" + newValue + ")");
                            return true;
                        }
                );

            }


            try {

                try {
                    if (DexCollectionType.getDexCollectionType() != DexCollectionType.LibreReceiver) {
                        safeRemovePreference(collectionCategory, libre2settings, "xdrip_libre2_advanced_settings");
                    }
                } catch (NullPointerException e) {
                    Log.wtf(TAG, "Nullpointer Libre2Settings: ", e);
                }

                try {
                    if (!DexCollectionType.hasWifi()) {
                        final String receiversIpAddresses = this.prefs.getString("wifi_recievers_addresses", "").trim();
                        if (receiversIpAddresses.equals("")) {
                            safeRemovePreference(collectionCategory, wifiRecievers, "wifi_recievers_addresses");
                        }
                    }
                } catch (NullPointerException e) {
                    Log.wtf(TAG, "Nullpointer wifireceivers ", e);
                }

                if ((collectionType != DexCollectionType.DexbridgeWixel)
                        && (collectionType != DexCollectionType.WifiDexBridgeWixel)) {
                    safeRemovePreference(collectionCategory, transmitterId, "dex_txid");
                }

                if (Build.VERSION.SDK_INT < 21) {
                    try {
                        if (colorScreen != null) {
                            colorScreen.removePreference(flairCategory);
                        }
                    } catch (Exception e) {
                        //
                    }
                }
                if (Build.VERSION.SDK_INT < 23) {
                    try {
                        ((PreferenceGroup) findPreference("xdrip_plus_display_category")).removePreference(findPreference("xdrip_plus_number_icon"));
                    } catch (Exception e) {
                        //
                    }
                }


                if (!Pref.getBooleanDefaultFalse("engineering_mode")) {
                    try {
                        final PreferenceScreen screen = (PreferenceScreen) findPreference("cloud_data_sync");
                        if (screen != null) {
                            screen.removePreference(findPreference("cloud_storage_web_deposit"));
                        }
                    } catch (Exception e) {
                        //
                    }
                }

                if (Home.get_master()) {
                    final PreferenceScreen desert_sync_screen = (PreferenceScreen) findPreference("xdrip_plus_desert_sync_settings");
                    try {
                        if (desert_sync_screen != null) {
                            desert_sync_screen.removePreference(findPreference("desert_sync_master_ip"));
                        }

                    } catch (Exception e) {
                        //
                    }
                }


                final PreferenceScreen g5_settings_screen = (PreferenceScreen) findPreference("xdrip_plus_g5_extra_settings");
                if (collectionType == DexCollectionType.DexcomG5) {
                    try {
                        safeAddPreference(collectionCategory, transmitterId, "dex_txid");
                        safeAddPreference(collectionCategory, g5_settings_screen, "xdrip_plus_g5_extra_settings");
                    } catch (NullPointerException e) {
                        Log.wtf(TAG, "Null pointer adding G5 prefs ", e);
                    }
                } else {
                    safeRemovePreference(collectionCategory, g5_settings_screen, "xdrip_plus_g5_extra_settings");
                }

                if (!engineering_mode) {
                    try {
                        if (!Experience.gotData()) {
                            getPreferenceScreen().removePreference(motionScreen);
                        }
                        if (calibrationSettingsScreen != null) {
                            calibrationSettingsScreen.removePreference(old_school_calibration_mode);
                        }
                    } catch (NullPointerException e) {
                        Log.wtf(TAG, "Nullpointer with engineering mode s ", e);
                    }
                }
                if ((!engineering_mode) || (!this.prefs.getBoolean("enable_bugfender", false))) {
                    if (loggingScreen != null) {
                        loggingScreen.removePreference(bfappid);
                    }
                }

            } catch (NullPointerException e) {
                Log.wtf(TAG, "Got null pointer exception removing pref: ", e);
            }

            if (engineering_mode || this.prefs.getString("update_channel", "").matches("alpha|nightly")) {
                final Preference update_channel_pref = findPreference("update_channel");
                if (update_channel_pref instanceof ListPreference) {
                    ListPreference update_channel = (ListPreference) update_channel_pref;
                    update_channel.setEntryValues(getResources().getStringArray(R.array.UpdateChannelE));
                    update_channel.setEntries(getResources().getStringArray(R.array.UpdateChannelDetailE));
                }
            }

            final DecimalFormat df = new DecimalFormat("#.#");

            final EditTextPreference pebbleSpecialValue = (EditTextPreference) findPreference("pebble_special_value");
            bindPreferenceSummaryToValueAndEnsureNumeric(pebbleSpecialValue);

            if (this.prefs.getString("units", "mgdl").compareTo("mmol") != 0) {
                df.setMaximumFractionDigits(0);
                pebbleSpecialValue.setDefaultValue("99");
                if (pebbleSpecialValue.getText().compareTo("5.5") == 0) {
                    pebbleSpecialValue.setText(df.format(Double.valueOf(pebbleSpecialValue.getText()) * Constants.MMOLL_TO_MGDL));
                }
            } else {
                df.setMaximumFractionDigits(1);
                pebbleSpecialValue.setDefaultValue("5.5");
                if (pebbleSpecialValue.getText().compareTo("99") == 0) {
                    pebbleSpecialValue.setText(df.format(Double.valueOf(pebbleSpecialValue.getText()) / Constants.MMOLL_TO_MGDL));
                }
            }

            try {
                findPreference("calibration_notifications").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        final UserNotification userNotification = UserNotification.lastCalibrationAlert();
                        if (userNotification != null) {
                            userNotification.delete();
                        }
                        return true;
                    }
                });
            } catch (Exception e) {
                //
            }

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                try {
                    findPreference("health_connect_enable").setOnPreferenceChangeListener((preference, newValue) -> {
                        if ((Boolean) newValue) {
                            Inevitable.task("check-health-connect", 300, () -> HealthGamut.init(getActivity()));
                        }
                        return true;
                    });
                } catch (Exception e) {
                    //
                }

                try {
                    findPreference("health_connect_manage").setOnPreferenceClickListener((preference) -> {
                        HealthGamut.init(getActivity()).openPermissionManager();
                        return true;
                    });
                } catch (Exception e) {
                    //
                }
            }


            if (collectionMethod != null) {
                bindPreferenceSummaryToValue(collectionMethod);
            }
            if (shareKey != null) {
                bindPreferenceSummaryToValue(shareKey);
            }

            final NamedSliderProcessor processor = new BgToSpeech();
            bindPreferenceSummaryAppendToIntegerValueFromLogSlider(findPreference("speak_readings_change_time"), processor, "time", false);
            bindPreferenceSummaryAppendToIntegerValueFromLogSlider(findPreference("speak_readings_change_threshold"), processor, "threshold", true);


            if (wifiRecievers != null) {
                wifiRecievers.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        preference.setSummary(newValue.toString());
                        ParakeetHelper.notifyOnNextCheckin(true);
                        return true;
                    }
                });
            }

            bindPreferenceTitleAppendToStringValue(findPreference("retention_days_bg_reading"));

            bindPreferenceTitleAppendToStringValue(findPreference("pendiq_pin"));

            bindWidgetUpdater();

            if (extraTagsForLogs != null) {
                extraTagsForLogs.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        ExtraLogTags.readPreference((String) newValue);
                        return true;
                    }
                });
            }

            // NOTE: transmitterId might not exist in some flavors / older XMLs. Guard it so the settings screen still works.
            if (transmitterId != null) {
                bindPreferenceSummaryToValue(transmitterId); // duplicated below but this sets initial value
                transmitterId.getEditText().setFilters(new InputFilter[]{new InputFilter.AllCaps()});
                transmitterId.getEditText().post(() -> {
                    try {
                        transmitterId.getEditText().setSelection(transmitterId.getEditText().getText().length());
                    } catch (Exception e) {
                        UserError.Log.d(TAG, "Could not set selection for transmitter id: " + e);
                    }
                });
                transmitterId.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        final Activity activity = getActivity();
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                Dialog.askIfNeeded(activity, (String) newValue);
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    //
                                }
                                Log.d(TAG, "Trying to restart collector due to tx id change");

                                clearDataWhenTransmitterIdEntered((String) newValue);

                                CollectionServiceStarter.restartCollectionService(xdrip.getAppContext());
                            }
                        }).start();
                        sBindPreferenceSummaryToValueListener.onPreferenceChange(preference, newValue);

                        return true;
                    }
                });
            } else {
                Log.wtf(TAG, "Missing preference dex_txid (transmitter id)");
            }

            if (collectionMethod != null) {
                collectionMethod.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    @Override
                    public boolean onPreferenceChange(Preference preference, Object newValue) {

                        DexCollectionType collectionType = DexCollectionType.getType((String) newValue);

                        if (collectionType != DexCollectionType.DexcomShare) {
                            safeRemovePreference(collectionCategory, shareKey, "share_key");
                            safeRemovePreference(collectionCategory, findPreference("scan_share2_barcode"), "scan_share2_barcode");
                            safeRemovePreference(otherCategory, interpretRaw, "interpret_raw");
                            safeAddPreference(otherCategory, predictiveBG, "predictive_bg");
                            safeAddPreference(alertsCategory, calibrationAlertsScreen, "calibration_alerts_screen");
                        } else {
                            safeAddPreference(collectionCategory, shareKey, "share_key");
                            safeAddPreference(collectionCategory, findPreference("scan_share2_barcode"), "scan_share2_barcode");
                            safeAddPreference(otherCategory, interpretRaw, "interpret_raw");
                            safeRemovePreference(otherCategory, predictiveBG, "predictive_bg");
                            safeRemovePreference(alertsCategory, calibrationAlertsScreen, "calibration_alerts_screen");
                            AllPrefsFragment.this.prefs.edit().putBoolean("calibration_notifications", false).apply();
                        }

                        if (DexCollectionType.hasLibre(collectionType)) {
                            safeAddPreference(collectionCategory, nfcSettings, "xdrip_plus_nfc_settings");
                            NFCReaderX.handleHomeScreenScanPreference(xdrip.getAppContext(), prefs.getBoolean("nfc_scan_homescreen", false) && prefs.getBoolean("use_nfc_scan", false));
                            if (!engineering_mode)
                                try {
                                    if (nfcScreen != null) {
                                        nfcScreen.removePreference(findPreference("nfc_test_diagnostic"));
                                    }
                                } catch (NullPointerException e) {
                                    //
                                }
                        } else {
                            safeRemovePreference(collectionCategory, nfcSettings, "xdrip_plus_nfc_settings");
                            NFCReaderX.handleHomeScreenScanPreference(xdrip.getAppContext(), false);
                        }

                        if (!DexCollectionType.hasWifi()) {
                            String receiversIpAddresses;
                            receiversIpAddresses = AllPrefsFragment.this.prefs.getString("wifi_recievers_addresses", "");
                            if (receiversIpAddresses == null || receiversIpAddresses.trim().equals("")) {
                                safeRemovePreference(collectionCategory, wifiRecievers, "wifi_recievers_addresses");
                            } else {
                                safeAddPreference(collectionCategory, wifiRecievers, "wifi_recievers_addresses");
                            }
                        } else {
                            safeAddPreference(collectionCategory, wifiRecievers, "wifi_recievers_addresses");
                        }

                        if ((collectionType != DexCollectionType.DexbridgeWixel)
                                && (collectionType != DexCollectionType.WifiDexBridgeWixel)) {
                            safeRemovePreference(collectionCategory, transmitterId, "dex_txid");
                        } else {
                            safeAddPreference(collectionCategory, transmitterId, "dex_txid");
                        }

                        if (collectionType == DexCollectionType.DexcomG5) {
                            safeAddPreference(collectionCategory, transmitterId, "dex_txid");
                        }

                        if (collectionType == DexCollectionType.NSFollow) {
                            safeAddPreference(collectionCategory, nsFollowUrl, "nsfollow_url");
                            safeAddPreference(collectionCategory, nsFollowDownload, "nsfollow_download_treatments_screen");
                            safeAddPreference(collectionCategory, nsFollowLag, "nsfollow_lag");
                        }

                        if (collectionType == DexCollectionType.Follower) {
                            safeAddPreference(collectionCategory, xSyncFollowChime, "follower_chime");
                        }

                        String stringValue = newValue.toString();
                        if (preference instanceof ListPreference) {
                            ListPreference listPreference = (ListPreference) preference;
                            int index = listPreference.findIndexOfValue(stringValue);
                            preference.setSummary(
                                    index >= 0
                                            ? listPreference.getEntries()[index]
                                            : null);

                        } else if (preference instanceof RingtonePreference) {
                            if (TextUtils.isEmpty(stringValue)) {
                                preference.setSummary(R.string.pref_ringtone_silent);

                            } else {
                                Ringtone ringtone = RingtoneManager.getRingtone(
                                        preference.getContext(), Uri.parse(stringValue));
                                if (ringtone == null) {
                                    preference.setSummary(null);
                                } else {
                                    String name = ringtone.getTitle(preference.getContext());
                                    preference.setSummary(name);
                                }
                            }
                        } else {
                            preference.setSummary(stringValue);
                        }

                        if (preference.getKey().equals("dex_collection_method")) {
                            if (newValue.equals("Follower")) {
                                AllPrefsFragment.this.prefs.edit().putInt("bridge_battery", 0).apply();
                                AllPrefsFragment.this.prefs.edit().putInt("parakeet_battery", 0).apply();
                                if (AllPrefsFragment.this.prefs.getBoolean("plus_follow_master", false)) {
                                    AllPrefsFragment.this.prefs.edit().putBoolean("plus_follow_master", false).apply();
                                    JoH.static_toast(preference.getContext(), "Turning off xDrip+ Sync Master for Followers!", Toast.LENGTH_LONG);
                                }
                            }
                        }
                        CollectionServiceStarter.restartCollectionServiceBackground();

                        Inevitable.task("refresh-prefs", 100, new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    JoH.runOnUiThread(new Runnable() {
                                        @Override
                                        public void run() {
                                            if (parent != null) parent.refreshFragments();
                                        }
                                    });
                                } catch (Exception e) {
                                    Log.e(TAG, "Got exception refreshing fragments: " + e);
                                }
                            }
                        });
                        return true;
                    }
                });
            }

            removeLegacyPreferences();
            jumpToScreen(jumpTo);
        }

        private void removeLegacyPreferences() {
            // no-op
        }

        public static void checkReadPermission(final Activity activity) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (xdrip.getAppContext().checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {

                    ActivityCompat.requestPermissions(activity,
                            new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                            Constants.GET_PHONE_READ_PERMISSION);
                }
            }
        }

        private void showSearchFragment() {
            if (searchConfiguration == null) {
                try {
                    searchConfiguration = new SearchConfiguration(getActivity());
                    searchConfiguration.setBreadcrumbsEnabled(true);

                    searchConfiguration.getKeysList().addAll(getAllPreferenceKeys(this.getPreferenceScreen()));
                    searchConfiguration.index(R.xml.pref_general);
                    searchConfiguration.index(R.xml.pref_notifications);
                    searchConfiguration.index(R.xml.pref_data_source);
                    searchConfiguration.index(R.xml.pref_data_sync);
                    searchConfiguration.index(R.xml.pref_advanced_settings);
                    searchConfiguration.index(R.xml.xdrip_plus_prefs);
                } catch (NullPointerException e) {
                    Log.e(TAG, "Cannot find searchPreference item: " + e);
                }
            }
            searchConfiguration.showSearchFragment();

        }

        private void set_nfc_expiry_change_listeners() {
            if (nfc_expiry_days == null) {
                // NOTE: Some builds omit the NFC settings; guard against crashes.
                Log.wtf(TAG, "Missing preference nfc_expiry_days");
                return;
            }
            nfc_expiry_days.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    preference.getEditor().putString("nfc_expiry_days", (String) newValue).apply();
                    update_nfc_expiry_preferences(null);
                    return true;
                }
            });
            final Preference nfc_show_age = findPreference("nfc_show_age");
            if (nfc_show_age == null) {
                Log.wtf(TAG, "Missing preference nfc_show_age");
                return;
            }
            nfc_show_age.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    update_nfc_expiry_preferences((Boolean) newValue);
                    return true;
                }
            });
        }

        private void update_nfc_expiry_preferences(Boolean show_age) {
            try {
                final PreferenceScreen nfcScreen = (PreferenceScreen) findPreference("xdrip_plus_nfc_settings");
                final String nfc_expiry_days_string = AllPrefsFragment.this.prefs.getString("nfc_expiry_days", "14.5");

                final CheckBoxPreference nfc_show_age = (CheckBoxPreference) findPreference("nfc_show_age");
                nfc_show_age.setSummaryOff("Show the sensor expiry time based on " + nfc_expiry_days_string + " days");
                if (show_age == null) show_age = nfc_show_age.isChecked();
                if (show_age) {
                    nfcScreen.removePreference(nfc_expiry_days);
                } else {
                    nfc_expiry_days.setOrder(3);
                    nfcScreen.addPreference(nfc_expiry_days);
                }
            } catch (NullPointerException e) {
                //
            }
        }

        private void bindWidgetUpdater() {
            findPreference("widget_range_lines").setOnPreferenceChangeListener(new WidgetListener());
            findPreference("extra_status_line").setOnPreferenceChangeListener(new WidgetListener());
            findPreference("widget_status_line").setOnPreferenceChangeListener(new WidgetListener());
            findPreference("status_line_calibration_long").setOnPreferenceChangeListener(new WidgetListener());
            findPreference("status_line_calibration_short").setOnPreferenceChangeListener(new WidgetListener());
            findPreference("status_line_avg").setOnPreferenceChangeListener(new WidgetListener());
            findPreference("status_line_a1c_dcct").setOnPreferenceChangeListener(new WidgetListener());
            findPreference("status_line_a1c_ifcc").setOnPreferenceChangeListener(new WidgetListener());
            findPreference("status_line_in").setOnPreferenceChangeListener(new WidgetListener());
            findPreference("status_line_high").setOnPreferenceChangeListener(new WidgetListener());
            findPreference("status_line_low").setOnPreferenceChangeListener(new WidgetListener());
            findPreference("extra_status_line").setOnPreferenceChangeListener(new WidgetListener());
            findPreference("status_line_capture_percentage").setOnPreferenceChangeListener(new WidgetListener());
            findPreference("status_line_realtime_capture_percentage").setOnPreferenceChangeListener(new WidgetListener());
            findPreference("extra_status_stats_24h").setOnPreferenceChangeListener(new WidgetListener());

        }

        private void update_force_english_title(String param) {
            // NOTE: Some builds might not include language forcing preferences.
            // Guard everything because this gets called during onCreate.
            if (force_english == null || locale_choice == null) {
                Log.wtf(TAG, "Cannot update force language title (missing force_english/forced_language)");
                return;
            }

            try {
                String word;
                if (param.length() == 0) {
                    word = locale_choice.getEntry().toString();
                } else {
                    try {
                        word = (locale_choice.getEntries()[locale_choice.findIndexOfValue(param)]).toString();
                    } catch (Exception e) {
                        word = "Unknown";
                    }
                }
                force_english.setTitle("Force " + word + " Text");
            } catch (NullPointerException e) {
                Log.e(TAG, "Nullpointer in update_force_english_title: " + e);
            }
        }

        private static void recursive_notify_all_preference_screens(PreferenceGroup preferenceGroup) {
            if (preferenceGroup instanceof PreferenceScreen) {
                ((BaseAdapter) ((PreferenceScreen) preferenceGroup).getRootAdapter()).notifyDataSetChanged();
            } else {
                for (int index = 0; index < preferenceGroup.getPreferenceCount(); index++) {
                    final Preference pref = preferenceGroup.getPreference(index);
                    if (pref instanceof PreferenceGroup) {
                        recursive_notify_all_preference_screens((PreferenceGroup) pref);
                    }
                }
            }
        }

        private void setupBarcodeConfigScanner() {
            final Preference pref = findPreference("auto_configure");
            if (pref == null) {
                // NOTE: Some builds / translations may ship a reduced preferences XML.
                // Guard against NPE so the entire Preferences activity doesn't crash.
                Log.wtf(TAG, "Missing preference auto_configure (barcode config scanner)");
                return;
            }
            pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new AndroidBarcode(getActivity()).scan();
                    return true;
                }
            });
        }

        private void setupBarcodeShareScanner() {
            final Preference pref = findPreference("scan_share2_barcode");
            if (pref == null) {
                // NOTE: Some builds / translations may ship a reduced preferences XML.
                // Guard against NPE so the entire Preferences activity doesn't crash.
                Log.wtf(TAG, "Missing preference scan_share2_barcode (barcode share scanner)");
                return;
            }
            pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new AndroidBarcode(getActivity()).scan();
                    return true;
                }
            });
        }

        private void setupQrFromFile() {
            final Preference pref = findPreference("qr_code_from_file");
            if (pref == null) {
                // NOTE: Some builds / translations may ship a reduced preferences XML.
                // Guard against NPE so the entire Preferences activity doesn't crash.
                Log.wtf(TAG, "Missing preference qr_code_from_file (QR from file)");
                return;
            }
            pref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {
                    new QrCodeFromFile(getActivity()).scanFile();
                    return true;
                }
            });
        }

        private void refresh_extra_items() {
            try {
                if (this.prefs == null) return;
                if (!this.prefs.getBoolean("plus_extra_features", false)) {
                    // hidden

                } else {
                    // shown
                }
            } catch (Exception e) {
                Log.e(TAG, "Got exception in refresh extra: " + e.toString());
            }
        }

        public void jumpToScreen(final String screenKey) {
            if (screenKey == null) return;
            UserError.Log.d(TAG, "jump to screen: " + screenKey);
            PreferenceScreen subPreferenceScreen = (PreferenceScreen) findPreference(screenKey);
            final AllPrefsFragment fragment = this;
            JoH.runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    clickScreen(fragment, subPreferenceScreen);
                }
            });
        }

        private static void clickScreen(final PreferenceFragment fragment, final PreferenceScreen screen) {
            if (screen == null) return;
            if (fragment.getPreferenceScreen() != screen) {
                try {
                    final Method method = screen.getClass().getDeclaredMethod("onClick");
                    method.setAccessible(true);
                    method.invoke(screen);
                } catch (Exception e) {
                    android.util.Log.e(TAG, "" + e);
                }
            } else {
                android.util.Log.d(TAG, "Already on that screen");
            }
        }

        private void bindTTSListener() {
            findPreference("bg_to_speech").setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference, Object newValue) {
                    if ((Boolean) newValue) {
                        prefs.edit().putBoolean("bg_to_speech", true).commit();
                        final AlertDialog.Builder alertDialog = new AlertDialog.Builder(getActivity());
                        alertDialog.setTitle(R.string.install_text_to_speech_data_question);
                        alertDialog.setMessage(getString(R.string.install_text_to_speech_data_question) + "\n" + getString(R.string.after_installation_of_languages_you_might_have_to));
                        alertDialog.setCancelable(true);
                        alertDialog.setPositiveButton(R.string.ok, (dialog, which) -> SpeechUtil.installTTSData(getActivity()));
                        alertDialog.setNegativeButton(R.string.no, null);
                        final AlertDialog alert = alertDialog.create();
                        alert.show();
                        try {
                            BgToSpeech.testSpeech();
                        } catch (Exception e) {
                            Log.e(TAG, "Got exception with TTS: " + e);
                        }
                    } else {
                        BgToSpeech.tearDownTTS();
                    }
                    return true;
                }
            });

            findPreference("speech_speed").setOnPreferenceChangeListener((preference, newValue) ->
                    {
                        prefs.edit().putInt("speech_speed", (Integer) newValue).commit();
                        try {
                            BgToSpeech.testSpeech();
                        } catch (Exception e) {
                            Log.e(TAG, "Got exception with TTS: " + e);
                        }
                        return true;
                    }
            );
            findPreference("speech_pitch").setOnPreferenceChangeListener((preference, newValue) ->
                    {
                        prefs.edit().putInt("speech_pitch", (Integer) newValue).commit();
                        try {
                            BgToSpeech.testSpeech();
                        } catch (Exception e) {
                            Log.e(TAG, "Got exception with TTS: " + e);
                        }
                        return true;
                    }
            );
        }


        private static class WidgetListener implements Preference.OnPreferenceChangeListener {
            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                Context context = preference.getContext();
                if (AppWidgetManager.getInstance(context).getAppWidgetIds(new ComponentName(context, xDripWidget.class)).length > 0) {
                    context.startService(new Intent(context, WidgetUpdateService.class));
                }
                return true;
            }
        }
    }

    public static void handleUnitsChange(Preference preference, Object newValue, AllPrefsFragment allPrefsFragment) {
        try {
            SharedPreferences preferences;
            if (preference != null) {
                preferences = preference.getSharedPreferences();
            } else {
                preferences = PreferenceManager.getDefaultSharedPreferences(xdrip.getAppContext());
            }

            final Double highVal = Double.parseDouble(preferences.getString("highValue", "0"));
            final Double lowVal = Double.parseDouble(preferences.getString("lowValue", "0"));
            final Double default_insulin_sensitivity = Double.parseDouble(preferences.getString("profile_insulin_sensitivity_default", "54"));
            final Double default_target_glucose = Double.parseDouble(preferences.getString("plus_target_range", "100"));
            final Double persistent_high_Val = Double.parseDouble(preferences.getString("persistent_high_threshold", "0"));
            final Double forecast_low_Val = Double.parseDouble(preferences.getString("forecast_low_threshold", "0"));


            static_units = newValue.toString();
            if (newValue.toString().equals("mgdl")) {
                if (highVal < 36) {
                    ProfileEditor.convertData(Constants.MMOLL_TO_MGDL);
                    preferences.edit().putString("highValue", Long.toString(Math.round(highVal * Constants.MMOLL_TO_MGDL))).apply();
                    preferences.edit().putString("profile_insulin_sensitivity_default", Long.toString(Math.round(default_insulin_sensitivity * Constants.MMOLL_TO_MGDL))).apply();
                    preferences.edit().putString("plus_target_range", Long.toString(Math.round(default_target_glucose * Constants.MMOLL_TO_MGDL))).apply();
                    Profile.invalidateProfile();
                }
                if (persistent_high_Val < 36) {
                    ProfileEditor.convertData(Constants.MMOLL_TO_MGDL);
                    preferences.edit().putString("persistent_high_threshold", Long.toString(Math.round(persistent_high_Val * Constants.MMOLL_TO_MGDL))).apply();
                    Profile.invalidateProfile();
                }
                if (forecast_low_Val < 36) {
                    ProfileEditor.convertData(Constants.MMOLL_TO_MGDL);
                    preferences.edit().putString("forecast_low_threshold", Long.toString(Math.round(forecast_low_Val * Constants.MMOLL_TO_MGDL))).apply();
                    Profile.invalidateProfile();
                }
                if (lowVal < 36) {
                    ProfileEditor.convertData(Constants.MMOLL_TO_MGDL);
                    preferences.edit().putString("lowValue", Long.toString(Math.round(lowVal * Constants.MMOLL_TO_MGDL))).apply();
                    preferences.edit().putString("profile_insulin_sensitivity_default", Long.toString(Math.round(default_insulin_sensitivity * Constants.MMOLL_TO_MGDL))).apply();
                    preferences.edit().putString("plus_target_range", Long.toString(Math.round(default_target_glucose * Constants.MMOLL_TO_MGDL))).apply();
                    Profile.invalidateProfile();
                }

            } else {
                if (highVal > 35) {
                    ProfileEditor.convertData(Constants.MGDL_TO_MMOLL);
                    preferences.edit().putString("highValue", JoH.qs(highVal * Constants.MGDL_TO_MMOLL, 1)).apply();
                    preferences.edit().putString("profile_insulin_sensitivity_default", JoH.qs(default_insulin_sensitivity * Constants.MGDL_TO_MMOLL, 2)).apply();
                    preferences.edit().putString("plus_target_range", JoH.qs(default_target_glucose * Constants.MGDL_TO_MMOLL, 1)).apply();
                    Profile.invalidateProfile();
                }
                if (persistent_high_Val > 35) {
                    ProfileEditor.convertData(Constants.MGDL_TO_MMOLL);
                    preferences.edit().putString("persistent_high_threshold", JoH.qs(persistent_high_Val * Constants.MGDL_TO_MMOLL, 1)).apply();
                    Profile.invalidateProfile();
                }
                if (forecast_low_Val > 35) {
                    ProfileEditor.convertData(Constants.MGDL_TO_MMOLL);
                    preferences.edit().putString("forecast_low_threshold", JoH.qs(forecast_low_Val * Constants.MGDL_TO_MMOLL, 1)).apply();
                    Profile.invalidateProfile();
                }
                if (lowVal > 35) {
                    ProfileEditor.convertData(Constants.MGDL_TO_MMOLL);
                    preferences.edit().putString("lowValue", JoH.qs(lowVal * Constants.MGDL_TO_MMOLL, 1)).apply();
                    preferences.edit().putString("profile_insulin_sensitivity_default", JoH.qs(default_insulin_sensitivity * Constants.MGDL_TO_MMOLL, 2)).apply();
                    preferences.edit().putString("plus_target_range", JoH.qs(default_target_glucose * Constants.MGDL_TO_MMOLL, 1)).apply();
                    Profile.invalidateProfile();
                }
            }
            if (preference != null) preference.setSummary(newValue.toString());
            if (allPrefsFragment != null) {
                allPrefsFragment.setSummary("highValue");
                allPrefsFragment.setSummary("lowValue");
                allPrefsFragment.setSummary("persistent_high_threshold");
                allPrefsFragment.setSummary("forecast_low_threshold");
            }
            if (profile_insulin_sensitivity_default != null) {
                Log.d(TAG, "refreshing profile insulin sensitivity default display");
                profile_insulin_sensitivity_default.setTitle(format_insulin_sensitivity(profile_insulin_sensitivity_default.getTitle().toString(), ProfileEditor.minMaxSens(ProfileEditor.loadData(false))));

            }
            Profile.reloadPreferences(preferences);

        } catch (Exception e) {
            Log.e(TAG, "Got excepting processing high/low value preferences: " + e.toString());
        }
    }

    public static boolean isNumeric(String str) {
        try {
            Double.parseDouble(str);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }
}
