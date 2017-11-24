package com.davinci.wolf.login;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

/**
 * Created by aakash on 11/14/17.
 */
public class LoginViewModel extends AndroidViewModel {
	private final MutableLiveData<String> username = new MutableLiveData<>();
	private final MutableLiveData<String> password = new MutableLiveData<>();
	
	public LoginViewModel(@NonNull Application application) {
		super(application);
	}
	
	LiveData<String> getUsername() {
		return username;
	}
	
	LiveData<String> getPassword() {
		return password;
	}
	
	void setUsername(String username) {
		this.username.postValue(username);
	}
	
	void setPassword(String password) {
		this.password.postValue(password);
	}
}
