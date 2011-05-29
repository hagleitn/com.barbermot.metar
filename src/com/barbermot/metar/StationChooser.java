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
	LocationSearch ls;
	LocationStrategy locStrategy;

	List<String> defStations = new ArrayList<String>();
	List<Station> locStations = new ArrayList<Station>();
	
	public StationChooser(Context context) {
		locStrategy = new LocationStrategy(context);
		ls = new LocationSearch();
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		if (null != prefs.getString("station_4",null)) {
			defStations.add(prefs.getString("station_1", ""));
			defStations.add(prefs.getString("station_2", ""));
			defStations.add(prefs.getString("station_3", ""));
			defStations.add(prefs.getString("station_4", ""));
		}
	}
	
	public Status getStatus() {
		return status;
	}
	
	public List<Station> choose(Context context) {
		Location loc = LocationStrategy.getQuickLocation(context);
		Log.d(TAG,"Location: "+loc);
		
		List<String> names;
		if (loc == null) {
			status = Status.DEFAULT;
			names = defStations;
		} else {
			status = Status.LOCATION;
			names = ls.search(context, loc);
		}
		
		locStations.clear();
		for (String name: names) {
			locStations.add(new Station(name,true));
		}
		return locStations;
	}
	
}
