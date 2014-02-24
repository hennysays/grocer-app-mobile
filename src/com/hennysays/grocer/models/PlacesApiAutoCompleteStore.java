package com.hennysays.grocer.models;

import android.text.SpannableString;

public class PlacesApiAutoCompleteStore {
	private SpannableString itemName;
	private String itemLocation;
	private String reference;

	public PlacesApiAutoCompleteStore() {
	}

	public PlacesApiAutoCompleteStore(SpannableString itemName, String itemLocation, String reference) {
		this.itemName = itemName;
		this.itemLocation = itemLocation;
		this.reference = reference;
	}

	public void setItemName(SpannableString itemName) {
		this.itemName = itemName;
	}

	public SpannableString getItemName() {
		return this.itemName;
	}
	
	public void setItemLocation(String itemLocation) {
		this.itemLocation = itemLocation;
	}

	public String getItemLocation() {
		return this.itemLocation;
	}


	public void setReference(String reference) {
		this.reference = reference;
	}

	public String getReference() {
		return this.reference;
	}	
	
}
