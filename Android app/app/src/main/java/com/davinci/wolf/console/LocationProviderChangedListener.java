package com.davinci.wolf.console;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.davinci.wolf.utils.Statics;

import java.util.Objects;

/**
 * Created by aakash on 11/19/17.
 * BroadcastReceiver that listens to change in location
 * provider settings
 */
public class LocationProviderChangedListener extends BroadcastReceiver {
	private ConsoleActivity consoleActivity = null;
	
	public LocationProviderChangedListener(ConsoleActivity consoleActivity) {
		this.consoleActivity = consoleActivity;
	}
	
	@Override
	public void onReceive(Context context, Intent intent) {
		if (consoleActivity == null || !Objects.equals(intent.getAction(), "android.location.PROVIDERS_CHANGED"))
			return;
		consoleActivity.toggleLocationListener(Statics.isLocationEnabled(consoleActivity));
	}
}
