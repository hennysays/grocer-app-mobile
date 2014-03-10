package com.hennysays.grocer.adapters;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.text.SpannableString;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;

import com.hennysays.grocer.R;
import com.hennysays.grocer.util.GrocerUtilities;

public class SearchItemAutoCompleteAdapter extends ArrayAdapter<SpannableString> implements Filterable {
	private ArrayList<String> allItemsList; 
	private ArrayList<SpannableString> resultList = new ArrayList<SpannableString>();
//	private Object mLock = new Object();

	public SearchItemAutoCompleteAdapter(Context context, int textViewResourceId, ArrayList<String> allItemsList) {
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
				FilterResults filterResults = new FilterResults();
				ArrayList<SpannableString> result = new ArrayList<SpannableString>();
//				synchronized(mLock) { // Lock resources so resultList can't be used concurrently on multiple threads
					
					if (constraint != null && constraint.length()>1) {

						for (int i = 0; i < allItemsList.size(); i++) {
							SpannableString itemName = new SpannableString(allItemsList.get(i));
							if (itemName.toString().toUpperCase().contains(constraint.toString().toUpperCase()))  {
								GrocerUtilities.highlightCharsInSentence(itemName, constraint.toString(), getContext().getResources().getColor(R.color.light_green));
								result.add(itemName);
							}
						}
					}
	                // Assign the data to the FilterResults
                	filterResults.values = result;
                	filterResults.count = result.size();
//				}
                return filterResults;
            }

			@SuppressWarnings("unchecked")
			@Override
			protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null) {
//                	synchronized(mLock) { // not needed anymore since all resources accessed by asynch task perform filtering are local to it. PublishResults is called in Main UI thread
                		resultList.clear();
                		resultList.addAll((ArrayList<SpannableString>) results.values);
//                	}
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
