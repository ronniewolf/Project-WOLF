package com.davinci.wolf.settings.mode;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

/**
 * Created by aakash on 11/17/17.
 * persists selected index throughout the ModeActivity lifecycle
 */
public class ModeViewModel extends AndroidViewModel {
	private final MutableLiveData<Integer> eventEmitter = new MutableLiveData<>();
	
	public ModeViewModel(@NonNull Application application) {
		super(application);
		eventEmitter.setValue(0);
	}
	
	public LiveData<Integer> getEventEmitter() {
		return eventEmitter;
	}
	
	void setMode(int mode) {
		Integer selectedIndex = eventEmitter.getValue();
		if (selectedIndex == null) eventEmitter.setValue(mode);
		else if (mode != selectedIndex)
			eventEmitter.setValue(mode);
	}
}
