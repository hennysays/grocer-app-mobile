package com.hennysays.grocer.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hennysays.grocer.R;

public class GroceryListFragment extends Fragment {
	public static final String TAG = "Grocery List Fragment";
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_grocery_list, container,
				false);
		return view;
	}
}
