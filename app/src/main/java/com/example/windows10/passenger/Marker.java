package com.example.windows10.passenger;

import android.content.Intent;
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
        Intent i=new Intent(this,DriverDetail.class);
        startActivity(i);
    }


    public void onClickRequest(View view) {



            if(!MapsActivity.p.driverKey.isEmpty() && MapsActivity.p.r_status==0){
                Toast.makeText(this,"your prvious request is in pending",Toast.LENGTH_SHORT).show();
            }
            else if(!MapsActivity.p.driverKey.isEmpty() && MapsActivity.p.r_status==1){
                Toast.makeText(this,"your id is bounded to another taxi driver",Toast.LENGTH_SHORT).show();
            }
            else if(MapsActivity.p.driverKey.isEmpty() && MapsActivity.p.r_status==-1 && MapsActivity.driverForMarker.passengerKey.isEmpty() && MapsActivity.driverForMarker.r_status==-1){
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
            }
        else {
                Toast.makeText(this, "failed to send request", Toast.LENGTH_SHORT).show();

            }






    }
}
