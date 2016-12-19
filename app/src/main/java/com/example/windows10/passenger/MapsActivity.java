package com.example.windows10.passenger;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.constant.Unit;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.util.DirectionConverter;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStates;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback,GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,LocationListener,GoogleMap.OnInfoWindowClickListener  {

    private GoogleMap mMap;
    private GoogleApiClient mGoogleApiClient;
    private Location mLastLocation;
    private LocationRequest mLocationRequest;
    private LatLng destinationLocation;
    private Marker destinationMarker;
    private Marker myLocation;
    private boolean moveToCurrentLocation=false;
    private int defaultZoom=17;
    public static Passenger p;
    private DatabaseReference mDatabase;
    private DatabaseReference Database;
    private ChildEventListener v;
private Polyline polyline;
    private String Uid;
    private String path="users/passenger/";
    private Map<String,Driver_Marker> d=new HashMap<>();
    public static Driver driverForMarker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent i=getIntent();
         Uid=i.getStringExtra("Uid");


        mDatabase = FirebaseDatabase.getInstance().getReference(path+Uid+"/status");
        mDatabase.setValue(1);
        mDatabase = FirebaseDatabase.getInstance().getReference(path+Uid);


        // Read from the database
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                if(p!=null) {
                    if (p.r_status == 0 && dataSnapshot.getValue(Passenger.class).r_status == 1) {

                        Toast.makeText(MapsActivity.this, "driver accepted request", Toast.LENGTH_SHORT).show();
                        StaticFunc.Stop_T_Timer();




                    } else if (p.r_status == 0 && dataSnapshot.getValue(Passenger.class).r_status == -1) {

                        Toast.makeText(MapsActivity.this, "request cancelled", Toast.LENGTH_SHORT).show();
                        StaticFunc.Stop_T_Timer();
                    } else if (p.r_status == -1 && dataSnapshot.getValue(Passenger.class).r_status == 0) {
                        Toast.makeText(MapsActivity.this, "request sent", Toast.LENGTH_SHORT).show();
                        StaticFunc.T_Timer();
                    }
                }
                    p = dataSnapshot.getValue(Passenger.class);

            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value

            }
        });



        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    123);
        }
        ConnectGoogleClientApi();



        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }
    private void SetOnInfoWindowListener() {
        mMap.setOnInfoWindowClickListener(this);
    }
    @Override
    public void onInfoWindowClick(Marker marker) {
        if(marker.equals(myLocation)){
            Intent markerIntent=new Intent(this, MyDetail.class);
            startActivity(markerIntent);
        }

        else{
            driverForMarker=(Driver) marker.getTag();
            Intent markerIntent=new Intent(this, com.example.windows10.passenger.Marker.class);
            startActivity(markerIntent);
        }


    }
    private void setDestinationLocationListner(){
        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                if(destinationLocation!=null){

                    destinationMarker.remove();
                    polyline.remove();


                }
                destinationMarker=mMap.addMarker(new MarkerOptions()
                        .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ORANGE))
                        .anchor(0.5f, 0.5f) // Anchors the marker on the bottom left
                        .position(latLng)
                        .title("Destination Location")
                        .snippet("This is destination location"));
                destinationLocation=latLng;
                String serverKey = "AIzaSyAgBqU-Rmq-_qL-VFFO8arr9u_I-bUHyIE";
                final LatLng origin = new LatLng(p.x,p.y);

                final LatLng destination = new LatLng(latLng.latitude,latLng.longitude);

                    GoogleDirection.withServerKey(serverKey)
                            .from(origin)
                            .to(destination)
                            .transportMode(TransportMode.WALKING).unit(Unit.METRIC).alternativeRoute(true)
                            .execute(new DirectionCallback() {
                                @Override
                                public void onDirectionSuccess(Direction direction, String rawBody) {
                                    // Do something here
                                    if (direction.isOK()) {


                                        ArrayList<LatLng> directionPositionList = direction.getRouteList().get(0).getLegList().get(0).getDirectionPoint();
                                         polyline=mMap.addPolyline(DirectionConverter.createPolyline(MapsActivity.this, directionPositionList, 5, Color.RED));


                                    }
                                }

                                @Override
                                public void onDirectionFailure(Throwable t) {
                                    // Do something here

                                    Toast.makeText(MapsActivity.this, "fail", Toast.LENGTH_SHORT).show();
                                }
                            });



                Toast.makeText(MapsActivity.this,"Destination location set",Toast.LENGTH_SHORT).show();

            }
        });
    }


    @Override
    protected void onDestroy() {
        Toast.makeText(this,"ondistroy",Toast.LENGTH_SHORT).show();
        mDatabase = FirebaseDatabase.getInstance().getReference(path+Uid+"/status");
        mDatabase.setValue(0);

        if(!p.driverKey.isEmpty()) {
            String pathh = "users/driver/" + p.driverKey + "/passengerKey";
            StaticFunc.writeStringInFireBase(pathh, "");
            pathh = "users/driver/" + p.driverKey + "/r_status";
            StaticFunc.writeLongInFireBase(pathh, (long) -1);


            pathh = "users/passenger/" + p.key + "/driverKey";
            StaticFunc.writeStringInFireBase(pathh, "");

            pathh = "users/passenger/" + MapsActivity.p.key + "/r_status";
            StaticFunc.writeLongInFireBase(pathh, (long) -1);
        }

        super.onDestroy();

    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        mGoogleApiClient.connect();
    }

    @Override
    protected void onStop() {
        mGoogleApiClient.disconnect();




        super.onStop();
        Toast.makeText(this,"onstopcalled",Toast.LENGTH_SHORT).show();

    }



    private void ConnectGoogleClientApi() {

        if (mGoogleApiClient == null) {
            mGoogleApiClient = new GoogleApiClient.Builder(this)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }


    }





    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        EnableCurrentLocationIcon();
        setDestinationLocationListner();
        SetOnInfoWindowListener();
        if(mGoogleApiClient!=null && mLastLocation==null) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                        123);
            }
            mLastLocation = LocationServices.FusedLocationApi.getLastLocation(
                    mGoogleApiClient);
        }


        if(mLastLocation!=null)
            movToCurrentLocation();
        else{
            LatLng sydney = new LatLng(-34, 151);
            mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
        }


         Database=FirebaseDatabase.getInstance().getReference("users/driver");
         v= Database.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String previousChildName) {

                Driver driver;
                driver=dataSnapshot.getValue(Driver.class);
                d.put(dataSnapshot.getKey(),new Driver_Marker(null,driver));


                String key=dataSnapshot.getKey();
                if(driver.status!=0 && driver.passengerKey.isEmpty()) {
                    DrawMarker(key);

                }



            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                Driver driver=dataSnapshot.getValue(Driver.class);
                String key=dataSnapshot.getKey();
                Driver_Marker mp=d.get(key);
                if(mp.getD().status==0&&driver.status==1){
                    mp.d.status=1; // can remove
                    DrawMarker(dataSnapshot.getKey());
                    Toast.makeText(MapsActivity.this,"Driver gets online",Toast.LENGTH_SHORT).show();

                }
                else if(mp.getD().status==1&&driver.status==0){
                    mp.d.status=0; // can remove
                        RemoveMarker(key);
                    Toast.makeText(MapsActivity.this,"driver gets offline",Toast.LENGTH_SHORT).show();

                }

                 else if(mp.getD().r_status==-1 && driver.r_status==0){

                     if(!driver.passengerKey.equals(p.key)){
                         RemoveMarker(key);
                         Toast.makeText(MapsActivity.this,"driver reserver",Toast.LENGTH_SHORT).show();
                     }



                     mp.getD().r_status=driver.r_status;
                     mp.getD().passengerKey=driver.passengerKey;

                }

                 else if(mp.getD().r_status==0 && driver.r_status==1){


                     if(mp.getD().passengerKey.equals(p.key))
                         mp.getM().setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW));

                    mp.d.r_status=1;

                 }


                else if((mp.getD().r_status==0 && driver.r_status==-1) || (mp.getD().r_status==1 && driver.r_status==-1) ) {




                     if(!mp.getD().passengerKey.equals(p.key)){

                         DrawMarker(key);
                         Toast.makeText(MapsActivity.this,"driver free",Toast.LENGTH_SHORT).show();
                     }
                    else {
                         mp.getM().setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_name));
                         Toast.makeText(MapsActivity.this,"Request end",Toast.LENGTH_SHORT).show();
                     }

                     mp.getD().r_status=driver.r_status;
                     mp.getD().passengerKey=driver.passengerKey;

                }

                else {
//                    Toast.makeText(MapsActivity.this,"driver update coordinates",Toast.LENGTH_SHORT).show();
                    mp.getD().x=driver.x;// can remove
                    mp.getD().y=driver.y; //can remove

                    if(driver.status!=0 && driver.r_status==-1)
                    UpdateMarker(key);
                    if(driver.status==0)
                        RemoveMarker(key);

                }
