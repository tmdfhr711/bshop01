package example.android.laioh.bshop.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

import com.wizturn.sdk.central.CentralManager;

public class NotificationService extends Service {

    private CentralManager centralManager;

    public NotificationService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);



    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }
}
