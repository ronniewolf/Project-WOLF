package com.davinci.wolf.settings.mode;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;

import com.davinci.wolf.R;
import com.davinci.wolf.application.WolfApplication;
import com.davinci.wolf.settings.mode.ModeViewModel.ModeData;
import com.davinci.wolf.settings.mode.di.ActivityModule;
import com.davinci.wolf.settings.mode.di.DaggerModeComponent;

import javax.inject.Inject;
import javax.inject.Named;

public class ModeActivity extends AppCompatActivity
	implements OnClickListener, Observer<ModeData> {
	
	@Inject WolfApplication application;
	@Inject ModeViewModel viewModel;
	
	@Inject @Named("commute") ModeTile commute;
	@Inject @Named("tour") ModeTile tour;
	@Inject @Named("race") ModeTile race;
	@Inject @Named("offroad") ModeTile offroad;
	
	private ModeData modeData = null;
	private int savedIndex = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mode);
		
		DaggerModeComponent.builder()
			.appComponent(WolfApplication.getWolfApplication(this).getAppComponent())
			.activityModule(new ActivityModule(this))
			.build()
			.inject(this);
		
		viewModel.setModeData(new ModeData(savedIndex = application.getSavedMode(), false));
	}
	
	@Override public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slidedown_enter, R.anim.slidedown_exit);
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.tileCommute:
				viewModel.setMode(0);
				viewModel.setShouldUpdate(savedIndex != 0);
				break;
			case R.id.tileTour:
				viewModel.setMode(1);
				viewModel.setShouldUpdate(savedIndex != 1);
				break;
			case R.id.tileRace:
				viewModel.setMode(2);
				viewModel.setShouldUpdate(savedIndex != 2);
				break;
			case R.id.tileOffroad:
				viewModel.setMode(3);
				viewModel.setShouldUpdate(savedIndex != 3);
				break;
			case R.id.done:
				view.setVisibility(View.GONE);
				findViewById(R.id.progress).setVisibility(View.VISIBLE);
				new Handler().postDelayed(this::saveAndFinish, 1000);
				break;
		}
	}
	
	@Override
	public void onChanged(@Nullable ModeData modeData) {
		if (modeData == null) return;
		if (this.modeData == null || (this.modeData.selectedIndex != modeData.selectedIndex))
			switch (modeData.selectedIndex) {
				case 0:
					commute.setSelected(true);
					tour.setSelected(false);
					race.setSelected(false);
					offroad.setSelected(false);
					break;
				case 1:
					commute.setSelected(false);
					tour.setSelected(true);
					race.setSelected(false);
					offroad.setSelected(false);
					break;
				case 2:
					commute.setSelected(false);
					tour.setSelected(false);
					race.setSelected(true);
					offroad.setSelected(false);
					break;
				case 3:
					commute.setSelected(false);
					tour.setSelected(false);
					race.setSelected(false);
					offroad.setSelected(true);
					break;
			}
		FloatingActionButton done = findViewById(R.id.done);
		if (modeData.shouldSave && done.getVisibility() != View.VISIBLE)
			done.setVisibility(View.VISIBLE);
		if (!modeData.shouldSave && done.getVisibility() != View.GONE)
			done.setVisibility(View.GONE);
		
		this.modeData = modeData;
	}
	
	private void saveAndFinish() {
		if (modeData != null) application.setSavedMode(modeData.selectedIndex);
		setResult(RESULT_OK);
		finish();
		overridePendingTransition(R.anim.slidedown_enter, R.anim.slidedown_exit);
	}
}