//                mp.setD(dataSnapshot.getValue(Driver.class));









            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
                Toast.makeText(MapsActivity.this,"onchildremoved",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
                Toast.makeText(MapsActivity.this,"onchildmoved",Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(MapsActivity.this,"onchildedchancel",Toast.LENGTH_SHORT).show();
            }
        }
            );


    }

    private void DrawMarker(String key) {
        Driver_Marker d_m=d.get(key);
        if(d_m.m!=null){RemoveMarker(key);
        d_m.m=null;
        }
        if(d_m.getD().r_status==1){
            d_m.m = mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_YELLOW))
                    .anchor(0.5f, 0.5f) // Anchors the marker on the bottom left
                    .position(new LatLng(d_m.getD().x, d_m.getD().y))
                    .title("Taxi no." )
                    .snippet(d_m.getD().name));
            d_m.m.setTag( d_m.getD());
        }
        else {
            d_m.m = mMap.addMarker(new MarkerOptions()
                    .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_action_name))
                    .anchor(0.5f, 0.5f) // Anchors the marker on the bottom left
                    .position(new LatLng(d_m.getD().x, d_m.getD().y))
                    .title("Taxi no.")
                    .snippet(d_m.getD().name));
            d_m.m.setTag(d_m.getD());
        }
    }

    private void RemoveMarker(String key) {

        Driver_Marker d_m=d.get(key);
        if(d_m.m!=null)
        d_m.m.remove();
        d_m.m=null;
    }
    private void UpdateMarker(String key) {
        RemoveMarker(key);
        DrawMarker(key);
    }

    private void EnableCurrentLocationIcon() {

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    123);
        }
        mMap.setMyLocationEnabled(true);

    }
    private void movToCurrentLocation(){
        if(mLastLocation!=null) {

            LatLng l1 = new LatLng(mLastLocation.getLatitude(), mLastLocation.getLongitude());
            if(myLocation!=null){
                myLocation.remove();
            }
            myLocation=mMap.addMarker(new MarkerOptions().position(l1).title("Marker in your locatioon"));
            if(moveToCurrentLocation==false) {
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(l1, defaultZoom));
            moveToCurrentLocation=true;
            }
        }

    }
    protected void createLocationRequest() {
        mLocationRequest = new LocationRequest();
        mLocationRequest.setInterval(10000);
        mLocationRequest.setFastestInterval(5000);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);


    }

    private void CheckGpsAvailibility() {

        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(mLocationRequest);

        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(mGoogleApiClient,
                        builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
//                final LocationSettingsStates state = result.getLocationSettingsStates();

                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        // All location settings are satisfied. The client can initialize location
                        // requests here.
                        //...
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        // Location settings are not satisfied. But could be fixed by showing the user
                        // a dialog.
                        try {
                            // Show the dialog by calling startResolutionForResult(),
                            // and check the result in onActivityResult().
                            status.startResolutionForResult(MapsActivity.this,100);
                        } catch (IntentSender.SendIntentException e) {
                            // Ignore the error.
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        // Location settings are not satisfied. However, we have no way to fix the
                        // settings so we won't show the dialog.
                        //...
                        break;
                }
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode==100){

        }

    }

    private void startLocationUpdates(){
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    123);
        }

        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);
    }
    @Override
    public void onConnected(@Nullable Bundle bundle) {


        createLocationRequest();
//        CheckGpsAvailibility();
        startLocationUpdates();


    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onLocationChanged(Location location) {
        mDatabase=FirebaseDatabase.getInstance().getReference(path+Uid+"/x");
        mDatabase.setValue(location.getLatitude());
        mDatabase=FirebaseDatabase.getInstance().getReference(path+Uid+"/y");
        mDatabase.setValue(location.getLongitude());
        if(location!=null){
            mLastLocation=location;


        }

            movToCurrentLocation();



    }


}
