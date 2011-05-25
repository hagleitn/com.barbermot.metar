package com.barbermot.metar;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.apache.commons.net.ftp.FTPClient;

import android.util.Log;

public class Station {
	final static String noaa = "tgftp.nws.noaa.gov";
	final static String metarPathTemplate = "/data/observations/metar/stations/%s.TXT";
	final static String tafPathTemplate = "/data/forecasts/taf/stations/%s.TXT";
	
	String id;
	String metar = null;
	String taf = null;
	
	boolean hasForecast = false;
	
	String metarPath;
	String tafPath;
	final String TAG = "Station";
	
	public Station(String id) {
		this.id = id;
		metarPath = String.format(metarPathTemplate, id);
		tafPath = String.format(tafPathTemplate, id);
		update();
	}
	
	public String getMetar() {
		return metar;
	}
	
	public String getTaf() {
		return taf;
	}
	
	public boolean hasForecast() {
		return hasForecast;
	}
	
	public String getId() {
		return id;
	}
	
	public void update() {
		metar = readWxString(metarPath).trim();
		taf = readWxString(tafPath);
		if (taf == null) {
			hasForecast = false;
		} else {
			taf = taf.trim();
			hasForecast = true;
		}
	}
	
	@Override
	public String toString() {
		StringBuffer buf = new StringBuffer();
		buf.append(getMetar());
		if (hasForecast()) {
			buf.append("\n");
			buf.append(getTaf());
		}
		return buf.toString();
	}
	
	private String readWxString(InputStream is) throws IOException {
		String wx = new Scanner(is).useDelimiter("\\A").next();
		Log.d(TAG, "read done");

		int idx = wx.indexOf('\n');
		wx = wx.substring(idx == -1?0:idx);
		Log.d(TAG,wx);
		
		return wx;
	}
	
	private String readWxString(String path) {
		InputStream is = null;
		FTPClient con = new FTPClient();
		String res = null;
		
		try
		{
		    con.connect(noaa);
		    if (con.login("anonymous", ""))
		    {
		        con.enterLocalPassiveMode();
		        
		        Log.d(TAG,"Loading path:"+path);
		        is = con.retrieveFileStream(path);
		        
		        if (is != null) {
		        	res = readWxString(is);
		        } else {
		        	Log.d(TAG, "No InputStream");
		        }
		    }
		}
		catch (Exception e)
		{
		    Log.e(TAG,"ftp problem",e);
		}

		try
		{
			if (is != null) {
				is.close();
			}
		    con.logout();
		    con.disconnect();
		}
		catch (IOException e)
		{
			Log.e(TAG,"ftp logout problem",e);
		}		
		return res;
	}
}
