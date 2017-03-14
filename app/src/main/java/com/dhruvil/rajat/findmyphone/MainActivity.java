package com.dhruvil.rajat.findmyphone;

import android.location.Location;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class MainActivity extends AppCompatActivity {

    private final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED + WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Locate phone");
        setSupportActionBar(toolbar);

//        assert getSupportActionBar() != null;
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
        if (uri == null)
            uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

        final MediaPlayer mp = MediaPlayer.create(getApplicationContext(), uri);
        if (mp == null)
            Log.e(TAG,"MP is null");

        if (getIntent().getBooleanExtra("fromFCMService", false)) {
            if (getIntent().getBooleanExtra("fromFCMServiceRing", false)) {
                if (!mp.isPlaying())
                    mp.start();
            }

            String text = getIntent().getStringExtra("fromFCMServiceText");
            TextView notifierTextView = (TextView) findViewById(R.id.text_view_notifier);
            notifierTextView.setText(text);

            SmartLocation.with(this).location().start(new OnLocationUpdatedListener() {
                @Override
                public void onLocationUpdated(Location location) {
                    Log.e(TAG, "Latitude: " + location.getLatitude());
                    Log.e(TAG, "Longitude: " + location.getLongitude());
                }
            });
        }

        FloatingActionButton foundButton = (FloatingActionButton) findViewById(R.id.button_found);
        foundButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mp.isPlaying())
                    mp.stop();
                finish();
            }
        });
    }
}
