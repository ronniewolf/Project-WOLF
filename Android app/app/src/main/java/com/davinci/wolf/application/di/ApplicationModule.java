package com.davinci.wolf.application.di;

import android.content.Context;
import android.content.SharedPreferences;

import com.davinci.wolf.application.WolfApplication;
import com.davinci.wolf.Scopes.WolfApplicationScope;

import dagger.Module;
import dagger.Provides;

/**
 * Created by aakash on 11/13/17.
 */
@Module
public class ApplicationModule {
	private WolfApplication application = null;
	
	public ApplicationModule(WolfApplication application) {
		this.application = application;
	}
	
	//get application dependency, singleton
	@Provides @WolfApplicationScope
	WolfApplication getApplication() {
		return this.application;
	}
	
	//get sharedPreference dependency, singleton
	@Provides @WolfApplicationScope
	SharedPreferences getSharedPreferences(WolfApplication application) {
		return application.getSharedPreferences("DEFAULT", Context.MODE_PRIVATE);
	}
}