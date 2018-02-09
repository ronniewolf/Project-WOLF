package com.davinci.wolf.console.di;

import android.widget.AutoCompleteTextView;

import com.akexorcist.googledirection.GoogleDirection;
import com.akexorcist.googledirection.request.DirectionOriginRequest;
import com.davinci.wolf.R;
import com.davinci.wolf.console.ConsoleActivity;
import com.davinci.wolf.console.ConsoleViewModel;
import com.davinci.wolf.console.SearchAdapter;
import com.davinci.wolf.Scopes.ConsoleActivityScope;
import com.google.android.gms.maps.MapView;

import dagger.Module;
import dagger.Provides;

/**
 * Created by aakash on 11/14/17.
 */
@Module class MapModule {
	@Provides @ConsoleActivityScope
	MapView getMapView(ConsoleActivity consoleActivity) {
		MapView mapView = consoleActivity.findViewById(R.id.mapView);
		mapView.setSaveEnabled(true);
		mapView.setSaveFromParentEnabled(true);
		return mapView;
	}
	
	@Provides @ConsoleActivityScope
	SearchAdapter getSearchAdapter(ConsoleActivity consoleActivity, ConsoleViewModel viewModel) {
		SearchAdapter searchAdapter = new SearchAdapter(consoleActivity);
		AutoCompleteTextView search = consoleActivity.findViewById(R.id.search);
		search.setAdapter(searchAdapter);
		search.setOnItemClickListener(consoleActivity);
		search.addTextChangedListener(viewModel);
		return searchAdapter;
	}
	
	@Provides @ConsoleActivityScope
	DirectionOriginRequest getDirectionApi(ConsoleActivity consoleActivity) {
		return GoogleDirection.withServerKey(consoleActivity.getResources().getString(R.string.google_maps_key));
	}
}
