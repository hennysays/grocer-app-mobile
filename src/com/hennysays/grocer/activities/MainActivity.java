package com.hennysays.grocer.activities;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AppEventsLogger;
import com.facebook.FacebookRequestError;
import com.facebook.HttpMethod;
import com.facebook.Request;
import com.facebook.RequestAsyncTask;
import com.facebook.Response;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.hennysays.grocer.R;
import com.hennysays.grocer.adapters.GroceryListAdapter;
import com.hennysays.grocer.adapters.SearchItemAutoCompleteAdapter;
import com.hennysays.grocer.controller.Controller;
import com.hennysays.grocer.models.GroceryItem;
import com.hennysays.grocer.util.GrocerLocation;
import com.hennysays.grocer.views.ClearableAutoCompleteTextView;
import com.hennysays.grocer.views.ClearableAutoCompleteTextView.OnClearListener;

public class MainActivity extends ActionBarActivity implements ActionBar.TabListener {
	/*
	 * The {@link android.support.v4.view.PagerAdapter} that will provide
	 * fragments for each of the sections. We use a {@link
	 * android.support.v4.app.FragmentPagerAdapter} derivative, which will keep
	 * every loaded fragment in memory. If this becomes too memory intensive, it
	 * may be best to switch to a {@link
	 * android.support.v4.app.FragmentStatePagerAdapter}.
	 */
	private Context mContext;
	public SectionsPagerAdapter mSectionsPagerAdapter;
	/* The {@link ViewPager} that will host the section contents. */
	private ViewPager mViewPager;
	private final int NUM_TABS = 2;
	private GrocerLocation mLocation;
	private ActionBar mActionBar;

	public ArrayList<String> mList = new ArrayList<String>();
	
	public static final String PREFS_NAME = "MyPrefsFile";
	public static final String GROCERY_LIST = "GroceryList";
	
	private ArrayList<GroceryItem> mGroceryList;
	private GroceryListAdapter mGroceryListAdapter;
	private ClearableAutoCompleteTextView mClearableAutoCompleteSearchBarTextView;
	private SearchItemAutoCompleteAdapter mSearchItemAutoCompleteAdapter;
	private ListView mSearchResultsListView;
	private TextView mSearchTextView;
	private LinearLayout mSearchOptions;
	
	
	private SharedPreferences prefs;
	
	public boolean addToGroceryList(GroceryItem item) {
		if (mGroceryList.contains(item)) {
			return false;
		}
		else {
			mGroceryList.add(item);
			mGroceryListAdapter.add(item);
			prefs = getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
			Editor editor = prefs.edit();
			editor.putString(GROCERY_LIST,new Gson().toJson(mGroceryList));
			editor.commit();
			return true;
		}
	}
	
	public void removeFromGroceryList(int indx) {
		mGroceryList.remove(indx);
		mGroceryListAdapter.remove(indx);
		prefs = getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putString(GROCERY_LIST,new Gson().toJson(mGroceryList));
		editor.commit();
	}
	
	public ArrayList<GroceryItem> getGroceryList() {
		return mGroceryList;
	}
	
	public GroceryListAdapter getGroceryListAdapter() {
		return mGroceryListAdapter;
	}
		
	//FACEBOOK INTEGRATION
	public boolean isFbLoggedIn = false;
	private UiLifecycleHelper uiHelper;
	private static final List<String> PERMISSIONS = Arrays.asList("publish_actions");
	private static final int REAUTH_ACTIVITY_CODE = 100;
	private static final String PENDING_PUBLISH_KEY = "pendingPublishReauthorization";
	private boolean pendingPublishReauthorization = false;
	private Session.StatusCallback callback = new Session.StatusCallback() {
		@Override
		public void call(Session session, SessionState state,
				Exception exception) {
			onSessionStateChange(session, state, exception);
		}
	};

	@SuppressLint("InlinedApi")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		prefs = getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
		String glist = prefs.getString(GROCERY_LIST,"");
		 if(glist.equals("")) {
			 mGroceryList = new ArrayList<GroceryItem>();
		 }
		 else {
			 Type listType = new TypeToken<ArrayList<GroceryItem>>(){}.getType();
			 Gson gson = new Gson();
			 mGroceryList = gson.fromJson(glist,listType);			 
		 }
		
