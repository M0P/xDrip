package com.eveningoutpost.dexdrip.utils;

import static com.eveningoutpost.dexdrip.models.JoH.bytesToHex;
import static com.eveningoutpost.dexdrip.models.JoH.static_toast_long;

import android.graphics.Bitmap;
import android.graphics.Color;
import android.util.Base64;
import android.util.Log;

import com.eveningoutpost.dexdrip.models.JoH;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.Map;

/**
 * JamOrHam
 * <p>
 * Utils for serializing and deserializing QR codes
 */

public class QRcodeUtils {

    private static final String TAG = "qrcode utils";
    public static final String qrmarker = "xdpref:";
    public static final String qrmarker2 = "xdp2:";

    public static boolean hasDecoderMarker(final String scanresults) {
        if (scanresults == null) return false;
        return scanresults.startsWith(qrmarker) || scanresults.startsWith(qrmarker2);
    }

    public static Map<String, String> decodeString(String data) {
        Log.d(TAG, "jamorham qr decode: " + data);
        try {
            if (data.startsWith(qrmarker)) {
                data = data.substring(qrmarker.length());
                Log.d(TAG, "String to uncompress: " + data);
                data = JoH.uncompressString(data);
                Log.d(TAG, "Json after decompression: " + data);
                return new Gson().fromJson(data, new TypeToken<HashMap<String, String>>() {
                }.getType());

            } else if (data.startsWith(qrmarker2)) {
                data = data.substring(qrmarker2.length());
                Log.d(TAG, "String to uncompress: " + data + " len: " + data.length());
                final byte[] bytes = JoH.decompressBytesToBytes(Base64.decode(data, Base64.NO_PADDING | Base64.NO_WRAP));
                Log.d(TAG, "Json after decompression: " + bytes.length);
                return deserializeQr2(bytes);

            } else {
                Log.e(TAG, "No qrmarker on qrcode");
                return null;
            }
        } catch (Exception e) {
            Log.e(TAG, "Got exception during decodingString: " + e.toString());
            return null;
        }

    }

    public static byte[] serializeBinaryPrefsMap(final Map<String, byte[]> binaryPrefsMap) {
        int size = 2;
        short count = 0;
        for (final Map.Entry<String, byte[]> item : binaryPrefsMap.entrySet()) {
            size += item.getKey().getBytes(StandardCharsets.UTF_8).length;
            size += item.getValue().length;
            size += 4;
            count++;
        }
        final ByteBuffer bb = ByteBuffer.allocate(size);
        bb.putShort(count);
        for (final Map.Entry<String, byte[]> item : binaryPrefsMap.entrySet()) {
            final byte[] key = item.getKey().getBytes(StandardCharsets.UTF_8);
            final byte[] value = item.getValue();
            bb.putShort((short) key.length);
            bb.put(key);
            bb.putShort((short) value.length);
            bb.put(value);
        }
        return bb.array();
    }


    public static Map<String, String> deserializeQr2(final byte[] bytes) {
        if (bytes == null) return null;
        final ByteBuffer bb = ByteBuffer.wrap(bytes);
        final short count = bb.getShort();
        if (count < 1 || count > 100) {
            final String msg = "Count invalid on QR Code " + count;
            Log.e(TAG, msg);
            static_toast_long(msg);
            return null;
        }
        Log.d(TAG, "QR code element count: " + count);
        final Map<String, String> reply = new HashMap<>();
        try {
            for (int i = 0; i < count; i++) {
                final short keylen = bb.getShort();
                final byte[] keyBytes = new byte[keylen];
                bb.get(keyBytes);
                final short valuelen = bb.getShort();
                final byte[] valueBytes = new byte[valuelen];
                bb.get(valueBytes);
                String keyString = new String(keyBytes, StandardCharsets.UTF_8);
                boolean isBinary = false;
                if (keyString.startsWith("b__")) {
                    keyString = keyString.substring(3);
                    isBinary = true;
                }
                Log.d(TAG, "KEY: " + keyString + " byte length: " + valuelen);
                if (isBinary) {
                    reply.put(keyString, bytesToHex(valueBytes));
                } else {
                    reply.put(keyString, new String(valueBytes, StandardCharsets.UTF_8));
                }
            }
            return reply;

        } catch (Exception e) {
            final String msg = "QR code decoding error: " + e;
            Log.e(TAG, msg);
            static_toast_long(msg);
        }
        return null;
    }

    public static Bitmap createQRCodeBitmap(final byte[] data, final int width, final int height, String prefix) throws WriterException {
        final String inputData = prefix + Base64.encodeToString(data, Base64.NO_WRAP | Base64.NO_PADDING);
        Log.d(TAG, "Input data length: " + inputData.length());
        Map<EncodeHintType, Object> hints = new EnumMap<>(EncodeHintType.class);
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        final MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        final BitMatrix bitMatrix = multiFormatWriter.encode(inputData, BarcodeFormat.QR_CODE, width, height, hints);
        final Bitmap bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565);
        for (int i = 0; i < width; i++) {
            for (int j = 0; j < height; j++) {
                bitmap.setPixel(i, j, bitMatrix.get(i, j) ? Color.BLACK : Color.WHITE);
            }
        }
        return bitmap;
    }

}
