package edu.gatech.rts.ezfair;

import android.app.ListActivity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import edu.gatech.rts.ezfair.item.JoinedEntryAdapter;
import edu.gatech.rts.ezfair.item.JoinedEntryItem;

import org.apache.http.util.EntityUtils;

import static android.widget.AbsListView.*;

public class JoinedQueueActivity extends ListActivity {
    /** Called when the activity is first created. */

    ArrayList<JoinedEntryItem> joinedItems = new ArrayList<JoinedEntryItem>();
 //   ArrayList<OtherEntryItem> otherItems = new ArrayList<OtherEntryItem>();
    String username;
    ArrayList<HashMap<String, String>> joinedCompanyList;
  //  ArrayList<HashMap<String, String>> otherCompanyList;
    SharedPreferences sharedpreferences;
    int threshold;
    ArrayList<String> notificationArray = new ArrayList<String>();


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedpreferences = getSharedPreferences("Login_Details", Context.MODE_PRIVATE);
        if(sharedpreferences.contains("username")) {
            username = sharedpreferences.getString("username", null);
            Log.d("MyApp","**********Username: " + username);

            new GetCompanies(this).execute();
            setTitle("Joined Queues");


            final ListView listView = getListView();
            listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
            listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {

                @Override
                public void onItemCheckedStateChanged(ActionMode mode, int position,
                                                      long id, boolean checked) {
                    // Here you can do something when items are selected/de-selected,
                    // such as update the title in the CAB

                    // Capture total checked items
                    final int checkedCount = listView.getCheckedItemCount();
                    // Set the CAB title according to total checked items
                    mode.setTitle(checkedCount + " Selected");
                    Log.d("MyApp", "Detecting number of items:"+checkedCount);

                    // Calls toggleSelection method from ListViewAdapter Class
                    //adapter.toggleSelection(position);
                }

                @Override
                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    // Respond to clicks on the actions in the CAB
                    switch (item.getItemId()) {
                        case R.id.menu_delete:
                            deleteSelectedItems(listView);
                            //Toast.makeText(JoinedQueueActivity.this, "Delete is Selected", Toast.LENGTH_SHORT).show();
                            //update student_queue and company_queue table


                            mode.finish(); // Action picked, so close the CAB
                            return true;
                        default:
                            return false;
                    }
                }

                @Override
                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    // Inflate the menu for the CAB
                    MenuInflater inflater = mode.getMenuInflater();
                    inflater.inflate(R.menu.joined_queue_context, menu);
                    return true;
                }

                @Override
                public void onDestroyActionMode(ActionMode mode) {
                    // Here you can make any necessary updates to the activity when
                    // the CAB is removed. By default, selected items are deselected/unchecked.
                }

