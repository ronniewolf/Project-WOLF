package com.davinci.wolf.console.di;

import com.davinci.wolf.BuildConfig;
import com.davinci.wolf.console.ConsoleActivity;
import com.davinci.wolf.Scopes.ConsoleActivityScope;
import com.davinci.wolf.utils.FusedLocationProvider;
import com.google.android.gms.location.LocationRequest;

import dagger.Module;
import dagger.Provides;

/**
 * Created by aakash on 11/13/17.
 */
@Module class LocationModule {
	@Provides @ConsoleActivityScope
	LocationRequest getRequest() {
		return new LocationRequest()
			.setInterval(BuildConfig.LOCATION_DELAY)//500ms
			.setFastestInterval(BuildConfig.LOCATION_DELAY)//500ms
			.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
	}
	
	@Provides @ConsoleActivityScope
	FusedLocationProvider getFusedLocationProvider(ConsoleActivity activity, LocationRequest request) {
		FusedLocationProvider locationProvider = new FusedLocationProvider(activity, request);
		locationProvider.setLocationChangedListener(activity);
		return locationProvider;
	}
}