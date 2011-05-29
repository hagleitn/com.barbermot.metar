package com.barbermot.metar;


import android.app.Service;
import android.appwidget.AppWidgetProvider;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.view.View;
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

                int i = 0;
                int lines = 0;
                for (Station s: stations) {
                	String desc = s.toString();
                	lines += desc.split("\r\n|\r|\n").length+1;
                	if (i > 10 || lines > 27) {
                		break;
                	}
                	updateViews.setTextViewText(R.id.metar_text01+2*i, s.toString());
                	updateViews.setInt(R.id.metar_text01+2*i, "setVisibility", View.VISIBLE);
                	updateViews.setInt(R.id.metar_text01+2*i+1, "setVisibility", View.VISIBLE);
                	i++;
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
