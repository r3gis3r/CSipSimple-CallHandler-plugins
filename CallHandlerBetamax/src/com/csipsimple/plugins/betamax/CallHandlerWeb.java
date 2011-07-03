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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

public class CallHandlerWeb extends BroadcastReceiver {

	private static final String THIS_FILE = "CH BETAMAX";
	public static final String ACTION_DO_WEB_CALL = "com.csipsimple.plugins.betamax.DO_WEB_CALL";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (Utils.ACTION_GET_PHONE_HANDLERS.equals(intent.getAction())) {
			
			PendingIntent pendingIntent = null;
			// Extract infos from intent received
			String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

			// Extract infos from settings
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			String user = prefs.getString(CallHandlerConfig.KEY_TW_USER, "");
			String pwd = prefs.getString(CallHandlerConfig.KEY_TW_PWD, "");
			String nbr = prefs.getString(CallHandlerConfig.KEY_TW_NBR, "");
			String provider = prefs.getString(CallHandlerConfig.KEY_TW_PROVIDER, "");

			// We must handle that clean way cause when call just to
			// get the row in account list expect this to reply correctly
			if (!TextUtils.isEmpty(number) && 
					!TextUtils.isEmpty(user) && 
					!TextUtils.isEmpty(nbr) && 
					!TextUtils.isEmpty(pwd) &&
					!TextUtils.isEmpty(provider)) {
				// Build pending intent
				Intent i = new Intent(ACTION_DO_WEB_CALL);
				i.setClass(context, getClass());
				i.putExtra(Intent.EXTRA_PHONE_NUMBER, number);
				pendingIntent = PendingIntent.getBroadcast(context, 0, i, 0);
				
			}

			// Build icon
			Bitmap bmp = Utils.getBitmapFromResource(context, R.drawable.icon_web);

			// Build the result for the row (label, icon, pending intent, and
			// excluded phone number)
			Bundle results = getResultExtras(true);
			if (pendingIntent != null) {
				results.putParcelable(Utils.EXTRA_REMOTE_INTENT_TOKEN, pendingIntent);
			}
			
			// Text for the row
			String providerName = "";
			Resources r = context.getResources();
			if(!TextUtils.isEmpty(provider)) {
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
			results.putString(Intent.EXTRA_TITLE, providerName + " "+ r.getString(R.string.web_callback));
			Log.d(THIS_FILE, "icon is "+bmp);
			if (bmp != null) {
				results.putParcelable(Intent.EXTRA_SHORTCUT_ICON, bmp);
			}

			// DO *NOT* exclude from next tel: intent cause we use a http method
			// results.putString(Intent.EXTRA_PHONE_NUMBER, number);

		} else if (ACTION_DO_WEB_CALL.equals(intent.getAction())) {
			
			// Extract infos from intent received
			String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);

			// Extract infos from settings
			SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
			String user = prefs.getString(CallHandlerConfig.KEY_TW_USER, "");
			String pwd = prefs.getString(CallHandlerConfig.KEY_TW_PWD, "");
			String nbr = prefs.getString(CallHandlerConfig.KEY_TW_NBR, "");
			String provider = prefs.getString(CallHandlerConfig.KEY_TW_PROVIDER, "");

			// params
			List<NameValuePair> params = new LinkedList<NameValuePair>();
			params.add(new BasicNameValuePair("username", user));
			params.add(new BasicNameValuePair("password", pwd));
			params.add(new BasicNameValuePair("from", nbr));
			params.add(new BasicNameValuePair("to", number));
			String paramString = URLEncodedUtils.format(params, "utf-8");

			String requestURL = "https://www."+provider+"/myaccount/makecall.php?" + paramString;

			HttpClient httpClient = new DefaultHttpClient();
			HttpGet httpGet = new HttpGet(requestURL);

			// Create a response handler
			HttpResponse httpResponse;
			try {
				httpResponse = httpClient.execute(httpGet);
				Log.d(THIS_FILE, "response code is " + httpResponse.getStatusLine().getStatusCode() );
				if (httpResponse.getStatusLine().getStatusCode() == 200) {
					InputStreamReader isr = new InputStreamReader(httpResponse.getEntity().getContent());
					BufferedReader br = new BufferedReader(isr);
					
					String line;
					String fullReply = "";
					boolean foundSuccess = false;
					while( (line = br.readLine()) != null) {
						if(!TextUtils.isEmpty(line) && line.toLowerCase().contains("success")) {
							showToaster(context, "Success... wait a while you'll called back" );
							foundSuccess = true;
							break;
						}
						if(!TextUtils.isEmpty(line)) {
							fullReply = fullReply.concat(line);
						}
					}
					if(!foundSuccess) {
						showToaster(context, "Error : server error : "+fullReply );
					}
				} else {
					showToaster(context, "Error : invalid request " + httpResponse.getStatusLine().getStatusCode() );
				}
			} catch (ClientProtocolException e) {
				showToaster(context, "Error : " + e.getLocalizedMessage());
			} catch (IOException e) {
				showToaster(context, "Error : " + e.getLocalizedMessage());
			}

		}
	}

	
	private void showToaster(Context context, String text) {
		Toast.makeText(context, text, Toast.LENGTH_LONG).show();
	}
}
