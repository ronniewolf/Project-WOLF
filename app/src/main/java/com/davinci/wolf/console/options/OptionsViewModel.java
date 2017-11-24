package com.davinci.wolf.console.options;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.graphics.Bitmap;
import android.support.annotation.NonNull;

/**
 * Created by aakash on 11/17/17.
 */
public class OptionsViewModel extends AndroidViewModel {
	private Bitmap background = null;
	
	public OptionsViewModel(@NonNull Application application) {
		super(application);
	}
	
	public Bitmap getBackground() {
		return background;
	}
	
	Bitmap setBackground(Bitmap background) {
		return this.background = background;
	}
}
