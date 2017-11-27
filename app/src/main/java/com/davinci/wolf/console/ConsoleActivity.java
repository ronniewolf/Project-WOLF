package com.davinci.wolf.console;

import android.Manifest;
import android.annotation.SuppressLint;
import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintSet;
import android.support.design.widget.FloatingActionButton;
import android.support.transition.Scene;
import android.support.transition.Transition;
import android.support.transition.TransitionInflater;
import android.support.transition.TransitionListenerAdapter;
import android.support.transition.TransitionManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.akexorcist.googledirection.DirectionCallback;
import com.akexorcist.googledirection.constant.AvoidType;
import com.akexorcist.googledirection.constant.RequestResult;
import com.akexorcist.googledirection.constant.TransportMode;
import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.request.DirectionOriginRequest;
import com.davinci.wolf.R;
import com.davinci.wolf.about.AboutActivity;
import com.davinci.wolf.application.WolfApplication;
import com.davinci.wolf.authentication.AuthenticateActivity;
import com.davinci.wolf.console.ConsoleViewModel.ConsoleData;
import com.davinci.wolf.console.di.ActivityModule;
import com.davinci.wolf.console.di.DaggerConsoleComponent;
import com.davinci.wolf.console.options.OptionsFragment;
import com.davinci.wolf.login.LoginActivity;
import com.davinci.wolf.utils.FusedLocationProvider;
import com.davinci.wolf.utils.Statics;
import com.davinci.wolf.utils.ViewAnimator;
import com.github.ajalt.reprint.core.Reprint;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMap.OnMapClickListener;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.LatLng;

import javax.inject.Inject;

import at.favre.lib.dali.Dali;
import timber.log.Timber;

import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
import static android.content.pm.ActivityInfo.SCREEN_ORIENTATION_USER;
import static android.view.View.OnClickListener;
import static android.widget.AdapterView.GONE;
import static android.widget.AdapterView.OnItemClickListener;
import static android.widget.AdapterView.VISIBLE;
import static com.davinci.wolf.utils.FusedLocationProvider.LocationChangedListener;
import static com.davinci.wolf.utils.Statics.AUTHENTICATE_RC;
import static com.davinci.wolf.utils.Statics.AUTO_RC;
import static com.davinci.wolf.utils.Statics.ENABLE_LOCATION_RC;
import static com.davinci.wolf.utils.Statics.LOGIN_RC;
import static com.davinci.wolf.utils.Statics.MANUAL_RC;
import static com.davinci.wolf.utils.Statics.MODE_RC;
import static com.davinci.wolf.utils.Statics.toggleKeyboard;

