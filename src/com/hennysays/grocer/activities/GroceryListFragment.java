package com.hennysays.grocer.activities;

import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.CheckedTextView;
import android.widget.ListView;

import com.haarman.listviewanimations.itemmanipulation.AnimateDismissAdapter;
import com.haarman.listviewanimations.itemmanipulation.OnDismissCallback;
import com.hennysays.grocer.R;
import com.hennysays.grocer.adapters.GroceryListAdapter;
import com.hennysays.grocer.models.GroceryItem;

public class GroceryListFragment extends Fragment {
	public static final String TAG = "Grocery List Fragment";
	private GroceryListAdapter mAdapter;
	private ArrayList<GroceryItem> mGroceryList;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_grocery_list, container,false);		
		ListView listView = (ListView) view.findViewById(R.id.activity_animateremoval_listview);
		mAdapter = ((MainActivity) getActivity()).getGroceryListAdapter();
		mGroceryList = ((MainActivity) getActivity()).getGroceryList();
		mAdapter.addAll(mGroceryList);
		final AnimateDismissAdapter<String> animateDismissAdapter = new AnimateDismissAdapter<String>(mAdapter, new MyOnDismissCallback());
		animateDismissAdapter.setAbsListView(listView);
		listView.setAdapter(animateDismissAdapter);

		Button button = (Button) view.findViewById(R.id.activity_animateremoval_button);
		button.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {			
//				mAdapter.sortSelectedPositions();
				List<Integer> selectedPositions = mAdapter.getSelectedPositions();
				animateDismissAdapter.animateDismiss(selectedPositions);
//				for(Integer indx:selectedPositions) {
//					((MainActivity) getActivity()).removeFromGroceryList(indx);
//				}
				mAdapter.clearSelectedPositions();
			}
		});

		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				CheckedTextView tv = ((CheckedTextView) view);
				tv.toggle();
				if (tv.isChecked()) {
					mAdapter.addSelectedPositions(position);
				} else {
					mAdapter.removeSelectedPositions(position);
				}
			}
		});

		return view;
	}

	private class MyOnDismissCallback implements OnDismissCallback {

		@Override
		public void onDismiss(AbsListView listView, int[] reverseSortedPositions) {
			for (int position : reverseSortedPositions) {
				((MainActivity) getActivity()).removeFromGroceryList(position);
			}
		}
	}

//	private class MyListAdapter extends ArrayAdapter<GroceryItem> {
//		
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			CheckedTextView tv = (CheckedTextView) convertView;
//			if (tv == null) {
//				tv = (CheckedTextView) LayoutInflater.from(GroceryListFragment.this.getActivity()).inflate(R.layout.fragment_grocery_list_dismissrow, parent, false);
//			}
//			tv.setText(getItem(position).getName());
//			tv.setChecked(mSelectedPositions.contains(position));
//			return tv;
//		}
//	}

}








//	@Override
//	public View onCreateView(LayoutInflater inflater, ViewGroup container,
//			Bundle savedInstanceState) {
//		View view = inflater.inflate(R.layout.fragment_grocery_list, container,false);
//		mListView = (ListView) view.findViewById(R.id.activity_mylist_listview);
//		mAdapter = createListAdapter(); 
//		setContextualUndoAdapter();
//		return view;
//	}
//	
//	private void setContextualUndoAdapter() {
//		ContextualUndoAdapter adapter = new ContextualUndoAdapter(mAdapter, R.layout.undo_row, R.id.undo_row_undobutton,3000); // with 3 second countdown
//		adapter.setAbsListView(getListView());
//		getListView().setAdapter(adapter);
//		adapter.setDeleteItemCallback(this);
//	}
//	
//	public ListView getListView() {
//		return mListView;
//	}
//
//	protected ArrayAdapter<Integer> createListAdapter() {
//		return new MyListAdapter(getActivity(), getItems());
//	}
//
//	public static ArrayList<Integer> getItems() {
//		ArrayList<Integer> items = new ArrayList<Integer>();
//		for (int i = 0; i < 100; i++) {
//			items.add(i);
//		}
//		return items;
//	}
//
//	private static class MyListAdapter extends ArrayAdapter<Integer> {
//
//		private Context mContext;
//
//		public MyListAdapter(Context context, ArrayList<Integer> items) {
//			super(items);
//			mContext = context;
//		}
//
//		@Override
//		public long getItemId(int position) {
//			return getItem(position).hashCode();
//		}
//
//		@Override
//		public boolean hasStableIds() {
//			return true;
//		}
//
//		@Override
//		public View getView(int position, View convertView, ViewGroup parent) {
//			TextView tv = (TextView) convertView;
//			if (tv == null) {
//				tv = (TextView) LayoutInflater.from(mContext).inflate(R.layout.list_row, parent, false);
//			}
//			tv.setText("This is row number " + getItem(position));
//			return tv;
//		}
//	}
//
//	@Override
//	public void deleteItem(int position) {
//		mAdapter.remove(position);
//		mAdapter.notifyDataSetChanged();
//		
//	}
//}