		mContext = this;
		// Set up the action bar.
		mActionBar = getSupportActionBar();
		mActionBar.setDisplayShowTitleEnabled(false);
//		mActionBar.setDisplayHomeAsUpEnabled(false);
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		mGroceryListAdapter = new GroceryListAdapter(this, new ArrayList<Integer>());

		//  FACEBOOK INTEGRATION
		if (savedInstanceState != null) {
			pendingPublishReauthorization = savedInstanceState.getBoolean(PENDING_PUBLISH_KEY, false);
		}
		uiHelper = new UiLifecycleHelper(this, callback);
		uiHelper.onCreate(savedInstanceState);

		setContentView(R.layout.activity_main);
		
		mLocation = new GrocerLocation(this);
		mLocation.startListeningForLocationUpdates();

		// Create the adapter that will return a fragment for each of the three primary sections of the app.
		mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

		// Set up the ViewPager with the sections adapter.
		mViewPager = (ViewPager) findViewById(R.id.container);
		mViewPager.setAdapter(mSectionsPagerAdapter);

		// When swiping between different sections, select the corresponding tab.
		// We can also use ActionBar.Tab#select() to do this if we have a reference to the Tab.
		mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
			@Override
			public void onPageSelected(int position) {
				mActionBar.setSelectedNavigationItem(position);
			}
		});

		// For each of the sections in the app, add a tab to the action bar.
		for (int i = 0; i < mSectionsPagerAdapter.getCount(); i++) {
			// Create a tab with text corresponding to the page title defined by
			// the adapter. Also specify this Activity object, which implements
			// the TabListener interface, as the callback (listener) for when
			// this tab is selected.
			mActionBar.addTab(mActionBar.newTab().setText(mSectionsPagerAdapter.getPageTitle(i)).setTabListener(this));
		}
	}

	@Override
	public boolean onPrepareOptionsMenu (Menu menu) {
		menu.getItem(0).setEnabled(false);
	    if (!isFbLoggedIn)
	        menu.getItem(1).setTitle("Log In");
	    return true;
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
	    // Inflate the menu items for use in the action bar
	    MenuInflater inflater = getMenuInflater();
	    inflater.inflate(R.menu.main_activity_actions, menu);
	    return super.onCreateOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.action_logout:
			if(isFbLoggedIn) {
				if (Session.getActiveSession() != null) {
					Session.getActiveSession().closeAndClearTokenInformation();
				}
				Session.setActiveSession(null);
			}
			Intent intent = new Intent(this, SplashScreenActivity.class);
			this.startActivity(intent);
			this.finish();
			return true;
		
		case R.id.action_settings:
			Toast.makeText(this, "TODO: Not Implemented yet", Toast.LENGTH_SHORT).show();
		default:
			return super.onOptionsItemSelected(item);
		}
	}
	
	@Override
	public void onResume() {
		super.onResume();
		Session session = Session.getActiveSession();
		if (session != null && (session.isOpened() || session.isClosed())) {
			onSessionStateChange(session, session.getState(), null);
		}
		uiHelper.onResume();
		mLocation.startListeningForLocationUpdates();

		// Call the 'activateApp' method to log an app event for use in
		// analytics and advertising reporting. Do so in
		// the onResume methods of the primary Activities that an app may be
		// launched into.
		AppEventsLogger.activateApp(this);
	}

	@Override
	public void onPause() {
		super.onPause();
		uiHelper.onPause();
		mLocation.stopListeningForLocationUpdates();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		uiHelper.onDestroy();
	}

	@Override
	public void onTabSelected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
		// When the given tab is selected, switch to the corresponding page in
		// the ViewPager.
		switch(tab.getPosition()) {
		case 0:
			mActionBar.setDisplayShowCustomEnabled(true);
			break;
		default:
			mActionBar.setDisplayShowCustomEnabled(false);
			break;
		}
		mViewPager.setCurrentItem(tab.getPosition());
	}

	@Override
	public void onTabUnselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}

	@Override
	public void onTabReselected(ActionBar.Tab tab,
			FragmentTransaction fragmentTransaction) {
	}
	
	
	
	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		private Fragment mFragmentAtPos0;
		
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
		}		
		
		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				if(mFragmentAtPos0==null) {
					new SearchHttpAsyncTask().execute();
					mFragmentAtPos0 = new SearchHolderFragment();
				}
				return mFragmentAtPos0;
				
