package com.hennysays.grocer.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.json.JSONException;
import org.json.JSONObject;

import com.hennysays.grocer.R;
import com.hennysays.grocer.models.GroceryItem;
import com.hennysays.grocer.util.GrocerContext;


public final class Controller {
	public static Integer reportItem(GroceryItem item) {
		String url = GrocerContext.getContext().getResources().getString(R.string.url);
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);

		httpPost.addHeader(new BasicHeader("Content-Type", "application/json"));
		JSONObject itemJson = new JSONObject();

		try {

			itemJson.put("name",item.getName());
			itemJson.put("price",item.getPrice());
			itemJson.put("quantity",item.getQuantity());
			itemJson.put("units",item.getUnits());
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
			JSONObject resultJson = new JSONObject(sb.toString());
			int response = Integer.parseInt(resultJson.getString("success"));

			if (response == 0)
				return -1;
			else if (response == 1)
				return 1;

			}
		catch(JSONException e) {
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

	public static Integer searchItem(String query) {
//		String url = GrocerContext.getContext().getResources().getString(R.string.url);
		String url = "http://grocer-app.herokuapp.com/groceryItem/search";
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);

		httpPost.addHeader(new BasicHeader("Content-Type", "application/json"));
		JSONObject itemJson = new JSONObject();
		try {
			
			itemJson.put("query",query);
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
			JSONObject resultJson = new JSONObject(sb.toString());
			int response = Integer.parseInt(resultJson.getString("success"));

			if (response == 0)
				return -1;
			else if (response == 1)
				return 1;

			}
		catch(JSONException e) {
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
}
		
		
		
