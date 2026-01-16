package com.eveningoutpost.dexdrip.utils.bt;

import com.eveningoutpost.dexdrip.models.UserError;

import io.reactivex.disposables.Disposable;
import io.reactivex.plugins.RxJavaPlugins;

/**
 *
 * jamorham
 *
 * wrapper class to smooth rxandroidble migration
 */

public class Subscription implements Disposable {

    private final Disposable disposable;
    private volatile boolean unsubscribed;

    public Subscription(Disposable disposable) {
        this.disposable = disposable;
    }

    public boolean isUnsubscribed() {
        return unsubscribed;
    }

    public synchronized void unsubscribe() {
        dispose();
        unsubscribed = true;
    }


    @Override
    public void dispose() {
        disposable.dispose();
    }

    @Override
    public boolean isDisposed() {
        return disposable.isDisposed();
    }


    public static void addErrorHandler(final String TAG) {
        RxJavaPlugins.setErrorHandler(e -> UserError.Log.d(TAG, "RxJavaError: " + e.getMessage()));
    }

}
