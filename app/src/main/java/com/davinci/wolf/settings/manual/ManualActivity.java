package com.davinci.wolf.settings.manual;

import android.arch.lifecycle.Observer;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.davinci.wolf.R;
import com.davinci.wolf.application.WolfApplication;
import com.davinci.wolf.settings.manual.ManualViewModel.ManualData;
import com.davinci.wolf.settings.manual.di.ActivityModule;
import com.davinci.wolf.settings.manual.di.DaggerManualComponent;

import javax.inject.Inject;

public class ManualActivity extends AppCompatActivity
	implements Observer<ManualData>, OnClickListener {
	
	@Inject WolfApplication application;
	@Inject ManualViewModel viewModel;
	
	ManualData saved = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_manual);
		
		DaggerManualComponent.builder()
			.appComponent(WolfApplication.getWolfApplication(this).getAppComponent())
			.activityModule(new ActivityModule(this))
			.build()
			.inject(this);
		
		viewModel.setManualData(saved = application.getSavedManualData());
		init();
	}
	
	@Override
	public void onBackPressed() {
		super.onBackPressed();
		overridePendingTransition(R.anim.slidedown_enter, R.anim.slidedown_exit);
	}
	
	@Override
	public void onChanged(@Nullable ManualData manualData) {
		findViewById(R.id.done).setVisibility(saved.notEqual(manualData) ? View.VISIBLE : View.GONE);
		if (manualData == null) return;
		((TextView) findViewById(R.id.transmissionLabel)).setText(getString(R.string.manual_label, getPercent(manualData.getTransmission(), 2)));
		((TextView) findViewById(R.id.shocksLabel)).setText(getString(R.string.manual_label, getPercent(manualData.getShocks(), 10)));
	}
	
	@Override
	public void onClick(View view) {
		view.setVisibility(View.INVISIBLE);
		findViewById(R.id.progress).setVisibility(View.VISIBLE);
		new Handler().postDelayed(this::saveAndFinish, 1000);
	}
	
	private void init() {
		((SeekBar) findViewById(R.id.transmission)).setProgress(saved.getTransmission());
		((TextView) findViewById(R.id.transmissionLabel)).setText(getString(R.string.manual_label, getPercent(saved.getTransmission(), 2)));
		((SeekBar) findViewById(R.id.shocks)).setProgress(saved.getShocks());
		((TextView) findViewById(R.id.shocksLabel)).setText(getString(R.string.manual_label, getPercent(saved.getShocks(), 10)));
		((Switch) findViewById(R.id.abs)).setChecked(saved.getAbs());
	}
	
	private void saveAndFinish() {
		application.setSavedManualData(viewModel.getEventEmitter().getValue());
		setResult(RESULT_OK);
		finish();
		overridePendingTransition(R.anim.slidedown_enter, R.anim.slidedown_exit);
	}
	
	private int getPercent(int val, int total) {
		return val * 100 / total;
	}
}
