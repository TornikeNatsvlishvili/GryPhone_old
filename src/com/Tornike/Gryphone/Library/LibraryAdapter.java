package com.Tornike.Gryphone.Library;

import com.Tornike.Gryphone.R;

import java.util.ArrayList;
import java.util.TreeSet;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class LibraryAdapter extends BaseAdapter {

	private ArrayList<LibraryItem> items = new ArrayList<LibraryItem>();
	Context mContext;
	private LayoutInflater mInflater;
	public LibraryAdapter(Context context) {
		mContext = context;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void addItem(final LibraryItem item) {
		items.add(item);
		notifyDataSetChanged();
	}

	public int getCount() {
		return items.size();
	}

	public LibraryItem getItem(int position) {
		return items.get(position);
	}

	public long getItemId(int position) {
		return position;
	}

	public void clear() {
		items.clear();
		notifyDataSetChanged();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		int type = getItemViewType(position);

		if (v == null) {
			v = mInflater.inflate(R.layout.library_list_row, null);
		}
		LibraryItem item = items.get(position);
		if (item != null) {
			TextView tt = (TextView) v
					.findViewById(R.id.library_list_row_title);
			TextView st = (TextView) v
					.findViewById(R.id.library_list_row_date);
			
			if (tt != null) {
				tt.setText(item.getTitle());
			}
			if (st != null) {
				st.setText(item.getDate());
			}
		}

		return v;
	}
}