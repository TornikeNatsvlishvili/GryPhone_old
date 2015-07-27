package com.Tornike.Gryphone.CourseSchedule;

import java.util.Calendar;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.Tornike.Gryphone.R;

public class AddCourseActivity extends Activity {
	// Views
	EditText courseTitle;
	CheckBox classMon, classTue, classWed, classThu, classFri;
	Spinner classLocation;
	EditText classRoomNumber;
	TimePicker classTime;
	CheckBox labMon, labTue, labWed, labThu, labFri;
	Spinner labLocation;
	EditText labRoomNumber;
	TimePicker labTime;
	ImageButton confirmButton;

	// Variables
	boolean itemEditMode = false;
	int editedItemIndex = -1;
	int classIDCarryOver = -1, labIDCarryOver = -1;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.schedule_add_course_layout);

		// View stuff initialize
		courseTitle = (EditText) findViewById(R.id.schedule_course_title);
		classMon = (CheckBox) findViewById(R.id.schedule_course_mon);
		classTue = (CheckBox) findViewById(R.id.schedule_course_tues);
		classWed = (CheckBox) findViewById(R.id.schedule_course_wed);
		classThu = (CheckBox) findViewById(R.id.schedule_course_thur);
		classFri = (CheckBox) findViewById(R.id.schedule_course_fri);
		classLocation = (Spinner) findViewById(R.id.schedule_class_location);
		classRoomNumber = (EditText) findViewById(R.id.schedule_class_location_room);
		classTime = (TimePicker) findViewById(R.id.schedule_course_time);
		labMon = (CheckBox) findViewById(R.id.schedule_lab_mon);
		labTue = (CheckBox) findViewById(R.id.schedule_lab_tues);
		labWed = (CheckBox) findViewById(R.id.schedule_lab_wed);
		labThu = (CheckBox) findViewById(R.id.schedule_lab_thur);
		labFri = (CheckBox) findViewById(R.id.schedule_lab_fri);
		labLocation = (Spinner) findViewById(R.id.schedule_lab_location);
		labRoomNumber = (EditText) findViewById(R.id.schedule_lab_location_room);
		labTime = (TimePicker) findViewById(R.id.schedule_lab_time);
		confirmButton = (ImageButton) findViewById(R.id.schedule_course_confirm);

		confirmButton.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if (courseTitle.getText().toString().equals("")) {
					Toast.makeText(AddCourseActivity.this, "Enter class name!",
							Toast.LENGTH_SHORT).show();
					return;
				}
				Intent i = new Intent();
				Bundle b = new Bundle();

				CourseItem item = new CourseItem();
				item.setTitle(courseTitle.getText().toString());
				item.setClassDates(new boolean[] { classMon.isChecked(),
						classTue.isChecked(), classWed.isChecked(),
						classThu.isChecked(), classFri.isChecked() });

				// set focus to random view so timepicker gives us right times
				classRoomNumber.requestFocus();

				item.setClassTime(new int[] { classTime.getCurrentHour(),
						classTime.getCurrentMinute() });
				item.setClassLocation(classLocation
						.getSelectedItem()
						.toString()
						.substring(
								0,
								classLocation.getSelectedItem().toString()
										.indexOf(" -")));
				item.setClassLocationSpinnerIndx(classLocation
						.getSelectedItemPosition());
				if (classRoomNumber.getText().toString().equals("")) {
					item.setClassRoomNum("-1");
				} else {
					item.setClassRoomNum(classRoomNumber.getText().toString());
				}
				item.setLabDates(new boolean[] { labMon.isChecked(),
						labTue.isChecked(), labWed.isChecked(),
						labThu.isChecked(), labFri.isChecked() });

				// set focus to random view so timepicker gives us right times
				classRoomNumber.requestFocus();
				item.setLabTime(new int[] { labTime.getCurrentHour(),
						labTime.getCurrentMinute() });
				item.setLabLocation(labLocation
						.getSelectedItem()
						.toString()
						.substring(
								0,
								labLocation.getSelectedItem().toString()
										.indexOf(" -")));
				item.setLabLocationSpinnerIndx(labLocation
						.getSelectedItemPosition());
				if (labRoomNumber.getText().toString().equals("")) {
					item.setLabRoomNum("-1");
				} else {
					item.setLabRoomNum(labRoomNumber.getText().toString());
				}

				if (classIDCarryOver != -1)
					item.setClassID(classIDCarryOver);
				if (labIDCarryOver != -1)
					item.setLabID(labIDCarryOver);

				b.putParcelable("class", item);
				i.putExtras(b);

				if (itemEditMode) {
					i.putExtra("editedItem", true);
					i.putExtra("editedItemIndex", editedItemIndex);
				}

				setResult(RESULT_OK, i);
				finish();
			}
		});

		// If Edit was pressed load item data from Intent
		Bundle b = getIntent().getExtras();
		itemEditMode = b.getBoolean("editItem");
		if (itemEditMode) {
			editedItemIndex = b.getInt("itemIndex");
			loadItemData(b);
		}
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		setResult(RESULT_CANCELED, null);
		finish();
	}

	@Override
	public void onPause() {
		super.onPause();
		setResult(RESULT_CANCELED, null);
		finish();
	}

	public void loadItemData(Bundle b) {
		CourseItem item = b.getParcelable("class");
		courseTitle.setText(item.getTitle());
		boolean[] classDates = item.getClassDates();
		classMon.setChecked(classDates[0]);
		classTue.setChecked(classDates[1]);
		classWed.setChecked(classDates[2]);
		classThu.setChecked(classDates[3]);
		classFri.setChecked(classDates[4]);
		classLocation.setSelection(item.getClassLocationSpinnerIndx());
		String classRoomNum = item.getClassRoomNum();
		if (!classRoomNum.equals("-1")) {
			classRoomNumber.setText(classRoomNum);
		}
		classTime.setCurrentHour(item.getClassTime().get(Calendar.HOUR_OF_DAY));
		classTime.setCurrentMinute(item.getClassTime().get(Calendar.MINUTE));
		boolean[] labDates = item.getLabDates();
		labMon.setChecked(labDates[0]);
		labTue.setChecked(labDates[1]);
		labWed.setChecked(labDates[2]);
		labThu.setChecked(labDates[3]);
		labFri.setChecked(labDates[4]);
		labLocation.setSelection(item.getLabLocationSpinnerIndx());
		String loadLabRoomNum = item.getLabRoomNum();
		if (!loadLabRoomNum.equals("-1")) {
			labRoomNumber.setText(loadLabRoomNum);
		}
		labTime.setCurrentHour(item.getLabTime().get(Calendar.HOUR_OF_DAY));
		labTime.setCurrentMinute(item.getLabTime().get(Calendar.MINUTE));
		classIDCarryOver = item.getMakeClassID();
		labIDCarryOver = item.getMakeLabID();
	}
}