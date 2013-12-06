package cl.dbx.aips;

import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.ScanResult;
import android.util.Log;
import android.widget.Toast;

import cl.dbx.aips.AIPSActivity;


public class WiFiScanReceiver extends BroadcastReceiver {
	  private static final String TAG = "WiFiScanReceiver";
	  AIPSActivity aipsActivity;

	  public WiFiScanReceiver(AIPSActivity aipsActivity) {
	    super();
	    this.aipsActivity = aipsActivity;
	  }

	  @Override
	  public void onReceive(Context c, Intent intent) {
		aipsActivity.isScanning = false;
	    List<ScanResult> results = aipsActivity.wifi.getScanResults();
	    ScanResult bestSignal = null;
	    String messageResult="";
	    for (ScanResult result : results) {
	    	messageResult = messageResult+ " BSSID= "+result.BSSID+" SSID="+result.SSID+" capabilities"+result.capabilities+" RSSI="+result.level+"\n\n";
	    }

	    String message = String.format("%s networks found on cycle",
	        results.size());
	    Toast.makeText(aipsActivity, message, Toast.LENGTH_LONG).show();
	    aipsActivity.textStatus.setText(messageResult);
	    aipsActivity.log2file(messageResult);
	    //Toast.makeText(aipsActivity, messageResult, Toast.LENGTH_LONG).show();

	    Log.d(TAG, "onReceive() message: " + messageResult);
	    aipsActivity.wifi.startScan();
	    aipsActivity.isScanning = true;
	  }

	}
