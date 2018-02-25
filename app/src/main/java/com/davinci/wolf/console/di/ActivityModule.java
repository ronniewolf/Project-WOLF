package com.davinci.wolf.console.di;

import android.arch.lifecycle.ViewModelProviders;
import android.widget.TextView;

import com.davinci.wolf.R;
import com.davinci.wolf.application.WolfApplication;
import com.davinci.wolf.console.ConsoleActivity;
import com.davinci.wolf.console.ConsoleViewModel;
import com.davinci.wolf.console.LocationProviderChangedListener;

import dagger.Module;
import dagger.Provides;
import timber.log.Timber;

import static com.davinci.wolf.Scopes.ConsoleActivityScope;

/**
 * Created by aakash on 11/14/17.
 */
@Module
public class ActivityModule {
	private ConsoleActivity consoleActivity = null;
	
	public ActivityModule(ConsoleActivity consoleActivity) {
		this.consoleActivity = consoleActivity;
		this.consoleActivity.findViewById(R.id.console).setOnLongClickListener(consoleActivity);
		this.consoleActivity.findViewById(R.id.action).setOnLongClickListener(consoleActivity);
		this.consoleActivity.findViewById(R.id.done).setOnLongClickListener(consoleActivity);
		this.consoleActivity.findViewById(R.id.info).setOnLongClickListener(consoleActivity);
		((TextView) this.consoleActivity.findViewById(R.id.odometer)).setText(this.consoleActivity.getString(R.string.console_extra, 1, 0));
		TextView speedo = this.consoleActivity.findViewById(R.id.speedo);
		speedo.setText(R.string.console_speedo);
		speedo.setOnLongClickListener(consoleActivity);
	}
	
	@Provides @ConsoleActivityScope
	ConsoleActivity getConsoleActivity() {
		return this.consoleActivity;
	}
	
	@Provides @ConsoleActivityScope
	WolfApplication getApplication() {
		return WolfApplication.getWolfApplication(this.consoleActivity);
	}
	
	@Provides @ConsoleActivityScope
	ConsoleViewModel getConsoleViewModel(ConsoleActivity consoleActivity) {
		Timber.d("Initializing viewModel");
		ConsoleViewModel viewModel = ViewModelProviders.of(consoleActivity).get(ConsoleViewModel.class);
		viewModel.getEventEmitter().observe(consoleActivity, consoleActivity);
		return viewModel;
	}
	
	@Provides @ConsoleActivityScope
	LocationProviderChangedListener getProviderChangedListener(ConsoleActivity consoleActivity) {
		return new LocationProviderChangedListener(consoleActivity);
	}

	@Provides @ConsoleActivityScope
	TextView getSpeedometer(){
		return this.consoleActivity.findViewById(R.id.speedo);
	}
}
