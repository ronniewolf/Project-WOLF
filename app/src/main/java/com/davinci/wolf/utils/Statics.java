package com.davinci.wolf.utils;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.location.Address;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import com.akexorcist.googledirection.model.Direction;
import com.akexorcist.googledirection.model.Leg;
import com.akexorcist.googledirection.model.Route;
import com.davinci.wolf.R;
import com.davinci.wolf.console.ConsoleActivity;
import com.davinci.wolf.console.SearchAdapter.Suggestion;
import com.github.amlcurran.showcaseview.OnShowcaseEventListener;
import com.github.amlcurran.showcaseview.ShowcaseView;
import com.github.amlcurran.showcaseview.targets.ViewTarget;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * Created by aakash on 11/13/17.
 */
public class Statics {
	public static final String PREF_HAS_LOGGED_IN = "loggedIn?";
	public static final String PREF_SEL_MODE = "selectedMode";
	public static final String PREF_TRANS_LEVEL = "transmission";
	public static final String PREF_SHOCKS_LEVEL = "shocks";
	public static final String PREF_ABS = "abs";
	
	public static final String PREF_CONSOLE_SHOWN = "wasConsoleShown";
	public static final String PREF_OPTIONS_SHOWN = "wasOptionsShown";
	
	public static final int ENABLE_LOCATION_RC = 1001;
	public static final int LOGIN_RC = 1002;
	public static final int AUTHENTICATE_RC = 1003;
	public static final int MODE_RC = 1004;
	public static final int AUTO_RC = 1005;
	public static final int MANUAL_RC = 1006;
	
	private static boolean contains(ArrayList<Suggestion> suggestions, Suggestion arg) {
		for (Suggestion suggestion : suggestions)
			if ((suggestion.location.latitude == arg.location.latitude && suggestion.location.longitude == arg.location.longitude)
				|| suggestion.address.toLowerCase().trim().equals(arg.address.toLowerCase().trim()))
				return true;
		return false;
	}
	
	public static boolean isLocationEnabled(Context context) {
		LocationManager manager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
		return manager != null && manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
	}
	
