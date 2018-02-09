package com.davinci.wolf.login.di;

import android.arch.lifecycle.ViewModelProviders;

import com.davinci.wolf.R;
import com.davinci.wolf.Scopes.LoginActivityScope;
import com.davinci.wolf.application.WolfApplication;
import com.davinci.wolf.login.EditTextObserver;
import com.davinci.wolf.login.LoginActivity;
import com.davinci.wolf.login.LoginViewModel;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by aakash on 11/14/17.
 */
@Module
public class ActivityModule {
	private LoginActivity loginActivity = null;
	
	public ActivityModule(LoginActivity loginActivity) {
		this.loginActivity = loginActivity;
	}
	
	@Provides @LoginActivityScope
	LoginActivity getLoginActivity() {
		return this.loginActivity;
	}
	
	@Provides @LoginActivityScope
	WolfApplication getApplication() {
		return WolfApplication.getWolfApplication(this.loginActivity);
	}
	
	@Provides @LoginActivityScope
	LoginViewModel getLoginViewModel(LoginActivity loginActivity) {
		return ViewModelProviders.of(loginActivity).get(LoginViewModel.class);
	}
	
	@Provides @Named("username") @LoginActivityScope
	EditTextObserver getUsername(LoginActivity loginActivity, LoginViewModel loginViewModel) {
		return new EditTextObserver(loginActivity.findViewById(R.id.username), loginViewModel);
	}
	
	@Provides @Named("password") @LoginActivityScope
	EditTextObserver getPassword(LoginActivity loginActivity, LoginViewModel loginViewModel) {
		return new EditTextObserver(loginActivity.findViewById(R.id.password), loginViewModel);
	}
}
