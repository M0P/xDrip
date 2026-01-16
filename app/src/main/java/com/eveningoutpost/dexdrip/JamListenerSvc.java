package com.eveningoutpost.dexdrip;

// jamorham

import android.content.Context;

//import com.google.firebase.messaging.FirebaseMessagingService;

public class JamListenerSvc {

    private boolean injectable;

    public boolean isInjectable() {
        return injectable;
    }

//    @Override
    protected void attachBaseContext(Context base) {
//      super.attachBaseContext(base);
    }

    public void setInjectable() {
        attachBaseContext(xdrip.getAppContext());
        injectable = true;
    }



}
