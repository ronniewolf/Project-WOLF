package com.davinci.wolf.settings.manual.di;

import android.arch.lifecycle.ViewModelProviders;
import android.widget.SeekBar;
import android.widget.Switch;

import com.davinci.wolf.R;
import com.davinci.wolf.Scopes.ManualActivityScope;
import com.davinci.wolf.settings.manual.ManualActivity;
import com.davinci.wolf.settings.manual.ManualViewModel;

import dagger.Module;
import dagger.Provides;

/**
 * Created by aakash on 11/18/17.
 */
@Module @ManualActivityScope
public class ActivityModule {
	private ManualActivity manualActivity = null;
	
	public ActivityModule(ManualActivity manualActivity) {
		this.manualActivity = manualActivity;
	}
	
	@Provides @ManualActivityScope
	ManualActivity getManualActivity() {
		return this.manualActivity;
	}
	
	@Provides @ManualActivityScope
	ManualViewModel getViewModel(ManualActivity manualActivity) {
		ManualViewModel viewModel = ViewModelProviders.of(manualActivity).get(ManualViewModel.class);
		((SeekBar) manualActivity.findViewById(R.id.transmission)).setOnSeekBarChangeListener(viewModel);
		((SeekBar) manualActivity.findViewById(R.id.shocks)).setOnSeekBarChangeListener(viewModel);
		((Switch) manualActivity.findViewById(R.id.abs)).setOnCheckedChangeListener(viewModel);
		viewModel.getEventEmitter().observe(manualActivity, manualActivity);
		return viewModel;
	}
}
