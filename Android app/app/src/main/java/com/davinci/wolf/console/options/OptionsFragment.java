package com.davinci.wolf.console.options;

import android.animation.Animator;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLayoutChangeListener;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.widget.Toast;

import com.davinci.wolf.R;
import com.davinci.wolf.console.ConsoleActivity;
import com.davinci.wolf.console.options.di.DaggerOptionsComponent;
import com.davinci.wolf.console.options.di.FragmentModule;
import com.davinci.wolf.settings.auto.AutoActivity;
import com.davinci.wolf.settings.manual.ManualActivity;
import com.davinci.wolf.settings.mode.ModeActivity;
import com.davinci.wolf.utils.Statics;

import javax.inject.Inject;
import javax.inject.Named;

import static com.davinci.wolf.utils.Statics.AUTO_RC;
import static com.davinci.wolf.utils.Statics.MANUAL_RC;
import static com.davinci.wolf.utils.Statics.MODE_RC;
//Option fragment shown when user clicks on the console
public class OptionsFragment extends Fragment
	implements OnLayoutChangeListener,
	OnClickListener, View.OnLongClickListener {
	
	public static OptionsFragment newInstance(Bitmap background) {
		Bundle args = new Bundle();
		args.putParcelable("background", background);
		OptionsFragment fragment = new OptionsFragment();
		fragment.setArguments(args);
		return fragment;
	}
	
	private ConstraintLayout root = null;
	
	//Dependency Injection
	@Inject ConsoleActivity consoleActivity;
	@Inject OptionsViewModel viewModel;
	@Inject @Named("mode") FloatingActionButton mode;
	@Inject @Named("auto") FloatingActionButton auto;
	@Inject @Named("manual") FloatingActionButton manual;
	
	@Override @Nullable
	public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		root = (ConstraintLayout) inflater.inflate(R.layout.fragment_options, container, false);
		DaggerOptionsComponent.builder()
			.fragmentModule(new FragmentModule(this))
			.build()
			.inject(this);
		
		//set blurred background to root layout
		Bitmap background;
		Bundle args = getArguments();
		if (args != null) {
			background = viewModel.setBackground(getArguments().getParcelable("background"));
			args.clear();
		} else background = viewModel.getBackground();
		if (background != null) root.setBackground(new BitmapDrawable(getResources(), background));
		
		root.addOnLayoutChangeListener(this);
		root.setOnClickListener(this);
		
		return root;
	}
	
	@Override
	public void onLayoutChange(View view, int left, int top, int right, int bottom, int prevLeft, int prevTop, int prevRight, int prevBottom) {
		//circular reveal when animation shows up
		root.removeOnLayoutChangeListener(this);
		Point center = new Point((right - left) / 2, (bottom - top) / 2);
		Animator reveal = ViewAnimationUtils.createCircularReveal(root, center.x, center.y, 0, (int) Math.ceil(Statics.getHypotenuse(left, top, right, bottom)));
		reveal.setInterpolator(new FastOutSlowInInterpolator());
		reveal.setDuration(500);
		reveal.addListener(new Animator.AnimatorListener() {
			@Override
			public void onAnimationStart(Animator animator) {}
			
			@Override
			public void onAnimationEnd(Animator animator) {
				mode.setVisibility(View.VISIBLE);
				auto.setVisibility(View.VISIBLE);
				manual.setVisibility(View.VISIBLE);
			}
			
			@Override public void onAnimationCancel(Animator animator) {}
			
			@Override public void onAnimationRepeat(Animator animator) {}
		});
		reveal.start();
	}
	
	@Override
	public void onClick(View view) {
		//launch appropriate activity on corresponding button click
		switch (view.getId()) {
			case R.id.root:
				if (consoleActivity != null) consoleActivity.onBackPressed();
				break;
			case R.id.mode:
				consoleActivity.startActivityForResult(ModeActivity.class, MODE_RC);
				break;
			case R.id.auto:
				consoleActivity.startActivityForResult(AutoActivity.class, AUTO_RC);
				break;
			case R.id.manual:
				consoleActivity.startActivityForResult(ManualActivity.class, MANUAL_RC);
				break;
		}
	}
	
	@Override
	public boolean onLongClick(View view) {
		//Show toast on long click
		String toast = null;
		switch (view.getId()) {
			case R.id.mode:
				toast = "Set riding mode";
				break;
			case R.id.auto:
				toast = "Smart auto configuration";
				break;
			case R.id.manual:
				toast = "Manual configuration";
				break;
		}
		if (toast != null) Toast.makeText(consoleActivity, toast, Toast.LENGTH_LONG).show();
		return true;
	}
	
	public View findViewById(int Id) {
		return root.findViewById(Id);
	}
}
