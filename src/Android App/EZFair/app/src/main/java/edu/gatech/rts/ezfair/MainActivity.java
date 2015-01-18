package edu.gatech.rts.ezfair;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.os.Handler;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SharedPreferences preferences = getSharedPreferences("Login_Details", Context.MODE_PRIVATE);
                if (!preferences.getBoolean("isLoggedIn", false)) {
                    // user is not logged in redirect him to Login Activity
                    Intent newIntent = new Intent(getApplicationContext(), LoginActivity.class);
                    // Closing all the Activities
                    newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    // Add new Flag to start new Activity
                    newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    // Staring Login Activity
                    startActivity(newIntent);
                    finish();
                } else {
                    Intent newIntent = new Intent(getApplicationContext(), JoinedQueueActivity.class);
                    // Closing all the Activities
                    newIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

                    // Add new Flag to start new Activity
                    newIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                    // Staring Queue Activity
                    startActivity(newIntent);
                    finish();
                }
            }
        }, 2000);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
