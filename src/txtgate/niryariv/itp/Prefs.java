package txtgate.niryariv.itp;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.EditTextPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;


public class Prefs extends PreferenceActivity {
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		addPreferencesFromResource(R.xml.prefs);
	}
	
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
	    Preference pref = findPreference(key);

	    if (pref instanceof EditTextPreference) {
	    	EditTextPreference textPref = (EditTextPreference) pref;
	        pref.setSummary(textPref.getSummary());
	        Log.d("TXTGATE", "textPref.getSummary(): " + textPref.getSummary());
	    }
	}
}

