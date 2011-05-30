package com.barbermot.metar;

import com.barbermot.metar.WxProvider.UpdateService;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;
import android.os.IBinder;
import android.os.IInterface;
import android.location.LocationManager;
import android.util.Log;

public class LocationStrategy implements LocationListener {
	
	public static final String TAG = "LocationStrategy";
	public static final String KEY_LOCATION = "com.barbermot.metar.lat_lon";

	private Location currentLocation = null;
	private static final int DELTA = 1000 * 60 * 5;
	private static final int DELTA_TIME = 1000 * 60 * 5;
	private static final int DELTA_DIST = 10 * 1000;
	private Context context;
	
	public LocationStrategy(Context ctx) {
		Log.d(TAG,"Started");
		
		this.context = ctx;
		
		LocationManager mgr = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
		
		//currentLocation = mgr.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
		//Location cand = mgr.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		
		//if (this.isBetterLocation(cand, currentLocation)) {
		//	currentLocation = cand;
		//}
		
		mgr.requestLocationUpdates(LocationManager.PASSIVE_PROVIDER, DELTA_TIME, DELTA_DIST, this);
	}
	
	public static Location getQuickLocation(Context ctx) {
		Location tmp = null;
		LocationManager mgr = (LocationManager) ctx.getSystemService(Context.LOCATION_SERVICE);
		
		String provider = mgr.getBestProvider(new Criteria(), true);
		Log.d(TAG,"Best provider: "+provider);
		
		if (provider != null) {
			tmp = mgr.getLastKnownLocation(provider);
			Log.d(TAG,"Last known: "+tmp);
		}
		
		return tmp;
	}
	
	public synchronized Location getLocation() {
		return currentLocation;
	}
	
	public synchronized void updateLocation(Context context, Location loc) {
	}
	
	@Override
	public void onLocationChanged(Location loc) {
		Log.d(TAG, "location: "+loc);
		StationChooser.getChooser(context).setLocation(loc);
		Intent i = new Intent(context, UpdateService.class);
		context.startService(i);
	}

	@Override
	public void onProviderDisabled(String provider) {
	}

	@Override
	public void onProviderEnabled(String provider) {
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
	}
	
	protected boolean isBetterLocation(Location location, Location currentBestLocation) {
	    if (currentBestLocation == null) {
	        return true;
	    } else if (location == null) {
	    	return false;
	    }

	    // Check whether the new location fix is newer or older
	    long timeDelta = location.getTime() - currentBestLocation.getTime();
	    boolean isSignificantlyNewer = timeDelta > DELTA;
	    boolean isSignificantlyOlder = timeDelta < -DELTA;
	    boolean isNewer = timeDelta > 0;

	    // If it's been more than two minutes since the current location, use the new location
	    // because the user has likely moved
	    if (isSignificantlyNewer) {
	        return true;
	    // If the new location is more than two minutes older, it must be worse
	    } else if (isSignificantlyOlder) {
	        return false;
	    }

	    // Check whether the new location fix is more or less accurate
	    int accuracyDelta = (int) (location.getAccuracy() - currentBestLocation.getAccuracy());
	    boolean isLessAccurate = accuracyDelta > 0;
	    boolean isMoreAccurate = accuracyDelta < 0;
	    boolean isSignificantlyLessAccurate = accuracyDelta > 200;

	    // Check if the old and new location are from the same provider
	    boolean isFromSameProvider = isSameProvider(location.getProvider(),
	            currentBestLocation.getProvider());

	    // Determine location quality using a combination of timeliness and accuracy
	    if (isMoreAccurate) {
	        return true;
	    } else if (isNewer && !isLessAccurate) {
	        return true;
	    } else if (isNewer && !isSignificantlyLessAccurate && isFromSameProvider) {
	        return true;
	    }
	    return false;
	}

	private boolean isSameProvider(String provider1, String provider2) {
	    if (provider1 == null) {
	      return provider2 == null;
	    }
	    return provider1.equals(provider2);
	}
}
