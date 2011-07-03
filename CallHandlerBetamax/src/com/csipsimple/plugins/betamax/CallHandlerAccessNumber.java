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

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;

public class CallHandlerAccessNumber extends BroadcastReceiver {

	private static final String THIS_FILE = "CH BETAMAX";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (Utils.ACTION_GET_PHONE_HANDLERS.equals(intent.getAction())) {
			
			PendingIntent pendingIntent = null;
			// Extract infos from intent received
			String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

			// Extract infos from settings
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			String accessNbr = prefs.getString(CallHandlerConfig.KEY_TT_ACCESS_NBR, "");
			String provider = prefs.getString(CallHandlerConfig.KEY_TW_PROVIDER, "");

			// We must handle that clean way cause when call just to
			// get the row in account list expect this to reply correctly
			if (!TextUtils.isEmpty(number) && 
					!TextUtils.isEmpty(provider)) {
				// Build pending intent
				Intent i = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + accessNbr + ",," + number + "%23")); 
				pendingIntent = PendingIntent.getBroadcast(context, 0, i, 0);
				
			}

			// Build icon
			Bitmap bmp = Utils.getBitmapFromResource(context, R.drawable.icon_access);

			// Build the result for the row (label, icon, pending intent, and
			// excluded phone number)
			Resources r = context.getResources();
			String providerName = "";
			
			Bundle results = getResultExtras(true);
			if (pendingIntent != null) {
				results.putParcelable(Utils.EXTRA_REMOTE_INTENT_TOKEN, pendingIntent);
			}
			
			if(!TextUtils.isEmpty(provider)) {
				// Text for the row
				String[] arr = r.getStringArray(R.array.provider_values);
				String[] arrEntries = r.getStringArray(R.array.provider_entries);
				int i = 0;
				for(String prov : arr) {
					if(prov.equalsIgnoreCase(provider)) {
						providerName = arrEntries[i];
						break;
					}
					i++;
				}
			}
			results.putString(Intent.EXTRA_TITLE, providerName + " "+ r.getString(R.string.access_nbr));
			Log.d(THIS_FILE, "icon is "+bmp);
			if (bmp != null) {
				results.putParcelable(Intent.EXTRA_SHORTCUT_ICON, bmp);
			}

			// DO *NOT* exclude from next tel: intent cause we use a http method
			// results.putString(Intent.EXTRA_PHONE_NUMBER, number);

		} 
	}

}
