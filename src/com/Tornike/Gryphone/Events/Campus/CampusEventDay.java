package com.Tornike.Gryphone.Events.Campus;

import java.util.ArrayList;

public class CampusEventDay {
	private ArrayList<CampusEventItem> eventList;
	int dayNumber;
	String dayMonthWords,dayName;
	
	
	public CampusEventDay(){
		eventList = new ArrayList<CampusEventItem>();
		dayNumber = 0;
		dayMonthWords = "";
		dayName="";
	}
	public CampusEventDay(int n,String m){
		eventList = new ArrayList<CampusEventItem>();
		dayNumber = n;
		dayMonthWords = "";
		dayName="";
	}
	
	public CampusEventItem getEventItem(int index){
		return eventList.get(index);
	}
	
	public void addEventItem(String title, String url, String description,int day, int month, int year){
		eventList.add(new CampusEventItem(title, url,description,day,month,year));
		setDayNumber(day);
		setDayMonth(month+"");
		setDayName("IDONO");
	}
	public void addEventItem(CampusEventItem item){
		eventList.add(item);
		setDayNumber(item.getDateDay());
		setDayName(item.getDateDayWords());	
		setDayMonth(item.getDateMonthWords());
	}
	public void setDayNumber(int n){
		dayNumber = n;
	}
	
	public int getDayNumber(){
		return dayNumber;
	}
	public void setDayMonth(String m){
		dayMonthWords = m;
	}
	
	public String getDayMonth(){
		return dayMonthWords;
	}
	
	public void setDayName(String m){
		dayName= m;
	}
	
	public String getDayName(){
		return dayName;
	}
	
	public boolean gotEvents(){
		if(eventList.size()>0){
			return true;
		}else{
			return false;
		}
	}
	
	public int getAmountOfEventItems(){
		return eventList.size();
	}
	@Override
	public String toString(){
		return dayNumber +"";
	}
}
