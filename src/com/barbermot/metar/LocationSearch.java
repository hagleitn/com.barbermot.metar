package com.barbermot.metar;

import java.io.BufferedInputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Scanner;
import java.util.SortedSet;
import java.util.TreeSet;

import android.content.Context;
import android.location.Location;
import android.util.Log;

public class LocationSearch {
	
	private final String TAG = "LocationSearch";
	
	private class Loc implements Comparable<Loc> {
		public String name;
		public float x;
		public float y;
		public float dist;
		
		public int compareTo(Loc l) {
			if (l.dist > this.dist) {
				return -1;
			} else if (l.dist < this.dist) {
				return 1;
			} else {
				return this.name.compareTo(l.name);
			}
		}
		
		private void setDist(Location loc) {
			float y = (float)loc.getLatitude();
			float x = (float)loc.getLongitude();
			dist = (float) (Math.pow(x-this.x,2)+Math.pow(y-this.y,2));
		}
		
		public String toString() {
			return "{"+name+", "+x+", "+y+"}";
		}
	}
	
	public List<String> findFiles(Location loc) {
		List<String> l = new ArrayList<String>(9);
		int y = (int)loc.getLatitude();
		String sn = y>0?"n":"s";
		int x = (int)loc.getLongitude();
		String ew = x>0?"e":"w";
		y = Math.abs(y);
		x = Math.abs(x);
		y = (y/3)*3;
		x = (x/3)*3;
		String file = String.format("l_%02d%s_%03d%s", y,sn,x,ew);
		Log.d(TAG,"Using file: "+file);
		l.add(file);
		return l;
		
	}
	
	public List<String> search(Context context, Location loc) {
		List<String> results = new LinkedList<String>();
		SortedSet<Loc> locations = new TreeSet<Loc>();
		for (String f: findFiles(loc)) {
			addFromFile(context, locations, f, loc);
		}
		for (Loc l: locations) {
			results.add(l.name);
		}
		return results;
	}
		
	public void addFromFile(Context context, SortedSet<Loc> stations, String file, Location loc) {
		Loc tail = stations.size()>0?stations.last():null;
		try {
			int resID = context.getResources().getIdentifier("com.barbermot.metar:raw/"+file, null, null);
			InputStream in = context.getResources().openRawResource(resID);
			Scanner scan = new Scanner(new BufferedInputStream(in));
			while (scan.hasNext()) {
				Loc l = new Loc();
				String tmp;
				l.name = scan.next();
				
				tmp = scan.next();
				Log.d(TAG,"Latitude: "+tmp);
				if (tmp.contains("S")) {
					tmp = tmp.replace("S", "");
					Log.d(TAG,tmp);
					l.y = Float.parseFloat(tmp);
					l.y *= -1;
				} else if (tmp.contains("N")) {
					tmp = tmp.replaceAll("N", "");
					Log.d(TAG,tmp);
					l.y = Float.parseFloat(tmp);
				} else {
					Log.d(TAG, "Failed to parse float..."+tmp);
					continue;
				}
				
				tmp = scan.next();
				Log.d(TAG,"Longitude: "+tmp);
				if (tmp.contains("W")) {
					tmp = tmp.replace("W", "");
					Log.d(TAG,tmp);
					l.x = Float.parseFloat(tmp);
					l.x *= -1;
				} else if (tmp.contains("E")) {
					tmp = tmp.replace("E", "");
					Log.d(TAG,tmp);
					l.x = Float.parseFloat(tmp);
				} else {
					Log.d(TAG, "Failed to parse float..."+tmp);
					if (scan.hasNext()) scan.next();
					continue;
				}
				
				l.setDist(loc);
				
				if (stations.size() < 5 || tail.compareTo(l) > 0) {
					Log.d(TAG,"Adding "+l);
					stations.add(l);
					if (stations.size() > 5) {
						tail = stations.last();
						Log.d(TAG,"Removing"+tail);
						stations.remove(tail);
						
					} 
					tail = stations.last();
				}
				Log.d(TAG,"Looked at:"+l.toString());
			}
		} catch (Exception e) {
			Log.d(TAG,"Problem in loading stations",e);
		}
	}
}
