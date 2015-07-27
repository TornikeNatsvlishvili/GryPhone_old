package com.Tornike.Gryphone.Settings;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Calendar;

import com.Tornike.Gryphone.GryphoneActivity;
import com.Tornike.Gryphone.R;
import com.Tornike.Gryphone.CourseSchedule.ClassAlarmReceiver;
import com.Tornike.Gryphone.CourseSchedule.CourseItem;
import com.Tornike.Gryphone.CourseSchedule.CourseScheduleActivity;
import com.Tornike.Gryphone.CourseSchedule.LabAlarmReceiver;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.CompoundButton.OnCheckedChangeListener;

public class SettingActivity extends Activity implements OnTouchListener {
	// Constants
	public final static String TAG = "GryPhoneSettingActivity";
	final CharSequence[] settingNotifTypeItems = { "Vibrate & Alarm",
			"Vibrate", "None", "Disable Notifications" };
	final CharSequence[] settingReminderTimes = { "5 Minutes", "10 Minutes",
			"20 Minutes", "30 Minutes", "1 Hour" };
	public final int days[] = { 2, 3, 4, 5, 6 };
	// public final int MILLIS_WEEK = 604800000;
	public final int MILLIS_WEEK = 60000;

	// File editing
	String FILENAME = "CourseList";

	// Settings
	SharedPreferences settings;
	SharedPreferences.Editor editor;

	// Alarm Manger
	AlarmManager mAlarmManager;

	// CourseList
	ArrayList<CourseItem> courseList = new ArrayList<CourseItem>();

	// UI Elements
	LinearLayout settingSchedNotifLayout, settingMainInternetWarningStyle,
			settingSchedReminderLayout;
	AlertDialog schedNotifStyleDialog, schedReminderDialog;
	TextView schedCurrentNotifStyleTxt, topBarTxt, schedCurrentReminder;

