package com.hennysays.grocer.models;

import android.os.Parcel;
import android.os.Parcelable;


public class GroceryItem implements Parcelable {
	private String name;
	private String price;
	private String quantity;
	private String units;


	public GroceryItem() {
	}
	
	public GroceryItem(String name, String price, String quantity, String units) {
		this.name = name;
		this.price = price;
		this.quantity = quantity;
		this.units = units;
	}

	public GroceryItem(Parcel in) {
		readFromParcel(in);
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setPrice(String price) {
		this.price = price;
	}
	
	public String getPrice() {
		return this.price;
	}
	
	public void setQuantity(String quantity) {
		this.quantity = quantity;
	}
	
	public String getQuantity() {
		return this.quantity;
	}
	
	public void setUnits(String units) {
		this.units = units;
	}
	
	public String getUnits() {
		return this.units;
	}
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(price);
		dest.writeString(quantity);
		dest.writeString(units);
	}

	private void readFromParcel(Parcel in) {
		name = in.readString();
		price = in.readString();
		quantity = in.readString();
		units = in.readString();
	}

	public static final Parcelable.Creator<GroceryItem> CREATOR = new Parcelable.Creator<GroceryItem>() {
		public GroceryItem createFromParcel(Parcel in) {
			return new GroceryItem(in);
		}

		public GroceryItem[] newArray(int size) {
			return new GroceryItem[size];
		}
	};
}


