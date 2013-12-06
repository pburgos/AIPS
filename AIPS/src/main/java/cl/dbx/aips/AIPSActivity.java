package cl.dbx.aips;

import java.io.IOException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.EditText;
import android.widget.Toast;
import java.util.Date;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class AIPSActivity extends Activity implements OnClickListener {
    /** Called when the activity is first created. */
    private static final String TAG = "AIPS";
    WifiManager wifi;
    BroadcastReceiver receiver;

    TextView textStatus;
    Button buttonScanLoop;
    Button buttonStopLoop;
    EditText locationEditText;
    boolean isScanning = false;
    FileHandler handler;
    Logger logger;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // Setup UI
        textStatus = (TextView) findViewById(R.id.textStatus);
        buttonScanLoop = (Button) findViewById(R.id.buttonScanLoop);
        buttonScanLoop.setOnClickListener(this);

        buttonStopLoop = (Button) findViewById(R.id.buttonStop);
        buttonStopLoop.setOnClickListener(this);
        //Setup Location text box
        locationEditText =
                (EditText) findViewById(R.id.locationEditText);
        // Setup WiFi
        wifi = (WifiManager) getSystemService(Context.WIFI_SERVICE);

        // Get WiFi status
        WifiInfo info = wifi.getConnectionInfo();
        textStatus.append("\n\nWiFi Status: " + info.toString());
        Log.d(TAG, "onCreate()");


    }
    public void log2file(String msg){
        logger.info(msg);
    }
    public void hideKeyboard(){

        InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindow().getCurrentFocus().getWindowToken(), 0);
    }

    private void startUpWiFi() {
        // Register Broadcast Receiver
        if (receiver == null)
            receiver = new WiFiScanReceiver(this);

        registerReceiver(receiver, new IntentFilter(
                WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
    }

    @Override
    public void onStop() {
        unregisterReceiver(receiver);
    }

    @Override
    protected void onDestroy(){

        unregisterReceiver(receiver);
        super.onDestroy();

    }


    private void logfileSetup(String location){
        try{
            handler = new FileHandler("/sdcard/"+location+getDateTime()+".log");
            logger = Logger.getLogger("cl.dbx.aips");
            logger.addHandler(handler);
        } catch (IOException e) {

        }

    }
    private void logfileSetupClose(){
        logger.removeHandler(handler);
        handler.close();
    }

    private String getLocation(){
        return (String )locationEditText.getText().toString();
    }

    private String getDateTime() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss");
        Date date = new Date();
        return dateFormat.format(date);
    }
    public void onClick(View view) {

        if (view.getId() == R.id.buttonScanLoop) {
            hideKeyboard();
            if (!isScanning) {
                logfileSetup(getLocation());
                startUpWiFi();
                Log.d(TAG, "onClick() wifi.startScan()");
                wifi.startScan();
                isScanning = true;
            } else {
                Toast.makeText(this, "Scan Loop already running!",
                        Toast.LENGTH_LONG).show();
            }
        } else if (view.getId() == R.id.buttonStop) {
            if (isScanning) {
                unregisterReceiver(receiver);
                //receiver = null;
                Log.d(TAG, "onClick() wifi.stopScan()");
                isScanning = false;
                Toast.makeText(this, "Stopping!", Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(this, "Scan Loop not running!",
                        Toast.LENGTH_LONG).show();
            }
        }

    }
}