//			case 1:
//				return new ReportFragment();
//				
			case 1:
				return new GroceryListFragment();
			default:
				return null;
			}
		}
		
		@Override
		public int getCount() {
			return NUM_TABS;
		}

		@Override
		public CharSequence getPageTitle(int position) {
			Locale l = Locale.getDefault();
			switch (position) {
			case 0:
				return getString(R.string.search_menu).toUpperCase(l);
//			case 1:
//				return getString(R.string.report_menu).toUpperCase(l);
			case 1:
				return getString(R.string.grocery_list_menu).toUpperCase(l);
			default:
				return null;
			}
		}
	}

	@Override
	public void onSaveInstanceState(Bundle outState) {
		outState.putBoolean(PENDING_PUBLISH_KEY, pendingPublishReauthorization);
		uiHelper.onSaveInstanceState(outState);
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		uiHelper.onActivityResult(requestCode, resultCode, data);
	}

	public void publishStory(GroceryItem item) {		
		Session session = Session.getActiveSession();
		if (session != null && session.isOpened()) {
			// Check for publish permissions
			List<String> permissions = session.getPermissions();
			if (!isSubsetOf(PERMISSIONS, permissions)) {
				pendingPublishReauthorization = true;
				Session.NewPermissionsRequest newPermissionsRequest = new Session.NewPermissionsRequest(this, PERMISSIONS).setRequestCode(REAUTH_ACTIVITY_CODE);
				session.requestNewPublishPermissions(newPermissionsRequest);
				return;
			}

			Bundle postParams = new Bundle();
			String location = item.getStore().getName() + " (" + item.getStore().getCity() + ", " + item.getStore().getProvince() + ")";   
			String description = "I found " + item.getName() + " for " + item.getPriceString() + " at " + location + "!";
			postParams.putString("name", "Grocery Item Found!");
			//		postParams.putString("caption", "Testing");
			postParams.putString("description", description);
//			postParams.putString("link", "https://www.google.com");
			postParams.putString("link", "https://www.facebook.com/grocerappmtl");
			//		postParams.putString("picture", "http://grocer-app.herokuapp.com/images/logo.png");


			Request.Callback callback = new Request.Callback() {
				public void onCompleted(Response response) {
					String postId = null;
					try {
						JSONObject graphResponse = response.getGraphObject().getInnerJSONObject();
						postId = graphResponse.getString("id");
					}
					catch (NullPointerException e) {
						//					Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
					}
					catch (JSONException e) {
						Log.i("TEST","JSON error " + e.getMessage());
					}

					FacebookRequestError error = response.getError();
					if (error != null) {
						Toast.makeText(getApplicationContext(),error.getErrorMessage(), Toast.LENGTH_SHORT).show();
					} else {
						Log.d("Grocer",postId);
						Toast.makeText(getApplicationContext(),"You shared a deal!", Toast.LENGTH_SHORT).show();
					}
				}
			};

			Request request = new Request(session, "me/feed", postParams, HttpMethod.POST, callback);
			RequestAsyncTask task = new RequestAsyncTask(request);
			task.execute();

//			request = new Request(session, "713066668746180/feed", postParams, HttpMethod.POST, callback); // Grocer Page
//			task = new RequestAsyncTask(request);
//			task.execute();
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

	private void onSessionStateChange(Session session, SessionState state,
			Exception exception) {
		if (state.isOpened()) {
			isFbLoggedIn = true;
			// shareButton.setVisibility(View.VISIBLE);
			if (pendingPublishReauthorization && state.equals(SessionState.OPENED_TOKEN_UPDATED)) {
				pendingPublishReauthorization = false;
				//			publishStory();
				Toast.makeText(this, PERMISSIONS + "permissions granted. Please try again.", Toast.LENGTH_SHORT).show();
			}
		} else if (state.isClosed()) {
			isFbLoggedIn = true;
			// shareButton.setVisibility(View.INVISIBLE);
		}
	}

	
	@Override
	public void onBackPressed() {
		// If searchbox is open, onBackPress should handle this first
		if (mClearableAutoCompleteSearchBarTextView != null && mClearableAutoCompleteSearchBarTextView.getVisibility() == View.VISIBLE) {
			toggleSearch(true);
			return;
		}
		
		// If searchbox is closed, pop back nested fragment stack
		if(mViewPager.getCurrentItem()==0) {
			Fragment fragment = mSectionsPagerAdapter.getItem(0);
			if(fragment.getChildFragmentManager().getBackStackEntryCount() > 0) {
				fragment.getChildFragmentManager().popBackStack();
				return;
			}
		}
		
		super.onBackPressed();
	}

	private class SearchHttpAsyncTask extends AsyncTask<Void, Void, Integer> {
		View view;
		@Override
		protected void onPreExecute() {
			LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.actionbar_search, null);
			mClearableAutoCompleteSearchBarTextView = (ClearableAutoCompleteTextView) view.findViewById(R.id.search_autocompletetextview);
			mClearableAutoCompleteSearchBarTextView.setVisibility(View.INVISIBLE);
		}
		
		@Override
		protected Integer doInBackground(Void... param) {
			return Controller.searchItemAutoComplete("", mList);
		}

		@Override
		protected void onPostExecute(Integer result) {			
			mActionBar.setCustomView(view);
			setListViewAdapter(mList);
			ImageView searchIcon = (ImageView) view.findViewById(R.id.search_icon);
			searchIcon.setOnClickListener(new View.OnClickListener() {	
				@Override
				public void onClick(View v) {
					toggleSearch(false);
				}
			});
			
		}
	}
	
	TextWatcher mTextWatcher = new TextWatcher() {
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			if(s.length()>1) {
				mSearchTextView.setVisibility(View.VISIBLE);
				mSearchTextView.setText("Search for \"" + s + "\"");
			}
			else {				
				mSearchTextView.setVisibility(View.GONE);

			}
			mSearchItemAutoCompleteAdapter.getFilter().filter(s);
		}

		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
		}

		@Override
		public void afterTextChanged(Editable s) {

		}
	};
	
	public void setListViewAdapter(ArrayList<String> list) {
		mSearchItemAutoCompleteAdapter = new SearchItemAutoCompleteAdapter(mContext, R.layout.list_item_search, mList);
		mSearchOptions = (LinearLayout) findViewById(R.id.search_options);
		mSearchTextView = (TextView) findViewById(R.id.search_autocompletetextview_results_searchfor);
		mSearchTextView.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mClearableAutoCompleteSearchBarTextView.getWindowToken(), 0);
				String query = mClearableAutoCompleteSearchBarTextView.getText().toString();
				executeNextStage(query);
			}
		});
		mSearchResultsListView = (ListView) findViewById(R.id.search_autocompletetextview_results);
		mSearchResultsListView.setAdapter(mSearchItemAutoCompleteAdapter);
		mSearchResultsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				String query = ((TextView) view).getText().toString();
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mClearableAutoCompleteSearchBarTextView.getWindowToken(), 0);
//				clearableAutoCompleteSearchBarTextView.setText(query);
				executeNextStage(query);
			}
		});

		mClearableAutoCompleteSearchBarTextView.addTextChangedListener(mTextWatcher);
		mClearableAutoCompleteSearchBarTextView.setOnClearListener(new OnClearListener() {	
			@Override
			public void onClear() {
				InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
				imm.hideSoftInputFromWindow(mClearableAutoCompleteSearchBarTextView.getWindowToken(), 0);
				toggleSearch(true);
			}
		});	
		mClearableAutoCompleteSearchBarTextView.setOnKeyListener(new OnKeyListener()
		{
			
			public boolean onKey(View v, int keyCode, KeyEvent event)
			{
				if (event.getAction() == KeyEvent.ACTION_DOWN)
				{
					switch (keyCode)
					{
					case KeyEvent.KEYCODE_ENTER:
						Editable query = mClearableAutoCompleteSearchBarTextView.getText();
						String queryString = query.toString();
						if(queryString.equals("")) {
							break;
						}
						else {
							InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
							imm.hideSoftInputFromWindow(mClearableAutoCompleteSearchBarTextView.getWindowToken(), 0);
							executeNextStage(queryString);
						}
						return true;
					default:
						break;
					}
				}
				return false;
			}
		});
		mClearableAutoCompleteSearchBarTextView.setOnFocusChangeListener(new OnFocusChangeListener() {
			Fragment fragment = mSectionsPagerAdapter.getItem(0);
			FragmentManager fm = fragment.getChildFragmentManager();
			
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				Fragment resultsFragment = fm.findFragmentByTag(SearchResultsFragment.TAG);
				Fragment searchFragment = fm.findFragmentByTag(SearchFragment.TAG);
				
				if(hasFocus) {
					mSearchOptions.setVisibility(View.GONE);
					if(resultsFragment !=null) {
					fm.beginTransaction().hide(resultsFragment).show(searchFragment).addToBackStack(null).commit();
					}
				}
				else {
					mSearchOptions.setVisibility(View.VISIBLE);
				}
				
				
			}
		});
		
		
	}

	public void executeNextStage(String query) {		
		Fragment fragment = mSectionsPagerAdapter.getItem(0);
		switch(mViewPager.getCurrentItem()) {
		case 0:
			FragmentManager fm = fragment.getChildFragmentManager();
			FragmentTransaction ft = fm.beginTransaction();
			
			SearchResultsFragment frag = (SearchResultsFragment) fm.findFragmentByTag(SearchResultsFragment.TAG);
			SearchFragment frag2 = (SearchFragment) fm.findFragmentByTag(SearchFragment.TAG);
			
			 // if no previous results fragment found, create a new one
			toggleSearch(true);
			mClearableAutoCompleteSearchBarTextView.removeTextChangedListener(mTextWatcher);
			mClearableAutoCompleteSearchBarTextView.setText(query);
			mClearableAutoCompleteSearchBarTextView.addTextChangedListener(mTextWatcher);
			if(frag==null) {
				frag = new SearchResultsFragment();
	            Bundle args = new Bundle();
	            args.putString(SearchResultsFragment.QUERY, query);
	            frag.setArguments(args);
				ft.hide(frag2).add(R.id.fragment_search_nested_holder,frag,SearchResultsFragment.TAG).addToBackStack(null).commit();
			}
			else {
				fm.popBackStack();
				ft.hide(frag2).show(frag).commit();
				frag.runHttpAsyncTask(query);
			}

			break;
		}
	}
	
	protected void toggleSearch(boolean reset) {
		ImageView searchIcon = (ImageView) findViewById(R.id.search_icon);
		if (reset) {
			if(!mClearableAutoCompleteSearchBarTextView.getText().toString().equals("")) { // If there still is text in the searchbox, just delete the text
				mClearableAutoCompleteSearchBarTextView.setText("");
				mClearableAutoCompleteSearchBarTextView.clearFocus();
				
			}
			else {
				// hide search box and show search icon
				mClearableAutoCompleteSearchBarTextView.setVisibility(View.GONE);
				searchIcon.setVisibility(View.VISIBLE);
				
				
				// Case when a Results fragment has already been loaded, resetting from empty search box should not only close the searchbox, but also go back one level
				Fragment fragment = mSectionsPagerAdapter.getItem(0);
				FragmentManager fm = fragment.getChildFragmentManager();
				Fragment frag = fm.findFragmentByTag(SearchResultsFragment.TAG);
				if(frag!=null) {
					onBackPressed();
				}
				
				
			}
		} else {
			// hide search icon and show search box
			searchIcon.setVisibility(View.GONE);
			mClearableAutoCompleteSearchBarTextView.setVisibility(View.VISIBLE);
			mClearableAutoCompleteSearchBarTextView.requestFocus();
			// show the keyboard
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(mClearableAutoCompleteSearchBarTextView, InputMethodManager.SHOW_IMPLICIT);
		}
	}
}

