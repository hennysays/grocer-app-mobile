package com.hennysays.grocer.util;

import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;

public final class GrocerUtilities {

	public static void highlightCharsInSentence(SpannableString sentence, String key, int color) {
    	String tempSentence = sentence.toString().toUpperCase();
		String tempKey = key.toUpperCase();

		
		if (tempSentence.contains(tempKey)) {
			int start = tempSentence.indexOf(tempKey);
			int end = tempKey.length() + start;
			sentence.setSpan(new ForegroundColorSpan(color), start, end, 0);
		}
	}
}