                @Override
                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    // Here you can perform updates to the CAB due to
                    // an invalidate() request
                    return false;
                }

            });
        }
    }

    private class GetCompanies extends AsyncTask<Void, Void, Void> {

        Context context;
        public GetCompanies(Context cnt) {
            context = cnt;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(Void... arg0) {
            try {
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet("http://10.0.0.6/ezfair/android/get_company_queue.php?username=" + username);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                HttpEntity httpEntity = httpResponse.getEntity();
                String jsonStr = EntityUtils.toString(httpEntity);
                joinedCompanyList = new ArrayList<HashMap<String, String>>();
             //   otherCompanyList = new ArrayList<HashMap<String, String>>();

                Log.d("MyApp","Response JSON: " + jsonStr);

                if (jsonStr != null) {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    if(jsonObj != null) {

                        JSONArray joined_queue = jsonObj.getJSONArray("joined_queue");
                        Log.d("MyApp","***********Joined Queue");

                        if(joined_queue != null) {
                            Log.d("MyApp","***********Inside if " + joined_queue.length());
                            for (int i = 0; i < joined_queue.length(); i++) {
                                JSONObject c = joined_queue.getJSONObject(i);
                                Log.d("MyApp","***********Inside for");
                                if (c != null) {

                                    String id = c.getString("company_id");
                                    String name = c.getString("company_name");

                                    String my_token = c.getString("token");
                                    int count_ahead = c.getInt("count_ahead");
                                    int num_recruiters = c.getInt("num_recruiter");
                                    threshold = count_ahead / num_recruiters;
                                    if(threshold <= 3 && Integer.parseInt(my_token) > 0) {
                                        if(!notificationArray.contains(id)) {
                                              notificationArray.add(id);
                                            Log.d("MyApp","----company_id:" + id);
                                            Log.d("MyApp","----username:" + username);
                                              createNotification(name, id);
                                          }
                                    }
                                    Log.d("myApp","Threshold: " + threshold);
                                    System.out.println("***********in for loop");
                                    // hashmap for single company
                                    HashMap<String, String> company = new HashMap<String, String>();

                                    company.put("company_id", id);
                                    company.put("company_name", name);
                                    company.put("my_token", my_token);
                                    company.put("count_ahead", count_ahead+"");

                                    joinedCompanyList.add(company);
                                } else {
                                    Log.d("MyApp","*******JSONObject c is null");
                                }
                            }
                        }
                        else {
                            Log.d("MyApp","*******JSONArray joined queue is null");
                        }
                    }
                } else {
                    Log.d("MyApp","************Couldn't get any data from the url");
                }
            } catch (Exception e) {
               Log.d("MyApp","************Exception : " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
         //   joinedItems.add(new SectionItem("Joined Queues"));
            for(int i=0; i< joinedCompanyList.size(); i++) {

                joinedItems.add(new JoinedEntryItem(joinedCompanyList.get(i).get("company_name"), joinedCompanyList.get(i).get("my_token"),
                        joinedCompanyList.get(i).get("count_ahead")));
            }
          //  otherItems.add(new SectionItem("Other Companies"));
//            for(int i=0; i< otherCompanyList.size(); i++) {
//
//                otherItems.add(new OtherEntryItem(otherCompanyList.get(i).get("company_name"), otherCompanyList.get(i).get("count_ahead")));
//            }

            final JoinedEntryAdapter adapter = new JoinedEntryAdapter(context, joinedItems);

            setListAdapter(adapter);
        }
    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if(!joinedItems.get(position).isSection()){
            JoinedEntryItem item = (JoinedEntryItem)joinedItems.get(position);
           // Toast.makeText(this, "You clicked " + item.company_name , Toast.LENGTH_SHORT).show();
        }
        super.onListItemClick(l, v, position, id);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.joined_activity_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.logout: {
                // Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
                SharedPreferences.Editor editor = sharedpreferences.edit();
                editor.clear();
                editor.commit();
                Intent newIntent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(newIntent);
                finish();
                return true;
            }
            case R.id.other_companies: {
                Intent newIntent = new Intent(getApplicationContext(), OtherQueueActivity.class);
                startActivity(newIntent);
                finish();
                return true;
            }
            case R.id.refresh: {
//                Intent newIntent = new Intent(getApplicationContext(), JoinedQueueActivity.class);
//                startActivity(newIntent);
//                finish();
                //new GetCompanies(this).execute();
                recreate();
              //  createNotification("Microsoft","1");
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void deleteSelectedItems(ListView listView)
    {
        Log.d("MyApp","Detected CAB delete button click");
        SparseBooleanArray checked = listView.getCheckedItemPositions();
        Log.d("MyApp", "checked.size="+checked.size()+"");
        for (int i=0;i<checked.size();i++)
        {
            Log.d("MyApp", "checked.valueat="+checked.valueAt(i)+"");
            Log.d("MyApp", "checked.keyat="+checked.keyAt(i)+"");
            Log.d("MyApp", "listView.getItemAtPosition(checked.keyAt(i))="+listView.getItemAtPosition(checked.keyAt(i))+"");
            if(checked.valueAt(i))
            {
                JoinedEntryItem company=(JoinedEntryItem)listView.getItemAtPosition(checked.keyAt(i));
                Log.d("MyApp", "company.company_name="+company.company_name);
                String company_name=company.company_name;
                Iterator<HashMap<String,String>> it = joinedCompanyList.iterator();
                while(it.hasNext())
                {
                    HashMap<String,String> obj = it.next();
                    if(obj.containsValue(company_name))
                    {
                        String company_id=obj.get("company_id");
                        Log.d("MyApp", "com="+company_id);
                        new LeaveCompanies(this).execute(company_id);
                        recreate();
                    }
                }
            }
        }
    }
    private class LeaveCompanies extends AsyncTask<String, Void, Void> {

        Context context;
        public LeaveCompanies(Context cnt) {
            context = cnt;
        }
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

        }

        @Override
        protected Void doInBackground(String... arg0) {
            try {
                String company_id=arg0[0];
                Log.d("MyApp","company_id inside Async task:"+company_id);
                DefaultHttpClient httpClient = new DefaultHttpClient();
                HttpGet httpGet = new HttpGet("http://10.0.0.6/ezfair/android/leave_queue.php?username=" + username+"&company_id="+company_id);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                //HttpEntity httpEntity = httpResponse.getEntity();
                //String jsonStr = EntityUtils.toString(httpEntity);

                //Log.d("MyApp","Response JSON: " + jsonStr);

            } catch (Exception e) {
                Log.d("MyApp","**Exception : " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }

    public void createNotification(String companyName, String company_id) {
        // Prepare intent which is triggered if the
        // notification is selected
        Intent join_intent = new Intent(this, JoinPhysicalQueueService.class);
        PendingIntent join_pIntent = PendingIntent.getService(this, 0, join_intent, 0);

        Intent leave_intent = new Intent(this, LeavePhysicalQueueService.class);
      //  Bundle nullArgs = null;
       // leave_intent.putExtras(nullArgs);
        leave_intent.removeExtra("company_id");
        leave_intent.removeExtra("username");
        leave_intent.putExtra("company_id",company_id);
        leave_intent.putExtra("username",username);
        Log.d("MyApp","&&&&company_id: " + company_id);
        Log.d("MyApp","&&&&username: " + username);
        leave_intent.setAction("myString"+System.currentTimeMillis());
        leave_intent.setFlags(PendingIntent.FLAG_UPDATE_CURRENT);
        PendingIntent leave_pIntent = PendingIntent.getService(this, (int)System.currentTimeMillis(), leave_intent, 0);



//        SharedPreferences sp = getSharedPreferences("notify_company_details", Context.MODE_PRIVATE);
//        SharedPreferences.Editor editor = sp.edit();
//        editor.putString("company_id", company_id );
//        editor.putString("username", username );
//        editor.commit();

        // Build notification
        Notification n = new Notification.Builder(this)
                .setContentTitle("Please stand in line")
                .setContentText("Your turn for " + companyName + " is approaching")
                .setSmallIcon(R.drawable.ic_launcher)
                .setAutoCancel(true)
                .setDefaults(Notification.DEFAULT_SOUND | Notification.DEFAULT_SOUND | Notification.DEFAULT_LIGHTS)
                .addAction(R.drawable.ic_stat_join, "Join Queue", join_pIntent)
                .addAction(R.drawable.ic_stat_cancel, "Leave Queue", leave_pIntent)
                .setContentIntent(join_pIntent)
                .setContentIntent(leave_pIntent)
                .build();

        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        // Hide the notification after its selected

        notificationManager.notify(10090, n);
      //  recreate();

    }

//    public void doTimerTask(){
//
//        mTimerTask = new TimerTask() {
//            public void run() {
//                handler.post(new Runnable() {
//                    public void run() {
//                        // update view
//                        recreate();
//                        Log.d("MyApp", "TimerTask run");
//                    }
//                });
//            }};
//
//        // public void schedule (TimerTask task, long delay (milliseconds) before first execution, long period (milliseconds) between consecutive executions )
//        timer.schedule(mTimerTask, 500, 5*60*1000);  //
//
//    }
}