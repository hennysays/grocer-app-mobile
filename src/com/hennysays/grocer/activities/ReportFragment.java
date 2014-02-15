package com.hennysays.grocer.activities;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Toast;

import com.hennysays.grocer.R;
import com.hennysays.grocer.controller.Controller;
import com.hennysays.grocer.models.GroceryItem;

public class ReportFragment extends Fragment {
	private Button submitButton;
	private EditText name, price, quantity;
	private Spinner units;
	private ProgressBar progressBar;

	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.fragment_report, container,
				false);

		
		
		name = (EditText) view.findViewById(R.id.report_name_edit_text);
		price = (EditText) view.findViewById(R.id.report_price_edit_text);
		quantity = (EditText) view.findViewById(R.id.report_quantity_edit_text);
		units = (Spinner) view.findViewById(R.id.report_units_spinner);
		
		
		
		progressBar = (ProgressBar) view.findViewById(R.id.report_progress_bar);
		submitButton = (Button) view.findViewById(R.id.report_submit_button);
		submitButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				if(validate()) {
					new HttpAsyncTask().execute();
				}
			}
		});

		return view;
	}


	private boolean validate() {
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

			return Controller.reportItem(groceryItem);
		}

		@Override
		protected void onPostExecute(Integer result) {
			progressBar.setVisibility(View.GONE);
			Toast.makeText(getActivity().getBaseContext(), "Enter some data!", Toast.LENGTH_LONG).show();

			// TODO handle report item Response
		}

	}
}
