package com.example.windows10.passenger;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class DriverDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_driver_detail);

        TextView name=(TextView)findViewById(R.id.name1);
        TextView age=(TextView)findViewById(R.id.age1);
        TextView contactNumber=(TextView)findViewById(R.id.contactNumber1);
        TextView taxiNumber=(TextView)findViewById(R.id.taxiNumber1);
        TextView cnic=(TextView)findViewById(R.id.cnic1);



        name.setText(name.getText()+MapsActivity.driverForMarker.name);
        age.setText(age.getText()+Long.toString(MapsActivity.driverForMarker.age));
        contactNumber.setText(contactNumber.getText()+MapsActivity.driverForMarker.contactNumber);
        taxiNumber.setText(taxiNumber.getText()+MapsActivity.driverForMarker.taxiNumber);
        cnic.setText(cnic.getText()+MapsActivity.driverForMarker.cnic);


    }
}
