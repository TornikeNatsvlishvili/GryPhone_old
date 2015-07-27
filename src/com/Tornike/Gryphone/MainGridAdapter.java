package com.Tornike.Gryphone;

import com.Tornike.Gryphone.R;
import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.drawable.BitmapDrawable;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.RelativeLayout.LayoutParams;
import android.widget.TextView;
import android.widget.Toast;

public class MainGridAdapter extends BaseAdapter {

	private ArrayList<String> items = new ArrayList<String>();
	private ArrayList<Integer> icons = new ArrayList<Integer>();
	Context mContext;
	private LayoutInflater mInflater;

	public MainGridAdapter(Context context) {
		mContext = context;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void addItem(String item, int itemIconIndex, boolean lastItem) {
		items.add(item);
		icons.add(itemIconIndex);
		notifyDataSetChanged();
	}

	public int getCount() {
		return items.size();
	}

	public String getItem(int position) {
		return (String) items.get(position);
	}

	public void clear() {
		items.clear();
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		String itemName = (String) items.get(position);
		if (v == null) {
			v = mInflater.inflate(R.layout.main_grid_item, null);
		}
		if (itemName != null) {
			ImageView icon = (ImageView) v
					.findViewById(R.id.grid_item_main_icon);
			TextView text = (TextView) v.findViewById(R.id.grid_item_main_text);

			if (text != null) {
				text.setText(itemName);
			}
			if (icon != null) {
				icon.setImageDrawable(mContext.getResources().getDrawable(
						icons.get(position)));
			}
		}
		return v;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}
}