package com.barbermot.metar;

import java.util.List;
import java.util.ArrayList;

import android.content.Context;
import android.location.Location;
import android.util.Log;

import android.content.SharedPreferences;
import android.preference.PreferenceManager;

public class StationChooser {
	
	private final static String TAG = "StationChooser";
	
	enum Status {DEFAULT, LOCATION};
	Status status = Status.DEFAULT;

	List<Station> stations = new ArrayList<Station>();
	
	public StationChooser(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		if (null != prefs.getString("station_4",null)) {
			stations.add(new Station(prefs.getString("station_1", ""), prefs.getBoolean("show_taf_1", true)));
			stations.add(new Station(prefs.getString("station_2", ""), prefs.getBoolean("show_taf_2", true)));
			stations.add(new Station(prefs.getString("station_3", ""), prefs.getBoolean("show_taf_3", true)));
			stations.add(new Station(prefs.getString("station_4", ""), prefs.getBoolean("show_taf_4", true)));
		}
	}
	
	public Status getStatus() {
		return status;
	}
	
	public List<Station> choose(Context context) {
		Location loc = LocationStrategy.getQuickLocation(context);
		Log.d(TAG,"Location: "+loc);
		
		if (loc == null) {
			status = Status.DEFAULT;
			return stations;
		}
		
		// TODO: Find closest
		status = Status.LOCATION;
		return stations;
	}
	
}
