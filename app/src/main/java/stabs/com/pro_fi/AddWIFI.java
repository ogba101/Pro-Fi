package stabs.com.pro_fi;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AddWIFI extends AppCompatActivity {

    public static final String TAG = "AddWifi";
    public static final String NAME="NAME_TXT_VAL";
    public static final String WIFI="WIFI";
    public static final String RING="RINGTONE";
    public static final String MEDIA="MEDIA";
    public static final String NOTIF="NOTIFICATIONS";
    public static final String SYS="SYSTEM";

    private WifiAdapter wifiAdapter;
    WifiManager wifi;
    List<WifiConfiguration> wifis;
    List<String> names=new ArrayList <String>(); // NAMES OF WIFI
    List<String> scannedNetworks=new ArrayList<String>(); //
    String[] profileInfo = new String[6]; // ALL PROFILE DETAILS AND SETTINGS
    RecyclerView recyclerView;
    Profile profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.add1_activity_layout);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_view);

        Button backButton = (Button) findViewById(R.id.back_button);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        //Store info in this order - name, wifi, ringtone, media, notifications, system, into profileInfo
        profileInfo[0] = getIntent().getStringExtra(NAME);
        profileInfo[2] = getIntent().getStringExtra(RING);
        profileInfo[3] = getIntent().getStringExtra(MEDIA);
        profileInfo[4] = getIntent().getStringExtra(NOTIF);
        profileInfo[5] = getIntent().getStringExtra(SYS);

        wifi=(WifiManager) getSystemService(Context.WIFI_SERVICE);
        wifis=wifi.getConfiguredNetworks();
        WifiConfiguration [] array= new WifiConfiguration[wifis.size()];
        wifis.toArray(array);
        names.add("None");// Empty spot

        for(int i=0;i<wifis.size();i++)
        {
            names.add(array[i].SSID.replace("\"", ""));
        }
        Collections.sort(names);

        if (recyclerView != null) {
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerView.setLayoutManager(layoutManager);
            wifiAdapter = new WifiAdapter((ArrayList<String>) names);
            recyclerView.setAdapter(wifiAdapter);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }

    public void saveProfile(View v){
        profileInfo[1] = wifiAdapter.wifiName;
        DBHelper helper= DBHelper.getInstance(this);

        if ( profileInfo[1] == null ) {
            Toast.makeText(this, "Select a Wi-Fi", Toast.LENGTH_SHORT).show();
        } else if(!(helper.isUniqueWIFI(profileInfo[1]))) {
            Toast.makeText(this, "Wi-Fi already in use", Toast.LENGTH_SHORT).show();
        } else {
            profile = new Profile(
                    profileInfo[0],
                    profileInfo[1],
                    Integer.parseInt(profileInfo[2]),
                    Integer.parseInt(profileInfo[3]),
                    Integer.parseInt(profileInfo[4]),
                    Integer.parseInt(profileInfo[5])
            );

            //Write contents profileInfo to DB.
            helper.insertProfile(profile);

            //Switch to Home screen
            Intent myIntent=new Intent(this,MainActivity.class);
            startActivity(myIntent);
        }
    }
}
