package com.barbermot.metar;

import com.barbermot.metar.WxProvider.UpdateService;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceActivity;
import android.preference.Preference;
import android.preference.PreferenceManager;
import android.util.Log;
import android.widget.RemoteViews;

public class Preferences extends PreferenceActivity {
	
	int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;
	public static final String TAG = "Preferences";
	
	public final static String ACTIVE_KEY="com.barbermot.metar__active";
	public final static String LOCATION_KEY="use_location";
	public final static String TAF_KEY="show_taf";
	public final static String STATION_KEY="stations";
	public final static String OK_BUTTON_KEY="ok_button";
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		setResult(RESULT_CANCELED);
		
        Intent intent = getIntent();
        Bundle extras = intent.getExtras();
        if (extras != null) {
            appWidgetId = extras.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID, AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        Log.d(TAG,"WidgetId: "+appWidgetId);
        
        if (appWidgetId == AppWidgetManager.INVALID_APPWIDGET_ID) {
            finish();
        }
		
		addPreferencesFromResource(R.xml.prefs);
		
		Preference button = (Preference) findPreference(OK_BUTTON_KEY);
		button.setOnPreferenceClickListener(new OnPreferenceClickListener() {
					public boolean onPreferenceClick(Preference preference) {
						Context context = preference.getContext();
						//WxProvider.UpdateService.buildUpdate(context);
						Editor editor = preference.getEditor();
						if (editor != null) {
							editor.putBoolean(ACTIVE_KEY+appWidgetId, true);
							editor.commit();
						}
			            Intent resultValue = new Intent();
			            resultValue.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			            setResult(RESULT_OK, resultValue);
			            finish();
			            context.startService(new Intent(context, UpdateService.class));
			            return true;
					}

				});
	}
}