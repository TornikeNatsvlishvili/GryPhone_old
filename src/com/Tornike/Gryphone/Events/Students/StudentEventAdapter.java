package com.Tornike.Gryphone.Events.Students;

import com.Tornike.Gryphone.R;

import java.util.ArrayList;
import java.util.List;
import java.util.TreeSet;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
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
import android.widget.RelativeLayout;
import android.widget.TextView;

public class StudentEventAdapter extends BaseAdapter {
	public static final int TYPE_ITEM = 0;
	public static final int TYPE_SEPARATOR = 1;
	public static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

	private List items = new ArrayList();
	Context mContext;
	private LayoutInflater mInflater;
	private TreeSet mSeparatorsSet = new TreeSet();

	String[] months = { "January", "February", "March", "April", "May", "June",
			"July", "August", "September", "October", "November", "December" };

	public StudentEventAdapter(Context context) {
		mContext = context;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void addItem(StudentEventItem item) {
		items.add(item);
		notifyDataSetChanged();
	}

	public void addSeparatorItem(String item) {
		String[] temp = new String[2];
		temp[0] = item;
		temp[1] = "firstLoad";
		items.add(temp);
		// save separator position
		mSeparatorsSet.add(items.size() - 1);
		notifyDataSetChanged();
	}

	@Override
	public int getItemViewType(int position) {
		return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
	}

	@Override
	public int getViewTypeCount() {
		return TYPE_MAX_COUNT;
	}

	public int getCount() {
		return items.size();
	}

	public StudentEventItem getItem(int position) {
		if (getItemViewType(position) == TYPE_ITEM) {
			return (StudentEventItem) items.get(position);
		}
		return null;
	}

	public long getItemId(int position) {
		return position;
	}

	public void clear() {
		items.clear();
		mSeparatorsSet.clear();
		notifyDataSetChanged();
	}

	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		int type = getItemViewType(position);

		if (v == null) {
			if (type == TYPE_ITEM) {
				v = mInflater.inflate(R.layout.events_student_list_row, null);
			} else if (type == TYPE_SEPARATOR) {
				v = mInflater.inflate(R.layout.news_list_seperator, null);
			}

		}
		if (type == TYPE_ITEM) {
			StudentEventItem item = getItem(position);
			if (item != null) {
				RelativeLayout rel = (RelativeLayout) v
						.findViewById(R.id.event_student_row);
				TextView tt = (TextView) v
						.findViewById(R.id.event_student_list_row_title_text);
				TextView it = (TextView) v
						.findViewById(R.id.event_student_list_row_icon_text);
				TextView dt = (TextView) v
						.findViewById(R.id.event_student_list_row_date_text);
				TextView at = (TextView) v
						.findViewById(R.id.event_student_list_row_availability_text);
				ImageView iv = (ImageView) v
						.findViewById(R.id.event_student_list_row_icon);
				ImageView iv2 = (ImageView) v
						.findViewById(R.id.event_student_list_row_availability_icon);

				if (rel != null) {
					// rel.setBackgroundColor(item.getBackgroundColor());
				}
				if (tt != null) {
					tt.setText(item.getTitle());
					if (tt.getLineCount() > 2) {
						tt.setMaxLines(2);
						int loc = ((String) tt.getText()).lastIndexOf(" ", 53);
						tt.setText(tt.getText().subSequence(0, loc) + " ...");
					}
				}
				if (dt != null) {
					dt.setText(item.getDate());
				}
				if (at != null) {
					if (item.getEligibility().equals("FULL")) {
						at.setText(item.getEligibility() + ", "
								+ item.getAvailability() + " people waiting.");
					} else {
						at.setText(item.getEligibility() + ", "
								+ item.getAvailability() + " spots.");
					}
				}
				if (iv != null) {
					iv.setBackgroundColor(item.getTypeIcon());
				}
				if (iv2 != null) {
					iv2.setBackgroundColor(item.getAvailabilityIcon());
				}
				if( it!= null){
					it.setText(item.getIconText());
				}
			}
		} else if (type == TYPE_SEPARATOR) {
			String[] tempText = (String[]) items.get(position);
			String seperatorText = tempText[0];
			if (seperatorText != null) {
				TextView st = (TextView) v
						.findViewById(R.id.list_seperator_text);
				if (st != null) {
					st.setText(seperatorText);
				}
			}
		}

		return v;
	}
}