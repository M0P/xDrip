package com.eveningoutpost.dexdrip.healthconnect;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.health.connect.client.records.HeartRateRecord;
import androidx.health.connect.client.records.HeartRateRecord.Sample;
import androidx.health.connect.client.records.StepsRecord;

import com.eveningoutpost.dexdrip.models.HeartRate;
import com.eveningoutpost.dexdrip.models.JoH;
import com.eveningoutpost.dexdrip.models.StepCounter;
import com.eveningoutpost.dexdrip.models.UserError;

// jamorham

@RequiresApi(api = Build.VERSION_CODES.O)
public class ReadReplyProcessor {

    private static final String TAG = ReadReplyProcessor.class.getSimpleName();

    public static void process(final DataReply dataReply) {
        if (dataReply == null) {
            UserError.Log.d(TAG, "Null reply");
            return;
        }

        if (dataReply.stepsRecords != null) {
            for (final StepsRecord item : dataReply.stepsRecords) {
                StepCounter.createUniqueRecord(item.getStartTime().toEpochMilli() + ((item.getEndTime().toEpochMilli() - item.getStartTime().toEpochMilli()) / 2),
                        (int) item.getCount(),
                        true);
            }
        }

        if (dataReply.heartRateRecords != null) {
            for (final HeartRateRecord item : dataReply.heartRateRecords) {
                for (final Sample i : item.getSamples()) {
                    UserError.Log.d(TAG, "heart rate: " + JoH.dateTimeText(i.getTime().toEpochMilli()) + " bpm:" + i.getBeatsPerMinute());
                    HeartRate.create(i.getTime().toEpochMilli(), (int) i.getBeatsPerMinute(), 1);
                }
            }
        }
    }
}
