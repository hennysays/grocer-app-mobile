package com.hennysays.grocer.models;

import java.math.BigDecimal;
import java.util.ArrayList;

import android.os.Parcel;
import android.os.Parcelable;


public class GroceryStore implements Parcelable {
	private String name;
	private String street;
	private String city;
	private String province;
	private String country;
	private BigDecimal lat;
	private BigDecimal lng;
	private ArrayList<GroceryItem> items;
	private String id;
	

	public GroceryStore() {
	}

	public GroceryStore(String name, String street, String city, String province, String country, BigDecimal lat, BigDecimal lng, ArrayList<GroceryItem> items, String id) {
		this.name = name;
		this.street = street;
		this.city = city;
		this.province = province;
		this.country = country;
		this.lat = lat;
		this.lng = lng;
		this.items = items;
		this.id = id;
	}
	
	
	// Constructor with no items initialized, since one does not necessarily need to have any items to have a store
	public GroceryStore(String name, String street, String city, String province, String country, BigDecimal lat, BigDecimal lng, String id) {
		this.name = name;
		this.street = street;
		this.city = city;
		this.province = province;
		this.country = country;
		this.lat = lat;
		this.lng = lng;
		this.id = id;
	}

	public GroceryStore(Parcel in) {
		readFromParcel(in);
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public void setStreet(String street) {
		this.street = street;
	}

	public String getStreet() {
		return this.street;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getCity() {
		return this.city;
	}

	public void setProvince(String province) {
		this.province = province;
	}

	public String getProvince() {
		return this.province;
	}

	public void setCountry(String country) {
		this.country = country;
	}

	public String getCountry() {
		return this.country;
	}

	public void setLatitude(BigDecimal lat) {
		this.lat = lat;
	}

	public BigDecimal getLatitude() {
		return this.lat;
	}

	public void setLongitude(BigDecimal lng) {
		this.lng = lng;
	}

	public BigDecimal getLongitude() {
		return this.lng;
	}

	public void addToItems(GroceryItem item) {
		this.items.add(item);
	}
	
	public ArrayList<GroceryItem> getAllItems() {
		return this.items;
	}
	
	
	public void setId(String id) {
		this.id = id;
	}

	public String getId() {
		return this.id;
	}

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(street);
		dest.writeString(city);
		dest.writeString(province);
		dest.writeString(country);
		dest.writeString(lat.toString());
		dest.writeString(lng.toString());
		dest.writeTypedList(items);
		dest.writeString(id);
	}

	private void readFromParcel(Parcel in) {
		name = in.readString();
		street = in.readString();
		city = in.readString();
		province = in.readString();
		country = in.readString();
		lat = new BigDecimal(in.readString());
		lng = new BigDecimal(in.readString());
		in.readTypedList(items,GroceryItem.CREATOR);
		id = in.readString();
	}

	public static final Parcelable.Creator<GroceryStore> CREATOR = new Parcelable.Creator<GroceryStore>() {
		public GroceryStore createFromParcel(Parcel in) {
			return new GroceryStore(in);
		}

		public GroceryStore[] newArray(int size) {
			return new GroceryStore[size];
		}
	};
}
