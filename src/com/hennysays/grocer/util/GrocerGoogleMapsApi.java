package com.hennysays.grocer.util;

import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;
import java.util.ArrayList;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.graphics.Color;
import android.location.Location;
import android.text.SpannableString;
import android.util.Log;

import com.hennysays.grocer.models.GroceryStore;
import com.hennysays.grocer.models.PlacesApiAutoCompleteStore;

public final class GrocerGoogleMapsApi {
	private static final String LOG_TAG = "ExampleApp";

	private static final String PLACES_API_BASE = "https://maps.googleapis.com/maps/api/place";
	
	private static final String TYPE_AUTOCOMPLETE = "/autocomplete";
	private static final String TYPE_DETAILS = "/details";
	private static final String OUT_JSON = "/json";

	private static final String API_KEY = "AIzaSyD7mqycXzj1YcxV-bcT3R0P7spb-EzYI7M";
	

	public static  ArrayList<PlacesApiAutoCompleteStore> autocomplete(String input) {
	    ArrayList<PlacesApiAutoCompleteStore> resultList = null;
		Location location = GrocerLocation.getCurrentLocation();
	    HttpURLConnection conn = null;
	    StringBuilder jsonResults = new StringBuilder();
	    try {
	        StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_AUTOCOMPLETE + OUT_JSON);
	        sb.append("?sensor=false&key=" + API_KEY);
	        sb.append("&location=" + location.getLatitude() + "," + location.getLongitude());
	        sb.append("&radius=10000");
	        sb.append("&types=establishment");
	        sb.append("&components=country:ca");
	        sb.append("&input=" + URLEncoder.encode(input, "utf8"));

	        URL url = new URL(sb.toString());
	        conn = (HttpURLConnection) url.openConnection();
	        InputStreamReader in = new InputStreamReader(conn.getInputStream());

	        // Load the results into a StringBuilder
	        int read;
	        char[] buff = new char[1024];
	        while ((read = in.read(buff)) != -1) {
	            jsonResults.append(buff, 0, read);
	        }
	    } catch (MalformedURLException e) {
	        Log.e(LOG_TAG, "Error processing Places API URL", e);
	        return resultList;
	    } catch (IOException e) {
	        Log.e(LOG_TAG, "Error connecting to Places API", e);
	        return resultList;
	    } finally {
	        if (conn != null) {
	            conn.disconnect();
	        }
	    }

	    try {
	        // Create a JSON object hierarchy from the results
	        JSONObject jsonObj = new JSONObject(jsonResults.toString());
	        JSONArray predsJsonArray = jsonObj.getJSONArray("predictions");

	        // Extract the Place descriptions from the results
	        resultList = new ArrayList<PlacesApiAutoCompleteStore>(predsJsonArray.length());
	        for (int i = 0; i < predsJsonArray.length(); i++) {
	        	String item = predsJsonArray.getJSONObject(i).getString("description");
	        	
	        	String[] itemArray =  item.split(",");
	        	
	        	SpannableString itemName = new SpannableString(itemArray[0]);
				GrocerUtilities.highlightCharsInSentence(itemName, input, Color.MAGENTA);
	        	
	        	String itemLocation = "";
				for (int j = 1;j< itemArray.length-1; j++) {
					itemLocation += itemArray[j].trim() + ", ";
				}
				itemLocation += itemArray[itemArray.length-1];
	        	String reference = predsJsonArray.getJSONObject(i).getString("reference");
	        	PlacesApiAutoCompleteStore store = new PlacesApiAutoCompleteStore(itemName,itemLocation,reference);
	        	resultList.add(store);
	        }
	        
	    } catch (JSONException e) {
	        Log.e(LOG_TAG, "Cannot process JSON results", e);
	    }
	    return resultList;
	}
	
	public static  GroceryStore getPlaceDetails(String reference) {
		GroceryStore groceryStore = null;
	    HttpURLConnection conn = null;
	    StringBuilder jsonResults = new StringBuilder();
	    try {
	        StringBuilder sb = new StringBuilder(PLACES_API_BASE + TYPE_DETAILS + OUT_JSON);
	        sb.append("?key=" + API_KEY);
	        sb.append("&reference=" + URLEncoder.encode(reference, "utf8"));
	        sb.append("&sensor=false");

	        URL url = new URL(sb.toString());
	        conn = (HttpURLConnection) url.openConnection();
	        InputStreamReader in = new InputStreamReader(conn.getInputStream());

	        // Load the results into a StringBuilder
	        int read;
	        char[] buff = new char[1024];
	        while ((read = in.read(buff)) != -1) {
	            jsonResults.append(buff, 0, read);
	        }
	    } catch (MalformedURLException e) {
	        Log.e(LOG_TAG, "Error processing Places API URL", e);
	        return null;
	    } catch (IOException e) {
	        Log.e(LOG_TAG, "Error connecting to Places API", e);
	        return null;
	    } finally {
	        if (conn != null) {
	            conn.disconnect();
	        }
	    }

	    try {
	        // Create a JSON object hierarchy from the results
	        JSONObject jsonObj = new JSONObject(jsonResults.toString());
	        JSONObject resultJsonObject = jsonObj.getJSONObject("result");
	        
	        // Name
	        String name = resultJsonObject.getString("name");
	        
	        // Address
			// parse address
			String address = resultJsonObject.getString("formatted_address");
			String strAddress[] = { "", "", "", ""}; // {street, city, province, country}
			String strArray[] = address.split(",");
			int i=strAddress.length-1;
			for (int j = strArray.length - 1; j >= 0; j--) {
				if(i==0 && strArray.length>strAddress.length) {
					strAddress[0] = strArray[i] + ", " + strAddress[0];
				} else {
					strAddress[i] = strArray[j];
					i--;
				}
			}
			
			String street = strAddress[0];
			String city = strAddress[1];
			String province = strAddress[2];
			String country = strAddress[3];
			
	        // Geolocation
	        BigDecimal lat = new BigDecimal(resultJsonObject.getJSONObject("geometry").getJSONObject("location").getDouble("lat"));
	        BigDecimal lng = new BigDecimal(resultJsonObject.getJSONObject("geometry").getJSONObject("location").getDouble("lng"));
	        
	        groceryStore = new GroceryStore(name,street,city,province,country,lat,lng,null);
    	        
	    } catch (JSONException e) {
	        Log.e(LOG_TAG, "Cannot process JSON results", e);
	    }
	    return groceryStore;
	}
}
