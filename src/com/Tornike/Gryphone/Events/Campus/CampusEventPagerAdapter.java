package com.Tornike.Gryphone.Events.Campus;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.os.Parcelable;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

public class CampusEventPagerAdapter extends PagerAdapter {

	private ArrayList<RelativeLayout> views;
	private ArrayList<CampusEventItem> eventItems;
 	private Context context;

	public CampusEventPagerAdapter(Context c) {
		context = c;
		views = new ArrayList<RelativeLayout>();
		eventItems = new ArrayList<CampusEventItem>();
	}

	public void addItem(RelativeLayout lL, CampusEventItem cI) {
		views.add(lL);
		eventItems.add(cI);
	}
	public CampusEventItem  getEventItem(int position){
		return eventItems.get(position);
	}
	
	public void removeItem(int position) {
		views.remove(position);
	}
	
	public void clear(){
		views.clear();
	}

	@Override
	public void destroyItem(View view, int arg1, Object object) {
		((ViewPager) view).removeView((RelativeLayout) object);
	}

	@Override
	public void finishUpdate(View arg0) {

	}

	@Override
	public int getCount() {
		return views.size();
	}

	@Override
	public Object instantiateItem(View view, int position) {
		View myView = views.get(position);
		((ViewPager) view).addView(myView);
		return myView;
	}

	@Override
	public boolean isViewFromObject(View view, Object object) {
		return view.equals(object);
	}

	@Override
	public void restoreState(Parcelable arg0, ClassLoader arg1) {

	}

	@Override
	public Parcelable saveState() {
		return null;
	}

	@Override
	public void startUpdate(View arg0) {

	}
}
