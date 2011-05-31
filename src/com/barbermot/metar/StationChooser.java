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
	StationSearch ls;
	LocationStrategy locStrategy;
	static StationChooser chooser;
	public final static long MAX_TIME_DELTA = 1000 * 60 * 60;
	
	Location location = null;
	
	private StationChooser(Context context) {
		locStrategy = new LocationStrategy(context);
		ls = new StationSearch();
	}
	
	public static StationChooser getChooser(Context context) {
		if (chooser != null) {
			return chooser;
		}
		return chooser = new StationChooser(context);
	}
	
	public void setLocation(Location loc) {
		this.location = loc;
	}
	
	public Status getStatus() {
		return status;
	}
	
	public List<Station> choose(Context context) {
		SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
		
		boolean useLocation = prefs.getBoolean(Preferences.LOCATION_KEY, true);
		boolean showTaf = prefs.getBoolean(Preferences.TAF_KEY, true);

		Location loc = null;
		
		if (useLocation) {
			if (location != null && 
					System.currentTimeMillis()-this.location.getTime() < MAX_TIME_DELTA) {
				loc = location;
			}
			Log.d(TAG,"Location: "+loc);
		}
		
		List<String> names;
		if (loc == null) {
			names = new ArrayList<String>();
			String[] st = prefs.getString(Preferences.STATION_KEY,"").split(",");
			for (String s: st) {
				names.add(s.trim());
			}
			status = Status.DEFAULT;
		} else {
			status = Status.LOCATION;
			names = ls.search(context, loc);
		}
		
		List<Station> locStations = new ArrayList<Station>();
		for (String name: names) {
			locStations.add(new Station(name,showTaf));
		}
		return locStations;
	}
	
}
