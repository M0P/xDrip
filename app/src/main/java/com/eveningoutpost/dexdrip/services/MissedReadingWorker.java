package com.eveningoutpost.dexdrip.services;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

public class MissedReadingWorker extends Worker {

    public MissedReadingWorker(@NonNull Context context, @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {
        try {
            MissedReadingService.runOnce(getApplicationContext());
            return Result.success();
        } catch (Throwable t) {
            // Use retry so transient failures (BT stack, DB lock, etc.) can recover.
            return Result.retry();
        }
    }
}
