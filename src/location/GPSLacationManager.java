package location;

import java.util.List;
import java.util.Locale;

import edu.kuas.mis.wmc.app.Util;

import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Toast;

public class GPSLacationManager implements LocationListener {
	private GPSLacationListener gpsCB;
	private Context mContext; 
	private boolean gpsEnabled = false;
	private LocationManager mLocationManager;
    private String bestProvider = LocationManager.GPS_PROVIDER;
	
	public GPSLacationManager(Context context) {
		this.mContext = context;
		detectLocationProvider();
		onResume(5000l);
	}
	
	public GPSLacationManager(Context context, GPSLacationListener cb) {
		this(context);
		this.gpsCB = cb;
		makeLocationCallBack();
	}
	
	public void onResume(long minTime) {
		if(gpsEnabled) {
			mLocationManager.requestLocationUpdates(bestProvider, minTime, 1, this);
		}
	}
	
	public void onPause() {
		if(gpsEnabled) {
			mLocationManager.removeUpdates(this);
		}
	}
	
	public void onRestart() {
		detectLocationProvider();
	}
	
	public Location getLocation() {
    	if(mLocationManager != null && gpsEnabled) {
    		Location location = mLocationManager.getLastKnownLocation(bestProvider);
    		return location;
    	} else {
    		Toast.makeText(mContext, "無法定位座標", Toast.LENGTH_LONG).show();
    		return null;
    	}
    }
	
	public String getAddressByLocation(Location location) {
		String returnAddress = null;
		
		try {
			if(location != null) {
				//取得經度
	    		Double longitude = location.getLongitude();
	    		//取得緯度
	    		Double latitude = location.getLatitude();

	    		Geocoder gc = new Geocoder(mContext, Locale.TRADITIONAL_CHINESE);
	    		List<Address> lstAddress = gc.getFromLocation(latitude, longitude, 1);
	    		returnAddress = lstAddress.get(0).getAddressLine(0);
			}
		} catch(Exception e) {
			Util.log("GPSLacationManager.getAddressByLocation() error: " + e.getMessage());
		}
		return returnAddress;
	}
	
	public void regist(GPSLacationListener cb) {
		this.gpsCB = cb;
	}
	
	public void unRegist() {
		this.gpsCB = null;
	}

	@Override
	public void onLocationChanged(Location location) {
		if(gpsCB != null) {
			gpsCB.onLocationChanged(location);
		}
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		if(gpsCB != null) {
			gpsCB.onStatusChanged(provider, status, extras);
		}
	}

	@Override
	public void onProviderEnabled(String provider) {
		gpsEnabled = true;
	}

	@Override
	public void onProviderDisabled(String provider) {
		gpsEnabled = false;
		gpsOpenMsgShow();
	}

	private void detectLocationProvider() {
		LocationManager status = (LocationManager)(mContext.getSystemService(Context.LOCATION_SERVICE));
        if(status.isProviderEnabled(LocationManager.GPS_PROVIDER) || status.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
        	gpsEnabled = true;
        	mLocationManager = (LocationManager)mContext.getSystemService(Context.LOCATION_SERVICE);
        	Criteria criteria = new Criteria();
        	bestProvider = mLocationManager.getBestProvider(criteria, true);
        } else {
        	gpsOpenMsgShow();
        }
	}
	
	private void gpsOpenMsgShow() {
		Toast.makeText(mContext, "請開啟定位服務", Toast.LENGTH_LONG).show();
    	mContext.startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
	}
	
	private void makeLocationCallBack() {
		if(gpsCB != null) {
    		gpsCB.onLocationChanged(getLocation());
    	}
	}
}
