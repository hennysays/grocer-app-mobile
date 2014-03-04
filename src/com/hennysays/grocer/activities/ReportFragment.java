package com.hennysays.grocer.activities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.hennysays.grocer.R;
import com.hennysays.grocer.adapters.ReportImageSpinnerAdapter;
import com.hennysays.grocer.adapters.ReportNewStoreAutoCompleteAdapter;
import com.hennysays.grocer.controller.Controller;
import com.hennysays.grocer.models.GroceryItem;
import com.hennysays.grocer.models.GroceryStore;
import com.hennysays.grocer.util.GrocerAnimations;
import com.hennysays.grocer.util.GrocerGoogleMapsApi;

import eu.janmuller.android.simplecropimage.CropImage;

public class ReportFragment extends Fragment {
	public static final String TAG = "Report Fragment";
	private EditText name, price, quantity,street,city,province,country;
	
	private GroceryStore groceryStore;
//	private BigDecimal lat,lng;
	private Spinner units;
	private ProgressBar progressBar;
	private ImageView reportImage;
	private Spinner reportImageSpinner;
	private static final int CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE = 100;
	private static final int REQUEST_CODE_CROP_IMAGE = 101;
	public static final int MEDIA_TYPE_IMAGE = 1;
	private Uri fileUri;	
	ReportImageSpinnerAdapter noImageAdapter;
	ReportImageSpinnerAdapter withImageAdapter;
	private LinearLayout newStoreLayout;
	private TextView newStoreButton;
	private AutoCompleteTextView newStoreNameAutoTextView;
	ReportNewStoreAutoCompleteAdapter newStoreAutoCompleteAdapter;
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {		
		View view = inflater.inflate(R.layout.fragment_report, container,
				false);
		newStoreLayout = (LinearLayout) view.findViewById(R.id.report_new_store_info_linearLayout);
		newStoreButton = (TextView) view.findViewById(R.id.report_add_store_button_textView);
		newStoreButton.setOnClickListener(onClickListener);
		
		newStoreButton.setClickable(false);
		
		newStoreNameAutoTextView = (AutoCompleteTextView) view.findViewById(R.id.report_store_autoCompleteTextView);
		newStoreAutoCompleteAdapter = new ReportNewStoreAutoCompleteAdapter(getActivity(),R.id.list_item);
		
		
		newStoreNameAutoTextView.setHint("Enter a new store");
		newStoreNameAutoTextView.setAdapter(newStoreAutoCompleteAdapter);
		newStoreNameAutoTextView.setOnItemClickListener(onItemClickListener);
		
		
		// FOR KARAN - UNCOMMENT LATER
//		newStoreNameAutoTextView.setAdapter(null);
		
		
		street = (EditText) view.findViewById(R.id.report_new_store_street_editText);
		city = (EditText) view.findViewById(R.id.report_new_store_city_editText);
		province = (EditText) view.findViewById(R.id.report_new_store_province_editText);
		country = (EditText) view.findViewById(R.id.report_new_store_country_editText);
		
		name = (EditText) view.findViewById(R.id.report_name_editText);
		price = (EditText) view.findViewById(R.id.report_price_editText);
		quantity = (EditText) view.findViewById(R.id.report_quantity_editText);
		units = (Spinner) view.findViewById(R.id.report_units_spinner);
		
		noImageAdapter = new ReportImageSpinnerAdapter(getActivity(), android.R.layout.simple_spinner_item, getActivity().getResources().getStringArray(R.array.report_no_image_array));
		noImageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		withImageAdapter = new ReportImageSpinnerAdapter(getActivity(), android.R.layout.simple_spinner_item, getActivity().getResources().getStringArray(R.array.report_with_image_array));
		withImageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		progressBar = (ProgressBar) view.findViewById(R.id.report_progressBar);
		view.findViewById(R.id.report_submit_button).setOnClickListener(onClickListener);
		
		reportImageSpinner = (Spinner) view.findViewById(R.id.report_image_spinner);
		reportImageSpinner.setAdapter(noImageAdapter);
		reportImageSpinner.setOnItemSelectedListener(onItemSelectedListener);
		fileUri = Uri.parse("android.resource://com.hennysays.grocer/drawable/img_nature1");
		reportImage = (ImageView) view.findViewById(R.id.report_image_view);		
		reportImage.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				reportImageSpinner.performClick();
				return true;
			}
            
		});

		return view;
	}
	
	
    // On click listener for all views
    final private OnClickListener onClickListener = new OnClickListener() {
    	@Override
        public void onClick(final View v) {
            switch(v.getId()) {
                case R.id.report_submit_button:
                	if(validate()) {
                		new HttpAsyncTask().execute();
                	}                
                break;
                
                case R.id.report_add_store_button_textView:
                	GrocerAnimations animationView = new GrocerAnimations(newStoreLayout);
                	if(newStoreLayout.isShown()){
                		animationView.slideUp(getActivity(), newStoreLayout);
                		newStoreNameAutoTextView.setAdapter(null);
                		
                	}
                	else {
                		animationView.slideDown(getActivity(), newStoreLayout);
                		newStoreNameAutoTextView.setHint("Enter a new store");
                		newStoreNameAutoTextView.setAdapter(newStoreAutoCompleteAdapter);
                		newStoreNameAutoTextView.setOnItemClickListener(onItemClickListener);
                		
                	}
                break;
            }
        }
    };
	
    
    final private OnItemClickListener onItemClickListener = new OnItemClickListener() {
    	@Override
    	public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
    			long arg3) {
    		ReportNewStoreAutoCompleteAdapter.ViewHolder holder = (ReportNewStoreAutoCompleteAdapter.ViewHolder) arg1.getTag();
    		String reference = holder.getReference();
    		new GooglePlacesApiHttpAsyncTask().execute(reference);
    	}
    	
    };
    
    
    final private OnItemSelectedListener onItemSelectedListener = new OnItemSelectedListener() {
		@Override
		public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
				long arg3) {
				
			final String[] noImageOptions = getActivity().getResources().getStringArray(R.array.report_no_image_array);
				final String[] withImageOptions = getActivity().getResources().getStringArray(R.array.report_with_image_array);
				String label = arg0.getSelectedItem().toString();
				
				if(label.equals(noImageOptions[1]) || label.equals(withImageOptions[2])){
					// create Intent to take a picture and return control to the calling application
					Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

					fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
					intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name

					// start the image capture Intent
					startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
					reportImageSpinner.setSelection(0);
				}
				else if(label.equals(withImageOptions[1]) ) {
					reportImage.setImageBitmap(null);
					reportImageSpinner.setAdapter(noImageAdapter);
					reportImageSpinner.setSelection(0);
				} else if(label.equals(noImageOptions[2]) || label.equals(withImageOptions[3])) {
					Toast.makeText(getActivity(),"TODO: Feature not implemented yet", Toast.LENGTH_SHORT).show();
					reportImageSpinner.setSelection(0);
				}

		}
		@Override
		public void onNothingSelected(AdapterView<?> arg0) {
			// TODO Auto-generated method stub
		}
    };  	

    
	/** Create a file Uri for saving an image or video */
    private static Uri getOutputMediaFileUri(int type){
		return Uri.fromFile(getOutputMediaFile(type));
	}

	/** Create a File for saving an image or video */
	private static File getOutputMediaFile(int type){
		// To be safe, you should check that the SDCard is mounted
		// using Environment.getExternalStorageState() before doing this.

		File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), "Grocer");
		// This location works best if you want the created images to be shared
		// between applications and persist after your app has been uninstalled.

		// Create the storage directory if it does not exist
		if (! mediaStorageDir.exists()){
			if (! mediaStorageDir.mkdirs()){
				Log.d("GrocerApp", "failed to create directory");
				return null;
			}
		}

		// Create a media file name
		String timeStamp = new SimpleDateFormat("yyyyMMddHHmmss",Locale.US).format(new Date());
		File mediaFile;
		if (type == MEDIA_TYPE_IMAGE){
			mediaFile = new File(mediaStorageDir.getPath() + File.separator + "IMG_"+ timeStamp + ".jpg");
		}
		else {
			return null;
		}
		return mediaFile;
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch(requestCode) {
		case CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE:
			if (resultCode == Activity.RESULT_OK) {
				runCropImage();
			} else if (resultCode == Activity.RESULT_CANCELED) {
				// User cancelled the image capture
			} else {
				// Image capture failed, advise user
			}
			break;
		case REQUEST_CODE_CROP_IMAGE:
			InputStream inputStream;
			try {
				inputStream = getActivity().getContentResolver().openInputStream(fileUri);
				Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
				reportImage.setImageBitmap(bitmap);				
				reportImageSpinner.setAdapter(withImageAdapter);

				
				
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);		
	}

	private void runCropImage() {

		// create explicit intent
		Intent intent = new Intent(getActivity(), CropImage.class);

		// tell CropImage activity to look for image to crop
		intent.putExtra(CropImage.IMAGE_PATH, fileUri.getPath());

		// allow CropImage activity to rescale image
		intent.putExtra(CropImage.SCALE, true);
		// if the aspect ratio is fixed to ratio 3/2
		intent.putExtra(CropImage.ASPECT_X, 1);
		intent.putExtra(CropImage.ASPECT_Y, 1);

		// start activity CropImage with certain request code and listen
		// for result
		startActivityForResult(intent, REQUEST_CODE_CROP_IMAGE);
	}

	boolean validate() {
		if(name.getText().toString().trim().equals(""))
			return false;
		if(price.getText().toString().trim().equals(""))
			return false;
		if(quantity.getText().toString().trim().equals(""))
			return false;
		return true;    
	}

	
	private class GooglePlacesApiHttpAsyncTask extends AsyncTask<String,Void,Integer> {

		@Override
		protected Integer doInBackground(String... references) {
			groceryStore = GrocerGoogleMapsApi.getPlaceDetails(references[0]);
			
			
//			Log.d("TEST",groceryStore.getName());
//			GroceryStore groceryStore = GrocerGoogleMapsApi.getPlaceDetails(references[0]);
			return null;
		}
		
		@Override
		protected void onPostExecute(Integer result) {
			if(newStoreLayout.isShown()) {
				street.setText(groceryStore.getStreet());
				city.setText(groceryStore.getCity());
				province.setText(groceryStore.getProvince());
				country.setText(groceryStore.getCountry());
//				lat = groceryStore.getLatitude();
//				lng = groceryStore.getLongitude();
			}
		}
		
	}
	
	private class HttpAsyncTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected void onPreExecute() {
			progressBar.setVisibility(View.VISIBLE);
		}
		@Override
		protected Integer doInBackground(Void... params) {
			GroceryItem groceryItem = new GroceryItem();
			groceryItem.setName(name.getText().toString());
			groceryItem.setPrice(new BigDecimal(price.getText().toString()));
			groceryItem.setQuantity(Integer.parseInt(quantity.getText().toString()));
			groceryItem.setUnits(units.getSelectedItem().toString());
			
			groceryItem.setStore(groceryStore);
			
			
			
			// Encode image to string-based representation
			InputStream is;
			Bitmap bitmap = null;
			try {
				is = getActivity().getContentResolver().openInputStream(fileUri);
				bitmap = BitmapFactory.decodeStream(is);
				is.close();
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bitmap.compress(CompressFormat.JPEG, 100, baos);
			byte[] data = baos.toByteArray();
			String encodedImage = Base64.encodeToString(data, Base64.DEFAULT);

			groceryItem.setImage(encodedImage);

			return Controller.reportItem(groceryItem);
		}

		@Override
		protected void onPostExecute(Integer result) {
			progressBar.setVisibility(View.GONE);
			// TODO handle report item Response
		}

	}
}
