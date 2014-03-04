package com.hennysays.grocer.activities;

import java.util.ArrayList;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.hennysays.grocer.R;
import com.hennysays.grocer.activities.MainActivity.SectionsPagerAdapter.SearchPageListener;
import com.hennysays.grocer.adapters.SearchResultsAdapter;
import com.hennysays.grocer.controller.Controller;
import com.hennysays.grocer.models.GroceryItem;
import com.hennysays.grocer.views.ClearableAutoCompleteTextView;

public class SearchResultsFragment extends Fragment {
	public static final String TAG = "Search Results Fragment";  
	private SearchResultsAdapter mSearchResultsAdapter;
	private View mView;
	public static SearchPageListener listener;
	ArrayList<GroceryItem> list;
	boolean isQueried = false;
	private String prevQuery = null;
	
    public static SearchResultsFragment newInstance(SearchPageListener searchPageListener) {
        SearchResultsFragment fragment = new SearchResultsFragment();
        listener = searchPageListener;
        return fragment;
    }
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_search_results, container,false);
//		String query = getArguments().getString("query");
		
		ClearableAutoCompleteTextView view = (ClearableAutoCompleteTextView) getActivity().findViewById(R.id.search_autocompletetextview);
		String query;
		if(view != null) {
		query = ((TextView) view).getText().toString();		
		}
		else {
			query = prevQuery;
		}
		
		runHttpAsyncTask(query);
		return mView;

	}

	
	public void runHttpAsyncTask(String query) {
		try {
			if(!query.equals(prevQuery)) { // Check if current query is the same as the last one, if not, we'll query this current one
				isQueried = false; // reset flag
				prevQuery = query; // store current query to previous query
			}
		}
		catch(NullPointerException e) {
			
		}
			new HttpAsyncTask().execute(query);
		

	}
	
	private class HttpAsyncTask extends AsyncTask<String, Void, Integer> {
		ProgressBar progressBar = (ProgressBar) mView.findViewById(R.id.fragment_search_results_progressbar);
		ListView listView = (ListView) mView.findViewById(R.id.fragment_search_results_listview);
		TextView textView = (TextView) mView.findViewById(R.id.fragment_search_results_empty_textview);
		@Override
		protected void onPreExecute() {
			if(!isQueried) { // If we haven't queried, reset listview
				listView.setAdapter(null);
				list = new ArrayList<GroceryItem>();
			}
			textView.setVisibility(View.GONE);
			progressBar.setVisibility(View.VISIBLE);

		}
		@Override
		protected Integer doInBackground(String... param) {
			if(!isQueried) {
				return Controller.searchItem(param[0],list);
			}
			else return 1;
		}

		@Override
		protected void onPostExecute(Integer result) {
			isQueried = true;
			progressBar.setVisibility(View.GONE);
			if (list.size()==0) {
				textView.setVisibility(View.VISIBLE);
			}
			else {
				mSearchResultsAdapter = new SearchResultsAdapter(getActivity());
				mSearchResultsAdapter.addAll(list);
				SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(mSearchResultsAdapter);
				swingBottomInAnimationAdapter.setInitialDelayMillis(300);
				swingBottomInAnimationAdapter.setAbsListView(listView);
				listView.setAdapter(swingBottomInAnimationAdapter);
			}
		}
	}
}

