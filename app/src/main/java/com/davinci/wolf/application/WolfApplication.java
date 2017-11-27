package com.davinci.wolf.application;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.util.Log;

import com.davinci.wolf.BuildConfig;
import com.davinci.wolf.application.di.ApplicationModule;
import com.davinci.wolf.application.di.DaggerAppComponent;
import com.davinci.wolf.settings.manual.ManualViewModel.ManualData;
import com.davinci.wolf.utils.Statics;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by aakash on 11/13/17.
 */
public class WolfApplication extends Application {
	
	/**
	 * convenience method to get and promote application from context
	 * @param context: context to get application from, usually an activity
	 */
	public static WolfApplication getWolfApplication(Context context) {
		return (WolfApplication) context.getApplicationContext();
	}
	
	private DaggerAppComponent appComponent = null;
	@Inject SharedPreferences preferences;
	
	@Override
	public void onCreate() {
		super.onCreate();
		initLogging();
		(appComponent = (DaggerAppComponent) DaggerAppComponent.builder()
			.applicationModule(new ApplicationModule(this))
			.build())
			.inject(this);
	}
	
	//method to initialize logging
	private void initLogging() {
		Timber.plant(BuildConfig.DEBUG ?
			new Timber.DebugTree() :
			new Timber.Tree() {
				@Override
				protected void log(int priority, String tag, @NonNull String message, Throwable t) {
					if (priority == Log.ERROR && t != null) Timber.e(t);
				}
			});
	}
	
	/**
	 * method to get application component to inject in other dependent components
	 */
	public DaggerAppComponent getAppComponent() {
		return appComponent;
	}
	
	//checks if user is logged in, used in LoginActivity
	public boolean isLoggedIn() {
		return preferences.getBoolean(Statics.PREF_HAS_LOGGED_IN, false);
	}
	
	//set user has logged in, used in LoginActivity
	public boolean setLoggedIn() {
		return preferences.edit().putBoolean(Statics.PREF_HAS_LOGGED_IN, true).commit();
	}
	
	//gets which is macro mode is saved, used in ModeActivity
	public int getSavedMode() {
		return preferences.getInt(Statics.PREF_SEL_MODE, 0);
	}
	
	//sets macro mode, used in ModeActivity
	public void setSavedMode(int mode) {
		preferences.edit().putInt(Statics.PREF_SEL_MODE, mode).apply();
	}
	
	//gets the ManualData from persistence to populate in ManualActivity
	public ManualData getSavedManualData() {
		return new ManualData(preferences.getInt(Statics.PREF_TRAC_LEVEL, 0),
			preferences.getInt(Statics.PREF_SHOCKS_LEVEL, 0),
			preferences.getBoolean(Statics.PREF_ABS, false));
	}
	
	//set changes in ManualData from ManualActivity to persistence
	public void setSavedManualData(ManualData manualData) {
		preferences.edit()
			.putInt(Statics.PREF_TRAC_LEVEL, manualData.getTransmission())
			.putInt(Statics.PREF_SHOCKS_LEVEL, manualData.getShocks())
			.putBoolean(Statics.PREF_ABS, manualData.getAbs())
			.apply();
	}
	
	//checks if user has already been walkthrough the app
	public boolean wasConsoleShowcased() {
		return preferences.getBoolean(Statics.PREF_CONSOLE_SHOWN, false);
	}
	
	//sets user has completed app walkthrough
	public void setConsoleShowcased() {
		preferences.edit().putBoolean(Statics.PREF_CONSOLE_SHOWN, true).apply();
	}
}
