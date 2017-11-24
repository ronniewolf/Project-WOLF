package com.davinci.wolf.settings.manual.di;

import com.davinci.wolf.Scopes.ManualActivityScope;
import com.davinci.wolf.application.di.AppComponent;
import com.davinci.wolf.settings.manual.ManualActivity;

import dagger.Component;

/**
 * Created by aakash on 11/18/17.
 */

@ManualActivityScope
@Component(modules = {ActivityModule.class}, dependencies = {AppComponent.class})
public interface ManualComponent {
	void inject(ManualActivity manualActivity);
}
