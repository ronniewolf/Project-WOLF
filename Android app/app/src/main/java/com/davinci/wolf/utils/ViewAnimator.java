package com.davinci.wolf.utils;

import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import java.util.ArrayList;

/**
 * Created by aakash on 11/14/17.
 * Convenience class to animate view
 */
public class ViewAnimator implements Animation.AnimationListener {
	private View view = null;
	private Animation animation = null;
	private long DELAY = 0;
	
	private final ArrayList<AnimationListener> animationListeners = new ArrayList<>();
	
	/**
	 * @param view: view to animate
	 * @param animID: anim resource id describing the animation
	 * @param DELAY: delay of animation
	 */
	public ViewAnimator(View view, int animID, long DELAY) {
		this.view = view;
		this.DELAY = DELAY;
		(this.animation = AnimationUtils.loadAnimation(view.getContext(), animID))
			.setAnimationListener(this);
	}
	
	public ViewAnimator addAnimationListener(AnimationListener animationListener) {
		this.animationListeners.add(animationListener);
		return this;
	}
	
	public void start() {
		//native delay only delays the animation but but doesn't delay the listener
		//hence a postDelayed is used
		new Handler()
			.postDelayed(() -> view.startAnimation(animation), DELAY);
	}
	
	@Override
	public void onAnimationStart(Animation animation) {
		for (AnimationListener listener : animationListeners)
			listener.onAnimationStart(view);
	}
	
	@Override
	public void onAnimationEnd(Animation animation) {
		for (AnimationListener listener : animationListeners)
			listener.onAnimationEnd(view);
	}
	
	@Override public void onAnimationRepeat(Animation animation) {}
	
	public static class AnimationListener {
		public void onAnimationStart(View view) {}
		
		public void onAnimationEnd(View view) {}
	}
}

/*private final static int MODE_SIMULTANEOUS = 1, MODE_SEQUENTIAL = 2;
	
	public static void playTogether(int MODE, final ViewAnimator... animators) {
		if (MODE == MODE_SIMULTANEOUS) {
			for (ViewAnimator animator : animators)
				animator.start();
			return;
		}
		for (int i = 0, iL = animators.length - 1; i < iL; i++) {
			final int next = i + 1;
			animators[i].addAnimationListener(new AnimationListener() {
				@Override
				public void onAnimationEnd(View view) {
					animators[next].start();
				}
			});
		}
		animators[0].start();
	}*/