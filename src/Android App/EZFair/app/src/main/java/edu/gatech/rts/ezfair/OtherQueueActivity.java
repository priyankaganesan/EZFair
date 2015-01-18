package edu.gatech.rts.ezfair;

import android.app.ListActivity;
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
import android.widget.AbsListView;
import android.widget.ListView;
import android.widget.Toast;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Timer;
import java.util.TimerTask;

import edu.gatech.rts.ezfair.item.OtherEntryAdapter;
import edu.gatech.rts.ezfair.item.OtherEntryItem;

public class OtherQueueActivity extends ListActivity {

    ArrayList<HashMap<String, String>> otherCompanyList = new ArrayList<HashMap<String, String>>();
    ArrayList<OtherEntryItem> otherItems = new ArrayList<OtherEntryItem>();
    String username;
    SharedPreferences sharedpreferences;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sharedpreferences = getSharedPreferences("Login_Details", Context.MODE_PRIVATE);
        if(sharedpreferences.contains("username")) {
            username = sharedpreferences.getString("username", null);
            Log.d("MyApp","**********Username: " + username);
            new GetCompanies(this).execute();
            setTitle("Other Companies");
        }

        final ListView listView = getListView();
        listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
        listView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {

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
                        joinSelectedItems(listView);
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
                inflater.inflate(R.menu.others_queue_context, menu);
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
                otherCompanyList = new ArrayList<HashMap<String, String>>();

                Log.d("MyApp", "Response JSON: " + jsonStr);

                if (jsonStr != null) {
                    JSONObject jsonObj = new JSONObject(jsonStr);

                    // Getting JSON Array node
                    if(jsonObj != null) {
                        JSONArray other_queue = jsonObj.getJSONArray("other_queue");
                        Log.d("MyApp","*******JSONArray other queue");
                        if(other_queue != null) {
                            Log.d("MyApp","*******other queue is not null" + other_queue.length());
                            for (int i = 0; i < other_queue.length(); i++) {
                                JSONObject c = other_queue.getJSONObject(i);

                                if(c !=  null) {
                                    String id = c.getString("company_id");
                                    String name = c.getString("company_name");
                                    String count_ahead = c.getString("count_ahead");

                                    // hashmap for single company
                                    HashMap<String, String> company1 = new HashMap<String, String>();

                                    company1.put("company_id", id);
                                    company1.put("company_name", name);
                                    company1.put("count_ahead", count_ahead);

                                    otherCompanyList.add(company1);
                                }
                                else
                                {
                                    Log.d("MyApp","JSONObject c 2 is  null");
                                }
                            }
                        }
                        else {
                            Log.d("MyApp","*******JSONArray other_queue is null");
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

            //  otherItems.add(new SectionItem("Other Companies"));
            for(int i=0; i< otherCompanyList.size(); i++) {

                otherItems.add(new OtherEntryItem(otherCompanyList.get(i).get("company_name"), otherCompanyList.get(i).get("count_ahead")));
            }

            final OtherEntryAdapter adapter = new OtherEntryAdapter(context, otherItems);

            setListAdapter(adapter);
        }
    }
    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        if(!otherItems.get(position).isSection()){
            OtherEntryItem item = otherItems.get(position);
           // Toast.makeText(this, "You clicked " + item.company_name , Toast.LENGTH_SHORT).show();
        }
        super.onListItemClick(l, v, position, id);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.other_activity_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle presses on the action bar items
        switch (item.getItemId()) {
            case R.id.joined_companies: {
                // Toast.makeText(this, "Settings clicked", Toast.LENGTH_SHORT).show();
                Intent newIntent = new Intent(getApplicationContext(), JoinedQueueActivity.class);
                startActivity(newIntent);
                finish();
                return true;
            }
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
            case R.id.refresh: {
//                Intent newIntent = new Intent(getApplicationContext(), OtherQueueActivity.class);
//                startActivity(newIntent);
//                finish();
                recreate();
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void joinSelectedItems(ListView listView)
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
                OtherEntryItem company=(OtherEntryItem)listView.getItemAtPosition(checked.keyAt(i));
                Log.d("MyApp", "company.company_name="+company.company_name);
                String company_name=company.company_name;
                Iterator<HashMap<String,String>> it = otherCompanyList.iterator();
                while(it.hasNext())
                {
                    HashMap<String,String> obj = it.next();
                    if(obj.containsValue(company_name))
                    {
                        String company_id=obj.get("company_id");
                        Log.d("MyApp", "company_id="+company_id);
                        new JoinCompanies(this).execute(company_id);
                        recreate();
                    }
                }
            }
        }
    }

    private class JoinCompanies extends AsyncTask<String, Void, Void> {

        Context context;
        public JoinCompanies(Context cnt) {
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
                HttpGet httpGet = new HttpGet("http://10.0.0.6/ezfair/android/join_queue.php?username=" + username+"&company_id="+company_id);
                HttpResponse httpResponse = httpClient.execute(httpGet);
                //HttpEntity httpEntity = httpResponse.getEntity();
                //String jsonStr = EntityUtils.toString(httpEntity);

                //Log.d("MyApp","Response JSON: " + jsonStr);

            } catch (Exception e) {
                Log.d("MyApp","************Exception : " + e.getMessage());
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
        }
    }
}
