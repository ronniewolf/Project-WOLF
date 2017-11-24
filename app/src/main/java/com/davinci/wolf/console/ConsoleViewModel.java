package com.davinci.wolf.console;

import android.app.Application;
import android.arch.lifecycle.AndroidViewModel;
import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.content.Context;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.text.Editable;
import android.text.TextWatcher;

import com.akexorcist.googledirection.util.DirectionConverter;
import com.davinci.wolf.R;
import com.davinci.wolf.utils.Statics;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;

import java.util.ArrayList;

/**
 * Created by aakash on 11/15/17.
 * ConsoleViewModel persists throughout activity lifecycle
 */
public class ConsoleViewModel extends AndroidViewModel
	implements TextWatcher {
	//emits on change event if the data was mutated
	private final MutableLiveData<ConsoleData> eventEmitter = new MutableLiveData<>();
	//activity state variables
	private boolean hasChildActivities = false, mapInitialized = false;
	
	public ConsoleViewModel(@NonNull Application application) {
		super(application);
		//initialize the event emitter
		eventEmitter.setValue(new ConsoleData());
	}
	
	/**
	 * drops the destination marker on the provided googleMap
	 * @param googleMap: GoogleMap view to drop marker on
	 * @param latLng: the coordinate to drop marker at
	 * @param address: change the text if destination was set by selecting a suggestion
	 * @param animate: animate camera to the dropped marker
	 */
	void setDestination(GoogleMap googleMap, LatLng latLng, String address, boolean animate) {
		ConsoleData consoleData = eventEmitter.getValue();
		if (consoleData != null && consoleData.destination != null)
			consoleData.destination.remove();
		Marker destination = googleMap.addMarker(new MarkerOptions().position(latLng));
		if (animate) googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15));
		ConsoleData mutated = new ConsoleData(eventEmitter.getValue())
			.setDestination(destination)
			.setSearchText(address != null ? address : Statics.parseLatlng(destination.getPosition()));
		eventEmitter.setValue(mutated);
	}
	
	/**
	 * Draw the navigation on the provided googleMap
	 * @param context: required to draws the polyline
	 * @param googleMap: GoogleMap to draw the navigation on
	 * @param route: the navigation coordinates to draw on the googleMap
	 */
	void setNavigation(Context context, GoogleMap googleMap, ArrayList<LatLng> route) {
		eventEmitter.setValue(new ConsoleData(eventEmitter.getValue())
			.setNavigation(googleMap.addPolyline(DirectionConverter.createPolyline(context, route, 5, Color.BLUE))));
		googleMap.animateCamera(CameraUpdateFactory.newLatLngBounds(Statics.getRouteCamera(route),
			context.getResources().getDimensionPixelSize(R.dimen.cameraMargin)));
	}
	
	//set search mode
	void setInSearch(boolean value) {
		eventEmitter.setValue(new ConsoleData(eventEmitter.getValue())
			.setInSearch(value));
	}
	
	//set autocompletetextview text
	private void setSearchText(String searchText) {
		ConsoleData consoleData = eventEmitter.getValue();
		if (consoleData == null || consoleData.searchText.equals(searchText)) return;
		eventEmitter.setValue(new ConsoleData(eventEmitter.getValue())
			.setSearchText(searchText));
	}
	
	//clears the map of all markers and polyline if they exist
	void clearMap() {
		ConsoleData consoleData = eventEmitter.getValue();
		if (consoleData != null) {
			if (consoleData.navigation != null) consoleData.navigation.remove();
			if (consoleData.destination != null) consoleData.destination.remove();
		}
		eventEmitter.setValue(new ConsoleData(eventEmitter.getValue())
			.setDestination(null)
			.setNavigation(null)
			.setSearchText(""));
	}
	
	//restores marker and polyline saved in this class
	void restoreMap(GoogleMap googleMap, Context context) {
		ConsoleData consoleData = eventEmitter.getValue();
		if (consoleData == null) return;
		Marker destination = consoleData.destination;
		if (destination != null)
			consoleData.destination = googleMap.addMarker(new MarkerOptions().position(destination.getPosition()));
		Polyline navigation = consoleData.navigation;
		if (navigation != null)
			consoleData.navigation = googleMap.addPolyline(DirectionConverter.createPolyline(context, new ArrayList<>(navigation.getPoints()), 5, Color.BLUE));
	}
	
	//get eventemitter to listen to changes
	public LiveData<ConsoleData> getEventEmitter() {
		return this.eventEmitter;
	}
	
	//get if ConsoleActivity has any child activity
	boolean hasChildActivities() {
		return hasChildActivities;
	}
	
	//set if ConsoleActivity has any child activity
	void setHasChildActivities(boolean hasChildActivities) {
		this.hasChildActivities = hasChildActivities;
	}
	
	//get if map was initialized
	boolean isMapInitialized() {
		return mapInitialized;
	}
	
	//set if map was initialized
	void setMapInitialized() {
		this.mapInitialized = true;
	}
	
	//set search text in the view model
	@Override
	public void afterTextChanged(Editable editable) {
		setSearchText(editable.toString());
	}
	
	@Override
	public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
	
	@Override
	public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
	
	static class ConsoleData {
		private Marker destination = null;
		private Polyline navigation = null;
		private boolean inSearch = false;
		private String searchText = "";
		
		ConsoleData() {}
		
		//data to copy from previous condoleData
		ConsoleData(ConsoleData consoleData) {
			if (consoleData == null) return;
			this.destination = consoleData.destination;
			this.navigation = consoleData.navigation;
			this.inSearch = consoleData.inSearch;
			this.searchText = consoleData.searchText;
		}
		
		/** getter and setter */
		
		Marker getDestination() {
			return destination;
		}
		
		ConsoleData setDestination(Marker destination) {
			this.destination = destination;
			return this;
		}
		
		Polyline getNavigation() {
			return navigation;
		}
		
		ConsoleData setNavigation(Polyline navigation) {
			this.navigation = navigation;
			return this;
		}
		
		boolean isInSearch() {
			return inSearch;
		}
		
		ConsoleData setInSearch(boolean inSearch) {
			this.inSearch = inSearch;
			return this;
		}
		
		String getSearchText() {
			return searchText;
		}
		
		ConsoleData setSearchText(String searchText) {
			this.searchText = searchText;
			return this;
		}
	}
}
