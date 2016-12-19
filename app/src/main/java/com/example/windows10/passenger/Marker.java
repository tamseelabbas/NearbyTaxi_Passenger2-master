package com.example.windows10.passenger;

import android.*;
import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class Marker extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker);
    }

    public void driverDetail(View view) {
        Intent i = new Intent(this, DriverDetail.class);
        startActivity(i);
    }


    public void onClickRequest(View view) {


        if (!MapsActivity.p.driverKey.isEmpty() && MapsActivity.p.r_status == 0) {
            Toast.makeText(this, "your prvious request is in pending", Toast.LENGTH_SHORT).show();
        } else if (!MapsActivity.p.driverKey.isEmpty() && MapsActivity.p.r_status == 1) {
            Toast.makeText(this, "your id is bounded to another taxi driver", Toast.LENGTH_SHORT).show();
        } else if (MapsActivity.p.driverKey.isEmpty() && MapsActivity.p.r_status == -1 && MapsActivity.driverForMarker.passengerKey.isEmpty() && MapsActivity.driverForMarker.r_status == -1) {
            String path = "users/driver/" + MapsActivity.driverForMarker.key + "/passengerKey";
            StaticFunc.writeStringInFireBase(path, MapsActivity.p.key);
            path = "users/driver/" + MapsActivity.driverForMarker.key + "/r_status";
            StaticFunc.writeLongInFireBase(path, (long) 0);


            path = "users/passenger/" + MapsActivity.p.key + "/driverKey";
            StaticFunc.writeStringInFireBase(path, MapsActivity.driverForMarker.key);

            path = "users/passenger/" + MapsActivity.p.key + "/r_status";
            StaticFunc.writeLongInFireBase(path, (long) 0);


            Toast.makeText(this, "request sent", Toast.LENGTH_SHORT).show();
            StaticFunc.T_Timer();
            finish();
        } else {
            Toast.makeText(this, "failed to send request", Toast.LENGTH_SHORT).show();

        }


    }

    public void onClickMessage(View view) {
        String url = "tel:" + MapsActivity.driverForMarker.contactNumber;
        Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse(url));
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling


            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE},
                    444);
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        startActivity(intent);
    }
}
