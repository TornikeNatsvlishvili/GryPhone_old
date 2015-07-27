package com.Tornike.Gryphone.CourseSchedule;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;

import com.Tornike.Gryphone.Settings.Constants;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Debug;
import android.util.Log;
import android.widget.Toast;

public class BootReciever extends BroadcastReceiver {
	// Constants
	 public final int MILLIS_WEEK = 604800000;
//	public final int MILLIS_WEEK = 60000;
	public final int days[] = { 2, 3, 4, 5, 6 };

	// File editing
	String FILENAME = "CourseList";

	// Settings
	SharedPreferences settings;
	SharedPreferences.Editor editor;
	int scheduleReminderMins;

	// Alarm Manger
	AlarmManager mAlarmManager;

	// CourseList
	ArrayList<CourseItem> courseList = new ArrayList<CourseItem>();

	@Override
	public void onReceive(Context context, Intent intent) {

		// Initilize alarm manager
		mAlarmManager = (AlarmManager) context
				.getSystemService(context.ALARM_SERVICE);

		// Read Settings
		settings = context.getSharedPreferences(Constants.PREFS_NAME, 0);
		editor = settings.edit();
		readSettings(context);

		// Read course data
		readData(context);

		// Set alarms
		setAlarms(context);
	}

	public void readSettings(Context context) {
		scheduleReminderMins = settings.getInt(
				Constants.SETTING_SCHEDULE_REMINDER, 10);
	}

	public void readData(Context context) {
		courseList.clear();
		try {
			byte[] buffer = new byte[1024];
			StringBuffer fileContent = new StringBuffer("");
			FileInputStream fis = context.openFileInput(FILENAME);
			String stringContent;

			while (fis.read(buffer) != -1) {
				fileContent.append(new String(buffer));
			}
			stringContent = fileContent.toString().trim();
			stringContent = stringContent.replace("\r", "");

			int loc1 = 0, loc2 = 0;

			while ((loc1 = stringContent.indexOf("(NEW COURSE START)", loc2)) != -1) {
				CourseItem temp = new CourseItem();

				loc2 = stringContent.indexOf("(END)", loc1) + 5;
				temp.parseCourseItem(stringContent.substring(loc1, loc2));

				courseList.add(temp);

				stringContent = stringContent.substring(loc2,
						stringContent.length());
				loc2 = 0;
			}

			fis.close();
		} catch (FileNotFoundException e) {
			// do nothing
			// common when the app is first installed
		} catch (Exception e) {
			Log.e("ERROR", "CourseList Read error", e);
		}
	}

	public void setAlarms(Context context) {
		for (CourseItem item : courseList) {
			Intent classIntent = new Intent(context, ClassAlarmReceiver.class);
			classIntent.putExtra("className", item.getTitle());
			classIntent.putExtra("classLoc", item.getClassLocation());
			classIntent.putExtra("classRoomNum", item.getClassRoomNum());
			classIntent.putExtra("classID", item.getMakeClassID());
			classIntent.setAction("com.NOTHING");

			Intent labIntent = new Intent(context, LabAlarmReceiver.class);
			labIntent.putExtra("labName", item.getTitle());
			labIntent.putExtra("labLoc", item.getLabLocation());
			labIntent.putExtra("labRoomNum", item.getLabRoomNum());
			labIntent.putExtra("labID", item.getMakeLabID());
			labIntent.setAction("com.NOTHING");

			PendingIntent classPIntent = PendingIntent.getBroadcast(context,
					item.getMakeClassID(), classIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);

			PendingIntent labPIntent = PendingIntent.getBroadcast(context,
					item.getMakeLabID(), labIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);

			boolean classOnce = false, labOnce = false;
			for (int a = 0; a < item.getClassDates().length; a++) {
				if (item.getClassDates()[a] == true) {

					Calendar tempTime = (Calendar) item.getClassTime().clone();
					Calendar realTime = Calendar.getInstance();

					tempTime.set(Calendar.DAY_OF_WEEK, days[a]);

					// Move clock back according to settings
					tempTime.add(Calendar.MINUTE, -1 * scheduleReminderMins);

					boolean dateMovedForward = false;
					
					while(tempTime.get(Calendar.DAY_OF_YEAR) < realTime.get(Calendar.DAY_OF_YEAR)){
						tempTime.add(Calendar.MILLISECOND, MILLIS_WEEK);
					}
					
					if(!dateMovedForward && tempTime.compareTo(realTime) == -1){
						tempTime.add(Calendar.MILLISECOND, MILLIS_WEEK);
					}
					
					mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
							tempTime.getTimeInMillis(), MILLIS_WEEK,
							classPIntent);

					if (classOnce) {
						item.setClassTime(tempTime);
						classOnce = true;
					}
				}
			}

			for (int a = 0; a < item.getLabDates().length; a++) {
				if (item.getLabDates()[a] == true) {

					Calendar tempTime = (Calendar) item.getLabTime().clone();
					Calendar realTime = Calendar.getInstance();

					tempTime.set(Calendar.DAY_OF_WEEK, days[a]);

					// Move clock back according to settings
					tempTime.add(Calendar.MINUTE, -1 * scheduleReminderMins);

					boolean dateMovedForward = false;
					
					while(tempTime.get(Calendar.DAY_OF_YEAR) < realTime.get(Calendar.DAY_OF_YEAR)){
						tempTime.add(Calendar.MILLISECOND, MILLIS_WEEK);
					}
					
					if(!dateMovedForward && tempTime.compareTo(realTime) == -1){
						tempTime.add(Calendar.MILLISECOND, MILLIS_WEEK);
					}
					
					mAlarmManager
							.setRepeating(AlarmManager.RTC_WAKEUP,
									tempTime.getTimeInMillis(), MILLIS_WEEK,
									labPIntent);
					
					if (labOnce) {
						item.setLabTime(tempTime);
						labOnce = true;
					}
				}
			}
		}
	}
}
