package com.hennysays.grocer.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.hennysays.grocer.R;
import com.hennysays.grocer.models.GroceryItem;
import com.hennysays.grocer.util.GrocerContext;

public final class Controller {
	public static Integer reportItem(GroceryItem item) {
		String url = GrocerContext.getContext().getResources()
				.getString(R.string.url);
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);

		httpPost.addHeader(new BasicHeader("Content-Type", "application/json"));
		JSONObject itemJson = new JSONObject();

		try {			
			
			itemJson.put("name", item.getName());
			itemJson.put("price", item.getPrice());
			itemJson.put("quantity", item.getQuantity());
			itemJson.put("units", item.getUnits());
			itemJson.put("image",item.getImage());
			
			String json = itemJson.toString();
			StringEntity se = new StringEntity(json);
			httpPost.setEntity(se);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			// RESPONSE
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					httpResponse.getEntity().getContent(), "UTF-8"));
			String str;
			StringBuilder sb = new StringBuilder();
			while ((str = reader.readLine()) != null) {
				sb.append(str);
			}
			JSONObject resultJson = new JSONObject(sb.toString());
			int response = Integer.parseInt(resultJson.getString("success"));

			if (response == 0)
				return -1;
			else if (response == 1)
				return 1;

		} catch (JSONException e) {
			// TODO Auto-generated catch block
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		return 1;
	}

	public static Integer searchItem(String query, ArrayList<GroceryItem> list) {
		// String url = GrocerContext.getContext().getResources().getString(R.string.url);
		String url = "http://grocer-app.herokuapp.com/groceryItem/search";
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);

		httpPost.addHeader(new BasicHeader("Content-Type", "application/json"));
		JSONObject itemJson = new JSONObject();
		try {

			itemJson.put("query", query);
			String json = itemJson.toString();
			StringEntity se = new StringEntity(json);
			httpPost.setEntity(se);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					httpResponse.getEntity().getContent(), "UTF-8"));
			String str;
			StringBuilder sb = new StringBuilder();
			while ((str = reader.readLine()) != null) {
				sb.append(str);
			}

			JSONTokener tokener = new JSONTokener(sb.toString());
			JSONArray finalResult = new JSONArray(tokener);

			for (int i = 0; i < finalResult.length(); i++) {
				JSONObject jobj = finalResult.getJSONObject(i);
				GroceryItem item = new GroceryItem();
				item.setName(jobj.getString("name"));
				item.setPrice(jobj.getString("price"));
				item.setQuantity(jobj.getString("quantity"));
				item.setUnits(jobj.getString("units"));
				item.setId(jobj.getString("_id"));
				item.setImage(jobj.getString("image")); 				// Since Base64 String is too large for bundle to pass through intent, we'll read image later
				list.add(item);
			}

			return 1;
		}
		// int response = Integer.parseInt(resultJson.getString("success"));
		catch (JSONException e) {
			// TODO Auto-generated catch block
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return 1;
	}
	
	
	public static GroceryItem searchItemById(String id) {
		GroceryItem item = new GroceryItem();
		// String url = GrocerContext.getContext().getResources().getString(R.string.url);
		String url = "http://grocer-app.herokuapp.com/groceryItem/searchById";
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);

		httpPost.addHeader(new BasicHeader("Content-Type", "application/json"));
		JSONObject itemJson = new JSONObject();
		try {

			itemJson.put("query", id);
			String json = itemJson.toString();
			StringEntity se = new StringEntity(json);
			httpPost.setEntity(se);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), "UTF-8"));
			String str;
			StringBuilder sb = new StringBuilder();
			while ((str = reader.readLine()) != null) {
				sb.append(str);
			}
			JSONObject jobj = new JSONObject(sb.toString());
			item.setName(jobj.getString("name"));
			item.setPrice(jobj.getString("price"));
			item.setQuantity(jobj.getString("quantity"));
			item.setUnits(jobj.getString("units"));
			item.setId(jobj.getString("_id"));
			item.setImage(jobj.getString("image"));
		}
		// int response = Integer.parseInt(resultJson.getString("success"));
		catch (JSONException e) {
			// TODO Auto-generated catch block
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClientProtocolException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return item;
	}
	
}
