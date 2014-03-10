package com.hennysays.grocer.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.hennysays.grocer.R;

public class LimitDistanceSliderView extends LinearLayout {
	private int distance;
	private SeekBar slider; 
	private TextView result;
	public LimitDistanceSliderView(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TriStateToggleButtonView, 0, 0);
		distance = a.getInt(R.styleable.LimitDistanceSliderView_distance,50);
		a.recycle();
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.search_limitdistanceslider, this, true);	
		result = (TextView) view.findViewById(R.id.result);
		slider = (SeekBar) view.findViewById(R.id.distance_slider);
		result.setText(distance + " km");
		slider.setProgress(distance);
		
		slider.setOnSeekBarChangeListener( new OnSeekBarChangeListener() {

			@Override
			public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
				result.setText(Integer.toString(progress + 1) + " Km");
			}

			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}

			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			
		});
	}
}
