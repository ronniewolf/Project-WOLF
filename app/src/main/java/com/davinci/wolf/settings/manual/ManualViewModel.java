package com.davinci.wolf.settings.manual;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.support.annotation.NonNull;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.davinci.wolf.R;

/**
 * Created by aakash on 11/18/17.
 */
public class ManualViewModel extends AndroidViewModel
	implements OnSeekBarChangeListener, OnCheckedChangeListener {
	private final MutableLiveData<ManualData> eventEmitter = new MutableLiveData<>();
	
	public ManualViewModel(@NonNull Application application) {
		super(application);
		eventEmitter.setValue(new ManualData());
	}
	
	void setManualData(ManualData manualData) {
		eventEmitter.setValue(manualData);
	}
	
	public LiveData<ManualData> getEventEmitter() {
		return eventEmitter;
	}
	
	private void setTransmission(int transmission) {
		ManualData manualData = eventEmitter.getValue();
		if (manualData == null || manualData.transmission == transmission) return;
		eventEmitter.setValue(new ManualData(manualData).setTransmission(transmission));
	}
	
	private void setShocks(int shocks) {
		ManualData manualData = eventEmitter.getValue();
		if (manualData == null || manualData.shocks == shocks) return;
		eventEmitter.setValue(new ManualData(manualData).setShocks(shocks));
	}
	
	private void setAbs(boolean abs) {
		ManualData manualData = eventEmitter.getValue();
		if (manualData == null || manualData.abs == abs) return;
		eventEmitter.setValue(new ManualData(manualData).setAbs(abs));
	}
	
	@Override
	public void onProgressChanged(SeekBar seekBar, int progress, boolean b) {
		switch (seekBar.getId()) {
			case R.id.transmission:
				setTransmission(progress);
				break;
			case R.id.shocks:
				setShocks(progress);
				break;
		}
	}
	
	@Override
	public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
		setAbs(b);
	}
	
	@Override public void onStartTrackingTouch(SeekBar seekBar) {}
	
	@Override public void onStopTrackingTouch(SeekBar seekBar) {}
	
	public static class ManualData {
		int transmission = 0;
		int shocks = 0;
		boolean abs = false;
		
		ManualData() {}
		
		public ManualData(int transmission, int shocks, boolean abs) {
			this.transmission = transmission;
			this.shocks = shocks;
			this.abs = abs;
		}
		
		ManualData(ManualData manualData) {
			this.transmission = manualData.transmission;
			this.shocks = manualData.shocks;
			this.abs = manualData.abs;
		}
		
		ManualData setTransmission(int transmission) {
			this.transmission = transmission;
			return this;
		}
		
		ManualData setShocks(int shocks) {
			this.shocks = shocks;
			return this;
		}
		
		ManualData setAbs(boolean abs) {
			this.abs = abs;
			return this;
		}
		
		public int getTransmission() {
			return transmission;
		}
		
		public int getShocks() {
			return shocks;
		}
		
		public boolean getAbs() {
			return abs;
		}
		
		boolean notEqual(ManualData manualData) {
			return this.transmission != manualData.transmission
				|| this.shocks != manualData.shocks
				|| this.abs != manualData.abs;
		}
	}
}
