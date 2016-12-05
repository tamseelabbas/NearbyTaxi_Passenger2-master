package com.example.windows10.passenger;

/**
 * Created by WINDOWS10 on 11/16/2016.
 */

public class Driver {

    public String key;
    public String name;
    public String contactNumber;
    public String cnic;
    public String taxiNumber;
    public long age;
    public double x;
    public double y;

    public long status;
    public String passengerKey;
    public long r_status;

    public Driver() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public Driver(String key,String name,String contactNumber,long age,String cnic,String taxiNumber,double x,double y,long status,String passengerKeyKey,long r_status) {
        this.key=key;
        this.name=name;
        this.contactNumber=contactNumber;
        this.age=age;
        this.x=x;
        this.y=y;
        this.status=status;
        this.passengerKey=passengerKeyKey;
        this.r_status=r_status;
        this.cnic=cnic;
        this.taxiNumber=taxiNumber;


    }
}
