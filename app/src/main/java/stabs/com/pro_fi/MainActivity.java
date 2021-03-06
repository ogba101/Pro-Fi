package stabs.com.pro_fi;

import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SwitchCompat;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.appindexing.Action;
import com.google.android.gms.appindexing.AppIndex;
import com.google.android.gms.common.api.GoogleApiClient;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    /**
     * ATTENTION: This was auto-generated to implement the App Indexing API.
     * See https://g.co/AppIndexing/AndroidStudio for more information.
     */
    private GoogleApiClient client;
    public static final String TAG="MainActivity";
    AudioManager myAudioManager;
    private String oldWifi="";
    private boolean check;
    ArrayList<Profile> list;
    private Switch myswitch;
    private boolean backPressedToExitOnce = false;
    private Toast toast = null;
    private NetworkService networkService;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final TextView switchstatus=(TextView) findViewById(R.id.switchStatus);
        SwitchCompat mainswitch = (SwitchCompat) findViewById(R.id.compatSwitch);
        list = DBHelper.getInstance(this).getAllProfiles();
        check=false;
        networkService = new NetworkService(this);

        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.recycler_view);
        if (recyclerView != null)
        {
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            RecyclerView.Adapter mAdapter = new ProfileAdapter(list, this);
            recyclerView.setAdapter(mAdapter);
        }
        SharedPreferences sharedPrefs = getSharedPreferences("com.profi.xyz", MODE_PRIVATE);
         check=sharedPrefs.getBoolean("AutomaticSelect", false);
        mainswitch.setChecked(check);
        if (check) {
            // The toggle is enabled
            switchstatus.setText("Mode:    Automatic");
        } else {
            switchstatus.setText("Mode:    Manual");
            // The toggle is disabled
        }

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client = new GoogleApiClient.Builder(this).addApi(AppIndex.API).build();
        mainswitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
//                    autoActivate(); TODO: Method removed, replaced with checkConnection()
                    networkService.checkConnection();

                    // The toggle is enabled
                    switchstatus.setText("Mode:    Automatic");
                    SharedPreferences.Editor editor = getSharedPreferences("com.profi.xyz", MODE_PRIVATE).edit();
                    editor.putBoolean("AutomaticSelect", true);
                    editor.commit();


                } else {
                    switchstatus.setText("Mode:    Manual");
                    SharedPreferences.Editor editor = getSharedPreferences("com.profi.xyz", MODE_PRIVATE).edit();
                    editor.putBoolean("AutomaticSelect", false);
                    editor.commit();

                    // The toggle is disabled
                }
            }
        });
    }

//    TODO: Now done with DBHelper.getProfile(String), searching better with a db.
//    //return profile containing WIFI Name
//    public Profile getProfile(String WIFINAME)
//    {
//        for(int i=0;i<list.size();i++)
//        {
//            if(list.get(i).getWifi().equals(WIFINAME)) return list.get(i);
//        }
//        return null;
//    }

//    TODO: Now done using Receiver
//    public void autoActivate()
//    {
//        NetworkChangeReceiver ncr =  NetworkChangeReceiver.getInstance();
//        ncr.onReceive(this,new Intent());
//        SharedPreferences sharedPrefs = getSharedPreferences("com.profi.xyz", MODE_PRIVATE);
//        check=sharedPrefs.getBoolean("AutomaticSelect", false);
//
//        //if (check && ncr.isWifiConnected) {
//            if (ncr.isConnected()&& check) // if the wifi is connected
//            {
//            Profile profile=getProfile(ncr.wifiName); // get the profile
//                if(profile!=null)
//                {
////                    if(!(profile.getName().equals(oldWifi)))
////                    {
//                    activateProfile(profile,true);
//                    oldWifi=profile.getName(); //
//                    String message= "nothing";
//                    message = profile.getName();
//                    Log.e(TAG, "FOUND "+message);
//                    Toast.makeText(this, message + " Activated", Toast.LENGTH_SHORT).show();
//                    //}
//                }
//            }
//        else{
//        Log.e(TAG, "NOT FOUND ");}
//
//    }

//    TODO: Moved to NetworkService, to make it independent from any Activity
//    public void activateProfile(Profile profile, boolean Vib)
//    {
//        // Boolean Vib is for vibration
//        //TODO implement option for vibrate and silent
//        int RING =profile.getRingtone();
//        int NOTIF=profile.getNotification();
//        int SYS  =profile.getSystem();
//        int MEDIA=profile.getMedia();
//
//
//
//        myAudioManager = (AudioManager)this.getSystemService(Context.AUDIO_SERVICE);
//        // myAudioManager.setRingerMode(AudioManager.RINGER_MODE_SILENT);
//        myAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, MEDIA, 0);
//        myAudioManager.setStreamVolume(AudioManager.STREAM_RING,RING, 0);
//        myAudioManager.setStreamVolume(AudioManager.STREAM_NOTIFICATION,NOTIF, 0);
//        myAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM,SYS, 0);
//
//
//
//       // Log.e(TAG, "VALUES " + MEDIA +" "+ RING +" "+ NOTIF+" " + SYS);
//
//
//
//    }
    @Override
    public void onResume(){
        super.onResume();
//        autoActivate(); TODO: Method removed
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        return super.onOptionsItemSelected(item);
    }
    public void add_method(View v){
        Intent myIntent=new Intent(this,CreateProfile.class);
        startActivity(myIntent);
    }

    public void refreshConnection(View view) {
        //Something
    }
    @Override
    public void onBackPressed() {
        if (backPressedToExitOnce) {
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_HOME);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else {
            this.backPressedToExitOnce = true;
            showToast("Press again to exit");
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    backPressedToExitOnce = false;
                }
            }, 2000);
        }

    }
    private void showToast(String message) {
        if (this.toast == null) {
            // Create toast if found null, it would he the case of first call only
            this.toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);

        } else if (this.toast.getView() == null) {
            // Toast not showing, so create new one
            this.toast = Toast.makeText(this, message, Toast.LENGTH_SHORT);

        } else {
            // Updating toast message is showing
            this.toast.setText(message);
        }

        // Showing toast finally
        this.toast.show();
    }
    private void killToast() {
        if (this.toast!= null) {
            this.toast.cancel();
        }
    }
    @Override
    protected void onPause() {
        killToast();
        super.onPause();
//        pref=PreferenceManager.getDefaultSharedPreferences(getApplicationContext());
//        editor=pref.edit();
//        editor.putInt("itempos", pos);
//        editor.putBoolean("boolvalue", list.isItemChecked(pos));
//        editor.commit();
        //  Toast.makeText(getApplicationContext(), "PAUSE", 50).show();

    }
    @Override
    public void onStart() {
        super.onStart();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        client.connect();
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "Main Page",
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                Uri.parse("android-app://stabs.com.pro_fi/http/host/path")
        );
        AppIndex.AppIndexApi.start(client, viewAction);
    }

    @Override
    public void onStop() {
        super.onStop();

        // ATTENTION: This was auto-generated to implement the App Indexing API.
        // See https://g.co/AppIndexing/AndroidStudio for more information.
        Action viewAction = Action.newAction(
                Action.TYPE_VIEW,
                "Main Page",
                // make sure this auto-generated web page URL is correct.
                // Otherwise, set the URL to null.
                Uri.parse("http://host/path"),
                Uri.parse("android-app://stabs.com.pro_fi/http/host/path")
        );
        AppIndex.AppIndexApi.end(client, viewAction);
        client.disconnect();
    }
}
