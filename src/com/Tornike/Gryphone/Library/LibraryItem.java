package com.Tornike.Gryphone.Library;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.text.style.RelativeSizeSpan;
import android.text.style.StyleSpan;
import android.text.style.TextAppearanceSpan;

public class LibraryItem {
	private String title, author, date, location, callNum, status, link,
			numItems, publisher, description, subjects, series;

	LibraryItem() {
		link = "";
		title = "";
		author = "";
		date = "";
		location = "";
		callNum = "";
		status = "";
		numItems = "";
		publisher = "";
		description = "";
		subjects = "";
		series = "";
	}

	public void setTitle(String t) {
		title = t;
	}

	public String getTitle() {
		return title;
	}

	public void setAuthor(String t) {
		author = t;
	}

	public String getAuthor() {
		return author;
	}

	public void setDate(String t) {
		date = t;
	}

	public String getDate() {
		return date;
	}

	public void setLocation(String t) {
		location = t;
	}

	public String getLocation() {
		return location;
	}

	public void setCallNum(String t) {
		callNum = t;
	}

	public String getCallNum() {
		return callNum;
	}

	public void setStatus(String t) {
		status = t;
	}

	public String getStatus() {
		return status;
	}

	public void setNumItems(String n) {
		numItems = n;
	}

	public String getNumItems() {
		return numItems;
	}

	public void setLink(String t) {
		link = t;
	}

	public String getLink() {
		return link;
	}

	public void setPublisher(String p) {
		publisher = p;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setDescription(String d) {
		description = d;
	}

	public String getDescription() {
		return description;
	}

	public void setSubjects(String s) {
		subjects = s;
	}

	public String getSubjects() {
		return subjects;
	}

	public void setSeries(String s) {
		series = s;
	}

	public String getSeries() {
		return series;
	}

	public Spannable toStringSpannable() {
		String phrase;

		// check if location is a link
		if (location.contains("HREF") || location.contains("href")) {
			location = "Visit website for access";
		}

		// create phrase
		phrase = "Author: " + author + "\nPublishing Date: " + date
				+ "\nLocation: " + location + "\n" + "Status: " + status
				+ "\n" + "Call Number: " + callNum + "\nNumber of Items: "
				+ numItems;

		Spannable txt = new SpannableString(phrase);

		txt.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
				phrase.indexOf("Author:"), phrase.indexOf("Author:")
						+ "Author:".length(),
				Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		txt.setSpan(
				new StyleSpan(android.graphics.Typeface.BOLD),
				phrase.indexOf("Publishing Date:"),
				phrase.indexOf("Publishing Date:")
						+ "Publishing Date:".length(),
				Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		txt.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
				phrase.indexOf("Location:"), phrase.indexOf("Location:")
						+ "Location:".length(),
				Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		txt.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
				phrase.indexOf("Status:"), phrase.indexOf("Status:")
						+ "Status:".length(),
				Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		txt.setSpan(new StyleSpan(android.graphics.Typeface.BOLD),
				phrase.indexOf("Call Number:"), phrase.indexOf("Call Number:")
						+ "Call Number:".length(),
				Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		txt.setSpan(
				new StyleSpan(android.graphics.Typeface.BOLD),
				phrase.indexOf("Number of Items:"),
				phrase.indexOf("Number of Items:")
						+ "Number of Items:".length(),
				Spannable.SPAN_INCLUSIVE_INCLUSIVE);

		txt.setSpan(new ForegroundColorSpan(Color.rgb(175, 238, 238)),
				phrase.indexOf("Author:"), phrase.indexOf("Author:")
						+ "Author:".length(),
				Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		txt.setSpan(
				new ForegroundColorSpan(Color.rgb(175, 238, 238)),
				phrase.indexOf("Publishing Date:"),
				phrase.indexOf("Publishing Date:")
						+ "Publishing Date:".length(),
				Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		txt.setSpan(new ForegroundColorSpan(Color.rgb(175, 238, 238)),
				phrase.indexOf("Location:"), phrase.indexOf("Location:")
						+ "Location:".length(),
				Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		txt.setSpan(new ForegroundColorSpan(Color.rgb(175, 238, 238)),
				phrase.indexOf("Status:"), phrase.indexOf("Status:")
						+ "Status:".length(),
				Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		txt.setSpan(new ForegroundColorSpan(Color.rgb(175, 238, 238)),
				phrase.indexOf("Call Number:"), phrase.indexOf("Call Number:")
						+ "Call Number:".length(),
				Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		txt.setSpan(
				new ForegroundColorSpan(Color.rgb(175, 238, 238)),
				phrase.indexOf("Number of Items:"),
				phrase.indexOf("Number of Items:")
						+ "Number of Items:".length(),
				Spannable.SPAN_INCLUSIVE_INCLUSIVE);
		return txt;
	}
}
