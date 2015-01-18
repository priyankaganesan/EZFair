package edu.gatech.rts.ezfair;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

public class JoinPhysicalQueueService extends IntentService {
    private Handler mHandler;
    public JoinPhysicalQueueService()
    {
        super(null);
    }
    public JoinPhysicalQueueService(String name) {
        super(null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mHandler = new Handler();
       // Toast.makeText(getApplicationContext(),"Position in Queue Confirmed", Toast.LENGTH_SHORT).show();
        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        notificationManager.cancel(10090);
        return super.onStartCommand(intent, flags, startId);
    }


    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private class ToastRunnable implements Runnable {
        String mText;

        public ToastRunnable(String text) {
            mText = text;
        }

        @Override
        public void run(){
            Toast.makeText(getApplicationContext(), mText, Toast.LENGTH_LONG).show();
        }
    }


    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("MyApp", "Inside HandleIntent of Join");
        mHandler.post(new ToastRunnable("Your position in the queue is confirmed."));
    }
}
