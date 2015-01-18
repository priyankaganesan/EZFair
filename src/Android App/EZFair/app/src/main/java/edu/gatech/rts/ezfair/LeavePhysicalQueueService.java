package edu.gatech.rts.ezfair;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;

import static android.content.Intent.getIntent;

public class LeavePhysicalQueueService extends IntentService {
    private Handler mHandler;
    public LeavePhysicalQueueService()
    {
        super(null);
    }
    public LeavePhysicalQueueService(String name) {
        super(null);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // Toast.makeText(getApplicationContext(),"Position in Queue Confirmed", Toast.LENGTH_SHORT).show();
        String company_id = intent.getExtras().getString("company_id");
        String username = intent.getExtras().getString("username");
        Log.d("MyApp", "***company_id inside leave physical queue service:" + company_id);
        Log.d("MyApp", "***username inside leave physical queue service:" + username);
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
        Log.d("MyApp","Inside Handle of LeavePhyQ");
        String company_id = intent.getStringExtra("company_id");
        String username = intent.getStringExtra("username");
        Log.d("MyApp", "company_id inside leave physical queue service:" + company_id);
        Log.d("MyApp", "username inside leave physical queue service:" + username);
        DefaultHttpClient httpClient = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet("http://10.0.0.6/ezfair/android/leave_queue.php?username="+ username+"&company_id="+company_id);
        try {
            HttpResponse httpResponse = httpClient.execute(httpGet);
        } catch (IOException e) {
            Log.d("MyApp", "Exception: " + e.getMessage());
            e.printStackTrace();
        }
      //  mHandler.post(new ToastRunnable("You have left the queue."));
    }
}
