package com.davinci.wolf.authentication;

import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.davinci.wolf.R;
import com.github.ajalt.reprint.core.AuthenticationFailureReason;
import com.github.ajalt.reprint.core.AuthenticationListener;
import com.github.ajalt.reprint.core.Reprint;

//AuthenticationActivity authenticates user's fingerprint to start using the actual app
public class AuthenticateActivity extends AppCompatActivity
	implements AuthenticationListener {
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE);
		setContentView(R.layout.activity_authenticate);
		Reprint.authenticate(this);
	}
	
	@Override
	public void onSuccess(int moduleTag) {
		Toast.makeText(this, "Authentication successful", Toast.LENGTH_SHORT).show();
		setResult(RESULT_OK);
		finish();
	}
	
	@Override
	public void onFailure(AuthenticationFailureReason failureReason, boolean fatal, CharSequence errorMessage, int moduleTag, int errorCode) {
		Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
		setResult(RESULT_CANCELED);
	}
}
