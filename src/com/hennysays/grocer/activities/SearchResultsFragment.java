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
import com.hennysays.grocer.adapters.SearchResultsAdapter;
import com.hennysays.grocer.controller.Controller;
import com.hennysays.grocer.models.GroceryItem;

public class SearchResultsFragment extends Fragment {
	public static final String TAG = "Search Results Fragment";
	public static final String QUERY = "Query";
	private SearchResultsAdapter mSearchResultsAdapter;
	private View mView;
	ArrayList<GroceryItem> list;
    
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		mView = inflater.inflate(R.layout.fragment_search_results, container,false);
		Bundle args = getArguments();
		String query = args.getString(QUERY);
		runHttpAsyncTask(query);
		return mView;

	}
	
	public void runHttpAsyncTask(String query) {
			new HttpAsyncTask().execute(query);
	}
	
	private class HttpAsyncTask extends AsyncTask<String, Void, Integer> {
		ProgressBar progressBar = (ProgressBar) mView.findViewById(R.id.fragment_search_results_progressbar);
		ListView listView = (ListView) mView.findViewById(R.id.fragment_search_results_listview);
		TextView textView = (TextView) mView.findViewById(R.id.fragment_search_results_empty_textview);
		@Override
		protected void onPreExecute() {
			listView.setAdapter(null);
			list = new ArrayList<GroceryItem>();
			textView.setVisibility(View.GONE);
			progressBar.setVisibility(View.VISIBLE);

		}
		@Override
		protected Integer doInBackground(String... param) {
			return Controller.searchItem(param[0],list);
		}

		@Override
		protected void onPostExecute(Integer result) {
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

