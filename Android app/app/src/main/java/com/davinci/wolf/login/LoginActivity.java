package com.davinci.wolf.login;

import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import com.davinci.wolf.R;
import com.davinci.wolf.application.WolfApplication;
import com.davinci.wolf.login.di.ActivityModule;
import com.davinci.wolf.login.di.DaggerLoginComponent;
import com.davinci.wolf.utils.ViewAnimator;

import javax.inject.Inject;
import javax.inject.Named;

import static android.view.View.INVISIBLE;
import static android.view.View.VISIBLE;

public class LoginActivity extends AppCompatActivity
	implements View.OnClickListener {
	
	//Dependency Injection
	@Inject WolfApplication application;
	@Inject @Named("username") EditTextObserver username;
	@Inject @Named("password") EditTextObserver password;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
		setContentView(R.layout.activity_login);
		setTitle(R.string.loginTitle);
		
		DaggerLoginComponent.builder()
			.activityModule(new ActivityModule(this))
			.build().inject(this);
		//findViewById(R.id.login).setOnClickListener(this);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
	}
	
	@Override
	public void onClick(View view) {
		//shrink the login button and show a progress bar
		//this is usually done in async task, not done here because this one's fake
		new ViewAnimator(view, R.anim.shrink, 0)
			.addAnimationListener(new ViewAnimator.AnimationListener() {
				@Override public void onAnimationEnd(View v) {
					v.setVisibility(INVISIBLE);
					findViewById(R.id.progress).setVisibility(VISIBLE);
					//hide the progress bar and finish this activity with positive result
					new Handler()
						.postDelayed(() -> {
							if (application.setLoggedIn()) {
								setResult(RESULT_OK);
								finish();
							} else {
								Toast.makeText(LoginActivity.this, "Unable to login\nTry again later", Toast.LENGTH_SHORT).show();
								finish();
							}
						}, 2500);
				}
			})
			.start();
	}
}
