package com.hennysays.grocer.activities;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.hennysays.grocer.R;
import com.hennysays.grocer.adapters.ReportNewStoreAutoCompleteAdapter;
import com.hennysays.grocer.controller.Controller;
import com.hennysays.grocer.models.GroceryItem;
import com.hennysays.grocer.util.GrocerAnimations;
import com.hennysays.grocer.util.ReportImageSpinnerAdapter;

import eu.janmuller.android.simplecropimage.CropImage;

public class ReportFragment extends Fragment {
//	private Button submitButton,addImageButton;
	private EditText name, price, quantity;
//	private TextView locationView;
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
//	private Location location;
	private LinearLayout newStoreLayout;
	private TextView newStoreButton;
	private AutoCompleteTextView newStoreName;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_report, container,
				false);



		newStoreLayout = (LinearLayout) view.findViewById(R.id.report_new_store_info_linearLayout);
		newStoreButton = (TextView) view.findViewById(R.id.report_add_store_button_textView);
		newStoreButton.setOnClickListener(onClickListener);
		newStoreName = (AutoCompleteTextView) view.findViewById(R.id.report_store_autoCompleteTextView);
		newStoreName.setAdapter(new ReportNewStoreAutoCompleteAdapter(getActivity(),R.layout.list_item));
		name = (EditText) view.findViewById(R.id.report_name_editText);
		price = (EditText) view.findViewById(R.id.report_price_editText);
		quantity = (EditText) view.findViewById(R.id.report_quantity_editText);
		units = (Spinner) view.findViewById(R.id.report_units_spinner);
		noImageAdapter = new ReportImageSpinnerAdapter(getActivity(), android.R.layout.simple_spinner_item, getActivity().getResources().getStringArray(R.array.report_no_image_array));
		noImageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		
		withImageAdapter = new ReportImageSpinnerAdapter(getActivity(), android.R.layout.simple_spinner_item, getActivity().getResources().getStringArray(R.array.report_with_image_array));
		withImageAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
//		locationView = (AutoCompleteTextView) view.findViewById(R.id.report_location_autocompletetextview);
		
//		
//		location = GrocerLocation.getCurrentLocation();
//		String text = "lat: " + location.getLatitude() + ", long: " + location.getLongitude();
//		locationView.setText(text);
		
		
		progressBar = (ProgressBar) view.findViewById(R.id.report_progressBar);
		view.findViewById(R.id.report_submit_button).setOnClickListener(onClickListener);
//		submitButton = (Button) view.findViewById(R.id.report_submit_button);
//		submitButton.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				if(validate()) {
//					new HttpAsyncTask().execute();
//				}
//			}
//		});

		reportImage = (ImageView) view.findViewById(R.id.report_image_view);
		reportImage.setOnTouchListener(new OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				reportImageSpinner.performClick();
				return true;
			}
            
		});
		reportImageSpinner = (Spinner) view.findViewById(R.id.report_image_spinner);
		
		
		reportImageSpinner.setAdapter(noImageAdapter);
		reportImageSpinner.setOnItemSelectedListener(onItemSelectedListener);
//		view.findViewById(R.id.report_add_image_button).setOnClickListener(onClickListener);
//		addImageButton.setOnClickListener(new View.OnClickListener() {
//			@Override
//			public void onClick(View v) {
//				// create Intent to take a picture and return control to the calling application
//				Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//
//				fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE); // create a file to save the image
//				intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri); // set the image file name
//
//				// start the image capture Intent
//				startActivityForResult(intent, CAPTURE_IMAGE_ACTIVITY_REQUEST_CODE);
//			}
//		});

		return view;
	}
	
	
    // On click listener for all views
    final private OnClickListener onClickListener = new OnClickListener() {
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
                	}
                	else {
                		animationView.slideDown(getActivity(), newStoreLayout);
                	}
                break;
            }
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

				((TextView)arg1).setText(null); 
			}
			else if(label.equals(withImageOptions[1]) ) {
				reportImage.setImageBitmap(null);
				reportImageSpinner.setAdapter(noImageAdapter);
			}
			
			
			
				switch (arg0.getSelectedItemPosition()) {
				case 0:

					break;
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

	private class HttpAsyncTask extends AsyncTask<Void, Void, Integer> {

		@Override
		protected void onPreExecute() {
			progressBar.setVisibility(View.VISIBLE);
		}
		@Override
		protected Integer doInBackground(Void... params) {
			GroceryItem groceryItem = new GroceryItem();
			groceryItem.setName(name.getText().toString());
			groceryItem.setPrice(price.getText().toString());
			groceryItem.setQuantity(quantity.getText().toString());
			groceryItem.setUnits(units.getSelectedItem().toString());

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
