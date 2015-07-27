package com.Tornike.Gryphone.CourseSchedule;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.Calendar;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.Tornike.Gryphone.GryphoneActivity;
import com.Tornike.Gryphone.R;
import com.Tornike.Gryphone.CourseSchedule.CourseScheduleAdapter;
import com.Tornike.Gryphone.Maps.MapsActivity;
import com.Tornike.Gryphone.Settings.Constants;

public class CourseScheduleActivity extends Activity {
	// Constants
	 public final int MILLIS_WEEK = 604800000;
//	public final int MILLIS_WEEK = 60000;

	// Alarm Manger
	AlarmManager mAlarmManager;

	// Settings
	SharedPreferences settings;
	SharedPreferences.Editor editor;
	int scheduleReminderMins;
	boolean tempFirstRun;

	// AlertDialog
	AlertDialog noticeMaybeErrors;

	// Views
	ImageButton addCourse;
	ListView courseListView;
	ImageView topButton;
	TextView topText;
	AlertDialog courseOptionsDialog;

	// File editing
	String FILENAME = "CourseList";

	// ListView stuff
	CourseScheduleAdapter courseListAdapter;

	// Variables
	int days[] = { 2, 3, 4, 5, 6 };

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setContentView(R.layout.schedule_layout);

		// View stuff initialize
		topButton = (ImageView) findViewById(R.id.top_bar_icon);
		addCourse = (ImageButton) findViewById(R.id.top_bar_add_course);
		courseListView = (ListView) findViewById(R.id.schedule_course_list);
		topText = (TextView) findViewById(R.id.top_bar_text);

		topText.setText("Class Schedule");
		courseListAdapter = new CourseScheduleAdapter(this);
		courseListView.setAdapter(courseListAdapter);

		// top gryph button to go back to main
		topButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				finish();
			}
		});
		addCourse.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(CourseScheduleActivity.this,
						AddCourseActivity.class);
				intent.putExtra("editItem", false);
				startActivityForResult(intent, 1);
			}
		});
		courseListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1,
					final int clickPosition, long arg3) {
				final CourseItem selectedItem = courseListAdapter
						.getItem(clickPosition);

				AlertDialog.Builder buildingListDialog = new AlertDialog.Builder(
						CourseScheduleActivity.this);
				buildingListDialog.setTitle(selectedItem.getTitle()
						+ " Options...");
				buildingListDialog.setItems(new CharSequence[] { "Edit",
						"Delete", "Class location on map",
						"Lab location on map" },
						new DialogInterface.OnClickListener() {
							public void onClick(DialogInterface dialog, int item) {
								if (item == 0) {
									Intent intent = new Intent(
											CourseScheduleActivity.this,
											AddCourseActivity.class);
									Bundle b = new Bundle();
									b.putParcelable("class", selectedItem);
									b.putBoolean("editItem", true);
									intent.putExtras(b);

									// clear alarms
									cancelClassAlarms(selectedItem
											.getMakeClassID());
									cancelLabAlarms(selectedItem.getMakeLabID());

									startActivityForResult(intent, 1);
								} else if (item == 1) {
									cancelClassAlarms(selectedItem
											.getMakeClassID());
									cancelLabAlarms(selectedItem.getMakeLabID());
									courseListAdapter.removeItem(clickPosition);
									saveData();
								} else if (item == 2) {
									Intent i = new Intent(
											CourseScheduleActivity.this,
											MapsActivity.class);
									i.putExtra("buildingName",
											selectedItem.getClassLocation());
									startActivity(i);
								} else if (item == 3) {
									Intent i = new Intent(
											CourseScheduleActivity.this,
											MapsActivity.class);
									i.putExtra("buildingName",
											selectedItem.getLabLocation());
									startActivity(i);
								}
							}
						});
				courseOptionsDialog = buildingListDialog.create();
				courseOptionsDialog.show();
			}
		});

		// Initilize alarm manager
		mAlarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

		// Read Settings
//		settings = getSharedPreferences(Constants.PREFS_NAME, 0);
//		editor = settings.edit();
//		readSettings(this);

		// Read data
		readData();

