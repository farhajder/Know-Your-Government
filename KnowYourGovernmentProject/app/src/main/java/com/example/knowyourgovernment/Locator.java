package com.example.knowyourgovernment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import static android.content.Context.LOCATION_SERVICE;

public class Locator {
    private MainActivity owner;
    private LocationManager locationManager;
    private LocationListener locationListener;

    public Locator(MainActivity activity) {
        owner = activity;
        if (checkPermission()) {
            setUpLocationManager();
            determineLocation();
        }
    }

    public void setUpLocationManager() {

        if (locationManager != null) { return;}
        if (!checkPermission()) { return;}
        locationManager = (LocationManager) owner.getSystemService(LOCATION_SERVICE);
        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                Toast.makeText(owner, "Update from " + location.getProvider(), Toast.LENGTH_SHORT).show();
                owner.setData(location.getLatitude(), location.getLongitude());
            }
            public void onStatusChanged(String provider, int status, Bundle extras) { }
            public void onProviderEnabled(String provider) { }
            public void onProviderDisabled(String provider) { }
        };
    }

    public void shutdown() {
        try { locationManager.removeUpdates(locationListener); }
        catch (SecurityException e) { Log.e("PERMISSION_EXCEPTION","PERMISSION_NOT_GRANTED"); }
        locationManager = null;
    }

    // chooses the best location provider Network -> Passive -> GPS
    public void determineLocation() {

        if (!checkPermission()) {return;}
        if (locationManager == null) {setUpLocationManager();}
        if (locationManager != null) {
            Location loc = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            if (loc != null) {
                owner.setData(loc.getLatitude(), loc.getLongitude());
                Toast.makeText(owner, "Using " + LocationManager.NETWORK_PROVIDER + " Location provider", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (locationManager != null) {
            Location loc = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
            if (loc != null) {
                owner.setData(loc.getLatitude(), loc.getLongitude());
                Toast.makeText(owner, "Using " + LocationManager.PASSIVE_PROVIDER + " Location provider", Toast.LENGTH_SHORT).show();
                return;
            }
        }

        if (locationManager != null) {
            Location loc = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (loc != null) {
                owner.setData(loc.getLatitude(), loc.getLongitude());
                Toast.makeText(owner, "Using " + LocationManager.GPS_PROVIDER + " Location provider", Toast.LENGTH_SHORT).show();
                return;
            }
        }
        owner.noLocationAvailable();
        return;
    }

    // validates location permission from user if permission is not already set.
    private boolean checkPermission() {
        if (ContextCompat.checkSelfPermission(owner, Manifest.permission.ACCESS_FINE_LOCATION) !=
                PackageManager.PERMISSION_GRANTED) { ActivityCompat.requestPermissions(owner,
                                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 5);
            return false;
        }
        return true;
    }
}
