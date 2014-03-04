package com.hennysays.grocer.models;

import java.math.BigDecimal;
import java.text.DecimalFormat;

import android.os.Parcel;
import android.os.Parcelable;


public class GroceryItem implements Parcelable {
	private String name;
	private BigDecimal price;
	private int quantity;
	private String units;
	private String image;
	private GroceryStore store;
	private String id;
	


	public GroceryItem() {
	}
	
	public GroceryItem(String name, BigDecimal price, int quantity, String units, String image, GroceryStore store, String id) {
		this.name = name;
		this.price = price;
		this.quantity = quantity;
		this.units = units;
		this.image = image;
		this.store = store;
		this.id = id;
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
	
	public void setPrice(BigDecimal price) {
		this.price = price;
	}
	
	public BigDecimal getPrice() {
		return this.price;
	}
	
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}
	
	public int getQuantity() {
		return this.quantity;
	}
	
	public void setUnits(String units) {
		this.units = units;
	}
	
	public String getUnits() {
		return this.units;
	}
	
	public void setImage(String image) {	
		this.image = image;
	}
	
	public String getImage() {
		return this.image;
	}
	
	public void setStore(GroceryStore store) {
		this.store = store;
	}
	
	public GroceryStore getStore() {
		return this.store;
	}
	
	public void setId(String id) {
		this.id = id;
	}
	
	public String getId() {
		return this.id;
	}
	
	public String getPriceString() {
		DecimalFormat df = new DecimalFormat();
		df.setMinimumFractionDigits(2);
		df.setMaximumFractionDigits(2);
		String priceString = df.format(price);
		String result = "$" + priceString;
		if(!units.equals("N/A")) {
			result +="/";
			if(quantity > 1) {
				result += String.valueOf(quantity) + " ";
			}
			result += units;
		}
		else {
			if(quantity > 1) {
				result += "/" + String.valueOf(quantity);
			}
		}
		return result;

	}
	
	public String getUnitPriceString() {
		BigDecimal unitPrice = null;
		if(units.equals("ml") || units.equals("g")) {
			BigDecimal test = new BigDecimal(quantity);
			BigDecimal test2 = new BigDecimal(100);
//			unitPrice = price.divide(new BigDecimal(quantity)).multiply(new BigDecimal(100));
			try {
			unitPrice = price.divide(test,5);
			unitPrice = unitPrice.multiply(test2);
			}
			catch(ArithmeticException e) {
				e.printStackTrace();
			}
		}
		else {
			unitPrice = price.divide(new BigDecimal(quantity));
		}
		DecimalFormat df = new DecimalFormat();
		df.setMinimumFractionDigits(2);
		df.setMaximumFractionDigits(2);
		String priceString = df.format(unitPrice);
		String result = "$" + priceString;
		if(!units.equals("N/A")) {
			result +="/" + units;
		}
		return result;

	}
	
	
	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(name);
		dest.writeString(price.toString());
		dest.writeInt(quantity);
		dest.writeString(units);
		dest.writeString(image);
		dest.writeParcelable(store,flags);
		dest.writeString(id);
		
	}

	private void readFromParcel(Parcel in) {
		name = in.readString();
		price = new BigDecimal(in.readString());
		quantity = in.readInt();
		units = in.readString();
		image = in.readString();
		store = in.readParcelable(getClass().getClassLoader());
		id = in.readString();
		
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


