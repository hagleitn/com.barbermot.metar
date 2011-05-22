package com.barbermot.metar;

import java.util.List;
import java.util.ArrayList;

import android.content.Context;
import android.location.Location;
import android.util.Log;

public class StationChooser {
	
	private final static String TAG = "StationChooser";
	
	enum Status {DEFAULT, LOCATION};
	Status status = Status.DEFAULT;

	List<Station> stations = new ArrayList<Station>();
	
	public StationChooser() {
		stations.add(new Station("KSJC"));
		stations.add(new Station("KRHV"));
		stations.add(new Station("KLVK"));
		stations.add(new Station("KSFO"));
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
		
		status = Status.LOCATION;
		return stations;
	}
	
}
