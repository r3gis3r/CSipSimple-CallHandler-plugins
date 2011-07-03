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
package com.csipsimple.plugins.betamax;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;

public class Utils {
	public static final String EXTRA_REMOTE_INTENT_TOKEN = "android.intent.extra.remote_intent_token";
	public static final String ACTION_GET_PHONE_HANDLERS = "com.csipsimple.phone.action.HANDLE_CALL";

	public static Bitmap getBitmapFromResource(Context ctxt, int resId) {
		Drawable icon = ctxt.getResources().getDrawable(resId);
		BitmapDrawable bd = ((BitmapDrawable) icon);
		return bd.getBitmap();
	}
}
