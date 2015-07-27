package com.Tornike.Gryphone.News;

public class NewsItem {
	private String title;
	private String url;
	private String story;
	private int newsDay, newsMonth, newsYear;
	private String newsDayWords,newsMonthWords;
	
	private String[] weekdays = {"Sunday", "Monday", "Tuesday", "Wednesday",
			"Thursday", "Friday", "Saturday" };
	private String[] weekdaysShort = {"Sun", "Mon", "Tue", "Wed",
			"Thu", "Fri", "Sat" };
	private String[] months = { "January", "February", "March", "April", "May", "June",
			"July", "August", "September", "October", "November", "December" };
	private String[] monthsShort = { "Jan", "Feb", "Mar", "Apr", "May", "Jun",
			"Jul", "Aug", "Sep", "Oct", "Nov", "Dec" };
	
	public NewsItem() {
		title = "";
		url = "";
		story = "";
		newsDay = 0;
		newsMonth = 0;
		newsYear = 0;
		newsDayWords="";
		newsMonthWords="";
	}

	public NewsItem(String t, String u, String s) {
		title = t;
		url = u;
		story = s;
		newsDay = 0;
		newsMonth = 0;
		newsYear = 0;
		newsDayWords="";
		newsMonthWords="";
	}

	public NewsItem(String t, String u, String s, String d) {
		title = t;
		url = u;
		story = s;
		setNewsDate(d);
	}

	public String getURL() {
		return url;
	}

	public void setURL(String u) {
		url = u;
	}

	public void setTitle(String t) {
		title = t;
	}

	public String getTitle() {
		return title;
	}
	
	public void setNewsDate(String d){
		//Mon, 28 Nov 2011 13:01:25 -0500
		int comma = d.indexOf(",");
		int space1 = d.indexOf(" ",comma+3);
		int space2 = d.indexOf(" ",space1+1);
		int space3 = d.indexOf(" ",space2+1);
		
		String weekday = d.substring(0,comma);
		String monthday = d.substring(comma+2,space1);
		String month = d.substring(space1+1,space2);
		String year = d.substring(space2+1,space3);
		
		newsYear = Integer.parseInt(year);
		newsDay = Integer.parseInt(monthday);
		
		for(int a = 0; a<weekdaysShort.length;a++){
			if(weekday.equals(weekdaysShort[a])){
				newsDayWords = weekdays[a];
				break;
			}
		}
		for(int a = 0; a<monthsShort.length;a++){
			if(month.equals(monthsShort[a])){
				newsMonthWords = months[a];
				newsMonth = a;
				break;
			}
		}		
	}
	
	public int getDateYear() {
		return newsYear;
	}

	public int getDateMonth() {
		return newsMonth;
	}

	public int getDateDay() {
		return newsDay;
	}

	public String getDateDayWords() {
		return newsDayWords;
	}
	
	public String getDateMonthWords() {
		return newsMonthWords;
	}
	
	public int[] getNewsDate() {
		return new int[] { newsYear, newsMonth, newsDay };
	}

	public String getStory() {
		return story;
	}

	public void setStory(String s) {
		story = s;
	}

	public boolean equals(NewsItem item) {
		//Log.e("---------------",item.getTitle().equals(title)+" " +item.getStory().equals(story)+" "+item.getURL().equals(url));
		if (item.getTitle().equals(title) && item.getStory().equals(story)
				&& item.getURL().equals(url)) {
			return true;
		} else {
			return false;
		}
	}

	@Override
	public String toString() {
		return "Title: " + title + "\nDate: " + newsDay + "/" + newsMonth + "/"
				+ newsYear + "\nURL: " + url;
	}
}
