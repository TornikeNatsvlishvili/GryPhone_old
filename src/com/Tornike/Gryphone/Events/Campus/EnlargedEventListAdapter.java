package com.Tornike.Gryphone.Events.Campus;

import com.Tornike.Gryphone.R;
import com.Tornike.Gryphone.Events.*;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class EnlargedEventListAdapter extends ArrayAdapter<CampusEventItem> {

	private ArrayList<CampusEventItem> items;
	Context mContext;
	private LayoutInflater mInflater;
	String[] months = { "January", "February", "March", "April", "May", "June",
			"July", "August", "September", "October", "November", "December" };

	public EnlargedEventListAdapter(Context context, int layoutResourceId,
			ArrayList<CampusEventItem> i) {
		super(context, layoutResourceId, i);
		items = i;
		mContext = context;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void addItem(CampusEventItem item) {
		items.add(item);
		notifyDataSetChanged();
	}

	public int getCount() {
		return items.size();
	}

	public CampusEventItem getItem(int position) {
		return items.get(position);
	}

	public void clear() {
		items.clear();
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			LayoutInflater vi = (LayoutInflater) mContext
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			v = vi.inflate(R.layout.events_list_row_enlarged,null);
		}
		CampusEventItem EventItem = items.get(position);
		if (EventItem != null) {
			TextView tt = (TextView) v.findViewById(R.id.toptext);
			TextView bt = (TextView) v.findViewById(R.id.bottomtext);
			TextView st = (TextView) v.findViewById(R.id.datetext);
			ImageView iv = (ImageView) v.findViewById(R.id.list_row_icon);
			if (tt != null) {
				tt.setText(EventItem.getTitle());
			}
			if (bt != null) {
				bt.setText(EventItem.getDescription().substring(0, 65) + "...");
			}
			if (st != null) {
				st.setText(months[EventItem.getEventDate()[1]] + " "
						+ EventItem.getEventDate()[2]);
			}
			if (iv != null) {
				iv.setImageDrawable(mContext.getResources().getDrawable(
						R.drawable.events_eventicon));
			}
		}
		return v;
	}
}