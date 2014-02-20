package com.hennysays.grocer.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.hennysays.grocer.util.GrocerGoogleMapsApi;

public class ReportNewStoreAutoCompleteAdapter extends ArrayAdapter<String> implements Filterable {
	private ArrayList<String> resultList;
	
	public ReportNewStoreAutoCompleteAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}
	
	@Override
	public int getCount() {
		return resultList.size();
	}
	
	@Override
	public String getItem(int index) {
		return resultList.get(index);
	}
	
	public Filter getFilter() {
		Filter filter = new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    // Retrieve the autocomplete results.
                    resultList = GrocerGoogleMapsApi.autocomplete(constraint.toString());

                    // Assign the data to the FilterResults
                    filterResults.values = resultList;
                    filterResults.count = resultList.size();
                }
                return filterResults;
            }

			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    notifyDataSetChanged();
                }
                else {
                    notifyDataSetInvalidated();
                }
            }
		};
        return filter;
	}
}