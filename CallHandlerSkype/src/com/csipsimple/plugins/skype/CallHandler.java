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
package com.csipsimple.plugins.skype;

import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

public class CallHandler extends BroadcastReceiver {

	private static final String THIS_FILE = "CH SKYPE";

	@Override
	public void onReceive(Context context, Intent intent) {
		if (Utils.ACTION_GET_PHONE_HANDLERS.equals(intent.getAction())) {
			
			PendingIntent pendingIntent = null;
			// Extract infos from intent received
			String number = intent.getStringExtra(Intent.EXTRA_PHONE_NUMBER);
			ResolveInfo ri = Utils.getSkypePackage(context);
			if (!TextUtils.isEmpty(number) && ri != null) {
				// Build pending intent
				Intent i = new Intent();
				i.setAction("android.intent.action.CALL_PRIVILEGED");
				i.setPackage("com.skype.raider");
				i.setData(Uri.fromParts("tel", number, null));
				pendingIntent = PendingIntent.getActivity(context, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);
			}

			// Build icon
			Bitmap bmp = Utils.getBitmapFromResource(context, R.drawable.icon);

			// Build the result for the row (label, icon, pending intent, and
			// excluded phone number)
			
			Bundle results = getResultExtras(true);
			if (pendingIntent != null) {
				results.putParcelable(Utils.EXTRA_REMOTE_INTENT_TOKEN, pendingIntent);
			}
			
			results.putString(Intent.EXTRA_TITLE, context.getResources().getString(R.string.use_skype));
			Log.d(THIS_FILE, "icon is "+bmp);
			if (bmp != null) {
				results.putParcelable(Intent.EXTRA_SHORTCUT_ICON, bmp);
			}

			// DO *NOT* exclude from next tel: intent cause we use a http method
			// results.putString(Intent.EXTRA_PHONE_NUMBER, number);

		}
	}

}
