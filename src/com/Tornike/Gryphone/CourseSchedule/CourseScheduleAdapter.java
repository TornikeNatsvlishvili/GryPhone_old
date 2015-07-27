package com.Tornike.Gryphone.CourseSchedule;

import com.Tornike.Gryphone.R;
import com.Tornike.Gryphone.Events.*;
import java.util.ArrayList;
import java.util.Calendar;

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

public class CourseScheduleAdapter extends BaseAdapter {

	private ArrayList<CourseItem> items = new ArrayList<CourseItem>();
	private ArrayList<Integer> itemColors = new ArrayList<Integer>();
	boolean colorCounter = false;
	Context mContext;
	private LayoutInflater mInflater;

	public CourseScheduleAdapter(Context context) {
		mContext = context;
		mInflater = (LayoutInflater) mContext
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public void addItem(CourseItem item) {
		items.add(item);
		if (!colorCounter) {
			itemColors.add(Color.argb(255, 50, 50, 50));
			colorCounter = true;
		} else {
			itemColors.add(Color.argb(255, 100, 100, 100));
			colorCounter = false;
		}
		notifyDataSetChanged();
	}
	public void removeItem(int position){
		items.remove(position);
		notifyDataSetChanged();
	}
	public int getCount() {
		return items.size();
	}

	public CourseItem getItem(int position) {
		return items.get(position);
	}
	
	public void setItem(int position,CourseItem item) {
		items.set(position,item);
		notifyDataSetChanged();
	}
	
	public void clear() {
		items.clear();
		notifyDataSetChanged();
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View v = convertView;
		if (v == null) {
			v = mInflater.inflate(R.layout.schedule_list_row, null);
		}
		v.setBackgroundColor(itemColors.get(position));
		TextView txtTitle = (TextView) v
				.findViewById(R.id.schedule_course_title);
		TextView txtClassTime = (TextView) v
				.findViewById(R.id.schedule_class_time);
		ImageView classDays[] = new ImageView[5];
		classDays[0] = (ImageView) v.findViewById(R.id.schedule_class_img_mon);
		classDays[1] = (ImageView) v.findViewById(R.id.schedule_class_img_tue);
		classDays[2] = (ImageView) v.findViewById(R.id.schedule_class_img_wed);
		classDays[3] = (ImageView) v.findViewById(R.id.schedule_class_img_thu);
		classDays[4] = (ImageView) v.findViewById(R.id.schedule_class_img_fri);
		TextView txtClassLocation = (TextView) v
				.findViewById(R.id.schedule_class_location);
		TextView txtClassRoom = (TextView) v
				.findViewById(R.id.schedule_class_location_room);

		TextView txtLabTime = (TextView) v.findViewById(R.id.schedule_lab_time);
		ImageView labDays[] = new ImageView[5];
		labDays[0] = (ImageView) v.findViewById(R.id.schedule_lab_img_mon);
		labDays[1] = (ImageView) v.findViewById(R.id.schedule_lab_img_tue);
		labDays[2] = (ImageView) v.findViewById(R.id.schedule_lab_img_wed);
		labDays[3] = (ImageView) v.findViewById(R.id.schedule_lab_img_thu);
		labDays[4] = (ImageView) v.findViewById(R.id.schedule_lab_img_fri);
		TextView txtLabLocation = (TextView) v
				.findViewById(R.id.schedule_lab_location);
		TextView txtLabRoom = (TextView) v
				.findViewById(R.id.schedule_lab_location_room);

		if (txtTitle != null) {
			txtTitle.setText(items.get(position).getTitle());
		}
		if (txtClassTime != null) {
			int hour = items.get(position).getClassTime().get(Calendar.HOUR_OF_DAY);
			int minute = items.get(position).getClassTime().get(Calendar.MINUTE);
			String amPm = "";
			if (hour > 11) {
				hour -= 12;
				amPm = "PM";
			} else {
				amPm = "AM";
			}
			if (minute < 10) {
				txtClassTime.setText(hour + ":0" + minute + " " + amPm);
			} else {
				txtClassTime.setText(hour + ":" + minute + " " + amPm);
			}
		}
		for (int a = 0; a < 5; a++) {
			if (classDays[a] != null) {
				if (items.get(position).getClassDates()[a]) {
					classDays[a]
							.setBackgroundDrawable((mContext.getResources())
									.getDrawable(R.drawable.schedule_green_orb));
				} else {
					classDays[a]
							.setBackgroundDrawable((mContext.getResources())
									.getDrawable(R.drawable.schedule_red_orb));
				}
			}
		}

		if (txtClassLocation != null) {
			txtClassLocation.setText(items.get(position).getClassLocation());
		}
		if (txtClassRoom != null) {
			String temp = items.get(position).getClassRoomNum()+"";
			if(temp.equals("-1")){
				txtClassRoom.setText("N/A");
			}else{
				txtClassRoom.setText(temp);
			}
		}

		if (txtLabTime != null) {
			int hour = items.get(position).getLabTime().get(Calendar.HOUR_OF_DAY);
			int minute = items.get(position).getLabTime().get(Calendar.MINUTE);
			String amPm = "";
			if (hour > 11) {
				hour -= 12;
				amPm = "PM";
			} else {
				amPm = "AM";
			}
			if (minute < 10) {
				txtLabTime.setText(hour + ":0" + minute + " " + amPm);
			} else {
				txtLabTime.setText(hour + ":" + minute + " " + amPm);
			}
		}
		for (int a = 0; a < 5; a++) {
			if (labDays[a] != null) {
				if (items.get(position).getLabDates()[a]) {
					labDays[a].setBackgroundDrawable((mContext.getResources())
							.getDrawable(R.drawable.schedule_green_orb));
				} else {
					labDays[a].setBackgroundDrawable((mContext.getResources())
							.getDrawable(R.drawable.schedule_red_orb));
				}
			}
		}
		if (txtLabLocation != null) {
			txtLabLocation.setText(items.get(position).getLabLocation());
		}
		if (txtLabRoom != null) {
			String temp = items.get(position).getLabRoomNum()+"";
			if(temp.equals("-1")){
				txtLabRoom.setText("N/A");
			}else{
				txtLabRoom.setText(temp);
			}
		}
		return v;
	}

	@Override
	public long getItemId(int position) {
		return 0;
	}
}