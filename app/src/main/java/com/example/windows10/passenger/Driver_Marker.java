package com.example.windows10.passenger;

import com.google.android.gms.maps.model.Marker;

/**
 * Created by WINDOWS10 on 11/19/2016.
 */

public class Driver_Marker {
    public Marker m;
    public Driver d;


    public Driver_Marker(Marker m, Driver d) {
        this.m = m;
        this.d = d;
    }

    public Marker getM() {
        return m;
    }

    public Driver getD() {
        return d;
    }

    public void setM(Marker m) {
        this.m = m;
    }

    public void setD(Driver d) {
        this.d = d;
    }
}
