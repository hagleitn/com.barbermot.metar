package com.barbermot.metar;


import android.app.Service;
import android.appwidget.AppWidgetProvider;

import java.util.ArrayList;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.IBinder;
import android.os.SystemClock;
import android.util.Log;
import android.widget.RemoteViews;
import java.util.List;

import com.barbermot.metar.R;

public class WxProvider extends AppWidgetProvider {
	
	final static String TAG = "MetarProvider";
	
    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager,
        int[] appWidgetIds) {
        // To prevent any ANR timeouts, we perform the update in a service
        context.startService(new Intent(context, UpdateService.class));
    }

    public static class UpdateService extends Service {
        @Override
        public void onStart(Intent intent, int startId) {
            // Build the widget update for today
            RemoteViews updateViews = buildUpdate(this);

            // Push update for this widget to the home screen
            ComponentName thisWidget = new ComponentName(this, WxProvider.class);
            AppWidgetManager manager = AppWidgetManager.getInstance(this);
            manager.updateAppWidget(thisWidget, updateViews);
        }

        public RemoteViews buildUpdate(Context context) {
            // Pick out month names from resources
            // Resources res = context.getResources();
            RemoteViews updateViews = null;
            
            StringBuffer buf = new StringBuffer();
    		for (Station m: getMetars()) {
    			buf.append(m.getWx());
    			buf.append("\n\n");
    		}
    		
    		String wx = buf.toString();
    		
            if (true) {
                updateViews = new RemoteViews(context.getPackageName(), R.layout.main);
    
                updateViews.setTextViewText(R.id.metar_text, wx);
                
                /*
                // When user clicks on widget, launch to Wiktionary definition page
                String definePage = res.getString(R.string.template_define_url,
                        Uri.encode(wordTitle));
                Intent defineIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(definePage));
                PendingIntent pendingIntent = PendingIntent.getActivity(context,
                        0 , defineIntent, 0 );
               
                updateViews.setOnClickPendingIntent(R.id.widget, pendingIntent); */
    
            } else {
                // Didn't find word of day, so show error message
                updateViews = new RemoteViews(context.getPackageName(), R.layout.main);
                updateViews.setTextViewText(R.id.metar_text, "Unable");
            }
            return updateViews;
        }

        @Override
        public IBinder onBind(Intent intent) {
            // We don't need to bind to this service
            return null;
        }
        
        private List<Station> getMetars() {
    		List<Station> l = new ArrayList<Station>(1);
    		l.add(new Station("KSFO"));
    		l.add(new Station("KSJC"));
    		l.add(new Station("KRHV"));
    		return l;
    	}
    }
}