//ConsoleActivity shows all vehicle vitals along with a map for navigation
public class ConsoleActivity extends AppCompatActivity
	implements OnMapReadyCallback, OnMapClickListener,
	DirectionCallback, LocationChangedListener,
	OnClickListener, OnLongClickListener, OnItemClickListener,
	Observer<ConsoleData> {
	
	//Dependency Injection, Injects all following variables after initialization
	DaggerConsoleComponent component = null;
	@Inject WolfApplication application;
	@Inject ConsoleViewModel viewModel;
	@Inject FusedLocationProvider locationProvider;
	@Inject MapView mapView;
	@Inject SearchAdapter searchAdapter;
	@Inject DirectionOriginRequest directionsApi;
	@Inject LocationProviderChangedListener providerChangedListener;
	
	private GoogleMap googleMap = null;
	
	//previous state variable is stored to show all relevant animations
	private ConsoleData consoleData = null;
	//store the location permission request code to avoid asking permission multiple times
	private int PERMISSION_REQUEST_CODE = -1;
	//did we return from an orientation change, is options fragment visible
	private boolean configChanged = false, optionsVisible = false;
	
	@Override @SuppressLint("ClickableViewAccessibility")
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		int layout = R.layout.activity_console;
		boolean inSearch = false;
		//did we return from an orientation change
		if (savedInstanceState != null) {
			//dont ask for location permission anymore if the saved value is not -1
			PERMISSION_REQUEST_CODE = savedInstanceState.getInt("REQUEST_CODE", -1);
			//if we were in search then we set the layout to search layout
			inSearch = savedInstanceState.getBoolean("inSearch", false);
			if (inSearch) layout = R.layout.activity_console_search;
			configChanged = true;
		}
		
		setContentView(layout);
		if (inSearch)//action button is search mode shows a clear icon
			((FloatingActionButton) findViewById(R.id.action)).setImageResource(R.drawable.icon_clear);
		
		//dependency injection
		(component = (DaggerConsoleComponent) DaggerConsoleComponent.builder()
			.activityModule(new ActivityModule(this))
			.build()).inject(this);
		mapView.onCreate(savedInstanceState);
		
		//listen to options fragment removed, set its container frame layout
		// to gone to prevent touch event block
		getSupportFragmentManager().addOnBackStackChangedListener(() -> {
			if (getSupportFragmentManager().findFragmentByTag("options") == null)
				findViewById(R.id.container).setVisibility(GONE);
		});
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		//save permission request code and search mode
		mapView.onSaveInstanceState(outState);
		super.onSaveInstanceState(outState);
		outState.putInt("REQUEST_CODE", PERMISSION_REQUEST_CODE);
		outState.putBoolean("inSearch", consoleData.isInSearch());
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		//if an activityForResult was started, we dont want to restart that child activity
		if (viewModel.hasChildActivities()) {
			viewModel.setHasChildActivities(false);
			return;
		}
		//if user has'nt logged in, then launch the LoginActivity
		if (application.isLoggedIn()) {
			Reprint.initialize(this);
			//Launch AuthenticationActivity if hwd present and user has'nt authenticated
			if (Reprint.isHardwarePresent() && !viewModel.isAuthenticated()) {
				viewModel.setHasChildActivities(true);
				authenticate();
			} else getPermissions();//else we continue with asking necessary permissions
		} else {
			viewModel.setHasChildActivities(true);
			startActivityForResult(new Intent(this, LoginActivity.class), LOGIN_RC);
		}
	}
	
	@Override
	protected void onResume() {
		mapView.onResume();
		//listen to location provider changes start getting location updates if the required config is met
		registerReceiver(providerChangedListener, new IntentFilter("android.location.PROVIDERS_CHANGED"));
		super.onResume();
	}
	
	@Override
	protected void onPause() {
		//stop listening to location provider changes
		unregisterReceiver(providerChangedListener);
		//stop getting location updates
		locationProvider.stopUpdates();
		mapView.onPause();
		super.onPause();
	}
	
	@Override
	public void onLowMemory() {
		mapView.onLowMemory();
		super.onLowMemory();
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mapView.onDestroy();
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		//if child activity was used to ask a permission,
		// we reset it since it has served it's purpose
		PERMISSION_REQUEST_CODE = -1;
		switch (requestCode) {
			case LOGIN_RC:
				if (resultCode == RESULT_OK)
					//if login was successful and we have biometric hwd,
					//we start AuthenticationActivity else we ask location permissions
					if (Reprint.isHardwarePresent() && !viewModel.isAuthenticated()) {
						viewModel.setHasChildActivities(true);
						authenticate();
					} else getPermissions();
				else finish();
				break;
			case AUTHENTICATE_RC:
				//authentication was successful,
				//get location permissions
				if (resultCode == RESULT_OK) {
					getPermissions();
					viewModel.setAuthenticated();
				} else finish();//if authentication failed we exit
				break;
			case ENABLE_LOCATION_RC:
				//if location provider is in high accuracy, we initialize the googleMap;
				if (Statics.isLocationEnabled(this)) {
					viewModel.setMapInitialized();
					mapView.getMapAsync(this);
				} else Toast.makeText(this, "Please enable locations", Toast.LENGTH_SHORT).show();
				break;
			case MODE_RC:
				//ModeActivity finished
				if (resultCode == RESULT_OK)
					Toast.makeText(this, "Mode calibration successful", Toast.LENGTH_SHORT).show();
				locationProvider.startUpdates();
				break;
			case AUTO_RC:
				//AutoActivity finished
				if (resultCode == RESULT_OK)
					Toast.makeText(this, "Smart calibration successful", Toast.LENGTH_SHORT).show();
				locationProvider.startUpdates();
				break;
			case MANUAL_RC:
				//ManualActivity finished
				if (resultCode == RESULT_OK)
					Toast.makeText(this, "Manual calibration successful", Toast.LENGTH_SHORT).show();
				locationProvider.startUpdates();
				break;
		}
	}
	
	@Override
	public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
		//reset requestCode
		PERMISSION_REQUEST_CODE = -1;
		viewModel.setHasChildActivities(false);
		//location permission wasn't granted, show error toast and return
		if (requestCode != PERMISSION_REQUEST_CODE || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
			Toast.makeText(this, "Please provide location permissions", Toast.LENGTH_LONG).show();
			return;
		}
		//if location provider is in high accuracy, we initialize googleMap
		if (Statics.isLocationEnabled(this)) {
			viewModel.setMapInitialized();
			mapView.getMapAsync(this);
			//else we ask user to change the location provider setting
		} else if (PERMISSION_REQUEST_CODE == -1) {
			viewModel.setHasChildActivities(true);
			locationProvider.enableLocationService();
		}
	}
	
	@Override
	public void onBackPressed() {
		//if we're in search mode, we go back to console mode
		if (consoleData != null && consoleData.isInSearch()) {
			viewModel.clearMap();
			showConsole();
			return;
		}
		//if optionFragment was visible, we pop the fragment
		super.onBackPressed();
		if (optionsVisible) optionsVisible = false;
	}
	
	@Override
	public void onMapReady(GoogleMap googleMap) {
		//get hold of google map initialize all child views
		this.googleMap = googleMap;
		init();
	}
	
	@Override
	public void onClick(View view) {
		switch (view.getId()) {
			case R.id.action:
				//action button was clicked (search/cancel icon)
				actionClicked();
				break;
			case R.id.back:
				//if in search mode we go back to console mode
				showConsole();
				break;
			case R.id.done:
				//if we have destination and navigation data, we simply go back to console
				if (consoleData.getNavigation() != null) {
					showConsole();
					return;
				}
				//if we only have destination, we fetch navigation
				// data after animating the done button
				if (consoleData.getDestination() != null) {
					new ViewAnimator(view, R.anim.shrink, 0)
						.addAnimationListener(new ViewAnimator.AnimationListener() {
							@Override public void onAnimationEnd(View v) {
								v.setVisibility(GONE);
								findViewById(R.id.progress).setVisibility(VISIBLE);
								directionsApi.from(Statics.parseLocation(searchAdapter.getCurrentLocation()))
									.to(consoleData.getDestination().getPosition())
									.avoid(AvoidType.FERRIES)
									.transportMode(TransportMode.DRIVING)
									.execute(ConsoleActivity.this);
							}
						})
						.start();
					return;
				}
				break;
			case R.id.console:
				//show optionsFragment if not already shown, ignore otherwise
				if (getSupportFragmentManager().findFragmentByTag("options") == null
					&& !optionsVisible && googleMap != null) {
					optionsVisible = true;
					googleMap.snapshot(bitmap -> {
						findViewById(R.id.container).setVisibility(VISIBLE);
						getSupportFragmentManager().beginTransaction()
							.add(R.id.container, OptionsFragment.newInstance(Dali.create(this)
								.load(Statics.overlay(bitmap, Statics.getScreenshot(findViewById(R.id.root))))
								.blurRadius(25)
								.getAsBitmap()), "options")
							.addToBackStack("options")
							.commit();
					});
				}
				break;
			case R.id.info:
				//show AboutActivity
				startActivity(new Intent(this, AboutActivity.class));
				break;
		}
	}
	
	@Override
	public boolean onLongClick(View view) {
		String toast = null;
		switch (view.getId()) {
			case R.id.console:
				toast = "Show options";
				break;
			case R.id.action:
				toast = consoleData.isInSearch() ? "Clear" : consoleData.getNavigation() == null ? "Search" : "Cancel";
				break;
			case R.id.done:
				toast = consoleData.getNavigation() != null ? "Start Navigation" : "Find Route";
				break;
			case R.id.info:
				toast = "About";
				break;
		}
		if (toast != null) Toast.makeText(this, toast, Toast.LENGTH_LONG).show();
		return true;
	}
	
	@Override
	public void onMapClick(LatLng latLng) {
		//if we're in search mode and we already have a navigation data
		//we ignore map clicks
		if (consoleData == null || !consoleData.isInSearch() ||
			consoleData.getNavigation() != null || googleMap == null)
			return;
		//else we drop marker on the coordinate
		viewModel.setDestination(googleMap, latLng, null, false);
	}
	
	@Override
	public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
		//if item in suggestion was clicked, we drop a marker at the suggested location
		if (googleMap != null) {
			SearchAdapter.Suggestion suggestion = (SearchAdapter.Suggestion) adapterView.getItemAtPosition(i);
			viewModel.setDestination(googleMap, suggestion.location, suggestion.address, true);
		}
	}
	
	@Override
	public void onLocationChanged(Location location) {
		if (location == null || googleMap == null) return;
		//if we are not in search mode, we animate camera to that location
		if (!consoleData.isInSearch())
			googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Statics.parseLocation(searchAdapter.setCurrentLocation(location)),
				consoleData.getNavigation() != null ? 20 : 15));
			//else we set search location in searchAdapter
		else searchAdapter.setCurrentLocation(location);
	}
	
	@Override
	public void onChanged(@Nullable ConsoleData consoleData) {
		if (consoleData == null) return;
		
		FloatingActionButton done = findViewById(R.id.done);
		//if we're returning from an orientation change,
		//we simply reinitialize everything to how it was
		if (configChanged) {
			configChanged = false;
			FloatingActionButton actionButton = findViewById(R.id.action);
			if ((this.consoleData = consoleData).isInSearch()) {
				AutoCompleteTextView search = findViewById(R.id.search);
				String saved = consoleData.getSearchText();
				search.setText(saved);
				search.setSelection(saved.length());
				if (consoleData.getNavigation() != null) {
					done.setImageResource(R.drawable.icon_navigate);
					done.setVisibility(VISIBLE);
				} else if (consoleData.getDestination() != null) {
					done.setImageResource(R.drawable.icon_direction);
					done.setVisibility(VISIBLE);
				}
				actionButton.setImageResource(R.drawable.icon_clear);
			}
			//if we had a navigation we set a clear button
			if (this.consoleData.getNavigation() != null)
				actionButton.setImageResource(R.drawable.icon_clear);
			return;
		}
		//if search mode was toggled, we mutate root layout to search mode
		if (googleMap != null && this.consoleData.isInSearch() != consoleData.isInSearch())
			mutateLayout(consoleData.isInSearch(), consoleData.getNavigation() != null);
		String newSearch = consoleData.getSearchText();
		//we set the text to autocomplete search textview if it is different
		if (this.consoleData == null || !newSearch.equals(this.consoleData.getSearchText())) {
			AutoCompleteTextView search = findViewById(R.id.search);
			if (!search.getText().toString().equals(newSearch)) {
				search.setText(newSearch);
				search.setSelection(newSearch.length());
			}
		}
		//if a coordinate was typed, we simply drop a marker on that coordinate
		if (this.consoleData != null && !newSearch.equals(this.consoleData.getSearchText())
			&& Statics.isCoordinate(newSearch) && consoleData.getDestination() == null && googleMap != null)
			viewModel.setDestination(googleMap, Statics.parseString(newSearch), null, true);
		
		//a destination was set, we show a button to fetch navigation data
		if (consoleData.isInSearch() && consoleData.getDestination() != null && done.getVisibility() != VISIBLE)
			showPositiveButton(done, R.drawable.icon_direction);
		//a navigation was fetched, we show a button to start navigation
		if (consoleData.isInSearch() && consoleData.getNavigation() != null && done.getVisibility() != VISIBLE)
			showPositiveButton(done, R.drawable.icon_navigate);
		//navigation was cleared, now set back the action button to search
		if (!consoleData.isInSearch() && consoleData.getNavigation() == null)
			((FloatingActionButton) findViewById(R.id.action)).setImageResource(R.drawable.icon_search);
		
		//all views reconciled, now we save new data as old data
		this.consoleData = consoleData;
	}
	
	@Override
	public void onDirectionSuccess(Direction direction, String rawBody) {
		//we've received navigation data, hide the progress bar
		findViewById(R.id.progress).setVisibility(GONE);
		if (!consoleData.isInSearch()) return;
		switch (direction.getStatus()) {
			case RequestResult.OK:
				//if successfully received data, we set navigation data in the view model
				viewModel.setNavigation(this, googleMap, Statics.getRoute(direction));
				return;
			default:
				//else we show an error message
				Toast.makeText(this, direction.getErrorMessage(), Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	public void onDirectionFailure(Throwable t) {
		//failed to receive navigation data, show an error message
		Timber.e(t);
		Toast.makeText(this, t.getLocalizedMessage(), Toast.LENGTH_LONG).show();
	}
	
	/**
	 * convenience method startActivityForResult with ActivityClass and request code
	 *
	 * @param activityClass: Class name of the activity to start
	 * @param requestCode:   Request Code to start activity with
	 */
	public void startActivityForResult(Class<? extends AppCompatActivity> activityClass, int requestCode) {
		viewModel.setHasChildActivities(true);
		startActivityForResult(new Intent(this, activityClass), requestCode);
		overridePendingTransition(R.anim.slideup_enter, R.anim.slideup_exit);
	}
	
	//we're asking location provider to change accuracy from FusedLocationProvider class
	//which has a constructor with this activity as argument, to prevent multiple requests
	public int getPERMISSION_REQUEST_CODE() {
		return PERMISSION_REQUEST_CODE;
	}
	
	//we set permission request code from fusedLocationProvider class
	public int setPERMISSION_REQUEST_CODE() {
		return this.PERMISSION_REQUEST_CODE = Statics.ENABLE_LOCATION_RC;
	}
	
	//we start listening for location updates if location provider
	// accuracy was set to high or we stop listening otherwise
	void toggleLocationListener(boolean enable) {
		if (enable && Statics.isLocationEnabled(this) && !locationProvider.isReceivingUpdates()) {
			//initialize map first if it wasn't initialize, otherwise start listening for location updates
			if (viewModel.isMapInitialized()) locationProvider.startUpdates();
			else mapView.getMapAsync(this);
			Toast.makeText(this, "Locations enabled", Toast.LENGTH_SHORT).show();
			return;
		}
		//disable location updates only if we're listening for updates
		if (!enable && locationProvider.isReceivingUpdates()) {
			locationProvider.stopUpdates();
			Toast.makeText(this, "Enable locations to navigate", Toast.LENGTH_SHORT).show();
		}
	}
	
	//we allow rotation only in search mode
	private void changeOrientation(boolean sensor) {
		setRequestedOrientation(sensor ? SCREEN_ORIENTATION_USER : SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
	}
	
	//we get relevant permissions
	private void getPermissions() {
		//ask for location permission if not acquired
		if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
			!= PackageManager.PERMISSION_GRANTED && PERMISSION_REQUEST_CODE == -1) {
			viewModel.setHasChildActivities(true);
			ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
				PERMISSION_REQUEST_CODE = (int) Math.abs(1000 * Math.random()));
			//else if location provider has a high accuracy we initialise map
		} else if (Statics.isLocationEnabled(this)) {
			viewModel.setMapInitialized();
			mapView.getMapAsync(this);
			//if location provider has no high accuracy, we ask user to change location provider settings
		} else if (PERMISSION_REQUEST_CODE == -1) {
			viewModel.setHasChildActivities(true);
			locationProvider.enableLocationService();
		}
	}
	
	//start activity for authentication
	private void authenticate() {
		startActivityForResult(new Intent(this, AuthenticateActivity.class), AUTHENTICATE_RC);
	}
	
	//initialize the map
	private void init() {
		//change the compass location
		@SuppressLint("ResourceType")
		View locationCompass = ((View) this.mapView.findViewById(1).getParent()).findViewById(5);
		RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) locationCompass.getLayoutParams();
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_END, RelativeLayout.TRUE);
		layoutParams.addRule(RelativeLayout.ALIGN_PARENT_START, 0);
		layoutParams.setMargins(0, getResources().getDimensionPixelOffset(R.dimen.compassTop), 0, 0);
		layoutParams.setMarginEnd(getResources().getDimensionPixelOffset(R.dimen.compassEnd));
		layoutParams.width = ViewGroup.LayoutParams.WRAP_CONTENT;
		
		//check if we've location permissions
		if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
			!= PackageManager.PERMISSION_GRANTED) return;
		googleMap.setMyLocationEnabled(true);
		googleMap.setTrafficEnabled(true);
		googleMap.setOnMapClickListener(this);
		toggleMapControls(!consoleData.isInSearch());
		//restore map if orientation changed
		viewModel.restoreMap(googleMap, this);
		
		//if walkthrough hasn't occurred, we start a walkthrough
		if (!application.wasConsoleShowcased())
			Statics.showConsoleShowcase(this, new Statics.OnShowcaseGoneListener() {
				@Override public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
					application.setConsoleShowcased();
					if (Statics.isLocationEnabled(ConsoleActivity.this))
						locationProvider.startUpdates();
				}
			});
			//else we simply start listening for location updates
		else if (Statics.isLocationEnabled(this)) locationProvider.startUpdates();
	}
	
	//hide these controls if in search mode
	private void toggleMapControls(boolean show) {
		UiSettings settings = googleMap.getUiSettings();
		settings.setCompassEnabled(show);
		settings.setZoomControlsEnabled(show);
		settings.setZoomGesturesEnabled(true);
		settings.setMyLocationButtonEnabled(show);
	}
	
	//action button was clicked
	private void actionClicked() {
		if (consoleData == null || !locationProvider.isReceivingUpdates()) return;
		//if we're in search mode we clear the map and shrink the done button
		if (consoleData.isInSearch()) {
			FloatingActionButton done = findViewById(R.id.done);
			if (done.getVisibility() == View.VISIBLE)
				new ViewAnimator(done, R.anim.shrink, 0)
					.addAnimationListener(new ViewAnimator.AnimationListener() {
						@Override public void onAnimationEnd(View view) {
							FloatingActionButton fab = (FloatingActionButton) view;
							fab.setVisibility(View.GONE);
							fab.setImageResource(R.drawable.icon_direction);
							viewModel.clearMap();
						}
					})
					.start();
			else viewModel.clearMap();
			//if we're in console mode and we've a navigation, we show dialog to clear the current navigation
		} else if (consoleData.getNavigation() != null) {
			new AlertDialog.Builder(this)
				.setIcon(R.mipmap.ic_launcher_round)
				.setTitle(R.string.cancelNav)
				.setMessage(R.string.cancelNav_Description)
				.setNegativeButton(android.R.string.cancel, (dialogInterface, i) -> dialogInterface.dismiss())
				.setPositiveButton(android.R.string.ok, (dialogInterface, i) -> {
					dialogInterface.dismiss();
					viewModel.clearMap();
					googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Statics.parseLocation(searchAdapter.getCurrentLocation()), 15));
				})
				.create()
				.show();
			//else we mutate the layout to search mode
		} else if (searchAdapter.getCurrentLocation() != null) viewModel.setInSearch(true);
	}
	
	//mutate layout to console mode and animate the camera
	private void showConsole() {
		viewModel.setInSearch(false);
		googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(Statics.parseLocation(searchAdapter.getCurrentLocation()),
			consoleData.getNavigation() != null ? 20 : 15));
	}
	
	//mutates the layout with animation
	private void mutateLayout(boolean inSearch, boolean cancel) {
		ConstraintSet mutated = new ConstraintSet();
		mutated.clone(this, inSearch ? R.layout.activity_console_search : R.layout.activity_console);
		TransitionManager.go(new Scene(findViewById(R.id.root)),
			TransitionInflater.from(this).inflateTransition(R.transition.console_transition)
				.addListener(new TransitionListenerAdapter() {
					@Override
					public void onTransitionStart(@NonNull Transition transition) {
						if (inSearch) {
							toggleMapControls(false);
							((FloatingActionButton) findViewById(R.id.action))
								.setImageResource(R.drawable.icon_clear);
						} else {
							toggleKeyboard(ConsoleActivity.this, findViewById(R.id.search), false);
							changeOrientation(false);
						}
					}
					
					@Override
					public void onTransitionEnd(@NonNull Transition transition) {
						if (inSearch) {
							changeOrientation(true);
							toggleKeyboard(ConsoleActivity.this, findViewById(R.id.search), true);
						} else {
							toggleMapControls(true);
							((FloatingActionButton) findViewById(R.id.action))
								.setImageResource(cancel ? R.drawable.icon_clear : R.drawable.icon_search);
						}
					}
				}));
		mutated.applyTo(findViewById(R.id.root));
	}
	
	//show done button with provided icon and animation
	private void showPositiveButton(FloatingActionButton done, int resource) {
		new ViewAnimator(done, R.anim.grow, 0)
			.addAnimationListener(new ViewAnimator.AnimationListener() {
				@Override public void onAnimationStart(View view) {
					((FloatingActionButton) view).setImageResource(resource);
					view.setVisibility(VISIBLE);
				}
			})
			.start();
	}
}