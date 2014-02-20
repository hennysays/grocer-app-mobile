package com.hennysays.grocer.util;

import android.content.Context;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;

import com.hennysays.grocer.R;

public class GrocerAnimations {
	private View view;
	
	public GrocerAnimations(View view) {
		this.view = view;
	}
	
	public void slideDown(Context context, View v) {
		Animation a = AnimationUtils.loadAnimation(context, R.anim.slide_down);
		if(a !=null) {
			a.reset();
			if(v != null && !v.isShown()) {
				v.setVisibility(View.VISIBLE);
				v.clearAnimation();
				v.startAnimation(a);
			}
		}
	}
	
	public void slideUp(Context context, View v) {
		Animation a = AnimationUtils.loadAnimation(context, R.anim.slide_up);
		a.setAnimationListener(animationListener);
		if(a !=null) {
			a.reset();
			if(v != null) {
				v.clearAnimation();
				v.startAnimation(a);
			}
		}
	}
	
	final private AnimationListener animationListener = new AnimationListener() {
		@Override
		public void onAnimationEnd(Animation animation) {
        	if(view.isShown()){
        		view.setVisibility(View.GONE);
        	}
		}
		@Override
		public void onAnimationRepeat(Animation animation) {
			// TODO Auto-generated method stub
		}
		@Override
		public void onAnimationStart(Animation animation) {

			// TODO Auto-generated method stub
		}
		
	};
	
}
