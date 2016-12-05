package com.example.windows10.passenger;

import android.os.CountDownTimer;
import android.os.Handler;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Timer;
import java.util.TimerTask;

import static com.example.windows10.passenger.MapsActivity.p;

/**
 * Created by WINDOWS10 on 11/27/2016.
 */

 public class StaticFunc {

    private static DatabaseReference mDatabase;

    public static void writeStringInFireBase(String path,String value){

        mDatabase = FirebaseDatabase.getInstance().getReference(path);
        mDatabase.setValue(value);

    }
    public static void writeLongInFireBase(String path,Long value){

        mDatabase = FirebaseDatabase.getInstance().getReference(path);
        mDatabase.setValue(value);

    }
    static public Handler h = new Handler();
    static public String k_key;
    static public Runnable runnable = new Runnable() {

        @Override
         public void run() {
            if(!p.driverKey.isEmpty() && p.r_status==0) {
                String pathh = "users/driver/" + p.driverKey + "/passengerKey";
                StaticFunc.writeStringInFireBase(pathh, "");
                pathh = "users/driver/" + p.driverKey + "/r_status";
                StaticFunc.writeLongInFireBase(pathh, (long) -1);


                pathh = "users/passenger/" + p.key + "/driverKey";
                StaticFunc.writeStringInFireBase(pathh, "");

                pathh = "users/passenger/" + p.key + "/r_status";
                StaticFunc.writeLongInFireBase(pathh, (long) -1);

            }
        }
    };

    public static void T_Timer(){

        h.postDelayed(runnable, 20000);
    }
    public static void Stop_T_Timer(){

        h.removeCallbacks(runnable);
    }




}
