package com.hennysays.grocer.controller;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicHeader;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.util.Base64;

import com.hennysays.grocer.R;
import com.hennysays.grocer.models.GroceryItem;
import com.hennysays.grocer.models.GroceryStore;
import com.hennysays.grocer.util.GrocerContext;

public final class Controller {
	public static Integer reportItem(GroceryItem item) {
		String url = GrocerContext.getContext().getResources()
				.getString(R.string.url);
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);

		httpPost.addHeader(new BasicHeader("Content-Type", "application/json;charset=UTF-8"));

		try {			
			JSONObject itemJson = new JSONObject();	
			itemJson.put("name", item.getName());
			itemJson.put("price", item.getPrice());
			itemJson.put("quantity", item.getQuantity());
			itemJson.put("units", item.getUnits());
			itemJson.put("image",item.getImage());
			
			GroceryStore store = item.getStore();
			JSONObject storeJson = new JSONObject();
			storeJson.put("name", store.getName());
			storeJson.put("street", store.getStreet());
			storeJson.put("city", store.getCity());
			storeJson.put("province", store.getProvince());
			storeJson.put("country", store.getCountry());
			storeJson.put("lat",store.getLatitude().doubleValue());
			storeJson.put("lng",store.getLongitude().doubleValue());
			itemJson.put("store",storeJson);	
			
			String json = itemJson.toString();
			StringEntity se = new StringEntity(json,HTTP.UTF_8);
			httpPost.setEntity(se);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			// RESPONSE
			BufferedReader reader = new BufferedReader(new InputStreamReader(
					httpResponse.getEntity().getContent(), HTTP.UTF_8));
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

		httpPost.addHeader(new BasicHeader("Content-Type", "application/json;charset=UTF-8"));
		JSONObject itemJson = new JSONObject();
		try {

			itemJson.put("query", query);
			String json = itemJson.toString();
			StringEntity se = new StringEntity(json,HTTP.UTF_8);
			httpPost.setEntity(se);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					httpResponse.getEntity().getContent(), HTTP.UTF_8));
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
				try {
				item.setPrice(new BigDecimal(jobj.getDouble("price")));
				}
				catch(JSONException e) {
					item.setPrice(new BigDecimal(0));
				}
				try {
					item.setQuantity(jobj.getInt("quantity"));
				}
				catch(JSONException e) {
					item.setQuantity(0);
				}
				try {
					item.setUnits(jobj.getString("units"));
				}
				catch(JSONException e) {
					item.setUnits(null);
				}
				try {
					item.setImage(jobj.getString("image"));
				} catch(JSONException e) {					
					item.setImage(loadImageFromNetwork("https://s3.amazonaws.com/Grocer/logo.png"));
				}
				
				item.setId(jobj.getString("_id"));
				
				JSONObject jobjStore = jobj.getJSONObject("store");
				GroceryStore store = new GroceryStore();
				store.setName(jobjStore.getString("name"));
				store.setStreet(jobjStore.getString("street"));
				store.setCity(jobjStore.getString("city"));
				store.setProvince(jobjStore.getString("province"));
				store.setCountry(jobjStore.getString("country"));
				store.setLatitude(new BigDecimal(jobjStore.getDouble("lat")));
				store.setLongitude(new BigDecimal(jobjStore.getDouble("lng")));
				store.setId(jobjStore.getString("_id"));
				
				item.setStore(store);
				
				
				
				
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

		httpPost.addHeader(new BasicHeader("Content-Type", "application/json;charset=UTF-8"));
		JSONObject itemJson = new JSONObject();
		try {

			itemJson.put("query", id);
			String json = itemJson.toString();
			StringEntity se = new StringEntity(json);
			httpPost.setEntity(se);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			BufferedReader reader = new BufferedReader(new InputStreamReader(httpResponse.getEntity().getContent(), HTTP.UTF_8));
			String str;
			StringBuilder sb = new StringBuilder();
			while ((str = reader.readLine()) != null) {
				sb.append(str);
			}
			JSONObject jobj = new JSONObject(sb.toString());
			item.setName(jobj.getString("name"));
			item.setPrice(new BigDecimal(jobj.getDouble("price")));
			item.setQuantity(jobj.getInt("quantity"));
			item.setUnits(jobj.getString("units"));
			item.setImage(jobj.getString("image"));
			item.setId(jobj.getString("_id"));
			
			JSONObject jobjStore = jobj.getJSONObject("store");
			GroceryStore store = new GroceryStore();
			store.setName(jobjStore.getString("name"));
			store.setStreet(jobjStore.getString("street"));
			store.setCity(jobjStore.getString("city"));
			store.setProvince(jobjStore.getString("province"));
			store.setCountry(jobjStore.getString("country"));
			store.setLatitude(new BigDecimal(jobjStore.getDouble("lat")));
			store.setLongitude(new BigDecimal(jobjStore.getDouble("lng")));
			store.setId(jobjStore.getString("_id"));
			
			item.setStore(store);
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
	
	public static Integer searchItemAutoComplete(String query, ArrayList<String> list) {
		// String url = GrocerContext.getContext().getResources().getString(R.string.url);
		String url = "http://grocer-app.herokuapp.com/groceryItem/searchUnique";
		HttpClient httpClient = new DefaultHttpClient();
		HttpPost httpPost = new HttpPost(url);

		httpPost.addHeader(new BasicHeader("Content-Type", "application/json;charset=UTF-8"));
		JSONObject itemJson = new JSONObject();
		try {

			itemJson.put("query", query);
			String json = itemJson.toString();
			StringEntity se = new StringEntity(json);
			httpPost.setEntity(se);
			HttpResponse httpResponse = httpClient.execute(httpPost);

			BufferedReader reader = new BufferedReader(new InputStreamReader(
					httpResponse.getEntity().getContent(), HTTP.UTF_8));
			String str;
			StringBuilder sb = new StringBuilder();
			while ((str = reader.readLine()) != null) {
				sb.append(str);
			}

			JSONTokener tokener = new JSONTokener(sb.toString());
			JSONArray finalResult = new JSONArray(tokener);

			for (int i = 0; i < finalResult.length(); i++) {
				list.add(finalResult.get(i).toString());
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
		
	private static String loadImageFromNetwork(final String s) {
		String bmpString = null;
		try {
			URL url = new URL(s);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.connect();
			InputStream is = conn.getInputStream();
			Bitmap bmp = BitmapFactory.decodeStream(is);
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			bmp.compress(CompressFormat.JPEG, 100, baos);
			byte[] data = baos.toByteArray();
			bmpString = Base64.encodeToString(data, Base64.DEFAULT);
			
			
		} catch (Exception e) {
			System.out.println("getImage failure:" + e);
			e.printStackTrace();
		}
		return bmpString;
	}

	
	
	
	
	
}
