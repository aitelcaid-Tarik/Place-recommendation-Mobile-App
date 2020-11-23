package com.example.map;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Point;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.View;

import com.google.android.gms.maps.OnStreetViewPanoramaReadyCallback;
import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.StreetViewPanoramaCamera;
import com.google.android.gms.maps.model.StreetViewPanoramaLocation;
import com.google.android.gms.maps.model.StreetViewPanoramaOrientation;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class StreetView extends Activity implements OnStreetViewPanoramaReadyCallback {

    String STREET_VIEW_BUNDLE = "StreetViewBundle";
    String latitude = "", longitude = "";
    Intent intent;
    FloatingActionButton back;

    private StreetViewPanorama streetViewPanorama;
    private StreetViewPanoramaFragment streetViewPanoramaFragment;

    private StreetViewPanorama.OnStreetViewPanoramaChangeListener streetViewPanoramaChangeListener = new StreetViewPanorama.OnStreetViewPanoramaChangeListener() {
        @Override
        public void onStreetViewPanoramaChange(StreetViewPanoramaLocation streetViewPanoramaLocation) {
            Log.e("ERROR", "Street View Panorama Change Listener");
        }
    };

    private StreetViewPanorama.OnStreetViewPanoramaClickListener streetViewPanoramaClickListener = (new StreetViewPanorama.OnStreetViewPanoramaClickListener() {
        @Override
        public void onStreetViewPanoramaClick(StreetViewPanoramaOrientation orientation) {

            Point point = streetViewPanorama.orientationToPoint(orientation);

            if (point != null) {
                streetViewPanorama.animateTo(
                        new StreetViewPanoramaCamera.Builder()
                                .orientation(orientation)
                                .zoom(streetViewPanorama.getPanoramaCamera().zoom)
                                .build(), 10000);
            }

        }
    });

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_streetview);
        streetViewPanoramaFragment = (StreetViewPanoramaFragment) getFragmentManager().findFragmentById(R.id.streetViewMap);
        back = (FloatingActionButton) findViewById(R.id.back);

        intent = getIntent();

        latitude = getIntent().getStringExtra("latitude");
        longitude = getIntent().getStringExtra("longitude");

        streetViewPanoramaFragment.getStreetViewPanoramaAsync(this);
        Bundle streetViewBundle = null;
        if (savedInstanceState != null)
            streetViewBundle = savedInstanceState.getBundle(STREET_VIEW_BUNDLE);
        streetViewPanoramaFragment.onCreate(streetViewBundle);


        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), MapsActivity.class);
                startActivity(intent);
                finish();

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        streetViewPanoramaFragment.onResume();
    }

    @Override
    public void onSaveInstanceState(Bundle outState, PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        Bundle mStreetViewBundle = outState.getBundle(STREET_VIEW_BUNDLE);
        if (mStreetViewBundle == null) {
            mStreetViewBundle = new Bundle();
            outState.putBundle(STREET_VIEW_BUNDLE, mStreetViewBundle);
        }
        streetViewPanoramaFragment.onSaveInstanceState(mStreetViewBundle);
    }

    @Override
    public void onStreetViewPanoramaReady(StreetViewPanorama streetViewPanorama) {
        this.streetViewPanorama = streetViewPanorama;
        this.streetViewPanorama.setPosition(new LatLng(Double.parseDouble(latitude), Double.parseDouble(longitude)));
        this.streetViewPanorama.setOnStreetViewPanoramaChangeListener(streetViewPanoramaChangeListener);
        this.streetViewPanorama.setOnStreetViewPanoramaClickListener(streetViewPanoramaClickListener);
    }

    @Override
    protected void onStop() {
        super.onStop();
        streetViewPanoramaFragment.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}