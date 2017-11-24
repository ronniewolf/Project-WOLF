package com.davinci.wolf.console.options.di;

import com.davinci.wolf.Scopes.OptionsFragmentScope;
import com.davinci.wolf.console.options.OptionsFragment;

import dagger.Component;

/**
 * Created by aakash on 11/17/17.
 */
@OptionsFragmentScope
@Component(modules = {FragmentModule.class})
public interface OptionsComponent {
	void inject(OptionsFragment optionsFragment);
}
