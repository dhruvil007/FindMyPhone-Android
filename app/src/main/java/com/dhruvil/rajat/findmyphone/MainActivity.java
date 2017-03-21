package com.dhruvil.rajat.findmyphone;

import android.content.Context;
import android.location.Location;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import java.util.HashMap;
import java.util.Map;

import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.SmartLocation;

public class MainActivity extends AppCompatActivity implements GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private final String TAG = "MainActivity";
    private final Context context = this;
    private GoogleApiClient mGoogleApiClient;
    private boolean onlyLocation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED + WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Locate phone");
        setSupportActionBar(toolbar);

        Log.e(TAG, "" + getIntent().getBooleanExtra("fromFCMServiceRing", false));
        if (getIntent().getBooleanExtra("fromFCMServiceRing", false)) {
            Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (uri == null)
                uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);

            final MediaPlayer mp = MediaPlayer.create(getApplicationContext(), uri);
            if (mp == null)
                Log.e(TAG, "MP is null");

            if (getIntent().getBooleanExtra("fromFCMServiceRing", false)) {
                if (getIntent().getBooleanExtra("fromFCMServiceRing", false)) {
                    if (!mp.isPlaying())
                        mp.start();
                }

                String text = getIntent().getStringExtra("fromFCMServiceText");
                TextView notifierTextView = (TextView) findViewById(R.id.text_view_notifier);
                notifierTextView.setText(text);
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
        } else {
            onlyLocation = true;

            String text = getIntent().getStringExtra("fromFCMServiceText");
            TextView notifierTextView = (TextView) findViewById(R.id.text_view_notifier);
            notifierTextView.setText(text);

            FloatingActionButton foundButton = (FloatingActionButton) findViewById(R.id.button_found);
            foundButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }
        buildGoogleAPIClient();
    }

    private void sendLocation(final double latitude, final double longitude) {
        final String url = "http://10.0.2.2:8000/site/receive-location/";
        StringRequest stringRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.e("Response for location", "Success");
                Log.e("Response", response);

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, "" + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<>();
                params.put("latitude", "" + latitude);
                params.put("longitude", "" + longitude);
                return params;
            }
        };
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        requestQueue.add(stringRequest);
    }

    public void buildGoogleAPIClient() {
        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (onlyLocation) {
            Location mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
            if (mLastLocation != null) {
                Log.e(TAG, "" + mLastLocation.getLatitude() + " " + mLastLocation.getLongitude());
                sendLocation(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            }
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        Log.e(TAG, "Connection suspended");
    }

    @Override
    protected void onStart() {
        mGoogleApiClient.connect();
        super.onStart();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();
        super.onStop();
    }
}
