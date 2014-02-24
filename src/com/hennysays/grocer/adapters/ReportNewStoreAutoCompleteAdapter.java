package com.hennysays.grocer.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.hennysays.grocer.R;
import com.hennysays.grocer.models.PlacesApiAutoCompleteStore;
import com.hennysays.grocer.util.GrocerGoogleMapsApi;

public class ReportNewStoreAutoCompleteAdapter extends ArrayAdapter<PlacesApiAutoCompleteStore> implements Filterable {
	private ArrayList<PlacesApiAutoCompleteStore> resultList;
	
	public ReportNewStoreAutoCompleteAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
	}
	
	@Override
	public int getCount() {
		return resultList.size();
	}
	
	@Override
	public PlacesApiAutoCompleteStore getItem(int index) {
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
			
		    @Override
		    public CharSequence convertResultToString(final Object resultValue) {
		    	PlacesApiAutoCompleteStore item = (PlacesApiAutoCompleteStore) resultValue;
		        String result = item.getItemName().toString();
		    	return result;
		    }
		};
        return filter;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		ViewHolder holder;
		if (v==null) {
			v = LayoutInflater.from(getContext()).inflate(R.layout.list_item, parent, false);
			holder = new ViewHolder();
			holder.nameView = (TextView) v.findViewById(R.id.report_autocomplete_store_name_textView);
			holder.locationView = (TextView) v.findViewById(R.id.report_autocomplete_store_location_textView);
			v.setTag(holder);
		}
		else {
			holder = (ViewHolder) v.getTag();
		}
		holder.nameView.setText(getItem(position).getItemName());
		holder.locationView.setText(getItem(position).getItemLocation());
		holder.reference = getItem(position).getReference();
		return v;
	}
	
	public static class ViewHolder {
		TextView nameView;
		TextView locationView;
		String reference;
		
		public String getReference() {
			return this.reference;
		}
	}
}