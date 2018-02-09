package com.davinci.wolf.console.di;

import com.davinci.wolf.console.ConsoleActivity;
import com.davinci.wolf.Scopes.ConsoleActivityScope;

import dagger.Component;

/**
 * Created by aakash on 11/13/17.
 */
@ConsoleActivityScope
@Component(modules = {ActivityModule.class, LocationModule.class, MapModule.class})
public interface ConsoleComponent {
	void inject(ConsoleActivity consoleActivity);
}
