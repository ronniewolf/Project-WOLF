package com.davinci.wolf.about;

import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView.Adapter;
import android.support.v7.widget.RecyclerView.ViewHolder;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.davinci.wolf.R;

import java.util.ArrayList;

/**
 * Created by aakash on 11/21/17.
 */
public class AboutAdapter extends Adapter<AboutAdapter.AboutViewHolder> {
	final ArrayList<AboutData> aboutData = new ArrayList<>();
	
	@Override
	public AboutViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
		return new AboutViewHolder(LayoutInflater.from(parent.getContext()).inflate(R.layout.item_about, parent, false));
	}
	
	@Override
	public void onBindViewHolder(AboutViewHolder holder, int position) {
		holder.bind(aboutData.get(position));
	}
	
	@Override
	public int getItemCount() {
		return aboutData.size();
	}
	
	//AboutData holds the library used and it its respective author
	static class AboutData {
		final String library;
		final String author;
		
		AboutData(String library, String author) {
			this.library = library;
			this.author = author;
		}
	}
	
	static class AboutViewHolder extends ViewHolder {
		
		TextView title = null, subtitle = null;
		
		AboutViewHolder(View itemView) {
			super(itemView);
			CardView root = (CardView) itemView;
			(title = root.findViewById(R.id.title)).setMovementMethod(LinkMovementMethod.getInstance());
			(subtitle = root.findViewById(R.id.subtitle)).setMovementMethod(LinkMovementMethod.getInstance());
		}
		
		//bind AboutViewHolder with AboutData
		void bind(AboutData aboutData) {
			title.setText(Html.fromHtml(aboutData.library));
			//title.setMovementMethod(LinkMovementMethod.getInstance());
			subtitle.setText(Html.fromHtml(aboutData.author));
			//subtitle.setMovementMethod(LinkMovementMethod.getInstance());
		}
	}
}
