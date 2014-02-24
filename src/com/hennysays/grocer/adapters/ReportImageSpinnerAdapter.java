package com.hennysays.grocer.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ReportImageSpinnerAdapter extends ArrayAdapter<String> {
	
	public ReportImageSpinnerAdapter(Context context, int textViewResourceId, String[] strings) {
        super(context, textViewResourceId,strings);
    }
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = super.getView(position, convertView, parent);
		v.setVisibility(View.GONE);
		return v;
	}
		
	@Override
	public View getDropDownView(int position, View convertView, ViewGroup parent) {
		View v;
		if(position==0) {
			TextView tv = new TextView(getContext());
			tv.setVisibility(View.GONE);
			tv.setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
			
			v = tv;
		}
		else {
			v = super.getDropDownView(position, null, parent);
		}
		
        return v;
	
		
	}
	
//    @Override
//    public View getDropDownView(int position, View convertView, ViewGroup parent) {
////        View v = null;
////        if (position == 0) {
////            TextView tv = new TextView(getContext());
////            tv.setVisibility(View.GONE);
////            v = tv;
////        }
////        else {
//////        parent.setVerticalScrollBarEnabled(false);
////        v = super.getDropDownView(position, null, parent);
////        }
//        
//        View v = super.getDropDownView(position, null, parent);
//        if (position == 0) {
//            v.setVisibility(View.GONE);
//        }
//        return v;
//    }
}