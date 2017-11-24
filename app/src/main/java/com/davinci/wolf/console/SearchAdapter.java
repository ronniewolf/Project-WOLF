package com.davinci.wolf.console;

import android.content.Context;
import android.location.Geocoder;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import com.davinci.wolf.utils.Statics;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Locale;

/**
 * Created by aakash on 11/16/17.
 * AutocompletetextView search views adapter
 * handles geosearch as well
 */
public class SearchAdapter extends ArrayAdapter<SearchAdapter.Suggestion> {
	private final ArrayList<Suggestion> suggestions = new ArrayList<>();
	private final ArrayList<Suggestion> filtered = new ArrayList<>();
	private Location currentLocation = null;
	
	public SearchAdapter(@NonNull Context context) {
		super(context, android.R.layout.simple_list_item_single_choice);
	}
	
	//the class holds the location changes since it redundant to hold multiple references to same instance
	Location setCurrentLocation(Location currentLocation) {
		return this.currentLocation = currentLocation;
	}
	
	//get the saved location
	Location getCurrentLocation() {
		return currentLocation;
	}
	
	@Override
	public int getCount() {
		return filtered.size();
	}
	
	@Nullable @Override
	public Suggestion getItem(int position) {
		return filtered.get(position);
	}
	
	@NonNull @Override
	public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
		if (convertView == null)
			convertView = LayoutInflater.from(getContext())
				.inflate(android.R.layout.simple_spinner_dropdown_item, parent, false);
		((TextView) convertView).setText(filtered.get(position).address);
		return convertView;
	}
	
	@NonNull @Override
	public Filter getFilter() {
		return new GeoFilter(this);
	}
	
	/**
	 * Handles geo searching within
	 * a box of 0.02 coordinate size
	 */
	private static class GeoFilter extends Filter implements Comparator<Suggestion> {
		final SearchAdapter adapter;
		
		GeoFilter(SearchAdapter adapter) {
			this.adapter = adapter;
		}
		
		@Override
		protected FilterResults performFiltering(CharSequence charSequence) {
			try {
				if (charSequence != null && charSequence != ""
					&& charSequence.length() > 2 && !Statics.isCoordinate(charSequence.toString().toLowerCase().trim())
					&& adapter.currentLocation != null) {
					Location location = adapter.currentLocation;
					Statics.compileSuggestions(adapter.suggestions,
						Statics.normalizeSearch(new Geocoder(adapter.getContext(), Locale.getDefault())
							.getFromLocationName(charSequence.toString(), 5,
								location.getLatitude() - 1, location.getLongitude() - 1,
								location.getLatitude() + 1, location.getLongitude() + 1)));
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
			ArrayList<Suggestion> filtered = filterSuggestions(charSequence);
			FilterResults filterResults = new FilterResults();
			filterResults.count = filtered.size();
			filterResults.values = filtered;
			return filterResults;
		}
		
		@Override
		protected void publishResults(CharSequence charSequence, FilterResults filterResults) {
			//noinspection unchecked
			ArrayList<Suggestion> filtered = (ArrayList<Suggestion>) filterResults.values;
			Collections.sort(filtered, this);
			adapter.filtered.clear();
			adapter.filtered.addAll(filtered);
			adapter.notifyDataSetChanged();
		}
		
		@Override public CharSequence convertResultToString(Object resultValue) {
			return ((Suggestion) resultValue).address;
		}
		
		private ArrayList<Suggestion> filterSuggestions(CharSequence constraint) {
			if (constraint == null || constraint.equals("") || Statics.isCoordinate(constraint.toString()))
				return adapter.suggestions;
			ArrayList<Suggestion> filtered = new ArrayList<>();
			for (Suggestion suggestion : adapter.suggestions)
				if (suggestion.address.toLowerCase().trim().contains(constraint.toString().toLowerCase().trim()))
					filtered.add(suggestion);
			return filtered;
		}
		
		@Override
		public int compare(Suggestion s1, Suggestion s2) {
			Location first = new Location("first"),
				second = new Location("seconde");
			first.setLatitude(s1.location.latitude);
			first.setLongitude(s1.location.longitude);
			second.setLatitude(s2.location.latitude);
			second.setLongitude(s2.location.longitude);
			return Double.compare(adapter.currentLocation.distanceTo(first), adapter.currentLocation.distanceTo(second));
		}
	}
	
	//Holds suggestion address and it's corresponding coordinate
	public static class Suggestion {
		public final String address;
		public final LatLng location;
		
		public Suggestion(String address, LatLng location) {
			this.address = address;
			this.location = location;
		}
	}
}
