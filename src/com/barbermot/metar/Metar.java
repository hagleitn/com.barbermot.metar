package com.barbermot.metar;

import java.io.IOException;
import java.io.InputStream;
import java.util.Scanner;

import org.apache.commons.net.ftp.FTPClient;

import android.util.Log;

public class Metar {
	String id;
	String weather;
	final String noaa = "tgftp.nws.noaa.gov";
	final String path = "/data/observations/metar/stations/";
	final String TAG = "Metar";
	
	public Metar(String id) {
		this.id = id;
		update();
	}
	
	public String getWx() {
		return weather;
	}
	
	public String getId() {
		return id;
	}
	
	public void update() {
		
		InputStream is = null;
		FTPClient con = new FTPClient();
		
		try
		{
		    con.connect(noaa);
		    if (con.login("anonymous", ""))
		    {
		        con.enterLocalPassiveMode();
		        is = con.retrieveFileStream(path+id.toUpperCase()+".TXT");
		        
		        String wx = new Scanner(is).useDelimiter("\\A").next();
				Log.d(TAG, "read done");

				String[] wxs = wx.split("\\n");
				if (wxs.length > 1) { 
					weather = wxs[1];
				} else {
					weather = wx;
				}
				Log.d(TAG,weather);		        
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
	}
}
