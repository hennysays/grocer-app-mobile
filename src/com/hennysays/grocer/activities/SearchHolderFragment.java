package com.hennysays.grocer.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hennysays.grocer.R;

public class SearchHolderFragment extends Fragment {
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view =  inflater.inflate(R.layout.fragment_search_nested_holder, container, false);
		
		getChildFragmentManager().beginTransaction().add(R.id.fragment_search_nested_holder, new SearchFragment(), SearchFragment.TAG).commit();		
		return view;
	}
}
