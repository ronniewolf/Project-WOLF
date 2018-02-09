package com.davinci.wolf.console.options.di;

import android.arch.lifecycle.ViewModelProviders;
import android.support.design.widget.FloatingActionButton;

import com.davinci.wolf.R;
import com.davinci.wolf.console.ConsoleActivity;
import com.davinci.wolf.Scopes.OptionsFragmentScope;
import com.davinci.wolf.console.options.OptionsFragment;
import com.davinci.wolf.console.options.OptionsViewModel;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;

/**
 * Created by aakash on 11/17/17.
 */
@Module
public class FragmentModule {
	private OptionsFragment optionsFragment = null;
	
	public FragmentModule(OptionsFragment optionsFragment) {
		this.optionsFragment = optionsFragment;
		this.optionsFragment.findViewById(R.id.mode).setOnLongClickListener(optionsFragment);
		this.optionsFragment.findViewById(R.id.auto).setOnLongClickListener(optionsFragment);
		this.optionsFragment.findViewById(R.id.manual).setOnLongClickListener(optionsFragment);
	}
	
	@Provides @OptionsFragmentScope
	OptionsFragment getOptionsFragment() {
		return this.optionsFragment;
	}
	
	@Provides @OptionsFragmentScope
	ConsoleActivity getConsoleActivity(OptionsFragment optionsFragment) {
		return (ConsoleActivity) optionsFragment.getActivity();
	}
	
	@Provides @OptionsFragmentScope
	OptionsViewModel getOptionsViewModel(OptionsFragment optionsFragment) {
		return ViewModelProviders.of(optionsFragment).get(OptionsViewModel.class);
	}
	
	@Provides @OptionsFragmentScope @Named("mode")
	FloatingActionButton getModeButton(OptionsFragment optionsFragment) {
		FloatingActionButton button = (FloatingActionButton) optionsFragment.findViewById(R.id.mode);
		button.setOnClickListener(optionsFragment);
		return button;
	}
	
	@Provides @OptionsFragmentScope @Named("auto")
	FloatingActionButton getAutoButton(OptionsFragment optionsFragment) {
		FloatingActionButton button = (FloatingActionButton) optionsFragment.findViewById(R.id.auto);
		button.setOnClickListener(optionsFragment);
		return button;
	}
	
	@Provides @OptionsFragmentScope @Named("manual")
	FloatingActionButton getManualButton(OptionsFragment optionsFragment) {
		FloatingActionButton button = (FloatingActionButton) optionsFragment.findViewById(R.id.manual);
		button.setOnClickListener(optionsFragment);
		return button;
	}
}
