package com.Tornike.Gryphone.Events.Campus;

import java.util.Calendar;
import java.util.Locale;

public class CampusEventItem {
	private String title;
	private String url;
	private String description;
	private int eventDay, eventMonth, eventYear;
	private String eventDayWords, eventMonthWords;
	
	private String[] weekdays = {"Sunday", "Monday", "Tuesday", "Wednesday",
			"Thursday", "Friday", "Saturday" };
	private String[] months = { "January", "February", "March", "April", "May", "June",
			"July", "August", "September", "October", "November", "December" };
	
	public CampusEventItem(){
		title = "";
		url = "";
		description = "";
		eventDay = 0;
		eventMonth = 0;
		eventYear = 0;
	}
	
	public CampusEventItem(String t,String u,String d){
		title = t;
		url = u;
		description = d;
		eventDay = 0;
		eventMonth = 0;
		eventYear = 0;
		eventDayWords="";
		eventMonthWords="";
	}
	
	public CampusEventItem(String t, String u, String d, int dN, int dM, int dY){
		title = t;
		url = u;
		description = d;
		eventDay = dN;
		eventMonth = dM;
		eventYear = dY;
		eventDayWords="";
		eventMonthWords="";
	}
	
	public String getURL(){
		return url;
	}
	public void setURL(String u){
		url = u;
	}
	public void setTitle(String t){
		title = t;
	}
	public String getTitle(){
		return title;
	}
	public void setDescription(String d){
		description = d;
	}
	
	public String getDescription(){
		return description;
	}
	
	public void setEventDate(String d){
		//December 01, 2011
		int space1 = d.indexOf(" ");
		int comma1 = d.indexOf(",",space1);

		String month = d.substring(0,space1);
		String monthday = d.substring(space1+1,comma1);
		String year = d.substring(comma1+2,d.length());
		
		eventYear = Integer.parseInt(year);
		eventDay = Integer.parseInt(monthday);
		
		for(int a = 0; a<months.length;a++){
			if(month.equals(months[a])){
				eventMonthWords = month;
				eventMonth = a;
				break;
			}
		}	
		Calendar cal = Calendar.getInstance();
		cal.set(eventYear, eventMonth, eventDay);
		eventDayWords = weekdays[cal.get(Calendar.DAY_OF_WEEK)-1];
	}
	
	public void setEventDate(int[] dC){
		eventYear  = dC[0];
		eventMonth = dC[1];
		eventDay = dC[2];
		
		eventMonthWords = months[eventMonth];
			
		Calendar cal = Calendar.getInstance();
		cal.set(eventYear, eventMonth, eventDay);
		eventDayWords = weekdays[cal.get(Calendar.DAY_OF_WEEK)-1];
	}
	
	public int getDateYear(){
		return eventYear;
	}
	
	public int getDateMonth(){
		return eventMonth;
	}
	
	public int getDateDay(){
		return eventDay;
	}
	
	public String getDateDayWords(){
		return eventDayWords;
	}
	public String getDateMonthWords(){
		return eventMonthWords;
	}
	public int[] getEventDate(){
		return new int[]{eventYear,eventMonth,eventDay};
	}
	@Override
	public String toString(){
		return "Title: " + title + "\nURL: " + url + "\nDescription: "+ description;
	}
}
