package com.davinci.wolf.login;

import android.arch.lifecycle.LifecycleOwner;
import android.arch.lifecycle.Observer;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.design.widget.TextInputLayout;
import android.text.Editable;
import android.text.TextWatcher;

import com.davinci.wolf.R;

/**
 * Created by aakash on 11/14/17.
 */
public class EditTextObserver
	implements Observer<String>, TextWatcher {
	
	private TextInputEditText editText = null;
	private LoginViewModel loginViewModel = null;
	private int id = -1;
	
	public EditTextObserver(TextInputLayout layout, LoginViewModel loginViewModel) {
		this.editText = (TextInputEditText) layout.getEditText();
		this.loginViewModel = loginViewModel;
		if (this.editText == null) return;
		this.editText.addTextChangedListener(this);
		switch (this.id = layout.getId()) {
			case R.id.username:
				this.loginViewModel.getUsername().observe((LifecycleOwner) layout.getContext(), this);
				break;
			case R.id.password:
				this.loginViewModel.getPassword().observe((LifecycleOwner) layout.getContext(), this);
				break;
		}
	}
	
	@Override
	public void onChanged(@Nullable String s) {
		if (editText.getText().toString().equals(s)) return;
		editText.setText(s);
		editText.setSelection(s != null ? s.length() : 0);
	}
	
	@Override
	public void afterTextChanged(Editable editable) {
		if (editText == null) return;
		switch (id) {
			case R.id.username:
				loginViewModel.setUsername(editable.toString());
				break;
			case R.id.password:
				loginViewModel.setPassword(editable.toString());
				break;
		}
	}
	
	@Override public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
	
	@Override public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {}
}
