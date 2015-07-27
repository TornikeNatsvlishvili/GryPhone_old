package com.Tornike.Gryphone.News;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.os.Parcelable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;

public class NewsPagerAdapter extends PagerAdapter {

	private ArrayList<LinearLayout> views;
	private Context context;

	public NewsPagerAdapter(Context c) {
		context = c;
		views = new ArrayList<LinearLayout>();
	}

	public void addItem(LinearLayout lL) {
		views.add(lL);
		notifyDataSetChanged();
	}

	public void removeItem(int position) {
		views.remove(position);
		notifyDataSetChanged();
	}

	@Override
	public void destroyItem(View view, int arg1, Object object) {
		((ViewPager) view).removeView((LinearLayout) object);
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
	@Override
	public int getItemPosition(Object object) {
	    return POSITION_NONE;
	}
}
