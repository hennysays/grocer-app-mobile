package com.hennysays.grocer.activities;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.hennysays.grocer.R;
import com.hennysays.grocer.activities.MainActivity.SectionsPagerAdapter.SearchPageListener;

public class SearchFragment extends Fragment {
	public static final String TAG = "Search Fragment";  
	public static SearchPageListener listener;

    public static SearchFragment newInstance(SearchPageListener searchPageListener) {
        SearchFragment fragment = new SearchFragment();
        listener = searchPageListener;
        return fragment;
    }
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_search, container,
				false);
		return view;
	}
}
