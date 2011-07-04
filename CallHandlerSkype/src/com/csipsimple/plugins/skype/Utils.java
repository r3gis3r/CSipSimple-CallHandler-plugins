/**
 * Copyright (C) 2010 Regis Montoya (aka r3gis - www.r3gis.fr)
 * This file is part of CallHandlerBetamax.
 *
 *  CallHandlerBetamax is free software: you can redistribute it and/or modify
 *  it under the terms of the Apache License
 *
 *  CallHandlerBetamax is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  Apache License for more details.
 */
package com.csipsimple.plugins.skype;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.net.Uri.Builder;

public class Utils {
	public static final String EXTRA_REMOTE_INTENT_TOKEN = "android.intent.extra.remote_intent_token";
	public static final String ACTION_GET_PHONE_HANDLERS = "com.csipsimple.phone.action.HANDLE_CALL";

	public static Bitmap getBitmapFromResource(Context ctxt, int resId) {
		Drawable icon = ctxt.getResources().getDrawable(resId);
		BitmapDrawable bd = ((BitmapDrawable) icon);
		return bd.getBitmap();
	}
	
	public static ResolveInfo getSkypePackage(Context ctxt) {
		List<ResolveInfo> candidates = getIntentsForCall(ctxt);
		for(ResolveInfo ri : candidates) {
			if(ri.activityInfo.packageName.startsWith("com.skype")) {
				return ri;
			}
		}
		return null;
	}
	
	private static List<ResolveInfo> callIntents = null;
	public final static List<ResolveInfo> getIntentsForCall(Context ctxt){
		if(callIntents == null) {
			callIntents = getPossibleActivities(ctxt, getPriviledgedIntent("123"));
		}
		return callIntents;
	}

	public static Intent getPriviledgedIntent(String number) {
		Intent i = new Intent("android.intent.action.CALL_PRIVILEGED");
		Builder b = new Uri.Builder(); 
		b.scheme("tel").appendPath(number);
		i.setData( b.build() );
		return i;
	}
	

	public static List<ResolveInfo> getPossibleActivities(Context ctxt, Intent i){
		PackageManager pm = ctxt.getPackageManager();
		try {
			return pm.queryIntentActivities(i, PackageManager.MATCH_DEFAULT_ONLY | PackageManager.GET_RESOLVED_FILTER);
		}catch(NullPointerException e) {
			return new ArrayList<ResolveInfo>();
		}
	}
	
}
