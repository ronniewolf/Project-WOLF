package com.davinci.wolf.settings.auto;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.davinci.wolf.R;
//Auto Settings Activity
public class AutoActivity extends AppCompatActivity {
	
	private boolean backPressAllowed = false;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_auto);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		findViewById(R.id.progress).setVisibility(View.VISIBLE);
		new Handler().postDelayed(this::saveAndFinish, 5000);
	}
	
	@Override
	public void onBackPressed() {
		if (!backPressAllowed) return;
		super.onBackPressed();
		overridePendingTransition(R.anim.slidedown_enter, R.anim.slidedown_exit);
	}
	
	@Override
	public boolean onSupportNavigateUp() {
		onBackPressed();
		return true;
	}
	
	private void saveAndFinish() {
		backPressAllowed = true;
		setResult(RESULT_OK);
		finish();
		overridePendingTransition(R.anim.slidedown_enter, R.anim.slidedown_exit);
	}
}
