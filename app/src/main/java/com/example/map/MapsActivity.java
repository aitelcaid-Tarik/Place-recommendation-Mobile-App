package com.example.map;

import android.Manifest;
import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.maps.DirectionsApiRequest;
import com.google.maps.GeoApiContext;
import com.google.maps.PendingResult;
import com.google.maps.internal.PolylineEncoding;
import com.google.maps.model.DirectionsResult;
import com.google.maps.model.DirectionsRoute;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static android.graphics.Color.RED;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnInfoWindowClickListener, GoogleMap.OnPolylineClickListener {

    private GoogleMap mMap;


    static final String FINE_LOCATION = Manifest.permission.ACCESS_FINE_LOCATION;
    static final String COURSE_LOCATION = Manifest.permission.ACCESS_COARSE_LOCATION;
    static final int LOCATION_PERMISSION_REQUEST_CODE = 1234;

    Boolean mLocationPermissionsGranted = false;
    FusedLocationProviderClient mFusedLocationProviderClient;

    FloatingActionButton Gps, addLocation, list, satellite, night, street;

    double latitude = 0.0, lati = 0.0, longitude = 0.0, longi = 0.0;
    boolean flag = true;
    int temp = 1;
    Intent intent;

    ComponentName service;
    Intent intentMyService;

    DB_sqlite db = new DB_sqlite(this);
    ArrayList<Coordinates> myData = new ArrayList<>();

    Location currentLocation;

    ArrayList<PolylineData> mPolyLinesData = new ArrayList<>();
    Marker mSelectedMarker = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        //new Synchroniz.GetDataFromServer(this).execute(Synchroniz.HttpURLGet);

        Gps = (FloatingActionButton) findViewById(R.id.gps);
        addLocation = (FloatingActionButton) findViewById(R.id.addLocation);
        list = (FloatingActionButton) findViewById(R.id.list);
        satellite = (FloatingActionButton) findViewById(R.id.satellite);
        night = (FloatingActionButton) findViewById(R.id.night);
        street = (FloatingActionButton) findViewById(R.id.street);

        intent = getIntent();

        intentMyService = new Intent(this, MyService.class);

        service = startService(intentMyService);

        getLocationPermission();

        Places.initialize(getApplicationContext(), "YOUR KEY HERE");

        AutocompleteSupportFragment autocompleteFragment = (AutocompleteSupportFragment)
                getSupportFragmentManager().findFragmentById(R.id.autocomplete_fragment);

        autocompleteFragment.setPlaceFields(Arrays.asList(Place.Field.LAT_LNG, Place.Field.NAME));

        autocompleteFragment.setOnPlaceSelectedListener(new PlaceSelectionListener() {
            @Override
            public void onPlaceSelected(Place place) {

                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 14));
                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(place.getLatLng()).title(" my new location ").snippet("You are Here")).showInfoWindow();
                mMap.addCircle(new CircleOptions().center(place.getLatLng()).radius(100).strokeWidth(3f).strokeColor(RED).fillColor(Color.argb(70, 150, 50, 50)));

                Log.i("", "Place: " + place.getName() + ", " + place.getId());
            }

            @Override
            public void onError(Status status) {
                Log.i("", "An error occurred: " + status);
            }
        });
    }

    public void getDeviceLocation() {

        mFusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        try {
            if (mLocationPermissionsGranted) {

                final Task location = mFusedLocationProviderClient.getLastLocation();
                location.addOnCompleteListener(new OnCompleteListener() {
                    @Override
                    public void onComplete(Task task) {
                        if (task.isSuccessful()) {

                            currentLocation = (Location) task.getResult();

                            lati = currentLocation.getLatitude();
                            longi = currentLocation.getLongitude();


                        } else {

                            Toast.makeText(MapsActivity.this, "unable to get current location", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        } catch (SecurityException e) {
            Log.e("ERROR", e.getMessage());
        }
    }

    public void initMap() {

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(MapsActivity.this);
    }

    public void getLocationPermission() {

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION};

        if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {

            if (ContextCompat.checkSelfPermission(this.getApplicationContext(),
                    COURSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                mLocationPermissionsGranted = true;

                initMap();

            } else {
                ActivityCompat.requestPermissions(this,
                        permissions,
                        LOCATION_PERMISSION_REQUEST_CODE);
            }
        } else {
            ActivityCompat.requestPermissions(this,
                    permissions,
                    LOCATION_PERMISSION_REQUEST_CODE);
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        mLocationPermissionsGranted = false;

        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE: {

                if (grantResults.length > 0) {
                    for (int i = 0; i < grantResults.length; i++) {

                        if (grantResults[i] != PackageManager.PERMISSION_GRANTED) {
                            mLocationPermissionsGranted = false;
                            return;
                        }
                    }

                    mLocationPermissionsGranted = true;
                    initMap();
                }
            }
        }
    }

    public void showAll() {

        myData = db.getAll();

        for (Coordinates c : myData) {

            double lat = (c.getLatitude() == null || c.getLatitude().equals("") ? 0.0 : Double.parseDouble(c.getLatitude()));
            double lon = (c.getLongitude() == null || c.getLongitude().equals("") ? 0.0 : Double.parseDouble(c.getLongitude()));
            float rat = (c.getRating() == null || c.getRating().equals("") ? 0.0f : Float.parseFloat(c.getRating()));

            LatLng myPosition = new LatLng(lat, lon);

            if (rat >= 4) {
                mMap.addMarker(new MarkerOptions().position(myPosition).title(c.getName()).snippet(c.getDescription()).icon(BitmapDescriptorFactory.defaultMarker(120.0f)));
            } else if (rat >= 2) {
                mMap.addMarker(new MarkerOptions().position(myPosition).title(c.getName()).snippet(c.getDescription()).icon(BitmapDescriptorFactory.defaultMarker(270.0f)));
            } else {
                mMap.addMarker(new MarkerOptions().position(myPosition).title(c.getName()).snippet(c.getDescription()).icon(BitmapDescriptorFactory.defaultMarker(0.0f)));
            }
            mMap.addCircle(new CircleOptions().center(myPosition).radius(100).strokeWidth(3f).strokeColor(RED).fillColor(Color.argb(70, 150, 50, 50)));
        }

    }


    public void init() {

        Gps.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //Toast.makeText(MapsActivity.this, " i need permission ", Toast.LENGTH_SHORT).show();
                getDeviceLocation();
                mMap.setMyLocationEnabled(true);

                LatLng myPosition = new LatLng(lati, longi);

                mMap.addMarker(new MarkerOptions().position(myPosition).title("You are Here").icon(BitmapDescriptorFactory.defaultMarker(210.0f)));
                mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 14));
                showAll();

            }
        });

        addLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String s1 = Double.toString(latitude);
                String s2 = Double.toString(longitude);

                Intent intent = new Intent(getApplicationContext(), Synchroniz.class);

                intent.putExtra("first", s1);
                intent.putExtra("second", s2);

                startActivity(intent);
                finish();
            }
        });

        list.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent intent = new Intent(getApplicationContext(), RecyclerView_Activity.class);
                startActivity(intent);
                finish();
            }
        });

        satellite.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (flag) {
                    satellite.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.terrain));
                    mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
                    flag = false;

                } else if (!flag) {
                    satellite.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.satellite));
                    mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    flag = true;
                }
            }
        });

        night.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                if (temp == 1) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    night.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.night3));
                    boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.mapstyle1));
                    Toast.makeText(MapsActivity.this, " Style Set to : Aubergine ", Toast.LENGTH_SHORT).show();
                    temp = 2;

                } else if (temp == 2) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    night.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.night2));
                    boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.mapstyle2));
                    Toast.makeText(MapsActivity.this, " Style Set to : Night ", Toast.LENGTH_SHORT).show();
                    temp = 3;

                } else if (temp == 3) {
                    mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    night.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.terrain));
                    boolean success = mMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(getApplicationContext(), R.raw.mapstyle3));
                    Toast.makeText(MapsActivity.this, " Style Set to : Retro ", Toast.LENGTH_SHORT).show();
                    temp = 4;

                } else {
                    mMap.setMapType(GoogleMap.MAP_TYPE_TERRAIN);
                    night.setImageDrawable(ContextCompat.getDrawable(getApplicationContext(), R.drawable.night1));
                    Toast.makeText(MapsActivity.this, " Style Set to : Standard ", Toast.LENGTH_SHORT).show();
                    temp = 1;

                }
            }
        });

        street.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String s1 = Double.toString(latitude);
                String s2 = Double.toString(longitude);

                Intent intent = new Intent(getApplicationContext(), StreetView.class);

                intent.putExtra("latitude", s1);
                intent.putExtra("longitude", s2);

                startActivity(intent);

                finish();
            }
        });
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {

        mMap = googleMap;

        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        mMap.setMyLocationEnabled(true);
        mMap.setBuildingsEnabled(true);
        mMap.getUiSettings().setCompassEnabled(true);
        mMap.getUiSettings().setRotateGesturesEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

        mMap.setOnInfoWindowClickListener(this);
        mMap.setOnPolylineClickListener(this);

        if (intent.hasExtra("name")) {

            String name = intent.getStringExtra("name");
            String description = intent.getStringExtra("description");
            double lat = intent.getDoubleExtra("latitude", 0.0);
            double lon = intent.getDoubleExtra("longitude", 0.0);
            float rating = intent.getFloatExtra("rating", 0.0f);

            getDeviceLocation();
            LatLng myPosition = new LatLng(lat, lon);

            if (rating >= 4) {
                mMap.addMarker(new MarkerOptions().position(myPosition).title(name).snippet(description).icon(BitmapDescriptorFactory.defaultMarker(120.0f))).showInfoWindow();

            } else if (rating >= 2) {
                mMap.addMarker(new MarkerOptions().position(myPosition).title(name).snippet(description).icon(BitmapDescriptorFactory.defaultMarker(270.0f))).showInfoWindow();
            } else {
                mMap.addMarker(new MarkerOptions().position(myPosition).title(name).snippet(description).icon(BitmapDescriptorFactory.defaultMarker(0.0f))).showInfoWindow();
            }

            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(myPosition, 14));
            mMap.addCircle(new CircleOptions().center(myPosition).radius(100).strokeWidth(3f).strokeColor(RED).fillColor(Color.argb(70, 150, 50, 50)));


        } else if (intent.hasExtra("showAll")) {

            getDeviceLocation();
            mMap.setMyLocationEnabled(true);
            showAll();


        } else {

            getDeviceLocation();
            mMap.setMyLocationEnabled(true);

            LatLng centrale = new LatLng(34.007767, -6.838421);

            mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(centrale, 14));
            showAll();


        }

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {

            @Override
            public void onMapLongClick(LatLng latLng) {

                latitude = latLng.latitude;
                longitude = latLng.longitude;

                Toast.makeText(MapsActivity.this, latitude + "," + longitude, Toast.LENGTH_SHORT).show();

                mMap.clear();
                mMap.addMarker(new MarkerOptions().position(latLng).title("My new Location").snippet("You are Here")).showInfoWindow();
                mMap.addCircle(new CircleOptions().center(new LatLng(latitude, longitude)).radius(100).strokeWidth(3f).strokeColor(RED).fillColor(Color.argb(70, 150, 50, 50)));

            }
        });

        init();
    }

    @Override
    public void onInfoWindowClick(final Marker marker) {

        final AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setMessage("determine the road to " + marker.getTitle()).setCancelable(true).setPositiveButton("Yes", new DialogInterface.OnClickListener() {

            public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                mSelectedMarker = marker;
                calculateDirections(marker);
                dialog.dismiss();
            }
        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });

        final AlertDialog alert;
        alert = builder.create();

        alert.show();
    }

    private void addPolylinesToMap(final DirectionsResult result) {

        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {

                if (mPolyLinesData.size() > 0) {

                    for (PolylineData polylineData : mPolyLinesData) {
                        polylineData.getPolyline().remove();
                    }
                    mPolyLinesData.clear();
                    mPolyLinesData = new ArrayList<>();
                }

                double duration = 999999999;

                for (DirectionsRoute route : result.routes) {

                    List<com.google.maps.model.LatLng> decodedPath = PolylineEncoding.decode(route.overviewPolyline.getEncodedPath());
                    List<LatLng> newDecodedPath = new ArrayList<>();

                    for (com.google.maps.model.LatLng latLng : decodedPath) {
                        newDecodedPath.add(new LatLng(
                                latLng.lat,
                                latLng.lng
                        ));
                    }

                    Polyline polyline = mMap.addPolyline(new PolylineOptions().addAll(newDecodedPath));
                    polyline.setColor(Color.rgb(185, 185, 185));
                    polyline.setClickable(true);
                    mPolyLinesData.add(new PolylineData(polyline, route.legs[0]));

                    double tempDuration = route.legs[0].duration.inSeconds;

                    if (tempDuration < duration) {
                        duration = tempDuration;
                        onPolylineClick(polyline);
                    }
                    mSelectedMarker.setVisible(false);
                }
            }
        });
    }

    private void calculateDirections(Marker marker) {

        com.google.maps.model.LatLng destination = new com.google.maps.model.LatLng(
                marker.getPosition().latitude,
                marker.getPosition().longitude
        );

        DirectionsApiRequest directions = new DirectionsApiRequest(new GeoApiContext.Builder().apiKey("YOUR KEY HERE").build());

        directions.alternatives(true);
        directions.origin(
                new com.google.maps.model.LatLng(currentLocation.getLatitude(), currentLocation.getLongitude()
                )
        );

        directions.destination(destination).setCallback(new PendingResult.Callback<DirectionsResult>() {
            @Override
            public void onResult(DirectionsResult result) {

                if (result.routes.length == 0) {

                    Toast.makeText(MapsActivity.this, " can't find a way there  ", Toast.LENGTH_SHORT).show();
                }
                addPolylinesToMap(result);
            }

            @Override
            public void onFailure(Throwable e) {
                Log.e("ERROR", "onFailure: " + e.getMessage());
            }
        });
    }


    @Override
    public void onPolylineClick(Polyline polyline) {
        int index = 0;

        for (PolylineData polylineData : mPolyLinesData) {
            index++;

            if (polyline.getId().equals(polylineData.getPolyline().getId())) {

                polylineData.getPolyline().setColor(Color.rgb(14, 121, 255));
                polylineData.getPolyline().setZIndex(1);

                LatLng endLocation = new LatLng(polylineData.getLeg().endLocation.lat, polylineData.getLeg().endLocation.lng);

                mMap.addMarker(new MarkerOptions().position(endLocation).title("Route " + index).snippet("Duration : " + polylineData.getLeg().duration + "   Distance : " + polylineData.getLeg().distance
                )).showInfoWindow();

            } else {
                polylineData.getPolyline().setColor(Color.rgb(185, 185, 185));
                polylineData.getPolyline().setZIndex(0);
            }
        }
    }
}


