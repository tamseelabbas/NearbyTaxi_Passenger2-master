package com.example.windows10.passenger;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

public class MyDetail extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_detail);



        TextView name=(TextView)findViewById(R.id.name2);
        TextView age=(TextView)findViewById(R.id.age2);
        TextView contactNumber=(TextView)findViewById(R.id.contactNumber2);
        TextView cnic=(TextView)findViewById(R.id.cnic2);



        if(MapsActivity.p!=null) {
            name.setText(name.getText()+ MapsActivity.p.name);
            age.setText(age.getText()+Long.toString(MapsActivity.p.age));
            contactNumber.setText(contactNumber.getText()+MapsActivity.p.contactNumber);

        cnic.setText(cnic.getText()+ MapsActivity.p.cnic);
        }
    }

    @Override
    protected void onDestroy() {
//        Toast.makeText(this,"ondistroymydetail",Toast.LENGTH_SHORT).show();
        super.onDestroy();
    }
}