//		// Show warning Message
//		if (tempFirstRun) {
//			showFirstRunMessage();
//
//			editor.putBoolean(Constants.SETTING_SCHEDULE_FIRST_USE, false);
//			editor.commit();
//		}
	}

	@Override
	public void onResume() {
		super.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		if (courseOptionsDialog != null) {
			courseOptionsDialog.dismiss();
		}
		if (noticeMaybeErrors != null) {
			noticeMaybeErrors.dismiss();
		}
	}

	public Animation getFadeOutAnimation() {
		Animation temp = new AlphaAnimation(1, 0);
		temp.setDuration(500);
		return temp;
	}

	public Animation getFadeInAnimation() {
		Animation temp = new AlphaAnimation(0, 1);
		temp.setDuration(500);
		return temp;
	}

	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if (requestCode == 1) {
			if (resultCode == RESULT_OK) {
				// Check if this was a new course or just an edit
				Bundle bundle = data.getExtras();
				boolean editedItem = bundle.getBoolean("editedItem", false);

				CourseItem temp = bundle.getParcelable("class");

				if (!editedItem) {
					temp.getMakeClassID();
					temp.getMakeLabID();
				}

				Intent classIntent = new Intent(this, ClassAlarmReceiver.class);
				classIntent.putExtra("className", temp.getTitle());
				classIntent.putExtra("classLoc", temp.getClassLocation());
				classIntent.putExtra("classRoomNum", temp.getClassRoomNum());
				classIntent.putExtra("classID", temp.getMakeClassID());
				classIntent.setAction("com.NOTHING");

				Intent labIntent = new Intent(this, LabAlarmReceiver.class);
				labIntent.putExtra("labName", temp.getTitle());
				labIntent.putExtra("labLoc", temp.getLabLocation());
				labIntent.putExtra("labRoomNum", temp.getLabRoomNum());
				labIntent.putExtra("labID", temp.getMakeLabID());
				labIntent.setAction("com.NOTHING");

				PendingIntent classPIntent = PendingIntent.getBroadcast(this,
						temp.getMakeClassID(), classIntent,
						PendingIntent.FLAG_UPDATE_CURRENT);
				PendingIntent labPIntent = PendingIntent.getBroadcast(this,
						temp.getMakeLabID(), labIntent,
						PendingIntent.FLAG_UPDATE_CURRENT);

				for (int a = 0; a < temp.getClassDates().length; a++) {
					if (temp.getClassDates()[a] == true) {

						Calendar tempTime = (Calendar) temp.getClassTime()
								.clone();
						Calendar realTime = Calendar.getInstance();

						tempTime.set(Calendar.DAY_OF_WEEK, days[a]);

						// Move clock back according to settings
						tempTime.add(Calendar.MINUTE, -1 * scheduleReminderMins);
						
						boolean dateMovedForward = false;
						
						while(tempTime.get(Calendar.DAY_OF_YEAR) < realTime.get(Calendar.DAY_OF_YEAR)){
							tempTime.add(Calendar.MILLISECOND, MILLIS_WEEK);
							dateMovedForward = true;
						}

						if(!dateMovedForward && tempTime.compareTo(realTime) == -1){
							tempTime.add(Calendar.MILLISECOND, MILLIS_WEEK);
						}

						mAlarmManager.setRepeating(AlarmManager.RTC_WAKEUP,
								tempTime.getTimeInMillis(), MILLIS_WEEK,
								classPIntent);
					}
				}

				for (int a = 0; a < temp.getLabDates().length; a++) {
					if (temp.getLabDates()[a] == true) {

						Calendar tempTime = (Calendar) temp.getLabTime()
								.clone();
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
								labPIntent);
					}
				}

				if (editedItem) {
					int editedItemIndex = bundle.getInt("editedItemIndex", -1);
					courseListAdapter.setItem(editedItemIndex, temp);
				} else {
					courseListAdapter.addItem(temp);
				}

				saveData();
			}
		}
	}

	public void saveData() {
		deleteFile(FILENAME);
		String output = "";

		for (int a = 0; a < courseListAdapter.getCount(); a++) {
			CourseItem item = courseListAdapter.getItem(a);
			output += item.toString();
		}

		FileOutputStream fos;
		try {
			fos = openFileOutput(FILENAME, Context.MODE_PRIVATE);
			fos.write(output.getBytes());
			fos.close();
		} catch (Exception e) {
			Log.e("ERROR", "CourseList Save error", e);
		}
	}

	public void readData() {
		courseListAdapter.clear();
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

				courseListAdapter.addItem(temp);

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

//	public void readSettings(Context context) {
//		scheduleReminderMins = settings.getInt(
//				Constants.SETTING_SCHEDULE_REMINDER, 10);
//		tempFirstRun = settings.getBoolean(Constants.SETTING_SCHEDULE_FIRST_USE,
//				false);
//	}
//
//	public void showFirstRunMessage() {
//		AlertDialog.Builder builder = new AlertDialog.Builder(this);
//		String phrase = "This is a new feature, so if you find any bugs/errors please let me know by:"
//				+ "<br><br>1. Go to the main menu."
//				+ "<br>2. Click 'About'."
//				+ "<br>3. Choose appropriate subject."
//				+ "<br><br>To start using this feature add your classes by clicking the plus button above!"
//				+ "<br><br><br>Thank you & enjoy!";
//		builder.setTitle("Notice!");
//		builder.setMessage(Html.fromHtml(phrase));
//		builder.setNegativeButton("Okay", null);
//
//		noticeMaybeErrors = builder.create();
//		noticeMaybeErrors.show();
//	}
}