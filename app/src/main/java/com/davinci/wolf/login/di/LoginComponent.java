package com.davinci.wolf.login.di;

import com.davinci.wolf.Scopes.LoginActivityScope;
import com.davinci.wolf.login.LoginActivity;

import dagger.Component;

/**
 * Created by aakash on 11/14/17.
 */
@LoginActivityScope @Component(modules = {ActivityModule.class})
public interface LoginComponent {
	void inject(LoginActivity loginActivity);
}