	// Setting Variables
	String schedNotificationStyle;
	boolean mainInternetWarning;
	int scheduleReminderMins;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settings_layout);

		// Initilization Variables
		settings = getSharedPreferences(Constants.PREFS_NAME, 0);
		editor = settings.edit();

		// UI Initilization
		settingSchedNotifLayout = (LinearLayout) findViewById(R.id.settings_schedule_notif_layout);
		settingMainInternetWarningStyle = (LinearLayout) findViewById(R.id.settings_main_internet_warning_layout);
		settingSchedReminderLayout = (LinearLayout) findViewById(R.id.settings_schedule_reminder_layout);
		schedCurrentReminder = (TextView) findViewById(R.id.settings_current_sched_reminder);
		topBarTxt = (TextView) findViewById(R.id.top_bar_text);
		schedCurrentNotifStyleTxt = (TextView) findViewById(R.id.settings_current_sched_notif_style);

		topBarTxt.setText("Settings");
		settingSchedNotifLayout.setOnTouchListener(this);
		settingMainInternetWarningStyle.setOnTouchListener(this);
		settingSchedReminderLayout.setOnTouchListener(this);

		// Routine
		restoreSettings();
		readData();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (schedNotifStyleDialog != null) {
			schedNotifStyleDialog.dismiss();
		}
		if (schedReminderDialog != null) {
			schedReminderDialog.dismiss();
		}
		saveSettings();
	}

	public void restoreSettings() {
		schedNotificationStyle = settings.getString(
				Constants.SETTING_SCHEDULE_NOTFICATION,
				Constants.SETTING_SCHE_NOTIF_OPTIONS.vibrate.toString());
		// set chosen style in textbox
		int styleIndex = Constants.SETTING_SCHE_NOTIF_OPTIONS
				.getIndex(schedNotificationStyle);
		if (styleIndex != 3 && styleIndex != 2) {
			schedCurrentNotifStyleTxt.setText("Notify me with a "
					+ settingNotifTypeItems[styleIndex].toString()
							.toLowerCase());
		} else if (styleIndex == 2) {
			schedCurrentNotifStyleTxt
					.setText("Notify me without a vibrate or alarm");
		} else if (styleIndex == 3) {
			schedCurrentNotifStyleTxt.setText("Never notify me");
		}

		mainInternetWarning = settings.getBoolean(
				Constants.SETTING_MAIN_INTERNET_WARNING, true);

		scheduleReminderMins = settings.getInt(
				Constants.SETTING_SCHEDULE_REMINDER, 10);
		if (scheduleReminderMins != 60) {
			schedCurrentReminder.setText("Remind me " + scheduleReminderMins
					+ " minutes before class/lab");
		} else {
			schedCurrentReminder.setText("Remind me 1 Hour before class/lab");
		}
	}

	public void saveSettings() {
		editor.putString(Constants.SETTING_SCHEDULE_NOTFICATION,
				schedNotificationStyle);
		editor.putBoolean(Constants.SETTING_MAIN_INTERNET_WARNING,
				mainInternetWarning);
		editor.putInt(Constants.SETTING_SCHEDULE_REMINDER, scheduleReminderMins);

		editor.commit();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if (event.getAction() == event.ACTION_DOWN) {
			v.setBackgroundColor(Color.GRAY);
		} else if (event.getAction() == event.ACTION_UP) {
			v.setBackgroundColor(Color.TRANSPARENT);
		}

		if (event.getAction() == event.ACTION_UP) {
			int viewId = v.getId();
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
			switch (viewId) {
			case R.id.settings_schedule_notif_layout:
				builder.setTitle("Alert Mode:");
				builder.setItems(settingNotifTypeItems,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								switch (item) {
								case (0):
									schedNotificationStyle = Constants.SETTING_SCHE_NOTIF_OPTIONS.sound_vibrate
											.toString();
									break;
								case (1):
									schedNotificationStyle = Constants.SETTING_SCHE_NOTIF_OPTIONS.vibrate
											.toString();
									break;
								case (2):
									schedNotificationStyle = Constants.SETTING_SCHE_NOTIF_OPTIONS.none
											.toString();
									break;
								case (3):
									schedNotificationStyle = Constants.SETTING_SCHE_NOTIF_OPTIONS.disable
											.toString();
									break;
								}
								if (item != 3 && item != 2) {
									schedCurrentNotifStyleTxt
											.setText("Notify me with a "
													+ settingNotifTypeItems[item]
															.toString()
															.toLowerCase());
								} else if (item == 2) {
									schedCurrentNotifStyleTxt
											.setText("Notify me without a vibrate or alarm");
								} else if (item == 3) {
									schedCurrentNotifStyleTxt
											.setText("Never notify me");
								}
							}
						});
				schedNotifStyleDialog = builder.create();
				schedNotifStyleDialog.show();

				break;
			case R.id.settings_main_internet_warning_layout:
				mainInternetWarning = true;
				Toast.makeText(this, "Internet warning dialog reset!",
						Toast.LENGTH_SHORT).show();
				break;
			case R.id.settings_schedule_reminder_layout:
				builder.setTitle("Remind Me:");
				builder.setItems(settingReminderTimes,
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								switch (item) {
								case (0):
									scheduleReminderMins = 5;
									break;
								case (1):
									scheduleReminderMins = 10;
									break;
								case (2):
									scheduleReminderMins = 20;
									break;
								case (3):
									scheduleReminderMins = 30;
									break;
								case (4):
									scheduleReminderMins = 60;
									break;
								}

								schedCurrentReminder.setText("Remind me "
										+ settingReminderTimes[item].toString()
												.toLowerCase()
										+ " before class/lab");
							}
						});
				schedReminderDialog = builder.create();
				schedReminderDialog.show();
			}
			saveSettings();
		}
		return true;
	}

	public void resetAlarms() {
		for (CourseItem item : courseList) {
			cancelClassAlarms(item.getMakeClassID());
			cancelLabAlarms(item.getMakeLabID());
		}
		setAlarms();
	}

	public void readData() {
		courseList.clear();
		try {
			byte[] buffer = new byte[1024];
			StringBuffer fileContent = new StringBuffer("");
			FileInputStream fis = openFileInput(FILENAME);
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

	public void setAlarms() {
		for (CourseItem item : courseList) {
			Intent classIntent = new Intent(this, ClassAlarmReceiver.class);
			classIntent.putExtra("className", item.getTitle());
			classIntent.putExtra("classLoc", item.getClassLocation());
			classIntent.putExtra("classRoomNum", item.getClassRoomNum());
			classIntent.putExtra("classID", item.getMakeClassID());
			classIntent.setAction("com.NOTHING");

			Intent labIntent = new Intent(this, LabAlarmReceiver.class);
			labIntent.putExtra("labName", item.getTitle());
			labIntent.putExtra("labLoc", item.getLabLocation());
			labIntent.putExtra("labRoomNum", item.getLabRoomNum());
			labIntent.putExtra("labID", item.getMakeLabID());
			labIntent.setAction("com.NOTHING");

			PendingIntent classPIntent = PendingIntent.getBroadcast(this,
					item.getMakeClassID(), classIntent,
					PendingIntent.FLAG_UPDATE_CURRENT);

			PendingIntent labPIntent = PendingIntent.getBroadcast(this,
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

	public void cancelLabAlarms(int ids) {
		Intent labIntent = new Intent(this, LabAlarmReceiver.class);
		labIntent.setAction("com.NOTHING");

		PendingIntent labPIntent = PendingIntent.getBroadcast(this, ids,
				labIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		mAlarmManager.cancel(labPIntent);
	}

	public void cancelClassAlarms(int ids) {
		Intent classIntent = new Intent(this, ClassAlarmReceiver.class);
		classIntent.setAction("com.NOTHING");

		PendingIntent classPIntent = PendingIntent.getBroadcast(this, ids,
				classIntent, PendingIntent.FLAG_UPDATE_CURRENT);

		mAlarmManager.cancel(classPIntent);
	}
}
