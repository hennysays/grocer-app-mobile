package com.hennysays.grocer.adapters;

import java.math.BigDecimal;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Location;
import android.net.Uri;
import android.util.Base64;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.Session;
import com.haarman.listviewanimations.ArrayAdapter;
import com.hennysays.grocer.R;
import com.hennysays.grocer.activities.MainActivity;
import com.hennysays.grocer.models.GroceryItem;
import com.hennysays.grocer.models.GroceryStore;
import com.hennysays.grocer.util.GrocerLocation;

public class SearchResultsAdapter extends ArrayAdapter<GroceryItem> {

	private Context mContext;

	public SearchResultsAdapter(Context context) {
		mContext = context;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHolder viewHolder;
		View view = convertView;

		if (view == null) {
			view = LayoutInflater.from(mContext).inflate(R.layout.fragment_search_results_card, parent, false);
			viewHolder = new ViewHolder();
			viewHolder.textView1 = (TextView) view.findViewById(R.id.item_name);
			viewHolder.textView2 = (TextView) view.findViewById(R.id.item_price);
			viewHolder.textView3 = (TextView) view.findViewById(R.id.store_name);
			viewHolder.textView4 = (TextView) view.findViewById(R.id.store_distance);
			viewHolder.imageView1 = (ImageView) view.findViewById(R.id.item_image);
			viewHolder.imageView2 = (ImageView) view.findViewById(R.id.add_icon);
			viewHolder.imageView3 = (ImageView) view.findViewById(R.id.place_icon);
			viewHolder.imageView4 = (ImageView) view.findViewById(R.id.share_icon);
			view.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) view.getTag();
		}

		String cost = getItem(position).getPriceString();
//		String cost = getItem(position).getUnitPriceString();

		GroceryStore store = getItem(position).getStore();
		Location current = GrocerLocation.getCurrentLocation();

		float[] results = new float[1];
		Location.distanceBetween(current.getLatitude(),current.getLongitude(),store.getLatitude().doubleValue(),store.getLongitude().doubleValue(),results);
		String storeName = store.getName();

		float dist = results[0]/1000;
		BigDecimal bd = new BigDecimal(Float.toString(dist));
		bd = bd.setScale(2, BigDecimal.ROUND_HALF_UP);
		String distString = bd.toString() + " km away";
		String locationString = storeName.trim();


		ViewHolder2 viewHolder2 = new ViewHolder2();
		viewHolder2.item = getItem(position); 
		viewHolder.imageView2.setTag(viewHolder2);
		viewHolder.imageView3.setTag(viewHolder2);
		viewHolder.imageView4.setTag(viewHolder2);


		viewHolder.textView1.setText(getItem(position).getName());
		viewHolder.textView2.setText(cost);
		viewHolder.textView3.setText(locationString);
		viewHolder.textView4.setText(distString);
		setImageView(viewHolder, position);

		viewHolder.imageView2.setOnClickListener(onClickListener);
		viewHolder.imageView3.setOnClickListener(onClickListener);
		viewHolder.imageView4.setOnClickListener(onClickListener);

		return view;
	}

	private OnClickListener onClickListener = new OnClickListener() {
		@Override
		public void onClick(final View v) {
			ViewHolder2 viewHolder2 = (ViewHolder2) v.getTag();
			GroceryItem item = viewHolder2.item;
			switch(v.getId()) {
			case R.id.add_icon:
				Toast.makeText(mContext,"TODO: Feature not implemented yet", Toast.LENGTH_SHORT).show();				
				break;
			case R.id.place_icon:
				//				viewHolder2.googleMapsLocation = "http://maps.google.com/maps?q=loc:" + getItem(position).getStore().getLatitude().toString() + "," + getItem(position).getStore().getLongitude().toString() + " (" + getItem(position).getStore().getName() + ")";
				String googleMapsLocation = "geo:0,0?q=" + item.getStore().getLatitude().toString() + "," + item.getStore().getLongitude().toString() + " (" + item.getStore().getName() + ")";
				Uri googlemaps =  Uri.parse(googleMapsLocation);
				showMap(googlemaps);
				break;
			case R.id.share_icon:
				Session session = Session.getActiveSession();
				if(session!=null && session.isOpened()) {
					((MainActivity) mContext).publishStory(item);
				}
				else {
					Toast.makeText(mContext,"Not Logged In", Toast.LENGTH_SHORT).show();
				}
				break;
			}
		}
	};

	private void setImageView(final ViewHolder viewHolder, int position) {
	

//		if(image==null) {
//			String urlString = "https://s3.amazonaws.com/Grocer/logo.png";
//			new DownloadImageTask().execute(urlString);
			//			InputStream inputStream;
			//			try {
			//				inputStream = mContext.getContentResolver().openInputStream(fileUri);
			//				bitmap = BitmapFactory.decodeStream(inputStream);
			//			} catch (FileNotFoundException e) {
			//				e.printStackTrace();
			//			}
//		}
//		else {
			byte[] decodedString = Base64.decode(getItem(position).getImage(), Base64.DEFAULT);
			Bitmap bitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
			viewHolder.imageView1.setImageBitmap(bitmap);
//		}
	}

	private static class ViewHolder {
		TextView textView1;
		TextView textView2;
		TextView textView3;
		TextView textView4;
		ImageView imageView1;
		ImageView imageView2;
		ImageView imageView3;
		ImageView imageView4;

	}

	private static class ViewHolder2 {
		GroceryItem item;
	}

	private void showMap(Uri geoLocation) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setData(geoLocation);
		if (intent.resolveActivity(mContext.getPackageManager()) != null) {
			mContext.startActivity(intent);
		}
	}


}
