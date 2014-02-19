package com.hennysays.grocer.activities;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.Editable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.hennysays.grocer.R;

public class SearchFragment extends Fragment {

	private String TAG = "SearchFragment";
	private UiLifecycleHelper uiHelper;
	private Button searchButton, shareButton;
	private static final List<String> PERMISSIONS = Arrays
			.asList("publish_actions");
	private static final int REAUTH_ACTIVITY_CODE = 100;
	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	private boolean pendingPublishReauthorization = false;
	
	private View view;
	private AutoCompleteTextView autocompleteSearchBar;

	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		uiHelper = new UiLifecycleHelper(getActivity(), callback);
		uiHelper.onCreate(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		view = inflater.inflate(R.layout.fragment_search, container,
				false);

		autocompleteSearchBar = (AutoCompleteTextView) view.findViewById(R.id.search_view);
		searchButton = (Button) view.findViewById(R.id.search_button);
		searchButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Editable query = autocompleteSearchBar.getText();
				String queryString = query.toString();
				
				Intent intent = new Intent(getActivity(), GoogleCardsActivity.class);
				intent.putExtra("query",queryString);
				getActivity().startActivity(intent);
				
			}
		});

		shareButton = (Button) view.findViewById(R.id.shareButton);
		shareButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				publishStory();
			}

		});

		if (savedInstanceState != null) {
			pendingPublishReauthorization = savedInstanceState.getBoolean(
					PENDING_PUBLISH_KEY, false);
		}

		return view;
	}

	@Override
	public void onResume() {
		super.onResume();
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
			onSessionStateChange(session, session.getState(), null);
		}
		uiHelper.onResume();
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		super.onSaveInstanceState(outState);
		outState.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);
		uiHelper.onSaveInstanceState(outState);
	}

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isOpened()) {
			shareButton.setVisibility(View.VISIBLE);
			boolean test = state.equals(SessionState.OPENED_TOKEN_UPDATED);
			if (pendingPublishReauthorization && test) {
				pendingPublishReauthorization = false;
				publishStory();
			}
		} else if (state.isClosed()) {
			shareButton.setVisibility(View.INVISIBLE);
		}
	}

	private void publishStory() {
		Session session = Session.getActiveSession();
		if (session != null && session.isOpened()) {
			// Check for publish permissions
			List<String> permissions = session.getPermissions();
			if (!isSubsetOf(PERMISSIONS, permissions)) {
				pendingPublishReauthorization = true;
				Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(
						this, PERMISSIONS).setRequestCode(REAUTH_ACTIVITY_CODE);
				session.requestNewPublishPermissions(newPermissionsRequest);
				return;
			}

			Bundle postParams = new Bundle();
			postParams.putString("name", "Facebook SDK for Android");
			postParams.putString("caption", "Testing 1,2,3");
			postParams.putString("description", "Karan, I'm sharing this on news feed and group");
			postParams.putString("link", "https://developers.facebook.com/android");
			postParams.putString("picture", "https://raw.github.com/fbsamples/ios-3.x-howtos/master/Images/iossdk_logo.png");

			Request.Callback callback = new Request.Callback() {
				public void onCompleted(Response response) {
					JSONObject graphResponse = response.getGraphObject().getInnerJSONObject();
					String postId = null;
					try {
						postId = graphResponse.getString("id");
					} catch (JSONException e) {
						Log.i(TAG,"JSON error " + e.getMessage());
					}
					FacebookRequestError error = response.getError();
					if (error != null) {
						Toast.makeText(getActivity().getApplicationContext(),
								error.getErrorMessage(), Toast.LENGTH_SHORT)
								.show();
					} else {
						Toast.makeText(getActivity().getApplicationContext(),
								postId, Toast.LENGTH_LONG).show();
					}
				}
			};

			Request request = new Request(session, "me/feed", postParams, HttpMethod.POST, callback);
			RequestAsyncTask task = new RequestAsyncTask(request);
			task.execute();

			request = new Request(session, "1430874837147383/feed", postParams, HttpMethod.POST, callback);
			task = new RequestAsyncTask(request);
			task.execute();

		}
	}

	private boolean isSubsetOf(Collection<String> subset,
			Collection<String> superset) {
		for (String string : subset) {
			if (!superset.contains(string)) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);

		// Check for an incoming deep link
		Uri targetUri = getActivity().getIntent().getData();
		if (targetUri != null) {
			Log.i(TAG, "Incoming deep link: " + targetUri);
		}
	}  


//	private class HttpAsyncTask extends AsyncTask<Void, Void, Integer> {
//		private ArrayList<GroceryItem> list = new ArrayList<GroceryItem>();
//		
//		@Override
//		protected void onPreExecute() {
//			progressBar.setVisibility(View.VISIBLE);
//		}
//		@Override
//		protected Integer doInBackground(Void... param) {
//			autocompleteSearchBar = (AutoCompleteTextView) view.findViewById(R.id.search_view);
//			Editable query = autocompleteSearchBar.getText();
//			String queryString = query.toString();
//			return Controller.searchItem(queryString,list);
//		}
//
//		@Override
//		protected void onPostExecute(Integer result) {
//			progressBar.setVisibility(View.GONE);
//
//			Intent intent = new Intent(getActivity(), GoogleCardsActivity.class);
//			intent.putParcelableArrayListExtra("foundItems",list);
//			getActivity().startActivity(intent);
//
////			Toast.makeText(getActivity().getBaseContext(), "Search some data!", Toast.LENGTH_LONG).show();			
//
//
//			// TODO handle report item Response
//		}
//
//	}






}
