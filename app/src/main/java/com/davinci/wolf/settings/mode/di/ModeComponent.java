package com.davinci.wolf.settings.mode.di;

import com.davinci.wolf.application.di.AppComponent;
import com.davinci.wolf.Scopes.ModeActivityScope;
import com.davinci.wolf.settings.mode.ModeActivity;

import dagger.Component;

/**
 * Created by aakash on 11/18/17.
 */
@ModeActivityScope
@Component(modules = {ActivityModule.class}, dependencies = {AppComponent.class})
public interface ModeComponent {
	void inject(ModeActivity modeActivity);
}
