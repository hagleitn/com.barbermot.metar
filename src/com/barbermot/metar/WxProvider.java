package com.barbermot.metar;


import android.app.Service;
import android.appwidget.AppWidgetProvider;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.widget.RemoteViews;
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
        context.startService(new Intent(context, UpdateService.class));
        Log.d(TAG, "Update done.");
    }

    public static class UpdateService extends Service {
    	static StationChooser chooser;
    	
    	public static StationChooser getChooser(Context context) {
    		if (chooser != null) {
    			return chooser;
    		}
    		return chooser = new StationChooser(context);
    	}
    	
        @Override
        public void onStart(Intent intent, int startId) {
            RemoteViews updateViews = buildUpdate(this);

            // Push update for this widget to the home screen
            ComponentName thisWidget = new ComponentName(this, WxProvider.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(this);
            manager.updateAppWidget(thisWidget, updateViews);
        }

        public static RemoteViews buildUpdate(Context context) {
        	chooser = getChooser(context);
            RemoteViews updateViews = null;
            
            List<Station> stations = chooser.choose(context);
            
            if (true) {
                updateViews = new RemoteViews(context.getPackageName(), R.layout.main);

                if (stations.size() > 0) {
                	updateViews.setTextViewText(R.id.metar_text01, stations.get(0).toString());
                }
                
                if (stations.size() > 1) {
                	updateViews.setTextViewText(R.id.metar_text02, stations.get(1).toString());
                }
                
                if (stations.size() > 2) {
                	updateViews.setTextViewText(R.id.metar_text03, stations.get(2).toString());
                }
                
                if (stations.size() > 3) {
                	updateViews.setTextViewText(R.id.metar_text04, stations.get(3).toString());
                }
                
                switch(chooser.getStatus()) {
                case DEFAULT:
                	updateViews.setTextViewText(R.id.location_status, "No location available.");
                	break;
                case LOCATION:
                	updateViews.setTextViewText(R.id.location_status, "Location available");
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
