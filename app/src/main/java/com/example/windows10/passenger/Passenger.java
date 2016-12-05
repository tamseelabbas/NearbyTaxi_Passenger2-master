package com.example.windows10.passenger;

import java.util.List;

/**
 * Created by WINDOWS10 on 11/16/2016.
 */

public class Passenger {

public String key;
    public String name;
    public String contactNumber;
    public long age;
    public double x;
    public double y;

    public long destinationX;
    public long destinationY;
    public long status;
public String driverKey;
    public long r_status;
    public String cnic;


    public Passenger() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Passenger(String key,String name,String contactNumber,long age,String cnic,double x,double y,long status,String driverKey,long r_status,long destinationX,long destinationY) {
        this.key=key;
        this.name=name;
        this.contactNumber=contactNumber;
        this.age=age;
        this.x=x;
        this.y=y;
        this.status=status;
        this.driverKey=driverKey;
        this.r_status=r_status;
        this.destinationX=destinationX;
        this.destinationY=destinationY;
        this.cnic=cnic;


    }
}
