package com.hennysays.grocer.views;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hennysays.grocer.R;

public class TriStateToggleButtonView extends LinearLayout {
	private int position;
	private String labelName,button1Name,button2Name,button3Name;
	private TextView label,result,button1,button2,button3;
	
	public TriStateToggleButtonView(Context context, AttributeSet attrs) {
		super(context, attrs);

		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.TriStateToggleButtonView, 0, 0);
		position = a.getInt(R.styleable.TriStateToggleButtonView_labelPosition,0);
		labelName = a.getString(R.styleable.TriStateToggleButtonView_label);
		button1Name = a.getString(R.styleable.TriStateToggleButtonView_button1);
		button2Name = a.getString(R.styleable.TriStateToggleButtonView_button2);
		button3Name = a.getString(R.styleable.TriStateToggleButtonView_button3);
		a.recycle();
		
		LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.search_tristatetogglebutton, this, true);	
		label = (TextView) view.findViewById(R.id.label);
		result = (TextView) view.findViewById(R.id.result);
		button1 = (TextView) view.findViewById(R.id.button1);
		button2 = (TextView) view.findViewById(R.id.button2);
		button3 = (TextView) view.findViewById(R.id.button3);
		
		label.setText(labelName);
		button1.setText(button1Name);
		button2.setText(button2Name);
		button3.setText(button3Name);
		
		button1.setOnClickListener(mOnClickListener);
		button2.setOnClickListener(mOnClickListener);
		button3.setOnClickListener(mOnClickListener);
		
		switch(position) {
		case 0:
			setButton(button1);
			break;
		case 1:
			setButton(button2);
			break;
		case 2:
			setButton(button3);
			break;
		}
		
	}
	
	private OnClickListener mOnClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			setButton(v);
		}

		
	};

	public void setButton(View v) {
		switch(v.getId()) {
		case R.id.button1:
			position = 0;
			button1.setBackgroundResource(R.drawable.rounded_corners);
			button2.setBackgroundColor(Color.WHITE);
			button3.setBackgroundColor(Color.WHITE);
			button1.setTextColor(Color.WHITE);
			button2.setTextColor(Color.BLACK);
			button3.setTextColor(Color.BLACK);
			break;
		case R.id.button2:
			position = 1;
			button1.setBackgroundColor(Color.WHITE);
			button2.setBackgroundResource(R.drawable.rounded_corners);
			button3.setBackgroundColor(Color.WHITE);
			button1.setTextColor(Color.BLACK);
			button2.setTextColor(Color.WHITE);
			button3.setTextColor(Color.BLACK);
			break;
		case R.id.button3:
			position = 2;
			button1.setBackgroundColor(Color.WHITE);
			button2.setBackgroundColor(Color.WHITE);
			button3.setBackgroundResource(R.drawable.rounded_corners);
			button1.setTextColor(Color.BLACK);
			button2.setTextColor(Color.BLACK);
			button3.setTextColor(Color.WHITE);
			break;
	}
		
		result.setText(((TextView) v).getText().toString());
	}
	
}
