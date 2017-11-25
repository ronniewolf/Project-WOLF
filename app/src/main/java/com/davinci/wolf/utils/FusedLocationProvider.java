package com.davinci.wolf.utils;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.support.annotation.NonNull;
import android.widget.Toast;

import com.davinci.wolf.console.ConsoleActivity;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import static android.support.v4.content.ContextCompat.checkSelfPermission;

/**
 * Created by aakash on 11/13/17.
 * Convenience class to wrap all of google's api at a single place
 * with only one listener
 */
public class FusedLocationProvider extends LocationCallback
	implements OnSuccessListener<Location>, OnCompleteListener<LocationSettingsResponse> {
	
	//parent activity required to ask for location provider changes
	private ConsoleActivity activity = null;
	
	//actual client that is responsible to handle location request
	private FusedLocationProviderClient client = null;
	//the request parameter passed to the client
	private LocationRequest request = null;
	//Listener invoked when a location is available
	private LocationChangedListener locationChangedListener = null;
	
	private boolean receivingUpdates = false;
	
	/**
	 * @param activity: Parent activity, change to AppCompatActivity to make it generic
	 * @param request: LocationRequest parameter passed to FusedLocationProviderClient
	 */
	public FusedLocationProvider(ConsoleActivity activity, LocationRequest request) {
		client = new FusedLocationProviderClient(this.activity = activity);
		this.request = request;
	}
	
	public void setLocationChangedListener(LocationChangedListener locationChangedListener) {
		this.locationChangedListener = locationChangedListener;
	}
	
	//asks location provider settings change
	public void enableLocationService() {
		//checks if location provider is in high accuracy state
		if (activity.getPERMISSION_REQUEST_CODE() == -1)
			LocationServices.getSettingsClient(activity)
				.checkLocationSettings(new LocationSettingsRequest.Builder()
					.addLocationRequest(request).setNeedBle(true).build())
				.addOnCompleteListener(this);
	}
	
	public void startUpdates() {
		if (this.client == null || checkSelfPermission(client.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION)
			!= PackageManager.PERMISSION_GRANTED)
			return;
		receivingUpdates = true;
		client.getLastLocation().addOnSuccessListener(this);
		this.client.requestLocationUpdates(request, this, null);
	}
	
	public void stopUpdates() {
		receivingUpdates = false;
		this.client.removeLocationUpdates(this);
	}
	
	public boolean isReceivingUpdates() {
		return receivingUpdates;
	}
	
	@Override
	public void onComplete(@NonNull Task<LocationSettingsResponse> task) {
		try {
			task.getResult(ApiException.class);
		} catch (ApiException e) {
			//location provider is not in desired state, show system dialog to change that
			switch (e.getStatusCode()) {
				case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
					try {
						ResolvableApiException resolvable = (ResolvableApiException) e;
						resolvable.startResolutionForResult(activity, activity.setPERMISSION_REQUEST_CODE());
					} catch (Exception ex) {
						ex.printStackTrace();
						//user hasn't enable location so show the error message
						Toast.makeText(activity, "Please enable locations", Toast.LENGTH_LONG).show();
					}
					break;
			}
		}
	}
	
	@Override
	public void onLocationResult(LocationResult locationResult) {
		if (this.locationChangedListener == null) return;
		this.locationChangedListener.onLocationChanged(locationResult.getLocations().get(0));
	}
	
	@Override
	public void onSuccess(Location location) {
		if (this.locationChangedListener == null) return;
		this.locationChangedListener.onLocationChanged(location);
	}
	
	public interface LocationChangedListener {
		void onLocationChanged(Location location);
	}
}