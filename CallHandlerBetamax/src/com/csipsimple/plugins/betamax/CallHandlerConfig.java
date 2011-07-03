/**
 * Copyright (C) 2010 Regis Montoya (aka r3gis - www.r3gis.fr)
 * This file is part of CallHandlerBetamax.
 *
 *  CallHandlerBetamax is free software: you can redistribute it and/or modify
 *  it under the terms of the MIT Expat License.
 *
 *  CallHandlerBetamax is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  MIT Expat License. for more details.
 */
package com.csipsimple.plugins.betamax;


import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;

public class CallHandlerConfig extends PreferenceActivity implements OnSharedPreferenceChangeListener {
	public static final String KEY_TW_USER = "tw_user";
	public static final String KEY_TW_PWD = "tw_pwd";
	public static final String KEY_TW_NBR = "tw_from";
	public static final String KEY_TW_PROVIDER = "tw_provider";

	public static final String KEY_TT_ACCESS_NBR = "tt_access_nbr";
	
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.main_preference_screen);
        getPreferenceScreen().getSharedPreferences().registerOnSharedPreferenceChangeListener(this);
        updateDescriptions();
    }

	@Override
	public void onSharedPreferenceChanged(SharedPreferences sharedPreferences, String key) {
		updateDescriptions();
	}

	protected void updateDescriptions() {
		setStringFieldSummary(KEY_TW_USER);
		setStringFieldSummary(KEY_TW_NBR);
		setStringFieldSummary(KEY_TW_PROVIDER);
		setPasswordFieldSummary(KEY_TW_PWD);
		

		setStringFieldSummary(KEY_TT_ACCESS_NBR);
	}
	
	//Utilities for update Descriptions
	
	protected String getDefaultFieldSummary(String field_name){
		String val = "";
		try {
			String keyid = R.string.class.getField(field_name+"_summary").get(null).toString();
			val = getString( Integer.parseInt(keyid) );
		} catch (SecurityException e) {
			//Nothing to do : desc is null
		} catch (NoSuchFieldException e) {
			//Nothing to do : desc is null
		} catch (IllegalArgumentException e) {
			//Nothing to do : desc is null
		} catch (IllegalAccessException e) {
			//Nothing to do : desc is null
		}
		
		return val;
	}
	
	public void setStringFieldSummary(String fieldName){
		PreferenceScreen pfs = getPreferenceScreen();
		SharedPreferences sp = pfs.getSharedPreferences();
		Preference pref = pfs.findPreference(fieldName);
		
		String val = sp.getString(fieldName, "");
		if(val.equals("")){
			val = getDefaultFieldSummary(fieldName);
		}
		if(pref != null) {
			pref.setSummary(val);
		}
		
	}
	
	public void setPasswordFieldSummary(String fieldName){
		PreferenceScreen pfs = getPreferenceScreen();
		SharedPreferences sp = pfs.getSharedPreferences();
		Preference pref = pfs.findPreference(fieldName);
		
		String val = sp.getString(fieldName, "");
		
		if(val.equals("")){
			val = getDefaultFieldSummary(fieldName);
		}else{
			val = val.replaceAll(".", "*");
		}
		if(pref != null) {
			pref.setSummary(val);
		}
	}
}