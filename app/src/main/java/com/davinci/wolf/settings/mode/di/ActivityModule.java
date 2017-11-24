package com.davinci.wolf.settings.mode.di;

import android.arch.lifecycle.ViewModelProviders;

import com.davinci.wolf.R;
import com.davinci.wolf.Scopes.ModeActivityScope;
import com.davinci.wolf.settings.mode.ModeActivity;
import com.davinci.wolf.settings.mode.ModeTile;
import com.davinci.wolf.settings.mode.ModeViewModel;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by aakash on 11/18/17.
 */
@Module @ModeActivityScope
public class ActivityModule {
	private ModeActivity modeActivity = null;
	
	public ActivityModule(ModeActivity modeActivity) {
		this.modeActivity = modeActivity;
	}
	
	@Provides @ModeActivityScope
	ModeActivity getModeActivity() {
		return modeActivity;
	}
	
	@Provides @ModeActivityScope
	ModeViewModel getModeViewModel(ModeActivity modeActivity) {
		ModeViewModel viewModel = ViewModelProviders.of(modeActivity).get(ModeViewModel.class);
		viewModel.getEventEmitter().observe(modeActivity, modeActivity);
		return viewModel;
	}
	
	@Provides @ModeActivityScope @Named("commute")
	ModeTile getCommuteTile(ModeActivity modeActivity) {
		ModeTile modeTile = modeActivity.findViewById(R.id.tileCommute);
		modeTile.setOnClickListener(modeActivity);
		return modeTile;
	}
	
	@Provides @ModeActivityScope @Named("tour")
	ModeTile getTourTile(ModeActivity modeActivity) {
		ModeTile modeTile = modeActivity.findViewById(R.id.tileTour);
		modeTile.setOnClickListener(modeActivity);
		return modeTile;
	}
	
	@Provides @ModeActivityScope @Named("race")
	ModeTile getRaceTile(ModeActivity modeActivity) {
		ModeTile modeTile = modeActivity.findViewById(R.id.tileRace);
		modeTile.setOnClickListener(modeActivity);
		return modeTile;
	}
	
	@Provides @ModeActivityScope @Named("offroad")
	ModeTile getOffroadTile(ModeActivity modeActivity) {
		ModeTile modeTile = modeActivity.findViewById(R.id.tileOffroad);
		modeTile.setOnClickListener(modeActivity);
		return modeTile;
	}
}