	public static void toggleKeyboard(Activity activity, View view, boolean show) {
		InputMethodManager manager = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
		if (manager == null) return;
		if (show) manager.showSoftInput(view, InputMethodManager.RESULT_SHOWN);
		else
			manager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.RESULT_HIDDEN);
	}
	
	public static ArrayList<Suggestion> normalizeSearch(List<Address> addresses) {
		ArrayList<Suggestion> suggestions = new ArrayList<>();
		String text, tmp;
		for (Address address : addresses) {
			text = "";
			if ((tmp = address.getFeatureName()) != null)
				text += tmp;
			if ((tmp = address.getThoroughfare()) != null)
				text += ", " + tmp;
			if ((tmp = address.getSubLocality()) != null)
				text += ", " + tmp;
			if ((tmp = address.getLocality()) != null)
				text += ", " + tmp;
			if ((tmp = address.getPostalCode()) != null)
				text += ", " + tmp;
			suggestions.add(new Suggestion(text, new LatLng(address.getLatitude(), address.getLongitude())));
		}
		return suggestions;
	}
	
	public static boolean isCoordinate(String text) {
		return Pattern.compile("([+-]?\\d+\\.?\\d+)\\s*,\\s*([+-]?\\d+\\.?\\d+)").matcher(text).matches();
	}
	
	@Nullable
	public static LatLng parseString(String text) {
		try {
			String[] split = text.split(",");
			return new LatLng(Double.parseDouble(split[0]), Double.parseDouble(split[1]));
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String parseLatlng(LatLng latLng) {
		return latLng.latitude + ", " + latLng.longitude;
	}
	
	public static void compileSuggestions(ArrayList<Suggestion> suggestions, List<Suggestion> fetched) {
		for (Suggestion suggestion : fetched)
			if (!contains(suggestions, suggestion)) suggestions.add(suggestion);
	}
	
	public static LatLng parseLocation(Location location) {
		return new LatLng(location.getLatitude(), location.getLongitude());
	}
	
	@Nullable
	public static ArrayList<LatLng> getRoute(Direction direction) {
		List<Route> routes = direction.getRouteList();
		if (routes == null || routes.size() < 1) return null;
		List<Leg> legs = routes.get(0).getLegList();
		if (legs == null || legs.size() < 1) return null;
		return legs.get(0).getDirectionPoint();
	}
	
	@NonNull
	public static LatLngBounds getRouteCamera(ArrayList<LatLng> positions) {
		LatLngBounds.Builder builder = LatLngBounds.builder();
		for (LatLng position : positions)
			builder.include(position);
		return builder.build();
	}
	
	public static double getHypotenuse(int left, int top, int right, int bottom) {
		float cx = (right - left) / 2f, cy = (bottom - top) / 2f;
		return Math.sqrt(cx * cx + cy * cy);
	}
	
	public static Bitmap getScreenshot(View v) {
		Bitmap b = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
		Canvas c = new Canvas(b);
		v.draw(c);
		return b;
	}
	
	public static Bitmap overlay(Bitmap bitmap1, Bitmap bitmap2) {
		Canvas canvas = new Canvas(bitmap1);
		canvas.drawBitmap(bitmap2, new Matrix(), null);
		return bitmap1;
	}
	
	public static class OnShowcaseGoneListener implements OnShowcaseEventListener {
		@Override public void onShowcaseViewHide(ShowcaseView showcaseView) {}
		
		@Override public void onShowcaseViewDidHide(ShowcaseView showcaseView) {}
		
		@Override public void onShowcaseViewShow(ShowcaseView showcaseView) {}
		
		@Override public void onShowcaseViewTouchBlocked(MotionEvent motionEvent) {}
	}
	
	public static void showConsoleShowcase(ConsoleActivity activity, OnShowcaseGoneListener showcaseGoneListener) {
		ShowcaseView consoleShowcase = new ShowcaseView.Builder(activity)
			.setTarget(new ViewTarget(R.id.console, activity))
			.setContentTitle(R.string.console_showcase_title)
			.setContentText(R.string.console_showcase_desc)
			.hideOnTouchOutside()
			.withNewStyleShowcase()
			.setStyle(R.style.AppTheme_Showcase)
			.build();
		consoleShowcase.setOnShowcaseEventListener(new OnShowcaseGoneListener() {
			@Override
			public void onShowcaseViewDidHide(ShowcaseView showcaseView) {
				ShowcaseView searchShowcase = new ShowcaseView.Builder(activity)
					.setTarget(new ViewTarget(R.id.action, activity))
					.setContentTitle(R.string.search_showcase_title)
					.setContentText(R.string.search_showcase_desc)
					.hideOnTouchOutside()
					.withNewStyleShowcase()
					.setStyle(R.style.AppTheme_Showcase)
					.build();
				searchShowcase.setOnShowcaseEventListener(showcaseGoneListener);
			}
		});
		consoleShowcase.show();
	}
	
	/*public static Bitmap blur(Context context, Bitmap image, float scale, float radius) {
		int width = Math.round(image.getWidth() * scale),
			height = Math.round(image.getHeight() * scale);
		
		Bitmap inputBitmap = Bitmap.createScaledBitmap(image, width, height, false),
			outputBitmap = Bitmap.createBitmap(inputBitmap);
		RenderScript rs = RenderScript.create(context);
		Allocation tmpIn = Allocation.createFromBitmap(rs, inputBitmap),
			tmpOut = Allocation.createFromBitmap(rs, outputBitmap);
		
		ScriptIntrinsicBlur theIntrinsic = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
		theIntrinsic.setRadius(radius);
		theIntrinsic.setInput(tmpIn);
		theIntrinsic.forEach(tmpOut);
		tmpOut.copyTo(outputBitmap);
		
		return outputBitmap;
	}*/
}