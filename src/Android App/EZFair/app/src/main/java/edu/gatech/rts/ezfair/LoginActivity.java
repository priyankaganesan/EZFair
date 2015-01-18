package edu.gatech.rts.ezfair;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class LoginActivity extends Activity {

    private EditText username = null;
    private EditText password = null;
    private Button login;
    Intent switchIntent;
    SharedPreferences sharedpreferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = (EditText) findViewById(R.id.editText1);
        password = (EditText) findViewById(R.id.editText2);
        login = (Button) findViewById(R.id.button1);
      //  flag = 0;
    }

    // check network connection
    public boolean isConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(this.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.isConnected())
            return true;
        else
            return false;
    }

    public void login(View v) {
     //   Log.d("Started", "Inside method");
        switchIntent = new Intent(getApplicationContext(), OtherQueueActivity.class);
        boolean conn = isConnected();

        final EditText uname = (EditText) findViewById(R.id.editText1);
        final EditText pwd = (EditText) findViewById(R.id.editText2);
        final TextView msg = (TextView) findViewById(R.id.loginResult);
        String username = uname.getText().toString();
        String password = pwd.getText().toString();
       // Log.d("Set 2", "Obtained text views");
        new SignInActivity(this, msg).execute(username, password);
    }

    public static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String line = "";
        String result = "";
        while ((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();
        return result;
    }

    class SignInActivity extends AsyncTask<String, Void, String> {

        TextView result;
        Context context;
        String username;

        public SignInActivity(Context cnt, TextView statusField) {
            context = cnt;
            result = statusField;
            sharedpreferences = getSharedPreferences("Login_Details", Context.MODE_PRIVATE);
        }

        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... arg0) {
            try {
                username = (String) arg0[0];
                String password = (String) arg0[1];
                String link = "http://10.0.0.6/ezfair/android/login.php?username="
                        + username + "&password=" + password;
                URL url = new URL(link);
                HttpClient client = new DefaultHttpClient();
                HttpGet request = new HttpGet();
                request.setURI(new URI(link));
                HttpResponse response = client.execute(request);
                InputStream in = response.getEntity().getContent();
                String sb = LoginActivity.convertInputStreamToString(in);
                in.close();
                return sb;
            } catch (Exception e) {
                Log.d("MyApp","Exception: "+e.getMessage());
                return new String("Exception: " + e.getMessage());
            }
        }

        @Override
        protected void onPostExecute(String res) {
           Log.d("MyApp","Result : " + res);
            if (res.equals("1")) {
                this.result.setText("Login Successful");
                Editor editor = sharedpreferences.edit();
                editor.putString("username", username );
                editor.putBoolean("isLoggedIn",true);
                editor.commit();
                startActivity(switchIntent);
                finish();
            } else {
                result.setText("Login Failed. Please try again.");
            }
        }
    }
}