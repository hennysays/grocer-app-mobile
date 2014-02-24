package com.hennysays.grocer.activities;

import java.math.BigDecimal;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.haarman.listviewanimations.ArrayAdapter;
import com.haarman.listviewanimations.itemmanipulation.OnDismissCallback;
import com.haarman.listviewanimations.itemmanipulation.SwipeDismissAdapter;
import com.haarman.listviewanimations.swinginadapters.prepared.SwingBottomInAnimationAdapter;
import com.hennysays.grocer.R;
import com.hennysays.grocer.controller.Controller;
import com.hennysays.grocer.models.GroceryItem;
import com.hennysays.grocer.models.GroceryStore;
import com.hennysays.grocer.util.GrocerLocation;

public class GoogleCardsActivity extends BaseActivity implements OnDismissCallback {
//	private ProgressBar progressBar;
	private GoogleCardsAdapter mGoogleCardsAdapter;
	private ArrayList<GroceryItem> list = new ArrayList<GroceryItem>();
	private String query;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_googlecards);
//		progressBar = (ProgressBar) view.findViewById(R.id.search_progress_bar);
		Intent i = getIntent();
		query = i.getStringExtra("query");
		
		HttpAsyncTask task = new HttpAsyncTask();
		task.execute();
		
		ListView listView = (ListView) findViewById(R.id.activity_googlecards_listview);
		mGoogleCardsAdapter = new GoogleCardsAdapter(this);
		SwingBottomInAnimationAdapter swingBottomInAnimationAdapter = new SwingBottomInAnimationAdapter(new SwipeDismissAdapter(mGoogleCardsAdapter, this));
		swingBottomInAnimationAdapter.setInitialDelayMillis(300);
		swingBottomInAnimationAdapter.setAbsListView(listView);
		listView.setAdapter(swingBottomInAnimationAdapter);


	}

	private ArrayList<GroceryItem> getItems() {
		ArrayList<GroceryItem> items = new ArrayList<GroceryItem>();
		if(!list.isEmpty())
		for (int i = 0; i < list.size(); i++) {
			items.add(list.get(i));
		}
		return items;
	}

	@Override
	public void onDismiss(AbsListView listView, int[] reverseSortedPositions) {
		for (int position : reverseSortedPositions) {
			mGoogleCardsAdapter.remove(position);
		}
	}

	private static class GoogleCardsAdapter extends ArrayAdapter<GroceryItem> {

		private Context mContext;
//		private LruCache<Integer, Bitmap> mMemoryCache;

		public GoogleCardsAdapter(Context context) {
			mContext = context;

//			final int maxMemory = (int) (Runtime.getRuntime().maxMemory() / 1024);

//			// Use 1/8th of the available memory for this memory cache.
//			final int cacheSize = maxMemory;
//			mMemoryCache = new LruCache<Integer, Bitmap>(cacheSize) {
//				@Override
//				protected int sizeOf(Integer key, Bitmap bitmap) {
//					// The cache size will be measured in kilobytes rather than
//					// number of items.
//					return bitmap.getRowBytes() * bitmap.getHeight() / 1024;
//				}
//			};
		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder viewHolder;
			View view = convertView;
			if (view == null) {
				view = LayoutInflater.from(mContext).inflate(R.layout.activity_googlecards_card, parent, false);
				viewHolder = new ViewHolder();
				viewHolder.textView1 = (TextView) view.findViewById(R.id.activity_googlecards_card_textview1);
				viewHolder.textView2 = (TextView) view.findViewById(R.id.activity_googlecards_card_textview2);
				viewHolder.textView3 = (TextView) view.findViewById(R.id.activity_googlecards_card_textview3);
				viewHolder.imageView = (ImageView) view.findViewById(R.id.activity_googlecards_card_imageview);
				view.setTag(viewHolder);
			} else {
				viewHolder = (ViewHolder) view.getTag();
			}
			
			viewHolder.textView1.setText(getItem(position).getName());
			
			BigDecimal price = getItem(position).getPrice();
			int quantity = getItem(position).getQuantity();
			String units = getItem(position).getUnits();
			
			String cost = "$" + price.toString();
			
			if(!units.equals("N/A")) {
				cost +="/";
				if(quantity > 1) {
					cost += String.valueOf(quantity);
				}
				cost += " " + units;
			}
			viewHolder.textView2.setText(cost);
			
			GroceryStore store = getItem(position).getStore();
			
			
			Location current = GrocerLocation.getCurrentLocation();
			
			float[] results = new float[1];
			Location.distanceBetween(current.getLatitude(),current.getLongitude(),store.getLatitude().doubleValue(),store.getLongitude().doubleValue(),results);
			String storeName = store.getName();
			float dist = results[0]/1000;
			BigDecimal bd = new BigDecimal(Float.toString(dist));
	        bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
	        String distString = bd.toString();
			String locationString = storeName.trim() + " " + distString + "km away";
			viewHolder.textView3.setText(locationString);
			setImageView(viewHolder, position);

			return view;
		}

		private void setImageView(ViewHolder viewHolder, int position) {
//			int imageResId;
						
//			switch (position % 5) {
//			case 0:
//				imageResId = R.drawable.img_nature1;
//				break;
//			case 1:
//				imageResId = R.drawable.img_nature2;
//				break;
//			case 2:
//				imageResId = R.drawable.img_nature3;
//				break;
//			case 3:
//				imageResId = R.drawable.img_nature4;
//				break;
//			default:
//				imageResId = R.drawable.img_nature5;
//			}
//
//			Bitmap bitmap = getBitmapFromMemCache(imageResId);
//			if (bitmap == null) {
				
//				addBitmapToMemoryCache(imageResId, bitmap);
//			}
			
			byte[] decodedString = Base64.decode(getItem(position).getImage(), Base64.DEFAULT);
			Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
			viewHolder.imageView.setImageBitmap(bitmap);
		}

//		private void addBitmapToMemoryCache(int key, Bitmap bitmap) {
//			if (getBitmapFromMemCache(key) == null) {
//				mMemoryCache.put(key, bitmap);
//			}
//		}
//
//		private Bitmap getBitmapFromMemCache(int key) {
//			return mMemoryCache.get(key);
//		}

		private static class ViewHolder {
			TextView textView1;
			TextView textView2;
			TextView textView3;
			ImageView imageView;
		}
	}
	
	
	private class HttpAsyncTask extends AsyncTask<Void, Void, Integer> {
		
		@Override
		protected void onPreExecute() {
//			progressBar.setVisibility(View.VISIBLE);
		}
		@Override
		protected Integer doInBackground(Void... param) {

			Log.d("GROCER",query);
			return Controller.searchItem(query,list);
		}

		@Override
		protected void onPostExecute(Integer result) {
//			progressBar.setVisibility(View.GONE);
//			Toast.makeText(getActivity().getBaseContext(), "Search some data!", Toast.LENGTH_LONG).show();
			mGoogleCardsAdapter.addAll(getItems());


						


			// TODO handle report item Response
		}
		
	}
	
}
