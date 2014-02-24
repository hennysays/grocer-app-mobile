package com.hennysays.grocer.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.hennysays.grocer.models.GroceryItem;
import com.hennysays.grocer.util.GrocerUtilities;

public class SearchItemAutoCompleteAdapter extends ArrayAdapter<SpannableString> implements Filterable {
	private ArrayList<GroceryItem> allItemsList; 
	private ArrayList<SpannableString> resultList;
	
	public SearchItemAutoCompleteAdapter(Context context, int textViewResourceId, ArrayList<GroceryItem> allItemsList) {
		super(context, textViewResourceId);
		this.allItemsList = allItemsList;
		
	}
	
	@Override
	public int getCount() {
		return resultList.size();
	}
	
	@Override
	public SpannableString getItem(int index) {
		return resultList.get(index);
	}
	
	public Filter getFilter() {
		Filter filter = new Filter() {
			@Override
			protected FilterResults performFiltering(CharSequence constraint) {
				resultList = new ArrayList<SpannableString>();
                FilterResults filterResults = new FilterResults();
                if (constraint != null) {
                    for (int i = 0; i < allItemsList.size(); i++) {
                        SpannableString itemName = new SpannableString(allItemsList.get(i).getName());
                        if (itemName.toString().toUpperCase().contains(constraint.toString().toUpperCase()))  {
                        	GrocerUtilities.highlightCharsInSentence(itemName, constraint.toString(), Color.MAGENTA);
                            resultList.add(itemName);
                        }
                    }

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
			
//		    @Override
//		    public CharSequence convertResultToString(final Object resultValue) {
//		    	SpannableString item = (SpannableString) resultValue;
//		        String result = item.toString();
//		    	return result;
//		    }
			
		};
        return filter;
	}
}
