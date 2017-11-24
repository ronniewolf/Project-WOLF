package com.davinci.wolf.settings.mode;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;

/**
 * Created by aakash on 11/17/17.
 */
public class ModeViewModel extends AndroidViewModel {
	private final MutableLiveData<ModeData> eventEmitter = new MutableLiveData<>();
	
	public ModeViewModel(@NonNull Application application) {
		super(application);
		eventEmitter.setValue(new ModeData());
	}
	
	public LiveData<ModeData> getEventEmitter() {
		return eventEmitter;
	}
	
	public void setModeData(ModeData modeData) {
		eventEmitter.setValue(modeData);
	}
	
	void setMode(int mode) {
		ModeData modeData = eventEmitter.getValue();
		if (modeData == null) eventEmitter.setValue(new ModeData(mode, false));
		else if (mode != modeData.selectedIndex)
			eventEmitter.setValue(new ModeData(mode, modeData.shouldSave));
	}
	
	void setShouldUpdate(boolean shouldUpdate) {
		ModeData modeData = eventEmitter.getValue();
		if (modeData == null) eventEmitter.setValue(new ModeData(0, shouldUpdate));
		else if (shouldUpdate != modeData.shouldSave)
			eventEmitter.setValue(new ModeData(modeData.selectedIndex, shouldUpdate));
	}
	
	static class ModeData {
		int selectedIndex = 0;
		boolean shouldSave = false;
		
		ModeData() {}
		
		ModeData(int selectedIndex, boolean shouldSave) {
			this.selectedIndex = selectedIndex;
			this.shouldSave = shouldSave;
		}
	}
}
