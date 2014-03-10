package com.hennysays.grocer.adapters;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckedTextView;

import com.haarman.listviewanimations.ArrayAdapter;
import com.hennysays.grocer.R;
import com.hennysays.grocer.models.GroceryItem;

public class GroceryListAdapter extends ArrayAdapter<GroceryItem> {
	private Context mContext;
	private List<Integer> mSelectedPositions;

	public GroceryListAdapter(Context context, List<Integer> selected) {
		mSelectedPositions = selected;
		mContext = context;
	}

	public void addSelectedPositions(int position) {
		mSelectedPositions.add(position);
	}
	
	public void removeSelectedPositions(int position) {
		mSelectedPositions.remove(position);
	}
	
	public void clearSelectedPositions() {
		mSelectedPositions.clear();
	}
	
	public List<Integer> getSelectedPositions() {
		return mSelectedPositions;
	}
	
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		CheckedTextView tv = (CheckedTextView) convertView;
		if(tv==null) {
			tv = (CheckedTextView) LayoutInflater.from(mContext).inflate(R.layout.fragment_grocery_list_dismissrow, parent, false);
		}
		
		tv.setText(getItem(position).getName());
		tv.setChecked(mSelectedPositions.contains(position));
		return tv;
 	}
}