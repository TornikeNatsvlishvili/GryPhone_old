package com.Tornike.Gryphone.Bus;

import com.Tornike.Gryphone.R;
import com.Tornike.Gryphone.Events.*;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class BusAdapter extends BaseAdapter {

	private ArrayList<String> items = new ArrayList<String>();
	private ArrayList<Integer> itemStates = new ArrayList<Integer>();
	Context mContext;
	private LayoutInflater mInflater;
	
	public BusAdapter(Context context) {
		mContext = context;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void addItem(String item) {
		items.add(item);
		itemStates.add(Color.WHITE);
		notifyDataSetChanged();
	}

	public int getCount() {
		return items.size();
	}
	public void setHighlighted(int pos){
		int length = itemStates.size();
		for(int a = 0; a<length;a++){
			itemStates.set(a, Color.WHITE);
		}
		itemStates.set(pos, Color.YELLOW);
		notifyDataSetChanged();
	}
	public String getItem(int position) {
		return items.get(position);
	}

	public void clear() {
		items.clear();
		itemStates.clear();
		notifyDataSetChanged();
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			v = mInflater.inflate(R.layout.bus_list_item, null);
		}
		TextView text = (TextView) v.findViewById(R.id.bus_list_item_text);
		if (text != null) {
				text.setTextColor(itemStates.get(position));
			text.setText(items.get(position));
		}
		return v;
	}
}