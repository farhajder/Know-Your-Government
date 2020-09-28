package com.example.knowyourgovernment;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity implements DLCallBack<String>, View.OnClickListener{

    private static final String TAG = "MainActivity";
    String civicAPIURL ="https://www.googleapis.com/civicinfo/v2/representatives?key=";
    final String keyAPI = "AIzaSyA-LrhMyxAsYF5iF4uFUHbVLdQkO41QY1U";
    final String searchInput = "&address=";
    List<CivilGovernmentOfficial> civilGovernmentOfficialList;
    DataFetchTask dataFetchTask;
    RecyclerView governmentRecycleView;
    CivilGovernmentAdapter civilGovernmentAdapter;
    CardView govermentCardView;
    TextView locAddressTextView;
    OfficialAddress localaddress;
    private Locator locator;
    String address;
    static final int ACT_SHOW = 0;
    static final int NO_ITM_CLICK = -1;
    private static final String NO_RESP_REC="No response received.";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        dataFetchTask = new DataFetchTask(this);
        govermentCardView = (CardView)findViewById(R.id.cardViewCivil);
        locAddressTextView = (TextView) findViewById(R.id.mainTextView);
        if(isOnline()) {
            locator = new Locator(this);
            String apiSearch = civicAPIURL + keyAPI + searchInput + address;
            Log.d(TAG,"Addres "+address);
            dataFetchTask.execute(apiSearch);
        }
        else {
            showUserAlert("No Network Connection", "Data cannot be accessed/loaded \n without an internet connection.");
            locAddressTextView.setText("No data for Location");
        }
        setTitle("Know Your Government");
        governmentRecycleView = findViewById(R.id.mainRecycleView);
        civilGovernmentAdapter = new CivilGovernmentAdapter(this, civilGovernmentOfficialList);
        governmentRecycleView.setAdapter(civilGovernmentAdapter);
    }

    @Override
    public NetworkInfo getActiveNetworkInfo() {
        final String DEBUG_TAG = "NetworkStatusExample";
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
        boolean isWifiConn = networkInfo.isConnected();
        boolean isMobileConn = networkInfo.isConnected();
        Log.d(DEBUG_TAG, "Wifi connected: " + isWifiConn);
        Log.d(DEBUG_TAG, "Mobile connected: " + isMobileConn);
        return networkInfo;
    }

    public boolean isOnline() {
        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();
        return (networkInfo != null && networkInfo.isConnected());
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        Log.d("MainActivity","Menu inflater called ");
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.searchIcon :
                if(isOnline()) { searchDialog(); }
                else { showUserAlert("No Network Connection", "Data can not be retrieved \n without network connection"); }
                return true;
            case R.id.aboutAct :
                startAboutActivity();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private List<CivilGovernmentOfficial> readCivilJsonData(String jsonData){
        List<CivilGovernmentOfficial> lst = null;
        try {
            InputStream inputStream = new ByteArrayInputStream(jsonData.getBytes("UTF-8"));
            JSONParser JSONParser = new JSONParser(this);
            lst =  JSONParser.readCivilData(inputStream);
        }
        catch(Exception e){ e.printStackTrace(); }
        return lst;
    }

    private OfficialAddress readCivilAddressData(String jsonData){
        OfficialAddress officialAddress = null;
        try {
            InputStream inputStream = new ByteArrayInputStream(jsonData.getBytes("UTF-8"));
            JSONParser JSONParser = new JSONParser(this);
            officialAddress =  JSONParser.readAddressDetails(inputStream);
        }catch(Exception e){
            e.printStackTrace();
        }
        return officialAddress;
    }

    @Override
    public void updateFromDownload(String result) {
        if(result==null || result.length()<50 || result.equals(NO_RESP_REC)){
            showUserAlert("No Result","Can not fetch data");
            return;
        }
        else {
            civilGovernmentOfficialList = readCivilJsonData(result);
            localaddress = readCivilAddressData(result);
            Log.d(TAG," Local Address "+localaddress);
            address = localaddress.getState() + "," + localaddress.getCity() + "," + localaddress.getZip();
            locAddressTextView.setText(address);
            initCivilGovermentListView();
        }
    }

    @Override
    public void onProgressUpdate(int progressCode, int percentComplete) { }

    @Override
    public void finishDownloading() { }

    public void searchDialog(){
        final DataFetchTask dataFetchTask;
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter a City, State or Zip Code: ");
        final EditText input = new EditText(this);
        input.setGravity(Gravity.CENTER);
        input.setInputType(InputType.TYPE_CLASS_TEXT);
        input.setFilters(new InputFilter[] {new InputFilter.AllCaps()});
        builder.setView(input);
        locator = new Locator(this);
        dataFetchTask = new DataFetchTask(this);
        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String  m_Text = input.getText().toString();
                Log.d("MainActivity","test := "+ m_Text);
                if(isOnline()) {
                    locator.setUpLocationManager();
                    locator.determineLocation();
                    String apiSearch = civicAPIURL + keyAPI +searchInput+m_Text;
                    dataFetchTask.execute(apiSearch);
                }
                else{ showUserAlert("No Network Connection", "Data can not be fetched\n without network connection"); }
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }
    public void showUserAlert(String alertTitle, String alertMessage){
        LayoutInflater layoutInflater = LayoutInflater.from(MainActivity.this);
        View promptView = layoutInflater.inflate(R.layout.activity_dialog_alert, null);
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(MainActivity.this);
        alertDialogBuilder.setView(promptView);
        final TextView title = (TextView) promptView.findViewById(R.id.alertTitile);
        final TextView message = (TextView) promptView.findViewById(R.id.alertMsg);
        title.setText(alertTitle);
        message.setText(alertMessage);
        alertDialogBuilder.setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });
        AlertDialog alert = alertDialogBuilder.create();
        alert.show();
    }

    @Override
    public void onClick(View view) {
        int pos = governmentRecycleView.getChildLayoutPosition(view);
        CivilGovernmentOfficial civilGovernmentOfficial = civilGovernmentOfficialList.get(pos);
        Log.d(TAG,"Goverment officie selected => "+ civilGovernmentOfficial.getOfficeName());
        startOfficialActivity(ACT_SHOW,pos);
    }


    public void initCivilGovermentListView(){
        //civilGovernmentAdapter = new CivilGovernmentAdapter(this, civilGovernmentOfficialList);
        //governmentRecycleView.setAdapter(civilGovernmentAdapter);
        final LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        governmentRecycleView.setLayoutManager(layoutManager);
        civilGovernmentAdapter.notifyDataSetChanged();
    }

    public void startAboutActivity(){
        Intent abtActyIntent = new Intent(this,AboutActivity.class);
        startActivityForResult(abtActyIntent,0);
    }

    public void startOfficialActivity(int actionCode, int itemClickedPos){
        if(actionCode == ACT_SHOW) {
            CivilGovernmentOfficial civilGovernmentOfficial;
            if (itemClickedPos!= NO_ITM_CLICK){
                civilGovernmentOfficial = civilGovernmentOfficialList.get(itemClickedPos);
                civilGovernmentOfficial.setBannerTextAddress(localaddress);
            }
            else {
                Log.d(TAG,"Something wrong check code ");
                return;
            }
            Intent officalActIntent = new Intent(this, OfficialActivity.class);
            officalActIntent.putExtra(getString(R.string.SerializeGovementObject), civilGovernmentOfficial);
            startActivityForResult(officalActIntent, ACT_SHOW);
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == ACT_SHOW) {
            if (resultCode == RESULT_OK) {
            }
        }
    }
    private String doAddress(double latitude, double longitude) {
        Log.d(TAG, "doAddress: Latitude: " + latitude + ", Longitude: " + longitude);
        List<android.location.Address> addresses = null;
        for (int times = 0; times < 3; times++) {
            Geocoder geocoder = new Geocoder(this, Locale.getDefault());
            try {
                Log.d(TAG, "doAddress: Getting address now");
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                StringBuilder sb = new StringBuilder();
                for (android.location.Address ad : addresses) {
                    Log.d(TAG, "doLocation: " + ad);
                    sb.append(ad.getLocality()+" "+ad.getAdminArea());
                    sb.append("$" + ad.getPostalCode());

                }
                Log.d(TAG, "Address is" + sb);
                return sb.toString().trim();
            }
            catch (IOException e) {
                Log.d(TAG, "doAddress: " + e.getMessage());
            }
            Toast.makeText(this, "GeoCoder service is slow - please wait", Toast.LENGTH_SHORT).show();
        }
        Toast.makeText(this, "GeoCoder service timed out - please try again", Toast.LENGTH_LONG).show();
        return null;
    }

    public void setData(double lat, double lon) { address = doAddress(lat, lon); }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.d(TAG, "onRequestPermissionsResult: CALL: " + permissions.length);
        Log.d(TAG, "onRequestPermissionsResult: PERM RESULT RECEIVED");
        if (requestCode == 5) {
            Log.d(TAG, "onRequestPermissionsResult: permissions.length: " + permissions.length);
            for (int i = 0; i < permissions.length; i++) {
                if (permissions[i].equals(Manifest.permission.ACCESS_FINE_LOCATION)) {
                    if (grantResults[i] == PackageManager.PERMISSION_GRANTED) {
                        Log.d(TAG, "onRequestPermissionsResult: PERMISSION Granted");
                        locator.setUpLocationManager();
                        locator.determineLocation();
                    }
                    else {
                        Toast.makeText(this, "Location permission was denied - cannot determine address", Toast.LENGTH_LONG).show();
                        Log.d(TAG, "onRequestPermissionsResult: PERMISSION Denied");
                    }
                }
            }
        }
        Log.d(TAG, "onRequestPermissionsResult: Exited onRequestPermissionsResult");
    }

    public void noLocationAvailable() { Toast.makeText(this, "No location providers were available", Toast.LENGTH_LONG).show(); }

    @Override
    protected void onDestroy() {
        locator.shutdown();
        super.onDestroy();
    }
}