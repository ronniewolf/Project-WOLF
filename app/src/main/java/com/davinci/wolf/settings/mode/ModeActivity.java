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
import com.davinci.wolf.settings.mode.di.ActivityModule;
import com.davinci.wolf.settings.mode.di.DaggerModeComponent;

import javax.inject.Inject;
import javax.inject.Named;

import timber.log.Timber;

//Macro mode selection activity
public class ModeActivity extends AppCompatActivity
	implements OnClickListener, Observer<Integer> {
	
	//Dependency Injection
	@Inject WolfApplication application;
	@Inject ModeViewModel viewModel;
	@Inject @Named("commute") ModeTile commute;
	@Inject @Named("tour") ModeTile tour;
	@Inject @Named("race") ModeTile race;
	@Inject @Named("offroad") ModeTile offroad;
	
	private int savedIndex = 0, selectedIndex = 0;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mode);
		
		DaggerModeComponent.builder()
			.appComponent(WolfApplication.getWolfApplication(this).getAppComponent())
			.activityModule(new ActivityModule(this))
			.build()
			.inject(this);
		
		//initialize view model with saved data
		viewModel.setMode(savedIndex = application.getSavedMode());
	}
	
	@Override public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slidedown_enter, R.anim.slidedown_exit);
	}
	
	@Override public boolean onSupportNavigateUp() {
		onBackPressed();
		return true;
	}
	
	@Override
	public void onClick(View view) {
		//set selected mode to
		switch (view.getId()) {
			case R.id.tileCommute:
				viewModel.setMode(0);
				break;
			case R.id.tileTour:
				viewModel.setMode(1);
				break;
			case R.id.tileRace:
				viewModel.setMode(2);
				break;
			case R.id.tileOffroad:
				viewModel.setMode(3);
				break;
			case R.id.done:
				view.setVisibility(View.GONE);
				findViewById(R.id.progress).setVisibility(View.VISIBLE);
				new Handler().postDelayed(this::saveAndFinish, 1000);
				break;
		}
	}
	
	@Override
	public void onChanged(@Nullable Integer selectedIndex) {
		if (selectedIndex == null) return;
		Timber.d(selectedIndex.toString());
		commute.setSelected(selectedIndex == 0);
		tour.setSelected(selectedIndex == 1);
		race.setSelected(selectedIndex == 2);
		offroad.setSelected(selectedIndex == 3);
		//if savedIndex and selected index do not match we show the save button
		boolean shouldSave = selectedIndex != this.savedIndex;
		FloatingActionButton done = findViewById(R.id.done);
		//don't show if already visible
		if (shouldSave && done.getVisibility() != View.VISIBLE)
			done.setVisibility(View.VISIBLE);
		//don't hide if already hidden
		if (!shouldSave && done.getVisibility() != View.GONE)
			done.setVisibility(View.GONE);
		this.selectedIndex = selectedIndex;
	}
	
	private void saveAndFinish() {
		application.setSavedMode(selectedIndex);
		setResult(RESULT_OK);
		finish();
		overridePendingTransition(R.anim.slidedown_enter, R.anim.slidedown_exit);
	}
}
