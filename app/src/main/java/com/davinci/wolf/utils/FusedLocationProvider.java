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
 */
public class FusedLocationProvider extends LocationCallback
	implements OnSuccessListener<Location>, OnCompleteListener<LocationSettingsResponse> {
	
	private ConsoleActivity activity = null;
	
	private FusedLocationProviderClient client = null;
	private LocationRequest request = null;
	private LocationChangedListener locationChangedListener = null;
	
	private boolean receivingUpdates = false;
	
	public FusedLocationProvider(ConsoleActivity activity, LocationRequest request) {
		client = new FusedLocationProviderClient(this.activity = activity);
		this.request = request;
	}
	
	public void setLocationChangedListener(LocationChangedListener locationChangedListener) {
		this.locationChangedListener = locationChangedListener;
	}
	
	public void enableLocationService() {
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
			switch (e.getStatusCode()) {
				case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
					try {
						ResolvableApiException resolvable = (ResolvableApiException) e;
						resolvable.startResolutionForResult(activity, activity.setPERMISSION_REQUEST_CODE());
					} catch (Exception ex) {
						ex.printStackTrace();
						Toast.makeText(this.activity, "Please enable locations", Toast.LENGTH_LONG).show();
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