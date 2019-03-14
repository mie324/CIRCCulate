package com.example.circculate.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class MyFirebaseMessagingService extends Service {
    public MyFirebaseMessagingService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        return null;
//        throw new UnsupportedOperationException("Not yet implemented");
    }


}
