package com.davinci.wolf.application.di;

import com.davinci.wolf.application.WolfApplication;
import com.davinci.wolf.Scopes.WolfApplicationScope;

import dagger.Component;

/**
 * Created by aakash on 11/13/17.
 */
@WolfApplicationScope @Component(modules = {ApplicationModule.class})
public interface AppComponent {
	WolfApplication getWolfApplication();
	
	void inject(WolfApplication application);
}
