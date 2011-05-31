package com.barbermot.metar;


import android.app.Service;
import android.appwidget.AppWidgetProvider;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.IBinder;
import android.preference.PreferenceManager;
import android.view.View;
import android.widget.RemoteViews;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.List;
import android.util.Log;

import com.barbermot.metar.R;

public class WxProvider extends AppWidgetProvider {
	
	final static String TAG = "WxProvider";
	
	public WxProvider() {
		Log.d(TAG,"New provider started.");
	}
	
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
        int[] appWidgetIds) {
    	Log.d(TAG,"Update");
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    	boolean active = false;
    	for (int i :appWidgetIds) {
    		active = active || prefs.getBoolean(Preferences.ACTIVE_KEY+appWidgetIds, false);
    	}
    	if (active) {
    		context.startService(new Intent(context, UpdateService.class));
    	}
    }
    
    @Override 
    public void onDeleted(Context context, int[] appWidgetIds) {
    	SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
    	Editor editor = prefs.edit();
    	if (editor != null) {
    		for (int i: appWidgetIds) {
    			Log.d(TAG,"Removing: "+Preferences.ACTIVE_KEY+i);
    			editor.remove(Preferences.ACTIVE_KEY+i);
    		}
    		editor.commit();
    	}
    }

    public static class UpdateService extends Service {
    	
        @Override
        public void onStart(Intent intent, int startId) {
            RemoteViews updateViews = buildUpdate(this);
          
            // Push update for this widget to the home screen
            ComponentName thisWidget = new ComponentName(this, WxProvider.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(this);
            manager.updateAppWidget(thisWidget, updateViews);
           
            //this.stopSelf();
        }

        public static RemoteViews buildUpdate(Context context) {
        	StationChooser chooser = StationChooser.getChooser(context);
            RemoteViews updateViews = null;
            
            List<Station> stations = chooser.choose(context);

            updateViews = new RemoteViews(context.getPackageName(), R.layout.main);

            int i = 0;
            int lines = 0;
            for (Station s: stations) {
            	if (!s.hasObservation && !s.hasForecast) {
            		continue;
            	}
            	String desc = s.toString();
            	lines += desc.split("\r\n|\r|\n").length+1;
            	if (i > 10 || lines > 26) {
            		break;
            	}
            	updateViews.setTextViewText(R.id.metar_text01+2*i, s.toString());
            	updateViews.setInt(R.id.metar_text01+2*i, "setVisibility", View.VISIBLE);
            	updateViews.setInt(R.id.metar_text01+2*i+1, "setVisibility", View.VISIBLE);
            	i++;
            }
            
            Calendar cal = Calendar.getInstance();
            DateFormat df = DateFormat.getDateTimeInstance(DateFormat.SHORT,
                DateFormat.SHORT);
            
            String date = df.format(cal.getTime());

            if (i == 0) {
            	updateViews.setTextViewText(R.id.location_status, "Last update: "+ date + ", no data available.");
            } else {
            	switch(chooser.getStatus()) {
            	case DEFAULT:
            		updateViews.setTextViewText(R.id.location_status, "Last update: "+date+", default stations.");
            		break;
            	case LOCATION:
            		updateViews.setTextViewText(R.id.location_status, "Last update: "+date+", closest stations.");
            		break;
            	}
            }
            return updateViews;
        }

        @Override
        public IBinder onBind(Intent intent) {
            return null;
        }
    }
}
