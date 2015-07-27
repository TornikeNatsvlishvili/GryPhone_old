package com.Tornike.Gryphone.Events.Campus;

import com.Tornike.Gryphone.R;
import com.Tornike.Gryphone.Events.*;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.TranslateAnimation;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class CampusEventAdapter extends ArrayAdapter<CampusEventDay> {

	private ArrayList<CampusEventDay> items = new ArrayList<CampusEventDay>();
	Context mContext;
	private LayoutInflater mInflater;
	String[] months = { "January", "February", "March", "April", "May", "June",
			"July", "August", "September", "October", "November", "December" };

	public CampusEventAdapter(Context context, int textViewResourceId) {
		super(context, textViewResourceId);
		mContext = context;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void addItem(CampusEventDay item) {
		items.add(item);
		notifyDataSetChanged();
	}

	public int getCount() {
		return items.size();
	}

	public CampusEventDay getItem(int position) {
		return items.get(position);
	}

	public void clear() {
		items.clear();
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		CampusEventDay eventDay = items.get(position);
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.events_grid_item, null);
		}
		if (eventDay != null) {
			TextView tt = (TextView) v.findViewById(R.id.grid_date_day_text);
			TextView mt = (TextView) v.findViewById(R.id.grid_date_month_text);
			TextView bt = (TextView) v.findViewById(R.id.grid_event_items_text);
			if (tt != null) {
				tt.setText(eventDay.getDayNumber() + "");
			}
			if (mt != null) {
				mt.setText(eventDay.getDayName());
			}
			if (bt != null) {
				bt.setText(eventDay.getAmountOfEventItems() + " Events");
			}
		}
		return v;
	}
}