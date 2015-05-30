package com.haard.armaniroms;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.haard.armaniroms.history_database.DatabaseHelper;
import com.haard.armaniroms.home.HomeFragment;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class MainActivity extends ActionBarActivity {
    static String SENDER_ID = "1019787135827";
    static final String TAG = "GCMDemo";
    static GoogleCloudMessaging gcm;
    public static Context context;
    public static final String PROPERTY_REG_ID = "registration_id";
    static public SharedPreferences prefs;
    public static String regid="null";
    public static String msg="";
    public static String history="";
    static final String SERVER_URL = "http://doylefermi.comlu.com/register.php";
    public static List<String> mTitle;
    public static List<String> mLink;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        context=this.getApplicationContext();
        prefs=getSharedPreferences(MainActivity.class.getSimpleName(), Context.MODE_PRIVATE);
        mTitle=new ArrayList<String>();
        mLink=new ArrayList<String>();
        gcmcheck();

        history();

        FragmentManager fragmentManager=getSupportFragmentManager();
        FragmentTransaction transaction;
        transaction=fragmentManager.beginTransaction();
        Fragment f=new HomeFragment();
        transaction.replace(R.id.test, f).commit();

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

    public static void history()
    {
        DatabaseHelper databaseHelper=new DatabaseHelper(context);
        Cursor AllFriends = databaseHelper.getFriends();

        AllFriends.moveToFirst();

        while (!AllFriends.isAfterLast()) {
            String id= AllFriends.getString(0);
            String Name = AllFriends.getString(1);
            String title="Google";
            String link="www.google.com";
            if(Name.contains(";")){
                title=Name.substring(0,Name.indexOf(';'));
                link=Name.substring(Name.indexOf(';')+1,Name.length());
            }
            AllFriends.moveToNext();
            mTitle.add(title);
            mLink.add(link);
            Log.w("test", Name + id);

        }


    }
    public static void gcmcheck()
    {
        gcm = GoogleCloudMessaging.getInstance(context);
        regid = getRegistrationId(context);

        if (regid=="") {
            registerInBackground();
        }

    }

    private static String getRegistrationId(Context context) {
        String registrationId = prefs.getString(PROPERTY_REG_ID, "");
        if (registrationId.isEmpty()) {
            Log.i(TAG, "Registration not found.");
            return "";
        }
        return registrationId;
    }

    private static void registerInBackground() {

        new AsyncTask<Void,Void,String>() {

            @Override
            protected String doInBackground(Void... params) {

                try {
                    if (gcm == null) {
                        gcm = GoogleCloudMessaging.getInstance(context);
                    }
                    regid = gcm.register(SENDER_ID);
                    msg = "Device registered, registration ID=" + regid;

                    // You should send the registration ID to your server over HTTP,
                    // so it can use GCM/HTTP or CCS to send messages to your app.
                    // The request to your server should be authenticated if your app
                    // is using accounts.
                    sendRegistrationIdToBackend();

                    // For this demo: we don't need to send it because the device
                    // will send upstream messages to a server that echo back the
                    // message using the 'from' address in the message.

                    // Persist the regID - no need to register again.
                    storeRegistrationId(context, regid);
                } catch (IOException ex) {
                    msg = "Error :" + ex.getMessage();
                    // If there is an error, don't just keep trying to register.
                    // Require the user to click a button again, or perform
                    // exponential back-off.
                }
                return msg;
            }

            private void sendRegistrationIdToBackend() {
                final int MAX_ATTEMPTS = 5;
                final int BACKOFF_MILLI_SECONDS = 2000;
                final Random random = new Random();
                Log.i(TAG, "registering device (regId = " + regid + ")");
                String serverUrl = SERVER_URL;
                Map<String, String> params = new HashMap<String, String>();
                params.put("regId", regid);


                long backoff = BACKOFF_MILLI_SECONDS + random.nextInt(1000);
                // Once GCM returns a registration id, we need to register on our server
                // As the server might be down, we will retry it a couple
                // times.
                for (int i = 1; i <= MAX_ATTEMPTS; i++) {
                    Log.d(TAG, "Attempt #" + i + " to register");
                    try {
                        post(serverUrl, params);
                        // displayMessage(context, "Registered");
                        return;
                    } catch (IOException e) {
                        // Here we are simplifying and retrying on any error; in a real
                        // application, it should retry only on unrecoverable errors
                        // (like HTTP error code 503).
                        Log.e(TAG, "Failed to register on attempt " + i + ":" + e);
                        if (i == MAX_ATTEMPTS) {
                            break;
                        }
                        try {
                            Log.d(TAG, "Sleeping for " + backoff + " ms before retry");
                            Thread.sleep(backoff);
                        } catch (InterruptedException e1) {
                            // Activity finished before we complete - exit.
                            Log.d(TAG, "Thread interrupted: abort remaining retries!");
                            Thread.currentThread().interrupt();
                            return;
                        }
                        // increase backoff exponentially
                        backoff *= 2;
                    }
                }
                //  String message = context.getString(R.string.server_register_error,
                //        MAX_ATTEMPTS);
                //CommonUtilities.displayMessage(context, message);

            }
            private  void post(String endpoint, Map<String, String> params)throws IOException{
                URL url;
                try {
                    url = new URL(endpoint);
                } catch (MalformedURLException e) {
                    throw new IllegalArgumentException("invalid url: " + endpoint);
                }
                StringBuilder bodyBuilder = new StringBuilder();
                Iterator<Map.Entry<String, String>> iterator = params.entrySet().iterator();
                // constructs the POST body using the parameters
                while (iterator.hasNext()) {
                    Map.Entry<String, String> param = iterator.next();
                    bodyBuilder.append(param.getKey()).append('=')
                            .append(param.getValue());
                    if (iterator.hasNext()) {
                        bodyBuilder.append('&');
                    }
                }
                String body = bodyBuilder.toString();
                Log.v(TAG, "Posting '" + body+ "' to " + url);
                byte[] bytes = body.getBytes();
                HttpURLConnection conn = null;
                try {
                    Log.e("URL", "> " + url);
                    conn = (HttpURLConnection) url.openConnection();
                    conn.setDoOutput(true);
                    conn.setUseCaches(false);
                    conn.setFixedLengthStreamingMode(bytes.length);
                    conn.setRequestMethod("GET");
                    conn.setRequestProperty("Content-Type",
                            "application/x-www-form-urlencoded;charset=UTF-8");
                    // post the request
                    OutputStream out = conn.getOutputStream();
                    out.write(bytes);
                    out.close();
                    // handle the response
                    int status = conn.getResponseCode();
                    if (status != 200) {
                        throw new IOException("Post failed with error code " + status);
                    }
                } finally {
                    if (conn != null) {
                        conn.disconnect();
                    }
                }
            }

            protected void onPostExecute(String msg) {
                //setContentView(R.layout.activity_gcm_broadcast_receiver);
            }


        }.execute(null, null, null);}

    private static void storeRegistrationId(Context context, String regId) {
        //int appVersion = getAppVersion(context);
        // Log.i(TAG, "Saving regId on app version " + appVersion);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(PROPERTY_REG_ID, regId);
        //  editor.putInt(PROPERTY_APP_VERSION, appVersion);
        editor.commit();
    }
}
