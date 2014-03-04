package com.hennysays.grocer.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.Window;

import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.UiLifecycleHelper;
import com.hennysays.grocer.R;

public class SplashScreenActivity extends FragmentActivity {

    private static final String USER_SKIPPED_LOGIN_KEY = "user_skipped_login";
    
    private SplashFragment splashFragment;
    private boolean isResumed = false;
    private boolean userSkippedLogin = false;
    private UiLifecycleHelper uiHelper;
    private Session.StatusCallback callback = new Session.StatusCallback() {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            onSessionStateChange(session, state, exception);
        }
    };	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {		
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        getActionBar().hide();
        if (savedInstanceState != null) {
            userSkippedLogin = savedInstanceState.getBoolean(USER_SKIPPED_LOGIN_KEY);
        }
        uiHelper = new UiLifecycleHelper(this, callback);
        uiHelper.onCreate(savedInstanceState);
        
        setContentView(R.layout.activity_splash);
        splashFragment = new SplashFragment();
        getSupportFragmentManager().beginTransaction().add(R.id.container,splashFragment).commit();

        splashFragment.setSkipLoginCallback(new SplashFragment.SkipLoginCallback() {
            @Override
            public void onSkipLoginPressed() {
                userSkippedLogin = true;
                startNextActivity();
            }
        });
	}
		
        @Override
        public void onResume() {
            super.onResume();
            uiHelper.onResume();
            isResumed = true;

            // Call the 'activateApp' method to log an app event for use in analytics and advertising reporting.  Do so in
            // the onResume methods of the primary Activities that an app may be launched into.
//            AppEventsLogger.activateApp(this); // Do this in main activity
        }

        @Override
        public void onPause() {
            super.onPause();
            uiHelper.onPause();
            isResumed = false;
        }

        @Override
        public void onActivityResult(int requestCode, int resultCode, Intent data) {
            super.onActivityResult(requestCode, resultCode, data);
            uiHelper.onActivityResult(requestCode, resultCode, data);
        }

        @Override
        public void onDestroy() {
            super.onDestroy();
            uiHelper.onDestroy();
        }

        @Override
        protected void onSaveInstanceState(Bundle outState) {
            super.onSaveInstanceState(outState);
            uiHelper.onSaveInstanceState(outState);

            outState.putBoolean(USER_SKIPPED_LOGIN_KEY, userSkippedLogin);
        }

        @Override
        protected void onResumeFragments() {
            super.onResumeFragments();
            Session session = Session.getActiveSession();
            if (session != null && session.isOpened()) {
                userSkippedLogin = false;
                startNextActivity();
            }
        }


        private void onSessionStateChange(Session session, SessionState state, Exception exception) {
            if (isResumed) {
                // check for the OPENED state instead of session.isOpened() since for the
                // OPENED_TOKEN_UPDATED state, the selection fragment should already be showing.
                if (state.equals(SessionState.OPENED)) {
                    startNextActivity();
                } else if (state.isClosed()) {
//                	getSupportFragmentManager().beginTransaction().show(splashFragment).commit();
                }
            }
        }	
        
        protected void startNextActivity() {
            Intent intent = new Intent(this, MainActivity.class);
            this.startActivity(intent);
            this.finish();
        }	
	}
		