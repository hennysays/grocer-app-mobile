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
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ImageView;
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
	private SectionsPagerAdapter mSectionsPagerAdapter;
	/* The {@link ViewPager} that will host the section contents. */
	private ViewPager mViewPager;
	private final int NUM_TABS = 3;
	private GrocerLocation mLocation;
	private ActionBar mActionBar;

	public static final String PREFS_NAME = "MyPrefsFile";
	public static final String GROCERY_LIST = "GroceryList";
	private ArrayList<GroceryItem> mGroceryList;
	private SharedPreferences prefs;
	
	public boolean addToGroceryList(GroceryItem item) {
		if (mGroceryList.contains(item)) {
			return false;
		}
		else {
			mGroceryList.add(item);
			prefs = getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
			Editor editor = prefs.edit();
			editor.putString(GROCERY_LIST,new Gson().toJson(mGroceryList));
			editor.commit();
			return true;
		}
	}
	
	public void removeFromGroceryList(int indx) {
		mGroceryList.remove(indx);
		prefs = getSharedPreferences(PREFS_NAME,Context.MODE_PRIVATE);
		Editor editor = prefs.edit();
		editor.putString(GROCERY_LIST,new Gson().toJson(mGroceryList));
		editor.commit();
	}
	
	
	
	public ArrayList<GroceryItem> getGroceryList() {
		return mGroceryList;
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
//		if (Build.VERSION.SDK_INT >= 19) {
//			getWindow().addFlags(android.view.WindowManager.LayoutParams.FLAG_TRANSLUCENT_NAVIGATION);
//		}
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
			if (Session.getActiveSession() != null) {
			    Session.getActiveSession().closeAndClearTokenInformation();
			}

			Session.setActiveSession(null);
            Intent intent = new Intent(this, SplashScreenActivity.class);
            this.startActivity(intent);
            this.finish();
			return true;
		
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
	
	public interface SearchPageFragmentListener {
	    void onSwitchToNextFragment();
	}

	/**
	 * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
	 * one of the sections/tabs/pages.
	 */
	public class SectionsPagerAdapter extends FragmentPagerAdapter {
		
		private Fragment mFragmentAtPos0;
		private FragmentManager mFragmentManager;
		private SearchPageListener listener = new SearchPageListener();
		
		public SectionsPagerAdapter(FragmentManager fm) {
			super(fm);
			mFragmentManager = fm;
		}
		
	    public final class SearchPageListener implements SearchPageFragmentListener {
	    	public void onSwitchToNextFragment() {
	    		mFragmentManager.beginTransaction().remove(mFragmentAtPos0).commit();
	    		if (mFragmentAtPos0 instanceof SearchFragment){
	    			mFragmentAtPos0 = SearchResultsFragment.newInstance(listener);
	    		}
	    		else { // Instance of SearchResultsFragment
	    				mFragmentAtPos0 = SearchFragment.newInstance(listener);
	    		}
	    		notifyDataSetChanged();
	    	}
	    }	
		
		@Override
		public Fragment getItem(int position) {
			switch (position) {
			case 0:
				if(mFragmentAtPos0==null) {
					new SearchHttpAsyncTask().execute();
					mFragmentAtPos0 = SearchFragment.newInstance(listener);
				}
				return mFragmentAtPos0;
				
			case 1:
				return new ReportFragment();
				
			case 2:
				return new GroceryListFragment();
			default:
				return null;
			}
		}

		@Override
		public int getItemPosition(Object object) {
			if (object instanceof SearchFragment && mFragmentAtPos0 instanceof SearchResultsFragment)
				return POSITION_NONE;
			if (object instanceof SearchResultsFragment && mFragmentAtPos0 instanceof SearchFragment)
				return POSITION_NONE;
			return POSITION_UNCHANGED;
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
			case 1:
				return getString(R.string.report_menu).toUpperCase(l);
			case 2:
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
	public void onBackPressed() {
		ClearableAutoCompleteTextView searchBox = (ClearableAutoCompleteTextView) this.findViewById(R.id.search_autocompletetextview);
		if (searchBox != null && searchBox.getVisibility() == View.VISIBLE) {
			toggleSearch(true);
			return;
		}
		
		
//		Fragment page = getSupportFragmentManager().findFragmentByTag("android:switcher:" + R.id.container + ":" + mViewPager.getCurrentItem());
		
		if(mViewPager.getCurrentItem()==0 && mSectionsPagerAdapter.getItem(0) instanceof SearchResultsFragment) {
			SearchResultsFragment.listener.onSwitchToNextFragment();
			return;
		}
		
		super.onBackPressed();
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
			// #32a96e, #3ac47f color codes

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

	protected void toggleSearch(boolean reset) {
		ClearableAutoCompleteTextView clearableAutoCompleteSearchBarTextView = (ClearableAutoCompleteTextView) findViewById(R.id.search_autocompletetextview);
		ImageView searchIcon = (ImageView) findViewById(R.id.search_icon);
		if (reset) {
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.hideSoftInputFromWindow(clearableAutoCompleteSearchBarTextView.getWindowToken(), 0);
			// hide search box and show search icon
			clearableAutoCompleteSearchBarTextView.setText("");
			clearableAutoCompleteSearchBarTextView.setVisibility(View.GONE);
			searchIcon.setVisibility(View.VISIBLE);
		} else {
			// hide search icon and show search box
			searchIcon.setVisibility(View.GONE);
			clearableAutoCompleteSearchBarTextView.setVisibility(View.VISIBLE);
			clearableAutoCompleteSearchBarTextView.requestFocus();
			// show the keyboard
			InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
			imm.showSoftInput(clearableAutoCompleteSearchBarTextView, InputMethodManager.SHOW_IMPLICIT);
		}
	}
	
	private class SearchHttpAsyncTask extends AsyncTask<Void, Void, Integer> {
		ArrayList<String> list = new ArrayList<String>();
		View view;
		ClearableAutoCompleteTextView clearableAutoCompleteSearchBarTextView;
		@Override
		protected void onPreExecute() {
			LayoutInflater inflater = (LayoutInflater)getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			view = inflater.inflate(R.layout.actionbar_search, null);
			clearableAutoCompleteSearchBarTextView = (ClearableAutoCompleteTextView) view.findViewById(R.id.search_autocompletetextview);
			clearableAutoCompleteSearchBarTextView.setVisibility(View.INVISIBLE);
			ActionBar actionBar = getSupportActionBar();
		 	actionBar.setCustomView(view);
		}
		
		@Override
		protected Integer doInBackground(Void... param) {
			return Controller.searchItemAutoComplete("", list);
		}

		@Override
		protected void onPostExecute(Integer result) {			
				SearchItemAutoCompleteAdapter searchItemAutoCompleteAdapter = new SearchItemAutoCompleteAdapter(mContext, R.layout.list_item_search, list);
				searchItemAutoCompleteAdapter.setDropDownViewResource(R.layout.list_item_search); // SetDropDownViewResource not necessary for AutoCompleteTextView
//				searchItemAutoCompleteAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

				ImageView home = (ImageView) findViewById(android.R.id.home); 
				ViewGroup.MarginLayoutParams lp = (ViewGroup.MarginLayoutParams) home.getLayoutParams();
				int horizontalOffset = home.getWidth()+lp.rightMargin + clearableAutoCompleteSearchBarTextView.getPaddingLeft();
				clearableAutoCompleteSearchBarTextView.setDropDownHorizontalOffset(-horizontalOffset);
				clearableAutoCompleteSearchBarTextView.setAdapter(searchItemAutoCompleteAdapter);
				clearableAutoCompleteSearchBarTextView.setOnClearListener(new OnClearListener() {	
					@Override
					public void onClear() {
						toggleSearch(true);
					}
				});	
				clearableAutoCompleteSearchBarTextView.setOnKeyListener(new OnKeyListener()
				{
					public boolean onKey(View v, int keyCode, KeyEvent event)
					{
						if (event.getAction() == KeyEvent.ACTION_DOWN)
						{
							switch (keyCode)
							{
							case KeyEvent.KEYCODE_ENTER:
								clearableAutoCompleteSearchBarTextView.dismissDropDown();
								Editable query = clearableAutoCompleteSearchBarTextView.getText();
								String queryString = query.toString();
								if(queryString.equals("")) {
								}
								else {
//									SearchResultsFragment searchResultsFragment = new SearchResultsFragment();
//									Bundle args = new Bundle();
//									args.putString("query", queryString);
//									searchResultsFragment.setArguments(args);
									
//									FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
//									ft.replace(R.id.container,searchResultsFragment);
//									ft.setCustomAnimations(R.anim.slide_in_up, R.anim.slide_out_up);
//									ft.addToBackStack(null);
//									ft.commit();
									InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
									imm.hideSoftInputFromWindow(clearableAutoCompleteSearchBarTextView.getWindowToken(), 0);
									
									Fragment fragment = mSectionsPagerAdapter.getItem(0);
									if(fragment instanceof SearchFragment) {
										SearchFragment.listener.onSwitchToNextFragment();
									}
									else {
										((SearchResultsFragment) fragment).runHttpAsyncTask(queryString);
									}
									

								}
								return true;
							default:
								break;
							}
						}
						return false;
					}
				});
				clearableAutoCompleteSearchBarTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
					@Override
					public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
						String queryString = ((TextView) view).getText().toString();

						InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
						imm.hideSoftInputFromWindow(clearableAutoCompleteSearchBarTextView.getWindowToken(), 0);
						
						Fragment fragment = mSectionsPagerAdapter.getItem(0);
						if(fragment instanceof SearchFragment) {
							SearchFragment.listener.onSwitchToNextFragment();
						}
						else {
							((SearchResultsFragment) fragment).runHttpAsyncTask(queryString);
						}
					}
				});
				ImageView searchIcon = (ImageView) view.findViewById(R.id.search_icon);
				searchIcon.setOnClickListener(new View.OnClickListener() {	
					@Override
					public void onClick(View v) {
						toggleSearch(false);
					}
				});
		}
	}
}

