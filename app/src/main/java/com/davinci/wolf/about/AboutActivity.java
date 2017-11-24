package com.davinci.wolf.about;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.davinci.wolf.R;

import java.util.Collections;

//AboutActivity shows all information related to the app
public class AboutActivity extends AppCompatActivity {
	
	AboutAdapter aboutAdapter = null;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_about);
		((TextView)findViewById(R.id.author)).setMovementMethod(LinkMovementMethod.getInstance());
		Collections.addAll((aboutAdapter = new AboutAdapter()).aboutData, Dependencies.getLibraries());
		
		//initialize the recyclerView adapter
		RecyclerView recyclerView = this.findViewById(R.id.about);
		recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
		recyclerView.setAdapter(aboutAdapter);
	}
}
