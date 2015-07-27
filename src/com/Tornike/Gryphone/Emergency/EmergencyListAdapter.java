package com.Tornike.Gryphone.Emergency;

import com.Tornike.Gryphone.R;

import java.util.ArrayList;
import java.util.TreeSet;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class EmergencyListAdapter extends BaseAdapter {

	private Context mContext;
	private LayoutInflater mInflater;
	ArrayList<String> contactNameList = new ArrayList<String>();
	ArrayList<String> phoneNumberList = new ArrayList<String>();
	ArrayList<String> availibilityList = new ArrayList<String>();

	public EmergencyListAdapter(Context context) {
		mContext = context;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void addItem(String name, String phoneNum, String avail) {
		contactNameList.add(name);
		phoneNumberList.add(phoneNum);
		availibilityList.add(avail);
		notifyDataSetChanged();
	}

	public int getCount() {
		return contactNameList.size();
	}

	public String[] getItem(int position) {
		return new String[] { contactNameList.get(position),
				phoneNumberList.get(position), availibilityList.get(position) };
	}

	public long getItemId(int position) {
		return position;
	}

	public void clear() {
		contactNameList.clear();
		phoneNumberList.clear();
		availibilityList.clear();
		notifyDataSetChanged();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		int type = getItemViewType(position);

		if (v == null) {
			v = mInflater.inflate(R.layout.emergency_list_row, null);
		}
		if (contactNameList.get(position) != null) {
			TextView tt = (TextView) v
					.findViewById(R.id.emergency_list_row_title);
			TextView pt = (TextView) v
					.findViewById(R.id.emergency_list_row_phone);
			TextView at = (TextView) v
					.findViewById(R.id.emergency_list_row_avail);
			if (tt != null) {
				tt.setText(contactNameList.get(position));
			}
			if (pt != null) {
				pt.setText(phoneNumberList.get(position));
			}
			if (at != null) {
				if(availibilityList.get(position).equals("")){
					at.setVisibility(at.GONE);
				}else{
					at.setText(availibilityList.get(position));
				}
			}
		}
		return v;
	}
}